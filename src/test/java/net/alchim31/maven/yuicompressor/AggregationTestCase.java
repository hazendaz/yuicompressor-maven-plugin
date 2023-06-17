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
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.codehaus.plexus.build.DefaultBuildContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * The Class AggregationTestCase.
 */
// Note: public here needed for javadocs to work so don't remove it
public class AggregationTestCase {

    /** The dir. */
    @TempDir
    private File dir;

    /** The default build context. */
    private DefaultBuildContext defaultBuildContext = new DefaultBuildContext();

    /**
     * Test 0 to 1.
     *
     * @throws IOException
     *             the IO exception
     */
    @Test
    void test0to1() throws IOException {
        Aggregation target = new Aggregation();
        target.setOutput(new File(dir, "output.js"));

        Assertions.assertFalse(target.getOutput().exists());
        target.run(null, defaultBuildContext);
        Assertions.assertFalse(target.getOutput().exists());

        target.setIncludes(new String[] {});
        Assertions.assertFalse(target.getOutput().exists());
        target.run(null, defaultBuildContext);
        Assertions.assertFalse(target.getOutput().exists());

        target.setIncludes(new String[] { "**/*.js" });
        Assertions.assertFalse(target.getOutput().exists());
        target.run(null, defaultBuildContext);
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
        File f1 = new File(dir, "01.js");
        Files.write(f1.toPath(), "1".getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
        Aggregation target = new Aggregation();
        target.setOutput(new File(dir, "output.js"));
        target.setIncludes(new String[] { f1.getName() });

        Assertions.assertFalse(target.getOutput().exists());
        target.run(null, defaultBuildContext);
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
        File f1 = new File(dir, "01.js");
        Files.write(f1.toPath(), "1".getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);

        File f2 = new File(dir, "02.js");
        Files.write(f2.toPath(), "22\n22".getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);

        Aggregation target = new Aggregation();
        target.setOutput(new File(dir, "output.js"));

        target.setIncludes(new String[] { f1.getName(), f2.getName() });
        Assertions.assertFalse(target.getOutput().exists());
        target.run(null, defaultBuildContext);
        Assertions.assertTrue(target.getOutput().exists());
        Assertions.assertEquals(
                new String(Files.readAllBytes(f1.toPath()), StandardCharsets.UTF_8)
                        + new String(Files.readAllBytes(f2.toPath()), StandardCharsets.UTF_8),
                new String(Files.readAllBytes(target.getOutput().toPath()), StandardCharsets.UTF_8));

        target.getOutput().delete();
        target.setIncludes(new String[] { "*.js" });
        Assertions.assertFalse(target.getOutput().exists());
        target.run(null, defaultBuildContext);
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
        File f1 = new File(dir, "01.js");
        Files.write(f1.toPath(), "1".getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);

        File f2 = new File(dir, "02.js");
        Files.write(f2.toPath(), "22\n22".getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);

        Aggregation target = new Aggregation();
        target.setOutput(new File(dir, "output.js"));

        target.setIncludes(new String[] { f1.getName(), f1.getName(), f2.getName() });
        Assertions.assertFalse(target.getOutput().exists());
        target.run(null, defaultBuildContext);
        Assertions.assertTrue(target.getOutput().exists());
        Assertions.assertEquals(
                new String(Files.readAllBytes(f1.toPath()), StandardCharsets.UTF_8)
                        + new String(Files.readAllBytes(f2.toPath()), StandardCharsets.UTF_8),
                new String(Files.readAllBytes(target.getOutput().toPath()), StandardCharsets.UTF_8));

        target.getOutput().delete();
        target.setIncludes(new String[] { f1.getName(), "*.js" });
        Assertions.assertFalse(target.getOutput().exists());
        target.run(null, defaultBuildContext);
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
        File f1 = new File(dir, "01.js");
        Files.write(f1.toPath(), "1".getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);

        File f2 = new File(dir, "02.js");
        Files.write(f2.toPath(), "2".getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);

        Aggregation target = new Aggregation();
        target.setOutput(new File(dir, "output.js"));

        target.setIncludes(new String[] { f2.getName(), f1.getName() });
        Assertions.assertFalse(target.getOutput().exists());
        target.run(null, defaultBuildContext);
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
        File f1 = new File(dir, "01.js");
        Files.write(f1.toPath(), "1".getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);

        File f2 = new File(dir, "02.js");
        Files.write(f2.toPath(), "22\n22".getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);

        Aggregation target = new Aggregation();
        target.setOutput(new File(dir, "output.js"));
        target.setInsertNewLine(true);
        target.setIncludes(new String[] { f1.getName(), f2.getName() });

        Assertions.assertFalse(target.getOutput().exists());
        target.run(null, defaultBuildContext);
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
        File f1 = new File(dir, "01.js");
        Files.write(f1.toPath(), "1".getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);

        File f2 = new File(dir, "02.js");
        Files.write(f2.toPath(), "22\n22".getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);

        Aggregation target = new Aggregation();
        target.setOutput(new File(dir, "output.js"));

        target.setIncludes(new String[] { f1.getCanonicalPath(), f2.getName() });
        Assertions.assertFalse(target.getOutput().exists());
        target.run(null, defaultBuildContext);
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
        File f1 = File.createTempFile("test-01", ".js");
        Files.write(f1.toPath(), "1".getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);

        File f2 = new File(dir, "02.js");
        Files.write(f2.toPath(), "22\n22".getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);

        Aggregation target = new Aggregation();
        target.setOutput(new File(dir, "output.js"));

        try {
            target.setIncludes(new String[] { f1.getCanonicalPath(), f2.getName() });
            Assertions.assertFalse(target.getOutput().exists());
            target.run(null, defaultBuildContext);
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
        File f1 = new File(dir, "01.js");
        Files.write(f1.toPath(), "1".getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);

        File f2 = new File(dir, "02.js");
        Files.write(f2.toPath(), "22\n22".getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);

        Aggregation target = new Aggregation();
        target.setAutoExcludeWildcards(true);
        target.setOutput(new File(dir, "output.js"));

        Collection<File> previouslyIncluded = new HashSet<>();
        previouslyIncluded.add(f1);

        target.setIncludes(new String[] { f1.getName(), f2.getName() });
        Assertions.assertFalse(target.getOutput().exists());
        target.run(previouslyIncluded, defaultBuildContext);
        Assertions.assertTrue(target.getOutput().exists());
        Assertions.assertEquals(
                new String(Files.readAllBytes(f1.toPath()), StandardCharsets.UTF_8)
                        + new String(Files.readAllBytes(f2.toPath()), StandardCharsets.UTF_8),
                new String(Files.readAllBytes(target.getOutput().toPath()), StandardCharsets.UTF_8));

        target.getOutput().delete();
        target.setIncludes(new String[] { "*.js" });
        Assertions.assertFalse(target.getOutput().exists());
        // f1 was in previouslyIncluded so it is not included
        System.out.println("f1 is located at: " + f1.getPath());
        System.out.println("f2 is located at: " + f2.getPath());
        Assertions.assertEquals(target.run(previouslyIncluded, defaultBuildContext), Arrays.asList(f2));
        Assertions.assertTrue(target.getOutput().exists());
        Assertions.assertEquals(new String(Files.readAllBytes(f2.toPath()), StandardCharsets.UTF_8),
                new String(Files.readAllBytes(target.getOutput().toPath()), StandardCharsets.UTF_8));
    }
}
