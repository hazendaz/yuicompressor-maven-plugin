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
import org.mockito.ArgumentMatchers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mozilla.javascript.ErrorReporter;

/**
 * The Class JSLintCheckerTest.
 */
class JSLintCheckerTest {

    /** The temp js file. */
    private File tempJsFile;

    /**
     * Sets the up.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @BeforeEach
    void setUp() throws IOException {
        this.tempJsFile = File.createTempFile("test", ".js");
        this.tempJsFile.deleteOnExit();
        try (var writer = Files.newBufferedWriter(this.tempJsFile.toPath(), StandardCharsets.UTF_8)) {
            writer.write("var a = 1;");
        }
    }

    /**
     * Tear down.
     */
    @AfterEach
    void tearDown() {
        this.tempJsFile.delete();
    }

    /**
     * Test constructor creates temp file.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws NoSuchFieldException
     *             the no such field exception
     * @throws SecurityException
     *             the security exception
     * @throws IllegalArgumentException
     *             the illegal argument exception
     * @throws IllegalAccessException
     *             the illegal access exception
     */
    @Test
    void testConstructorCreatesTempFile() throws IOException, NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException {
        final var checker = new JSLintChecker();
        Assertions.assertNotNull(checker);
        // jslintPath should be set (private, so use reflection)
        final var field = JSLintChecker.class.getDeclaredField("jslintPath");
        field.setAccessible(true);
        final var path = (String) field.get(checker);
        Assertions.assertTrue(Path.of(path).toFile().exists());
    }

    /**
     * Test check calls basic rhino shell exec.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    void testCheckCallsBasicRhinoShellExec() throws Exception {
        final var checker = new JSLintChecker();
        final var reporter = Mockito.mock(ErrorReporter.class);
        try (MockedStatic<BasicRhinoShell> shellMock = Mockito.mockStatic(BasicRhinoShell.class)) {
            checker.check(this.tempJsFile, reporter);
            shellMock.verify(
                    () -> BasicRhinoShell.exec(ArgumentMatchers.any(String[].class), ArgumentMatchers.eq(reporter)));
        }
    }

}
