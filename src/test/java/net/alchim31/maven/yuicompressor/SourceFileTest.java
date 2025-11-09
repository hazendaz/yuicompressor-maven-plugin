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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The Class SourceFileTest.
 */
class SourceFileTest {

    /** The src root. */
    private Path srcRoot;

    /** The dest root. */
    private Path destRoot;

    /**
     * Sets the up.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @BeforeEach
    void setUp() throws IOException {
        this.srcRoot = Files.createTempDirectory("srcRoot");
        this.destRoot = Files.createTempDirectory("destRoot");
    }

    /**
     * Tear down.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @AfterEach
    void tearDown() throws IOException {
        try (var srcStream = Files.walk(this.srcRoot)) {
            srcStream.map(Path::toFile).forEach(File::delete);
        }
        try (var destStream = Files.walk(this.destRoot)) {
            destStream.map(Path::toFile).forEach(File::delete);
        }
    }

    /**
     * Test constructor with extension.
     */
    @Test
    void testConstructorWithExtension() {
        final var sf = new SourceFile(this.srcRoot.toFile(), this.destRoot.toFile(), "foo.js", false);
        Assertions.assertEquals(".js", sf.getExtension());
    }

    /**
     * Test constructor without extension.
     */
    @Test
    void testConstructorWithoutExtension() {
        final var sf = new SourceFile(this.srcRoot.toFile(), this.destRoot.toFile(), "foo", false);
        Assertions.assertEquals("", sf.getExtension());
    }

    /**
     * Test to file src root.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testToFileSrcRoot() throws IOException {
        final var filePath = this.srcRoot.resolve("bar.js");
        Files.write(filePath, "test".getBytes(StandardCharsets.UTF_8));
        final var sf = new SourceFile(this.srcRoot.toFile(), this.destRoot.toFile(), "bar.js", false);
        Assertions.assertEquals(filePath.toFile(), sf.toFile());
    }

    /**
     * Test to file dest root when dest as source and file exists.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testToFileDestRootWhenDestAsSourceAndFileExists() throws IOException {
        final var filePath = this.destRoot.resolve("baz.js");
        Files.write(filePath, "test".getBytes(StandardCharsets.UTF_8));
        final var sf = new SourceFile(this.srcRoot.toFile(), this.destRoot.toFile(), "baz.js", true);
        Assertions.assertEquals(filePath.toFile(), sf.toFile());
    }

    /**
     * Test to file dest root when dest as source and file does not exist.
     */
    @Test
    void testToFileDestRootWhenDestAsSourceAndFileDoesNotExist() {
        final var sf = new SourceFile(this.srcRoot.toFile(), this.destRoot.toFile(), "qux.js", true);
        // Should fall back to srcRoot
        final var expected = this.srcRoot.resolve("qux.js").toFile();
        Assertions.assertEquals(expected, sf.toFile());
    }

    /**
     * Test to dest file with suffix.
     */
    @Test
    void testToDestFileWithSuffix() {
        final var sf = new SourceFile(this.srcRoot.toFile(), this.destRoot.toFile(), "foo.js", false);
        final var expected = this.destRoot.resolve("foo-min.js").toFile();
        Assertions.assertEquals(expected, sf.toDestFile("-min"));
    }

    /**
     * Test to dest file with suffix no extension.
     */
    @Test
    void testToDestFileWithSuffixNoExtension() {
        final var sf = new SourceFile(this.srcRoot.toFile(), this.destRoot.toFile(), "foo", false);
        final var expected = this.destRoot.resolve("foo-min").toFile();
        Assertions.assertEquals(expected, sf.toDestFile("-min"));
    }

}
