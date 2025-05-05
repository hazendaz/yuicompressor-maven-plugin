/*
 * YuiCompressor Maven plugin
 *
 * Copyright 2012-2025 Hazendaz.
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
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import javax.inject.Inject;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.build.BuildContext;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.Scanner;

/**
 * Common class for mojos.
 */
public abstract class MojoSupport extends AbstractMojo {

    /** The Constant EMPTY_STRING_ARRAY. */
    private static final String[] EMPTY_STRING_ARRAY = {};

    /**
     * Javascript source directory. (result will be put to outputDirectory).
     */
    @Parameter(defaultValue = "${project.basedir}/src/main/js")
    private File sourceDirectory;

    /**
     * Single directory for extra files to include in the WAR.
     */
    @Parameter(defaultValue = "${project.basedir}/src/main/webapp")
    private File warSourceDirectory;

    /**
     * The directory where the webapp is built.
     */
    @Parameter(defaultValue = "${project.build.directory}/${project.build.finalName}")
    private File webappDirectory;

    /**
     * The output directory into which to copy the resources.
     */
    @Parameter(defaultValue = "${project.build.outputDirectory}", required = true)
    private File outputDirectory;

    /**
     * The list of resources we want to transfer.
     */
    @Parameter(defaultValue = "${project.resources}", required = true, readonly = true)
    private List<Resource> resources;

    /** list of additional excludes. */
    @Parameter
    private List<String> excludes;

    /** Use processed resources if available. */
    @Parameter(defaultValue = "false")
    private boolean useProcessedResources;

    /** list of additional includes. */
    @Parameter
    private List<String> includes;

    /** Excludes files from webapp directory. */
    @Parameter
    private boolean excludeWarSourceDirectory;

    /**
     * Excludes files from resources directories.
     */
    @Parameter(defaultValue = "false")
    private boolean excludeResources;

    /** Maven Project. */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    protected MavenProject project;

    /** [js only] Display possible errors in the code. */
    @Parameter(defaultValue = "true", property = "maven.yuicompressor.jswarn")
    protected boolean jswarn;

    /**
     * Whether to skip execution.
     */
    @Parameter(defaultValue = "false", property = "maven.yuicompressor.skip")
    private boolean skip;

    /**
     * Define if plugin must stop/fail on warnings.
     */
    @Parameter(defaultValue = "false", property = "maven.yuicompressor.failOnWarning")
    protected boolean failOnWarning;

    /**
     * Build Context.
     */
    @Inject
    protected BuildContext buildContext;

    /** The js error reporter. */
    protected ErrorReporter4Mojo jsErrorReporter;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skip) {
            getLog().debug("run of yuicompressor-maven-plugin skipped");
            return;
        }

        if (failOnWarning) {
            jswarn = true;
        }

        jsErrorReporter = new ErrorReporter4Mojo(getLog(), jswarn, buildContext);

        try {
            beforeProcess();
            processDir(sourceDirectory, outputDirectory, null, useProcessedResources);
            if (!excludeResources) {
                for (Resource resource : resources) {
                    File destRoot = outputDirectory;
                    if (resource.getTargetPath() != null) {
                        destRoot = outputDirectory.toPath().resolve(resource.getTargetPath()).toFile();
                    }
                    processDir(Path.of(resource.getDirectory()).toFile(), destRoot, resource.getExcludes(),
                            useProcessedResources);
                }
            }
            if (!excludeWarSourceDirectory) {
                processDir(warSourceDirectory, webappDirectory, null, useProcessedResources);
            }
            afterProcess();
        } catch (Exception e) {
            throw new MojoExecutionException("wrap: " + e.getMessage(), e);
        }

        getLog().info(String.format("nb warnings: %d, nb errors: %d", jsErrorReporter.getWarningCnt(),
                jsErrorReporter.getErrorCnt()));
        if (failOnWarning && jsErrorReporter.getWarningCnt() > 0) {
            throw new MojoFailureException("warnings on " + this.getClass().getSimpleName() + "=> failure ! (see log)");
        }
    }

    /**
     * Gets the default includes.
     *
     * @return the default includes
     */
    protected abstract String[] getDefaultIncludes();

    /**
     * Before process.
     *
     * @throws IOException
     *             the IO exception
     */
    protected abstract void beforeProcess() throws IOException;

    /**
     * After process.
     *
     * @throws IOException
     *             the IO exception
     */
    protected abstract void afterProcess() throws IOException;

    /**
     * Force to use defaultIncludes (ignore srcIncludes) to avoid processing resources/includes from other type than
     * *.css or *.js see https://github.com/davidB/yuicompressor-maven-plugin/issues/19
     *
     * @param srcRoot
     *            the src root
     * @param destRoot
     *            the dest root
     * @param srcExcludes
     *            the src excludes
     * @param destAsSource
     *            the dest as source
     *
     * @throws IOException
     *             the IO exception
     * @throws MojoFailureException
     *             the Mojo Failure Exception
     * @throws MojoExecutionException
     *             the Mojo Execution Exception
     */
    private void processDir(File srcRoot, File destRoot, List<String> srcExcludes, boolean destAsSource)
            throws IOException, MojoFailureException, MojoExecutionException {
        if (srcRoot == null) {
            return;
        }
        if (!srcRoot.exists()) {
            buildContext.addMessage(srcRoot, 0, 0, "Directory " + srcRoot.getPath() + " does not exist",
                    BuildContext.SEVERITY_WARNING, null);
            getLog().info("Directory " + srcRoot.getPath() + " does not exist");
            return;
        }
        if (destRoot == null) {
            throw new MojoFailureException("destination directory for " + srcRoot + " is null");
        }
        Scanner scanner;
        if (!buildContext.isIncremental()) {
            DirectoryScanner dScanner = new DirectoryScanner();
            dScanner.setBasedir(srcRoot);
            scanner = dScanner;
        } else {
            scanner = buildContext.newScanner(srcRoot);
        }

        if (includes == null) {
            scanner.setIncludes(getDefaultIncludes());
        } else {
            scanner.setIncludes(includes.toArray(new String[0]));
        }

        if (srcExcludes != null && !srcExcludes.isEmpty()) {
            scanner.setExcludes(srcExcludes.toArray(EMPTY_STRING_ARRAY));
        }
        if (excludes != null && !excludes.isEmpty()) {
            scanner.setExcludes(excludes.toArray(EMPTY_STRING_ARRAY));
        }
        scanner.addDefaultExcludes();

        scanner.scan();

        String[] includedFiles = scanner.getIncludedFiles();
        if (includedFiles == null || includedFiles.length == 0) {
            if (buildContext.isIncremental()) {
                getLog().info("No files have changed, so skipping the processing");
            } else {
                getLog().info("No files to be processed");
            }
            return;
        }
        for (String name : includedFiles) {
            SourceFile src = new SourceFile(srcRoot, destRoot, name, destAsSource);
            jsErrorReporter.setDefaultFileName("..."
                    + src.toFile().getCanonicalPath().substring(src.toFile().getCanonicalPath().lastIndexOf('/') + 1));
            jsErrorReporter.setFile(src.toFile());
            processFile(src);
        }
    }

    /**
     * Process file.
     *
     * @param src
     *            the src
     *
     * @throws IOException
     *             the IO exception
     * @throws MojoExecutionException
     *             the mojo execution exception
     */
    protected abstract void processFile(SourceFile src) throws IOException, MojoExecutionException;
}
