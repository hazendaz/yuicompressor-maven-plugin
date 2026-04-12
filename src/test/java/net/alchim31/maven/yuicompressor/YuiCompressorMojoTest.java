/*
 * YuiCompressor Maven plugin
 *
 * Copyright 2012-2026 Hazendaz.
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
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;

import org.apache.maven.api.plugin.testing.InjectMojo;
import org.apache.maven.api.plugin.testing.MojoExtension;
import org.apache.maven.api.plugin.testing.MojoTest;
import org.codehaus.plexus.build.DefaultBuildContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonatype.plexus.build.incremental.BuildContext;

/**
 * Tests for {@link YuiCompressorMojo} using the maven-plugin-testing-harness 3.5.1 JUnit 5 extension
 * ({@code @MojoTest}) combined with direct instantiation tests.
 */
// Note: public here needed for javadocs to work so don't remove it
@MojoTest
public class YuiCompressorMojoTest {

    /** Temporary directory for test output files. */
    @TempDir
    File tempDir;

    // ---------------------------------------------------------------- harness-based tests

    /**
     * Verify that the mojo can be looked up via the harness and has the expected configuration values from the test
     * pom.
     *
     * @param mojo
     *            the mojo instance injected by {@link MojoExtension}
     *
     * @throws Exception
     *             if field access fails
     */
    @Test
    @InjectMojo(goal = "compress", pom = "src/test/resources/unit/compress-basic-test/pom.xml")
    void testMojoConfiguredByHarness_hasExpectedConfigValues(YuiCompressorMojo mojo) throws Exception {
        Assertions.assertNotNull(mojo, "Mojo must be injected by the harness");
        // Encoding set to UTF-8 in the test pom
        final String encoding = (String) MojoExtension.getVariableValueFromObject(mojo, "encoding");
        Assertions.assertEquals("UTF-8", encoding, "encoding should be UTF-8 from test pom");
        // statistics disabled in the test pom
        final boolean statistics = (boolean) MojoExtension.getVariableValueFromObject(mojo, "statistics");
        Assertions.assertFalse(statistics, "statistics should be false per test pom");
        // default suffix should be the default "-min"
        final String suffix = (String) MojoExtension.getVariableValueFromObject(mojo, "suffix");
        Assertions.assertEquals("-min", suffix, "suffix should be '-min' (default)");
    }

    // ---------------------------------------------------------------- ratioOfSize

    /**
     * Test ratioOfSize with two files of known sizes.
     *
     * @throws Exception
     *             if test setup fails
     */
    @Test
    void testRatioOfSize_regularFiles() throws Exception {
        final var mojo = new YuiCompressorMojo();
        final File large = new File(tempDir, "large.js");
        final File small = new File(tempDir, "small.js");
        Files.write(large.toPath(), new byte[100]);
        Files.write(small.toPath(), new byte[50]);

        final long ratio = mojo.ratioOfSize(large, small);
        Assertions.assertEquals(50L, ratio, "Expected 50% ratio");
    }

    /**
     * Test ratioOfSize when the "100%" file is empty – Math.max prevents division by zero.
     *
     * @throws Exception
     *             if test setup fails
     */
    @Test
    void testRatioOfSize_emptyBaseFile() throws Exception {
        final var mojo = new YuiCompressorMojo();
        final File empty = new File(tempDir, "empty.js");
        final File small = new File(tempDir, "small.js");
        Files.write(empty.toPath(), new byte[0]);
        Files.write(small.toPath(), new byte[1]);

        // Math.max(0, 1) → base = 1; ratio = 1*100/1 = 100
        final long ratio = mojo.ratioOfSize(empty, small);
        Assertions.assertEquals(100L, ratio);
    }

    /**
     * Test ratioOfSize when both files are the same size.
     *
     * @throws Exception
     *             if test setup fails
     */
    @Test
    void testRatioOfSize_sameSize() throws Exception {
        final var mojo = new YuiCompressorMojo();
        final File f1 = new File(tempDir, "f1.js");
        final File f2 = new File(tempDir, "f2.js");
        Files.write(f1.toPath(), new byte[200]);
        Files.write(f2.toPath(), new byte[200]);

        Assertions.assertEquals(100L, mojo.ratioOfSize(f1, f2));
    }

    // ----------------------------------------------------------- gzipIfRequested

