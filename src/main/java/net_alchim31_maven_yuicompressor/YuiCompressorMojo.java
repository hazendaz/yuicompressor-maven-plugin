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

import com.yahoo.platform.yui.compressor.CssCompressor;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

/**
 * Apply compression on JS and CSS (using YUI Compressor).
 *
 * @author David Bernard
 * @since 2007-08-28
 */
@Mojo(name = "compress", defaultPhase = LifecyclePhase.PROCESS_RESOURCES, requiresProject = true, threadSafe = true)
public class YuiCompressorMojo extends MojoSupport {

    /**
     * Read the input file using "encoding".
     */
    @Parameter(defaultValue="${project.build.sourceEncoding}", property="file.encoding")
    private String encoding;

    /**
     * The output filename suffix.
     */
    @Parameter(defaultValue="-min", property="maven.yuicompressor.suffix")
    private String suffix;

    /**
     * If no "suffix" must be add to output filename (maven's configuration manage empty suffix like default).
     */
    @Parameter(defaultValue="false", property="maven.yuicompressor.nosuffix")
    private boolean nosuffix;

    /**
     * Insert line breaks in output after the specified column number.
     */
    @Parameter(defaultValue="-1", property="maven.yuicompressor.linebreakpos")
    private int linebreakpos;

    /** [js only] No compression. */
    @Parameter(defaultValue="false", property="maven.yuicompressor.nocompress")
    private boolean nocompress;

    /**
     * [js only] Minify only, do not obfuscate.
     */
    @Parameter(defaultValue="false", property="maven.yuicompressor.nomunge")
    private boolean nomunge;

    /**
     * [js only] Preserve unnecessary semicolons.
     */
    @Parameter(defaultValue="false", property="maven.yuicompressor.preserveAllSemiColons")
    private boolean preserveAllSemiColons;

    /**
     * [js only] disable all micro optimizations.
     */
    @Parameter(defaultValue="false", property="maven.yuicompressor.disableOptimizations")
    private boolean disableOptimizations;

    /**
     * force the compression of every files,
     * else if compressed file already exists and is younger than source file, nothing is done.
     */
    @Parameter(defaultValue="false", property="maven.yuicompressor.force")
    private boolean force;

    /**
     * a list of aggregation/concatenation to do after processing,
     * for example to create big js files that contain several small js files.
     * Aggregation could be done on any type of file (js, css, ...).
     */
    @Parameter
    private Aggregation[] aggregations;

    /**
     * request to create a gzipped version of the yuicompressed/aggregation files.
     */
    @Parameter(defaultValue="false", property="maven.yuicompressor.gzip")
    private boolean gzip;

    /** gzip level. */
    @Parameter(defaultValue="9", property="maven.yuicompressor.level")
    private int level;

    /**
     * show statistics (compression ratio).
     */
    @Parameter(defaultValue="true", property="maven.yuicompressor.statistics")
    private boolean statistics;

    /** aggregate files before minify. */
    @Parameter(defaultValue="false", property="maven.yuicompressor.preProcessAggregates")
    private boolean preProcessAggregates;

    /** use the input file as output when the compressed file is larger than the original. */
    @Parameter(defaultValue="true", property="maven.yuicompressor.useSmallestFile")
    private boolean useSmallestFile;

    /** The in size total. */
    private long inSizeTotal_;

    /** The out size total. */
    private long outSizeTotal_;

    /** Keep track of updated files for aggregation on incremental builds. */
    private Set<String> incrementalFiles;

    @Override
    protected String[] getDefaultIncludes() throws Exception {
        return new String[]{"**/*.css", "**/*.js"};
    }

    @Override
    public void beforeProcess() throws Exception {
        if (nosuffix) {
            suffix = "";
        }

        if (preProcessAggregates) aggregate();
    }

    @Override
    protected void afterProcess() throws Exception {
        if (statistics && (inSizeTotal_ > 0)) {
            getLog().info(String.format("total input (%db) -> output (%db)[%d%%]", inSizeTotal_, outSizeTotal_, ((outSizeTotal_ * 100) / inSizeTotal_)));
        }

        if (!preProcessAggregates) {
            aggregate();
        }
    }

    /**
     * Aggregate.
     *
     * @throws Exception the exception
     */
    private void aggregate() throws Exception {
        if (aggregations != null) {
            Set<File> previouslyIncludedFiles = new HashSet<File>();
            for (Aggregation aggregation : aggregations) {
                getLog().info("generate aggregation : " + aggregation.output);
                Collection<File> aggregatedFiles = aggregation.run(previouslyIncludedFiles, buildContext, incrementalFiles);
                previouslyIncludedFiles.addAll(aggregatedFiles);

                File gzipped = gzipIfRequested(aggregation.output);
                if (statistics) {
                    if (gzipped != null) {
                        getLog().info(String.format("%s (%db) -> %s (%db)[%d%%]", aggregation.output.getName(), aggregation.output.length(), gzipped.getName(), gzipped.length(), ratioOfSize(aggregation.output, gzipped)));
                    } else if (aggregation.output.exists()) {
                        getLog().info(String.format("%s (%db)", aggregation.output.getName(), aggregation.output.length()));
                    } else {
                        getLog().warn(String.format("%s not created", aggregation.output.getName()));
                    }
                }
            }
        }
    }

