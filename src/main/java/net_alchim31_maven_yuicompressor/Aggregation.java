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
package net_alchim31_maven_yuicompressor;

import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.build.BuildContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class Aggregation.
 */
public class Aggregation {

    /** The Constant Logger. */
    private static final Logger logger = LoggerFactory.getLogger(Aggregation.class);

    /** The input dir. */
    public File inputDir;

    /** The output. */
    public File output;

    /** The includes. */
    public String[] includes;

    /** The excludes. */
    public String[] excludes;

    /** The remove included. */
    public boolean removeIncluded;

    /** The insert new line. */
    public boolean insertNewLine;

    /** The insert file header. */
    public boolean insertFileHeader;

    /** The fix last semicolon. */
    public boolean fixLastSemicolon;

    /** The auto exclude wildcards. */
    public boolean autoExcludeWildcards;

    /**
     * Run.
     *
     * @param previouslyIncludedFiles the previously included files
     * @param buildContext the build context
     * @return the list
     * @throws Exception the exception
     */
    public List<File> run(Collection<File> previouslyIncludedFiles, BuildContext buildContext) throws Exception {
        return this.run(previouslyIncludedFiles, buildContext, null);
    }

    /**
     * Run.
     *
     * @param previouslyIncludedFiles the previously included files
     * @param buildContext the build context
     * @param incrementalFiles the incremental files
     * @return the list
     * @throws Exception the exception
     */
    public List<File> run(Collection<File> previouslyIncludedFiles, BuildContext buildContext, Set<String> incrementalFiles) throws Exception {
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
                        if (file.exists() && file.delete()) {
                          logger.warn("unable to delete file: {}", file);
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
     * @param file the file
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
     * @throws Exception the exception
     */
    private void defineInputDir() throws Exception {
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
     * @param previouslyIncludedFiles the previously included files
     * @param buildContext the build context
     * @param incrementalFiles the incremental files
     * @return the included files
     * @throws Exception the exception
     */
    private List<File> getIncludedFiles(Collection<File> previouslyIncludedFiles, BuildContext buildContext, Set<String> incrementalFiles) throws Exception {
        List<File> filesToAggregate = new ArrayList<>();
        if (includes != null) {
            for (String include : includes) {
                addInto(include, filesToAggregate, previouslyIncludedFiles);
            }
        }

        //If build is incremental with no delta, then don't include for aggregation
        if (buildContext.isIncremental()) {

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
        } else {
            return filesToAggregate;
        }

    }

    /**
     * Adds the into.
     *
     * @param include the include
     * @param includedFiles the included files
     * @param previouslyIncludedFiles the previously included files
     * @throws Exception the exception
     */
    private void addInto(String include, List<File> includedFiles, Collection<File> previouslyIncludedFiles) throws Exception {
        if (include.indexOf('*') > -1) {
            DirectoryScanner scanner = newScanner();
            scanner.setIncludes(new String[]{include});
            scanner.scan();
            String[] rpaths = scanner.getIncludedFiles();
            Arrays.sort(rpaths);
            for (String rpath : rpaths) {
                File file = new File(scanner.getBasedir(), rpath);
                if (!includedFiles.contains(file) && (previouslyIncludedFiles == null || !previouslyIncludedFiles.contains(file))) {
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
     * @throws Exception the exception
     */
    private DirectoryScanner newScanner() throws Exception {
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(inputDir);
        if ((excludes != null) && (excludes.length != 0)) {
            scanner.setExcludes(excludes);
        }
        scanner.addDefaultExcludes();
        return scanner;
    }
}