    /**
     * Test that gzipIfRequested returns null when gzip is disabled.
     *
     * @throws Exception
     *             if test setup fails
     */
    @Test
    void testGzipIfRequested_gzipDisabled_returnsNull() throws Exception {
        final var mojo = createMojoWithBuildContext();
        MojoExtension.setVariableValueToObject(mojo, "gzip", false);

        final File file = new File(tempDir, "test.js");
        Files.write(file.toPath(), "var x=1;".getBytes(StandardCharsets.UTF_8));

        Assertions.assertNull(mojo.gzipIfRequested(file), "Expected null when gzip is disabled");
    }

    /**
     * Test that gzipIfRequested returns null when the provided file is null.
     *
     * @throws Exception
     *             if test setup fails
     */
    @Test
    void testGzipIfRequested_nullFile_returnsNull() throws Exception {
        final var mojo = createMojoWithBuildContext();
        MojoExtension.setVariableValueToObject(mojo, "gzip", true);
        MojoExtension.setVariableValueToObject(mojo, "level", 9);

        Assertions.assertNull(mojo.gzipIfRequested(null), "Expected null for null file");
    }

    /**
     * Test that gzipIfRequested returns null when the file does not exist.
     *
     * @throws Exception
     *             if test setup fails
     */
    @Test
    void testGzipIfRequested_fileNotExists_returnsNull() throws Exception {
        final var mojo = createMojoWithBuildContext();
        MojoExtension.setVariableValueToObject(mojo, "gzip", true);
        MojoExtension.setVariableValueToObject(mojo, "level", 9);

        final File missing = new File(tempDir, "nonexistent.js");
        Assertions.assertNull(mojo.gzipIfRequested(missing), "Expected null when file does not exist");
    }

    /**
     * Test that gzipIfRequested returns null when the file is already a .gz file.
     *
     * @throws Exception
     *             if test setup fails
     */
    @Test
    void testGzipIfRequested_alreadyGzipped_returnsNull() throws Exception {
        final var mojo = createMojoWithBuildContext();
        MojoExtension.setVariableValueToObject(mojo, "gzip", true);
        MojoExtension.setVariableValueToObject(mojo, "level", 9);

        final File gzFile = new File(tempDir, "test.js.gz");
        Files.write(gzFile.toPath(), new byte[] { 0x1f, (byte) 0x8b });

        Assertions.assertNull(mojo.gzipIfRequested(gzFile), "Expected null for already-gzipped file");
    }

    /**
     * Test that gzipIfRequested creates a .gz file when gzip is enabled and the source file exists.
     *
     * @throws Exception
     *             if test setup fails
     */
    @Test
    void testGzipIfRequested_validFile_createsGzFile() throws Exception {
        final var mojo = createMojoWithBuildContext();
        MojoExtension.setVariableValueToObject(mojo, "gzip", true);
        MojoExtension.setVariableValueToObject(mojo, "level", 9);

        final File jsFile = new File(tempDir, "test.js");
        Files.write(jsFile.toPath(), "function hello(){}".getBytes(StandardCharsets.UTF_8));

        final File gzFile = mojo.gzipIfRequested(jsFile);

        Assertions.assertNotNull(gzFile, "Expected a gzipped file to be created");
        Assertions.assertTrue(gzFile.exists(), "Gzipped file should exist on disk");
        Assertions.assertTrue(gzFile.getName().endsWith(".gz"), "Gzipped file should have .gz extension");
        Assertions.assertTrue(gzFile.length() > 0, "Gzipped file should not be empty");
    }

    // ------------------------------------------------------ mojo execute via direct instantiation

    /**
     * Test that mojo execution is skipped when the {@code skip} parameter is true.
     *
     * @throws Exception
     *             if setup or execution fails
     */
    @Test
    void testMojoExecute_skipTrue_doesNotProcess() throws Exception {
        final var mojo = createAndConfigureMojo(new File(tempDir, "webapp-skip"), new File(tempDir, "output-skip"));
        MojoExtension.setVariableValueToObject(mojo, "skip", true);

        // Should complete without any processing (and without NullPointerException)
        mojo.execute();
    }