    @Override
    protected void processFile(SourceFile src) throws Exception {
        File inFile = src.toFile();
        getLog().debug("on incremental build only compress if input file has Delta");
        if (buildContext.isIncremental()) {
            if (!buildContext.hasDelta(inFile)) {
                if (getLog().isInfoEnabled()) {
                    getLog().info("nothing to do, " + inFile + " has no Delta");
                }
                return;
            }
            if (incrementalFiles == null) {
                incrementalFiles = new HashSet<>();
            }
        }

        if (getLog().isDebugEnabled()) {
            getLog().debug("compress file :" + src.toFile() + " to " + src.toDestFile(suffix));
        }

        File outFile = src.toDestFile(suffix);
        if (!nosuffix && isMinifiedFile(inFile)) {
            return;
        }
        getLog().debug("only compress if input file is younger than existing output file");
        if (!force && outFile.exists() && (outFile.lastModified() > inFile.lastModified())) {
            if (getLog().isInfoEnabled()) {
                getLog().info("nothing to do, " + outFile + " is younger than original, use 'force' option or clean your target");
            }
            return;
        }
        File outFileTmp = new File(outFile.getAbsolutePath() + ".tmp");
        FileUtils.forceDelete(outFileTmp);

        if (!outFile.getParentFile().exists() && !outFile.getParentFile().mkdirs()) {
            throw new MojoExecutionException("Cannot create resource output directory: " + outFile.getParentFile());
        }
        getLog().debug("use a temporary outputfile (in case in == out)");

        try (InputStreamReader in = new InputStreamReader(new FileInputStream(inFile), Charset.forName(encoding));
            /* outFileTmp will be deleted create with FileOutputStream  */
            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(outFileTmp), Charset.forName(encoding));) {

            getLog().debug("start compression");
            if (nocompress) {
                getLog().info("No compression is enabled");
                IOUtil.copy(in, out);
            } else if (".js".equalsIgnoreCase(src.getExtension())) {
                JavaScriptCompressor compressor = new JavaScriptCompressor(in, jsErrorReporter_);
                compressor.compress(out, linebreakpos, !nomunge, jswarn, preserveAllSemiColons, disableOptimizations);
            } else if (".css".equalsIgnoreCase(src.getExtension())) {
                compressCss(in, out);
            }
            getLog().debug("end compression");
        }

        boolean outputIgnored = useSmallestFile && inFile.length() < outFile.length();
        if (outputIgnored) {
            FileUtils.forceDelete(outFileTmp);
            FileUtils.copyFile(inFile, outFile);
            getLog().debug("output greater than input, using original instead");
        } else {
            FileUtils.forceDelete(outFile);
            FileUtils.rename(outFileTmp, outFile);
            buildContext.refresh(outFile);
        }

        if (buildContext.isIncremental()) {
            incrementalFiles.add(outFile.getCanonicalPath());
        }

        File gzipped = gzipIfRequested(outFile);
        if (statistics) {
            inSizeTotal_ += inFile.length();
            outSizeTotal_ += outFile.length();

            String fileStatistics;
            if (outputIgnored) {
                fileStatistics = String.format("%s (%db) -> %s (%db)[compressed output discarded (exceeded input size)]", inFile.getName(), inFile.length(), outFile.getName(), outFile.length());
            } else {
                fileStatistics = String.format("%s (%db) -> %s (%db)[%d%%]", inFile.getName(), inFile.length(), outFile.getName(), outFile.length(), ratioOfSize(inFile, outFile));
            }

            if (gzipped != null) {
                fileStatistics = fileStatistics + String.format(" -> %s (%db)[%d%%]", gzipped.getName(), gzipped.length(), ratioOfSize(inFile, gzipped));
            }
            getLog().info(fileStatistics);
        }
    }

    /**
     * Compress css.
     *
     * @param in the in
     * @param out the out
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void compressCss(InputStreamReader in, OutputStreamWriter out)
            throws IOException {
        try {
            CssCompressor compressor = new CssCompressor(in);
            compressor.compress(out, linebreakpos);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Unexpected characters found in CSS file. Ensure that the CSS file does not contain '$', and try again", e);
        }
    }

    /**
     * Gzip if requested.
     *
     * @param file the file
     * @return the file
     * @throws Exception the exception
     */
    protected File gzipIfRequested(File file) throws Exception {
        if (!gzip || (file == null) || (!file.exists())) {
            return null;
        }
        if (".gz".equalsIgnoreCase(FileUtils.getExtension(file.getName()))) {
            return null;
        }
        File gzipped = new File(file.getCanonicalPath() + ".gz");
        getLog().debug(String.format("create gzip version : %s", gzipped.getName()));
        try (FileInputStream in = new FileInputStream(file);
            GZIPOutputStream out = new GZIPOutputStream(buildContext.newFileOutputStream(gzipped)) {
                {
                    def.setLevel(level);
                }
            };) {
            IOUtil.copy(in, out);
        }
        return gzipped;
    }

    /**
     * Ratio of size.
     *
     * @param file100 the file 100
     * @param fileX the file X
     * @return the long
     * @throws Exception the exception
     */
    protected long ratioOfSize(File file100, File fileX) throws Exception {
        long v100 = Math.max(file100.length(), 1);
        long vX = Math.max(fileX.length(), 1);
        return (vX * 100) / v100;
    }

    /**
     * Checks if is minified file.
     *
     * @param inFile the in file
     * @return true, if is minified file
     */
    private boolean isMinifiedFile(File inFile) {
        String filename = inFile.getName().toLowerCase();
        return filename.endsWith(suffix + ".js") || filename.endsWith(suffix + ".css");
    }

}
