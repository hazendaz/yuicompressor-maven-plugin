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

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

/**
 * The Class BasicRhinoShellTest.
 */
class BasicRhinoShellTest {

    /** The temp file. */
    private Path tempFile;

    /**
     * Sets the up.
     *
     * @throws Exception
     *             the exception
     */
    @BeforeEach
    void setUp() throws Exception {
        this.tempFile = Files.createTempFile("rhino-test", ".js");
    }

    /**
     * Tear down.
     *
     * @throws Exception
     *             the exception
     */
    @AfterEach
    void tearDown() throws Exception {
        Files.deleteIfExists(this.tempFile);
    }

    /**
     * Test read file returns content.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    void testReadFileReturnsContent() throws Exception {
        final var content = "var a = 1;";
        try (var writer = Files.newBufferedWriter(this.tempFile, StandardCharsets.UTF_8)) {
            writer.write(content);
        }
        final var shell = new BasicRhinoShell();
        final var result = shell.readFile(this.tempFile.toString());
        Assertions.assertEquals(content, result.trim());
    }

    /**
     * Test read file throws runtime exception.
     */
    @Test
    void testReadFileThrowsRuntimeException() {
        final var shell = new BasicRhinoShell();
        Assertions.assertThrows(RuntimeException.class, () -> shell.readFile("nonexistent.js"));
    }

    /**
     * Test process options version.
     */
    @Test
    void testProcessOptionsVersion() {
        final var cx = Context.enter();
        final String[] args = { "-version", "170", "script.js" };
        final var result = BasicRhinoShell.processOptions(cx, args);
        Assertions.assertEquals("script.js", result[0]);
        Assertions.assertEquals(170, cx.getLanguageVersion());
        Context.exit();
    }

    /**
     * Test help does not throw.
     */
    @Test
    void testHelpDoesNotThrow() {
        final var shell = new BasicRhinoShell();
        shell.help(); // Should not throw
    }

    /**
     * Test quit sets flag.
     *
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
    void testQuitSetsFlag()
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        final var shell = new BasicRhinoShell();
        shell.quit();
        // Reflection to check private field
        final var field = BasicRhinoShell.class.getDeclaredField("quitting");
        field.setAccessible(true);
        Assertions.assertTrue((Boolean) field.get(shell));
    }

    /**
     * Test print logs.
     */
    @Test
    void testPrintLogs() {
        final var cx = Context.enter();
        final Scriptable scope = cx.initStandardObjects();
        final Object[] args = { "Hello", "World" };
        final var funObj = Mockito.mock(Function.class);
        // Should not throw
        BasicRhinoShell.print(cx, scope, args, funObj);
        Context.exit();
    }

    /**
     * Test warn does not throw.
     */
    @Test
    void testWarnDoesNotThrow() {
        final var cx = Context.enter();
        final Scriptable scope = cx.initStandardObjects();
        final Object[] args = { "Warning", 1, "source.js", 2 };
        final var funObj = Mockito.mock(Function.class);
        BasicRhinoShell.warn(cx, scope, args, funObj);
        Context.exit();
    }

    /**
     * Test exec() with a valid script file executes without throwing.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    void testExec_withValidScriptFile_doesNotThrow() throws Exception {
        final var content = "var result = 1 + 1;";
        try (var writer = Files.newBufferedWriter(this.tempFile, StandardCharsets.UTF_8)) {
            writer.write(content);
        }
        final var reporter = Mockito.mock(org.mozilla.javascript.ErrorReporter.class);
        // Should complete without throwing
        Assertions.assertDoesNotThrow(() -> BasicRhinoShell.exec(new String[] { this.tempFile.toString() }, reporter),
                "exec() with a valid script file should not throw");
    }

    /**
     * Test exec() with an empty argument list (would trigger interactive mode with stdin) – we just test no exception
     * for a simple evaluation scenario by providing a trivial script.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    void testExec_withScriptThatCallsPrint_doesNotThrow() throws Exception {
        Files.write(this.tempFile, "print('hello');".getBytes(StandardCharsets.UTF_8));
        final var reporter = Mockito.mock(org.mozilla.javascript.ErrorReporter.class);
        Assertions.assertDoesNotThrow(() -> BasicRhinoShell.exec(new String[] { this.tempFile.toString() }, reporter),
                "exec() with a print() call should not throw");
    }

    /**
     * Test load() static function – loading a valid script file should not throw.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    void testLoad_withValidFile_doesNotThrow() throws Exception {
        Files.write(this.tempFile, "var loaded = true;".getBytes(StandardCharsets.UTF_8));
        final var cx = Context.enter();
        try {
            final BasicRhinoShell shell = new BasicRhinoShell();
            cx.initStandardObjects(shell);
            final Scriptable scope = shell;
            final Object[] args = { this.tempFile.toString() };
            final var funObj = Mockito.mock(Function.class);
            Assertions.assertDoesNotThrow(() -> BasicRhinoShell.load(cx, scope, args, funObj),
                    "load() with a valid JS file should not throw");
        } finally {
            Context.exit();
        }
    }

    /**
     * Test getClassName() returns "global".
     */
    @Test
    void testGetClassName_returnsGlobal() {
        final var shell = new BasicRhinoShell();
        Assertions.assertEquals("global", shell.getClassName(), "getClassName() should return 'global'");
    }

    /**
     * Test processOptions with no arguments returns empty array.
     */
    @Test
    void testProcessOptions_noArgs_returnsEmptyArray() {
        final var cx = Context.enter();
        try {
            final String[] result = BasicRhinoShell.processOptions(cx, new String[0]);
            Assertions.assertEquals(0, result.length, "Empty args should return empty result");
        } finally {
            Context.exit();
        }
    }

    /**
     * Test processOptions with a non-flag argument returns the remaining args starting from that argument.
     */
    @Test
    void testProcessOptions_nonFlagArg_returnsRemainingArgs() {
        final var cx = Context.enter();
        try {
            final String[] result = BasicRhinoShell.processOptions(cx, new String[] { "script.js", "arg1" });
            Assertions.assertEquals(2, result.length, "Expected both remaining args");
            Assertions.assertEquals("script.js", result[0]);
            Assertions.assertEquals("arg1", result[1]);
        } finally {
            Context.exit();
        }
    }

    /**
     * Test version get set.
     */
    @Test
    void testVersionGetSet() {
        final var cx = Context.enter();
        final Scriptable scope = cx.initStandardObjects();
        final Object[] args = { 170 };
        final var funObj = Mockito.mock(Function.class);
        final var oldVersion = BasicRhinoShell.version(cx, scope, new Object[0], funObj);
        BasicRhinoShell.version(cx, scope, args, funObj);
        Assertions.assertEquals(0.0, oldVersion);
        Assertions.assertEquals(170, cx.getLanguageVersion());
        Context.exit();
    }

}