    /**
     * Test that JS files are compressed when the mojo processes a webapp directory containing a JS file.
     *
     * @throws Exception
     *             if test fails
     */
    @Test
    void testMojoExecute_compressesJsFile() throws Exception {
        final File webappDir = new File(tempDir, "webapp");
        webappDir.mkdirs();
        final File jsFile = new File(webappDir, "app.js");
        Files.write(jsFile.toPath(),
                "function greet(name) { var msg = 'Hello ' + name; return msg; }".getBytes(StandardCharsets.UTF_8));

        final File outputDir = new File(tempDir, "output");
        outputDir.mkdirs();

        final var mojo = createAndConfigureMojo(webappDir, outputDir);
        mojo.execute();

        final File compressedJs = new File(outputDir, "app-min.js");
        Assertions.assertTrue(compressedJs.exists(), "Compressed JS file should be created");
        Assertions.assertTrue(compressedJs.length() < jsFile.length(),
                "Compressed file should be smaller than original");
    }

    /**
     * Test that CSS files are compressed when the mojo processes a webapp directory containing a CSS file.
     *
     * @throws Exception
     *             if test fails
     */
    @Test
    void testMojoExecute_compressesCssFile() throws Exception {
        final File webappDir = new File(tempDir, "webapp-css");
        webappDir.mkdirs();
        final File cssFile = new File(webappDir, "style.css");
        Files.write(cssFile.toPath(), "body {\n  background-color: white;\n  color: black;\n  font-size: 14px;\n}\n"
                .getBytes(StandardCharsets.UTF_8));

        final File outputDir = new File(tempDir, "output-css");
        outputDir.mkdirs();

        final var mojo = createAndConfigureMojo(webappDir, outputDir);
        mojo.execute();

        final File compressedCss = new File(outputDir, "style-min.css");
        Assertions.assertTrue(compressedCss.exists(), "Compressed CSS file should be created");
        Assertions.assertTrue(compressedCss.length() < cssFile.length(),
                "Compressed CSS should be smaller than original");
    }

    /**
     * Test that the {@code nosuffix} option writes the compressed output with the original filename.
     *
     * @throws Exception
     *             if test fails
     */
    @Test
    void testMojoExecute_nosuffix_writesOriginalFilename() throws Exception {
        final File webappDir = new File(tempDir, "webapp-nosuffix");
        webappDir.mkdirs();
        final File jsFile = new File(webappDir, "app.js");
        Files.write(jsFile.toPath(), "function f(x) { return x * 2; }".getBytes(StandardCharsets.UTF_8));

        final File outputDir = new File(tempDir, "output-nosuffix");
        outputDir.mkdirs();

        final var mojo = createAndConfigureMojo(webappDir, outputDir);
        MojoExtension.setVariableValueToObject(mojo, "nosuffix", true);

        mojo.execute();

        final File outputFile = new File(outputDir, "app.js");
        Assertions.assertTrue(outputFile.exists(), "Output JS file with original name should be created");
    }

    /**
     * Test that already-minified files (matching the suffix pattern) are skipped.
     *
     * @throws Exception
     *             if test fails
     */
    @Test
    void testMojoExecute_alreadyMinifiedFile_isSkipped() throws Exception {
        final File webappDir = new File(tempDir, "webapp-skipmin");
        webappDir.mkdirs();
        final File minFile = new File(webappDir, "app-min.js");
        Files.write(minFile.toPath(), "function f(x){return x*2;}".getBytes(StandardCharsets.UTF_8));

        final File outputDir = new File(tempDir, "output-skipmin");
        outputDir.mkdirs();

        final var mojo = createAndConfigureMojo(webappDir, outputDir);
        mojo.execute();

        // app-min.js should not be re-processed (no app-min-min.js created)
        final File doubleMin = new File(outputDir, "app-min-min.js");
        Assertions.assertFalse(doubleMin.exists(), "Already-minified file should not be double-compressed");
    }

    /**
     * Test that statistics logging does not throw an exception when files are processed.
     *
     * @throws Exception
     *             if test fails
     */
    @Test
    void testMojoExecute_statisticsEnabled_doesNotThrow() throws Exception {
        final File webappDir = new File(tempDir, "webapp-stats");
        webappDir.mkdirs();
        final File jsFile = new File(webappDir, "app.js");
        Files.write(jsFile.toPath(), "var x = 1;".getBytes(StandardCharsets.UTF_8));

        final File outputDir = new File(tempDir, "output-stats");
        outputDir.mkdirs();

        final var mojo = createAndConfigureMojo(webappDir, outputDir);
        MojoExtension.setVariableValueToObject(mojo, "statistics", true);

        // Should not throw
        mojo.execute();
    }

    // ----------------------------------------------------------- helper methods

