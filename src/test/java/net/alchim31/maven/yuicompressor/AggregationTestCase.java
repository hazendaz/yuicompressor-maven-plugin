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
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.codehaus.plexus.build.DefaultBuildContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sonatype.plexus.build.incremental.BuildContext;

/**
 * The Class AggregationTestCase.
 */
// Note: public here needed for javadocs to work so don't remove it
@ExtendWith(MockitoExtension.class)
public class AggregationTestCase {

    /** The dir. */
    @TempDir
    File dir;

    /** The legacy build context. */
    @Mock
    BuildContext legacyBuildContext;

    /** The default build context. */
    DefaultBuildContext defaultBuildContext;

    /**
     * Sets the up.
     *
     * @throws IOException
     *             the io exception
     */
    @BeforeEach
    void setUp() throws IOException {
        // Ensure the mock returns a real OutputStream for output files
        Mockito.lenient().when(this.legacyBuildContext.newFileOutputStream(ArgumentMatchers.any(File.class)))
                .thenAnswer(invocation -> Files.newOutputStream(((File) invocation.getArgument(0)).toPath()));
        this.defaultBuildContext = new DefaultBuildContext(this.legacyBuildContext);
    }

    /**
     * Test 0 to 1.
     *
     * @throws IOException
     *             the IO exception
     */
    @Test
    void test0to1() throws IOException {
        final var target = new Aggregation();
        target.setOutput(this.dir.toPath().resolve("output.js").toFile());

        Assertions.assertFalse(target.getOutput().exists());
        target.run(null, this.defaultBuildContext);
        Assertions.assertFalse(target.getOutput().exists());

        target.setIncludes(new String[] {});
        Assertions.assertFalse(target.getOutput().exists());
        target.run(null, this.defaultBuildContext);
        Assertions.assertFalse(target.getOutput().exists());

        target.setIncludes(new String[] { "**/*.js" });
        Assertions.assertFalse(target.getOutput().exists());
        target.run(null, this.defaultBuildContext);
        Assertions.assertFalse(target.getOutput().exists());
    }

