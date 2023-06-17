/*
 * YuiCompressor Maven plugin
 *
 * Copyright 2012-2023 Hazendaz.
 *
 * Licensed under the GNU Lesser General Public License (LGPL),
 * version 2.1 or later (the "License").
 * You may not use this file except in compliance with the License.
 * You may read the licence in the 'lgpl.txt' file in the root folder of
 * project or obtain a copy at
 *
 *     https://www.gnu.org/licenses/lgpl-2.1.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.alchim31.maven.yuicompressor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.codehaus.plexus.build.BuildContext;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.IOUtil;

/**
 * The Class Aggregation.
 */
public class Aggregation {

    /** The input dir. */
    private File inputDir;

    /** The output. */
    private File output;

    /** The includes. */
    private String[] includes;

    /** The excludes. */
    private String[] excludes;

    /** The remove included. */
    private boolean removeIncluded;

    /** The insert new line. */
    private boolean insertNewLine;

    /** The insert file header. */
    private boolean insertFileHeader;

    /** The fix last semicolon. */
    private boolean fixLastSemicolon;

    /** The auto exclude wildcards. */
    private boolean autoExcludeWildcards;

    /**
     * Gets the output.
     *
     * @return the output
     */
    public File getOutput() {
        return output;
    }

    /**
     * Sets the output.
     *
     * @param output
     *            the new output
     */
    public void setOutput(File output) {
        this.output = output;
    }

    /**
     * Sets the includes.
     *
     * @param includes
     *            the new includes
     */
    public void setIncludes(String[] includes) {
        this.includes = includes;
    }

    /**
     * Sets the insert new line.
     *
     * @param insertNewLine
     *            the new insert new line
     */
    public void setInsertNewLine(boolean insertNewLine) {
        this.insertNewLine = insertNewLine;
    }

    /**
     * Sets the auto exclude wildcards.
     *
     * @param autoExcludeWildcards
     *            the new auto exclude wildcards
     */
    public void setAutoExcludeWildcards(boolean autoExcludeWildcards) {
        this.autoExcludeWildcards = autoExcludeWildcards;
    }

    /**
     * Run.
     *
     * @param previouslyIncludedFiles
     *            the previously included files
     * @param buildContext
     *            the build context
     *
     * @return the list
     *
     * @throws IOException
     *             the IO exception
     */
    public List<File> run(Collection<File> previouslyIncludedFiles, BuildContext buildContext) throws IOException {
        return this.run(previouslyIncludedFiles, buildContext, null);
    }

    /**
     * Run.
     *
     * @param previouslyIncludedFiles
     *            the previously included files
     * @param buildContext
     *            the build context
     * @param incrementalFiles
     *            the incremental files
     *
     * @return the list
     *
     * @throws IOException
     *             the IO exception
     */
    public List<File> run(Collection<File> previouslyIncludedFiles, BuildContext buildContext,
            Set<String> incrementalFiles) throws IOException {
        defineInputDir();

        List<File> files;
        if (autoExcludeWildcards) {
            files = getIncludedFiles(previouslyIncludedFiles, buildContext, incrementalFiles);
        } else {
            files = getIncludedFiles(null, buildContext, incrementalFiles);
        }

        if (!files.isEmpty()) {
            output = output.getCanonicalFile();
            output.getParentFile().mkdirs();
            try (OutputStream out = buildContext.newFileOutputStream(output)) {
                for (File file : files) {
                    if (file.getCanonicalPath().equals(output.getCanonicalPath())) {
                        continue;
                    }
                    try (FileInputStream in = new FileInputStream(file)) {
                        if (insertFileHeader) {
                            out.write(createFileHeader(file).getBytes(StandardCharsets.UTF_8));
                        }
                        IOUtil.copy(in, out);
                        if (fixLastSemicolon) {
                            out.write(';');
                        }
                        if (insertNewLine) {
                            out.write('\n');
                        }
                    }
                    if (removeIncluded) {
                        if (file.exists()) {
                            Files.delete(file.toPath());
                        }
                        buildContext.refresh(file);
                    }
                }
            }
        }
        return files;
    }

    /**
     * Creates the file header.
     *
     * @param file
     *            the file
     *
     * @return the string
     */
    private String createFileHeader(File file) {
        StringBuilder header = new StringBuilder();
        header.append("/*");
        header.append(file.getName());
        header.append("*/");

        if (insertNewLine) {
            header.append('\n');
        }

        return header.toString();
    }

    /**
     * Define input dir.
     *
     * @throws IOException
     *             the exception
     */
    private void defineInputDir() throws IOException {
        if (inputDir == null) {
            inputDir = output.getParentFile();
        }
        inputDir = inputDir.getCanonicalFile();
        if (!inputDir.isDirectory()) {
            throw new IllegalStateException("input directory not found: " + inputDir);
        }
    }

    /**
     * Gets the included files.
     *
     * @param previouslyIncludedFiles
     *            the previously included files
     * @param buildContext
     *            the build context
     * @param incrementalFiles
     *            the incremental files
     *
     * @return the included files
     *
     * @throws IOException
     *             the IO exception
     */
    private List<File> getIncludedFiles(Collection<File> previouslyIncludedFiles, BuildContext buildContext,
            Set<String> incrementalFiles) throws IOException {
        List<File> filesToAggregate = new ArrayList<>();
        if (includes != null) {
            for (String include : includes) {
                addInto(include, filesToAggregate, previouslyIncludedFiles);
            }
        }

        // If build is incremental with no delta, then don't include for aggregation
        if (!buildContext.isIncremental()) {
            return filesToAggregate;
        }
        if (incrementalFiles != null) {
            boolean aggregateMustBeUpdated = false;
            for (File file : filesToAggregate) {
                if (incrementalFiles.contains(file.getCanonicalPath())) {
                    aggregateMustBeUpdated = true;
                    break;
                }
            }

            if (aggregateMustBeUpdated) {
                return filesToAggregate;
            }
        }
        return new ArrayList<>();

    }

    /**
     * Adds the into.
     *
     * @param include
     *            the include
     * @param includedFiles
     *            the included files
     * @param previouslyIncludedFiles
     *            the previously included files
     */
    private void addInto(String include, List<File> includedFiles, Collection<File> previouslyIncludedFiles) {
        if (include.indexOf('*') > -1) {
            DirectoryScanner scanner = newScanner();
            scanner.setIncludes(new String[] { include });
            scanner.scan();
            String[] rpaths = scanner.getIncludedFiles();
            Arrays.sort(rpaths);
            for (String rpath : rpaths) {
                File file = new File(scanner.getBasedir(), rpath);
                if (!includedFiles.contains(file)
                        && (previouslyIncludedFiles == null || !previouslyIncludedFiles.contains(file))) {
                    includedFiles.add(file);
                }
            }
        } else {
            File file = new File(include);
            if (!file.isAbsolute()) {
                file = new File(inputDir, include);
            }
            if (!includedFiles.contains(file)) {
                includedFiles.add(file);
            }
        }
    }

    /**
     * New scanner.
     *
     * @return the directory scanner
     */
    private DirectoryScanner newScanner() {
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(inputDir);
        if (excludes != null && excludes.length != 0) {
            scanner.setExcludes(excludes);
        }
        scanner.addDefaultExcludes();
        return scanner;
    }
}