    /**
     * Creates a YuiCompressorMojo configured with a mock build context, source and output directories.
     *
     * @param warSourceDirectory
     *            the webapp source directory
     * @param webappDirectory
     *            the webapp output directory
     *
     * @return configured mojo instance
     *
     * @throws Exception
     *             if field injection fails
     */
    private YuiCompressorMojo createAndConfigureMojo(File warSourceDirectory, File webappDirectory) throws Exception {
        final var mojo = new YuiCompressorMojo();
        final var buildContext = buildDefaultBuildContext();
        MojoExtension.setVariableValueToObject(mojo, "buildContext", buildContext);
        MojoExtension.setVariableValueToObject(mojo, "encoding", "UTF-8");
        MojoExtension.setVariableValueToObject(mojo, "suffix", "-min");
        MojoExtension.setVariableValueToObject(mojo, "nosuffix", false);
        MojoExtension.setVariableValueToObject(mojo, "linebreakpos", -1);
        MojoExtension.setVariableValueToObject(mojo, "nocompress", false);
        MojoExtension.setVariableValueToObject(mojo, "nomunge", false);
        MojoExtension.setVariableValueToObject(mojo, "preserveAllSemiColons", false);
        MojoExtension.setVariableValueToObject(mojo, "disableOptimizations", false);
        MojoExtension.setVariableValueToObject(mojo, "force", false);
        MojoExtension.setVariableValueToObject(mojo, "gzip", false);
        MojoExtension.setVariableValueToObject(mojo, "level", 9);
        MojoExtension.setVariableValueToObject(mojo, "statistics", false);
        MojoExtension.setVariableValueToObject(mojo, "preProcessAggregates", false);
        MojoExtension.setVariableValueToObject(mojo, "useSmallestFile", false);
        MojoExtension.setVariableValueToObject(mojo, "skip", false);
        MojoExtension.setVariableValueToObject(mojo, "jswarn", false);
        MojoExtension.setVariableValueToObject(mojo, "failOnWarning", false);
        MojoExtension.setVariableValueToObject(mojo, "excludeResources", true);
        MojoExtension.setVariableValueToObject(mojo, "excludeWarSourceDirectory", false);
        MojoExtension.setVariableValueToObject(mojo, "warSourceDirectory", warSourceDirectory);
        MojoExtension.setVariableValueToObject(mojo, "webappDirectory", webappDirectory);
        MojoExtension.setVariableValueToObject(mojo, "outputDirectory", new File(tempDir, "classes"));
        MojoExtension.setVariableValueToObject(mojo, "sourceDirectory", new File(tempDir, "nonexistent-source"));
        MojoExtension.setVariableValueToObject(mojo, "resources", Collections.emptyList());
        return mojo;
    }

    /**
     * Creates a bare YuiCompressorMojo with only the build context injected.
     *
     * @return mojo with build context
     *
     * @throws Exception
     *             if field injection fails
     */
    private YuiCompressorMojo createMojoWithBuildContext() throws Exception {
        final var mojo = new YuiCompressorMojo();
        MojoExtension.setVariableValueToObject(mojo, "buildContext", buildDefaultBuildContext());
        return mojo;
    }

    /**
     * Builds a {@link DefaultBuildContext} backed by a lenient mock of the sonatype legacy {@link BuildContext}.
     *
     * @return a ready-to-use DefaultBuildContext
     *
     * @throws Exception
     *             if mocking fails
     */
    private DefaultBuildContext buildDefaultBuildContext() throws Exception {
        final BuildContext legacyCtx = Mockito.mock(BuildContext.class);
        Mockito.lenient().when(legacyCtx.newFileOutputStream(ArgumentMatchers.any(File.class)))
                .thenAnswer(inv -> Files.newOutputStream(((File) inv.getArgument(0)).toPath()));
        return new DefaultBuildContext(legacyCtx);
    }

    /**
     * Reads a private or protected field value by walking the class hierarchy.
     *
     * @param target
     *            the object to inspect
     * @param fieldName
     *            the field name
     *
     * @return the field value
     */
    @SuppressWarnings("unused")
    private static Object getField(Object target, String fieldName) {
        try {
            Class<?> clazz = target.getClass();
            Field field = null;
            while (clazz != null) {
                try {
                    field = clazz.getDeclaredField(fieldName);
                    break;
                } catch (NoSuchFieldException e) {
                    clazz = clazz.getSuperclass();
                }
            }
            if (field == null) {
                throw new NoSuchFieldException(fieldName);
            }
            field.setAccessible(true);
            return field.get(target);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Cannot get field '" + fieldName + "'", e);
        }
    }
}