    /**
     * Test 1 to 1.
     *
     * @throws IOException
     *             the IO exception
     */
    @Test
    void test1to1() throws IOException {
        final var f1 = this.dir.toPath().resolve("01.js").toFile();
        Files.write(f1.toPath(), "1".getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
        final var target = new Aggregation();
        target.setOutput(this.dir.toPath().resolve("output.js").toFile());
        target.setIncludes(new String[] { f1.getName() });

        Assertions.assertFalse(target.getOutput().exists());
        target.run(null, this.defaultBuildContext);
        Assertions.assertTrue(target.getOutput().exists());
        Assertions.assertEquals(new String(Files.readAllBytes(f1.toPath()), StandardCharsets.UTF_8),
                new String(Files.readAllBytes(target.getOutput().toPath()), StandardCharsets.UTF_8));
    }

    /**
     * Test 2 to 1.
     *
     * @throws IOException
     *             the IO exception
     */
    @Test
    void test2to1() throws IOException {
        final var f1 = this.dir.toPath().resolve("01.js").toFile();
        Files.write(f1.toPath(), "1".getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);

        final var f2 = this.dir.toPath().resolve("02.js").toFile();
        Files.write(f2.toPath(), "22\n22".getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);

        final var target = new Aggregation();
        target.setOutput(this.dir.toPath().resolve("output.js").toFile());

        target.setIncludes(new String[] { f1.getName(), f2.getName() });
        Assertions.assertFalse(target.getOutput().exists());
        target.run(null, this.defaultBuildContext);
        Assertions.assertTrue(target.getOutput().exists());
        Assertions.assertEquals(
                new String(Files.readAllBytes(f1.toPath()), StandardCharsets.UTF_8)
                        + new String(Files.readAllBytes(f2.toPath()), StandardCharsets.UTF_8),
                new String(Files.readAllBytes(target.getOutput().toPath()), StandardCharsets.UTF_8));

        target.getOutput().delete();
        target.setIncludes(new String[] { "*.js" });
        Assertions.assertFalse(target.getOutput().exists());
        target.run(null, this.defaultBuildContext);
        Assertions.assertTrue(target.getOutput().exists());
        Assertions.assertEquals(
                new String(Files.readAllBytes(f1.toPath()), StandardCharsets.UTF_8)
                        + new String(Files.readAllBytes(f2.toPath()), StandardCharsets.UTF_8),
                new String(Files.readAllBytes(target.getOutput().toPath()), StandardCharsets.UTF_8));
    }

    /**
     * Test no duplicate aggregation.
     *
     * @throws IOException
     *             the IO exception
     */
    @Test
    void testNoDuplicateAggregation() throws IOException {
        final var f1 = this.dir.toPath().resolve("01.js").toFile();
        Files.write(f1.toPath(), "1".getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);

        final var f2 = this.dir.toPath().resolve("02.js").toFile();
        Files.write(f2.toPath(), "22\n22".getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);

        final var target = new Aggregation();
        target.setOutput(this.dir.toPath().resolve("output.js").toFile());

        target.setIncludes(new String[] { f1.getName(), f1.getName(), f2.getName() });
        Assertions.assertFalse(target.getOutput().exists());
        target.run(null, this.defaultBuildContext);
        Assertions.assertTrue(target.getOutput().exists());
        Assertions.assertEquals(
                new String(Files.readAllBytes(f1.toPath()), StandardCharsets.UTF_8)
                        + new String(Files.readAllBytes(f2.toPath()), StandardCharsets.UTF_8),
                new String(Files.readAllBytes(target.getOutput().toPath()), StandardCharsets.UTF_8));

        target.getOutput().delete();
        target.setIncludes(new String[] { f1.getName(), "*.js" });
        Assertions.assertFalse(target.getOutput().exists());
        target.run(null, this.defaultBuildContext);
        Assertions.assertTrue(target.getOutput().exists());
        Assertions.assertEquals(
                new String(Files.readAllBytes(f1.toPath()), StandardCharsets.UTF_8)
                        + new String(Files.readAllBytes(f2.toPath()), StandardCharsets.UTF_8),
                new String(Files.readAllBytes(target.getOutput().toPath()), StandardCharsets.UTF_8));
    }

    /**
     * Test 2 to 1 order.
     *
     * @throws IOException
     *             the IO exception
     */
    @Test
    void test2to1Order() throws IOException {
        final var f1 = this.dir.toPath().resolve("01.js").toFile();
        Files.write(f1.toPath(), "1".getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);

        final var f2 = this.dir.toPath().resolve("02.js").toFile();
        Files.write(f2.toPath(), "2".getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);

        final var target = new Aggregation();
        target.setOutput(this.dir.toPath().resolve("output.js").toFile());

        target.setIncludes(new String[] { f2.getName(), f1.getName() });
        Assertions.assertFalse(target.getOutput().exists());
        target.run(null, this.defaultBuildContext);
        Assertions.assertTrue(target.getOutput().exists());
        Assertions.assertEquals(
                new String(Files.readAllBytes(f2.toPath()), StandardCharsets.UTF_8)
                        + new String(Files.readAllBytes(f1.toPath()), StandardCharsets.UTF_8),
                new String(Files.readAllBytes(target.getOutput().toPath()), StandardCharsets.UTF_8));
    }

    /**
     * Test 2 to 1 with new line.
     *
     * @throws IOException
     *             the IO exception
     */
    @Test
    void test2to1WithNewLine() throws IOException {
        final var f1 = this.dir.toPath().resolve("01.js").toFile();
        Files.write(f1.toPath(), "1".getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);

        final var f2 = this.dir.toPath().resolve("02.js").toFile();
        Files.write(f2.toPath(), "22\n22".getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);

        final var target = new Aggregation();
        target.setOutput(this.dir.toPath().resolve("output.js").toFile());
        target.setInsertNewLine(true);
        target.setIncludes(new String[] { f1.getName(), f2.getName() });

        Assertions.assertFalse(target.getOutput().exists());
        target.run(null, this.defaultBuildContext);
        Assertions.assertTrue(target.getOutput().exists());
        Assertions.assertEquals(
                new String(Files.readAllBytes(f1.toPath()), StandardCharsets.UTF_8) + "\n"
                        + new String(Files.readAllBytes(f2.toPath()), StandardCharsets.UTF_8) + "\n",
                new String(Files.readAllBytes(target.getOutput().toPath()), StandardCharsets.UTF_8));
    }

    /**
     * Test absolute path from inside.
     *
     * @throws IOException
     *             the IO exception
     */
    @Test
    void testAbsolutePathFromInside() throws IOException {
        final var f1 = this.dir.toPath().resolve("01.js").toFile();
        Files.write(f1.toPath(), "1".getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);

        final var f2 = this.dir.toPath().resolve("02.js").toFile();
        Files.write(f2.toPath(), "22\n22".getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);

        final var target = new Aggregation();
        target.setOutput(this.dir.toPath().resolve("output.js").toFile());

        target.setIncludes(new String[] { f1.getAbsolutePath(), f2.getName() });
        Assertions.assertFalse(target.getOutput().exists());
        target.run(null, this.defaultBuildContext);
        Assertions.assertTrue(target.getOutput().exists());
        Assertions.assertEquals(
                new String(Files.readAllBytes(f1.toPath()), StandardCharsets.UTF_8)
                        + new String(Files.readAllBytes(f2.toPath()), StandardCharsets.UTF_8),
                new String(Files.readAllBytes(target.getOutput().toPath()), StandardCharsets.UTF_8));
    }

    /**
     * Test absolute path from outside.
     *
     * @throws IOException
     *             the IO exception
     */
    @Test
    void testAbsolutePathFromOutside() throws IOException {
        final var f1 = File.createTempFile("test-01", ".js");
        Files.write(f1.toPath(), "1".getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);

        final var f2 = this.dir.toPath().resolve("02.js").toFile();
        Files.write(f2.toPath(), "22\n22".getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);

        final var target = new Aggregation();
        target.setOutput(this.dir.toPath().resolve("output.js").toFile());

        try {
            target.setIncludes(new String[] { f1.getAbsolutePath(), f2.getName() });
            Assertions.assertFalse(target.getOutput().exists());
            target.run(null, this.defaultBuildContext);
            Assertions.assertTrue(target.getOutput().exists());
            Assertions.assertEquals(
                    new String(Files.readAllBytes(f1.toPath()), StandardCharsets.UTF_8)
                            + new String(Files.readAllBytes(f2.toPath()), StandardCharsets.UTF_8),
                    new String(Files.readAllBytes(target.getOutput().toPath()), StandardCharsets.UTF_8));
        } finally {
            f1.delete();
        }
    }

    /**
     * Test auto exclude wildcards.
     *
     * @throws IOException
     *             the IO exception
     */
    @Test
    void testAutoExcludeWildcards() throws IOException {
        final var f1 = this.dir.toPath().resolve("01.js").toFile();
        Files.write(f1.toPath(), "1".getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);

        final var f2 = this.dir.toPath().resolve("02.js").toFile();
        Files.write(f2.toPath(), "22\n22".getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);

        final var target = new Aggregation();
        target.setAutoExcludeWildcards(true);
        target.setOutput(this.dir.toPath().resolve("output.js").toFile());

        final Collection<File> previouslyIncluded = new HashSet<>();
        previouslyIncluded.add(f1.getCanonicalFile());

        target.setIncludes(new String[] { f1.getName(), f2.getName() });
        Assertions.assertFalse(target.getOutput().exists());
        // First call uses path that does not deal with previouslyIncluded so both files are added
        final var content = target.run(previouslyIncluded, this.defaultBuildContext);
        Assertions.assertEquals(2, content.size());
        Assertions.assertTrue(target.getOutput().exists());
        Assertions.assertEquals(
                new String(Files.readAllBytes(f1.toPath()), StandardCharsets.UTF_8)
                        + new String(Files.readAllBytes(f2.toPath()), StandardCharsets.UTF_8),
                new String(Files.readAllBytes(target.getOutput().toPath()), StandardCharsets.UTF_8));

        target.getOutput().delete();
        target.setIncludes(new String[] { "*.js" });
        Assertions.assertFalse(target.getOutput().exists());
        // f1 was in previouslyIncluded so it is not included
        Assertions.assertEquals(target.run(previouslyIncluded, this.defaultBuildContext),
                Arrays.asList(f2.getCanonicalFile()));
        Assertions.assertTrue(target.getOutput().exists());
        Assertions.assertEquals(new String(Files.readAllBytes(f2.toPath()), StandardCharsets.UTF_8),
                new String(Files.readAllBytes(target.getOutput().toPath()), StandardCharsets.UTF_8));
    }

    /**
     * Test that an insert file header is prepended to each included file's content.
     *
     * @throws IOException
     *             the IO exception
     */
    @Test
    void testInsertFileHeader() throws IOException {
        final var f1 = this.dir.toPath().resolve("01.js").toFile();
        Files.write(f1.toPath(), "content1".getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);

        final var target = new Aggregation();
        target.setOutput(this.dir.toPath().resolve("output.js").toFile());
        target.setIncludes(new String[] { f1.getName() });
        setField(target, "insertFileHeader", true);

        target.run(null, this.defaultBuildContext);

        final var result = new String(Files.readAllBytes(target.getOutput().toPath()), StandardCharsets.UTF_8);
        Assertions.assertTrue(result.startsWith("/*01.js*/"), "Expected file header '/*01.js*/' but was: " + result);
        Assertions.assertTrue(result.contains("content1"), "Expected file content after header");
    }

    /**
     * Test that file header is followed by a newline when insertNewLine is also enabled.
     *
     * @throws IOException
     *             the IO exception
     */
    @Test
    void testInsertFileHeaderWithNewLine() throws IOException {
        final var f1 = this.dir.toPath().resolve("myfile.js").toFile();
        Files.write(f1.toPath(), "abc".getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);

        final var target = new Aggregation();
        target.setOutput(this.dir.toPath().resolve("output.js").toFile());
        target.setIncludes(new String[] { f1.getName() });
        target.setInsertNewLine(true);
        setField(target, "insertFileHeader", true);

        target.run(null, this.defaultBuildContext);

        final var result = new String(Files.readAllBytes(target.getOutput().toPath()), StandardCharsets.UTF_8);
        // Header should be "/*myfile.js*/\n" and then content, then "\n"
        Assertions.assertTrue(result.startsWith("/*myfile.js*/\n"),
                "Expected header with trailing newline, but was: " + result);
    }

    /**
     * Test that fixLastSemicolon appends a semicolon after each file's content.
     *
     * @throws IOException
     *             the IO exception
     */
    @Test
    void testFixLastSemicolon() throws IOException {
        final var f1 = this.dir.toPath().resolve("01.js").toFile();
        Files.write(f1.toPath(), "var a=1".getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);

        final var f2 = this.dir.toPath().resolve("02.js").toFile();
        Files.write(f2.toPath(), "var b=2".getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);

        final var target = new Aggregation();
        target.setOutput(this.dir.toPath().resolve("output.js").toFile());
        target.setIncludes(new String[] { f1.getName(), f2.getName() });
        setField(target, "fixLastSemicolon", true);

        target.run(null, this.defaultBuildContext);

        final var result = new String(Files.readAllBytes(target.getOutput().toPath()), StandardCharsets.UTF_8);
        // Each file's content gets a semicolon appended
        Assertions.assertEquals("var a=1;var b=2;", result);
    }

    /**
     * Test that removeIncluded deletes each source file after aggregation.
     *
     * @throws IOException
     *             the IO exception
     */
    @Test
    void testRemoveIncluded() throws IOException {
        final var f1 = this.dir.toPath().resolve("01.js").toFile();
        Files.write(f1.toPath(), "1".getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);

        final var f2 = this.dir.toPath().resolve("02.js").toFile();
        Files.write(f2.toPath(), "2".getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);

        final var target = new Aggregation();
        target.setOutput(this.dir.toPath().resolve("output.js").toFile());
        target.setIncludes(new String[] { f1.getName(), f2.getName() });
        setField(target, "removeIncluded", true);

        target.run(null, this.defaultBuildContext);

        Assertions.assertFalse(f1.exists(), "f1 should be removed after aggregation");
        Assertions.assertFalse(f2.exists(), "f2 should be removed after aggregation");
        Assertions.assertTrue(target.getOutput().exists(), "Output file should still exist");
    }

    /**
     * Test that defineInputDir throws IllegalStateException when the output parent is not a directory.
     *
     * @throws IOException
     *             the IO exception
     */
    @Test
    void testInvalidInputDirectory_throwsIllegalStateException() throws IOException {
        final var notADir = File.createTempFile("notadir", ".tmp");
        notADir.deleteOnExit();

        final var target = new Aggregation();
        // Set inputDir to a plain file (not a directory) via reflection
        setField(target, "inputDir", notADir);
        target.setOutput(this.dir.toPath().resolve("output.js").toFile());
        target.setIncludes(new String[] { "*.js" });

        Assertions.assertThrows(IllegalStateException.class, () -> target.run(null, this.defaultBuildContext));
    }

    /**
     * Test incremental build: when isIncremental returns true and the aggregated file is among the changed files, the
     * aggregation is performed.
     *
     * @throws IOException
     *             the IO exception
     */
    @Test
    void testIncrementalBuildWithDelta() throws IOException {
        final var f1 = this.dir.toPath().resolve("01.js").toFile();
        Files.write(f1.toPath(), "1".getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);

        // Configure the legacy context to report incremental build
        Mockito.when(this.legacyBuildContext.isIncremental()).thenReturn(true);

        final var target = new Aggregation();
        target.setOutput(this.dir.toPath().resolve("output.js").toFile());
        target.setIncludes(new String[] { f1.getName() });

        final Set<String> incrementalFiles = new HashSet<>();
        incrementalFiles.add(f1.getCanonicalPath());

        final var result = target.run(null, this.defaultBuildContext, incrementalFiles);
        Assertions.assertEquals(1, result.size(), "Expected one file to be aggregated");
        Assertions.assertTrue(target.getOutput().exists(), "Output should exist after aggregation with delta");
    }

    /**
     * Test incremental build: when isIncremental returns true but none of the included files changed, the aggregation
     * is skipped.
     *
     * @throws IOException
     *             the IO exception
     */
    @Test
    void testIncrementalBuildNoDelta() throws IOException {
        final var f1 = this.dir.toPath().resolve("01.js").toFile();
        Files.write(f1.toPath(), "1".getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);

        // Configure the legacy context to report incremental build
        Mockito.when(this.legacyBuildContext.isIncremental()).thenReturn(true);

        final var target = new Aggregation();
        target.setOutput(this.dir.toPath().resolve("output.js").toFile());
        target.setIncludes(new String[] { f1.getName() });

        // No files in the incremental delta set
        final Set<String> incrementalFiles = new HashSet<>();

        final var result = target.run(null, this.defaultBuildContext, incrementalFiles);
        Assertions.assertTrue(result.isEmpty(), "Expected no files to be aggregated when no delta");
        Assertions.assertFalse(target.getOutput().exists(), "Output should not be created when no delta");
    }

    /**
     * Test excludes: files matching the exclude pattern should not be included in the aggregation.
     *
     * @throws IOException
     *             the IO exception
     */
    @Test
    void testExcludes_wildcardIncludeWithExclude() throws IOException {
        final var f1 = this.dir.toPath().resolve("include.js").toFile();
        Files.write(f1.toPath(), "include".getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);

        final var f2 = this.dir.toPath().resolve("exclude.js").toFile();
        Files.write(f2.toPath(), "exclude".getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);

        final var target = new Aggregation();
        target.setOutput(this.dir.toPath().resolve("output.js").toFile());
        target.setIncludes(new String[] { "*.js" });
        setField(target, "excludes", new String[] { "exclude.js" });

        target.run(null, this.defaultBuildContext);

        Assertions.assertTrue(target.getOutput().exists());
        final var result = new String(Files.readAllBytes(target.getOutput().toPath()), StandardCharsets.UTF_8);
        Assertions.assertEquals("include", result, "Excluded file content should not appear in output");
    }

    /**
     * Helper method to set a private/protected field value via reflection.
     *
     * @param target
     *            the object to modify
     * @param fieldName
     *            the name of the field
     * @param value
     *            the value to set
     */
    private static void setField(Object target, String fieldName, Object value) {
        try {
            final Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Cannot set field '" + fieldName + "'", e);
        }
    }
}
