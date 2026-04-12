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

import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.build.BuildContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mozilla.javascript.EvaluatorException;

/**
 * Tests for {@link ErrorReporter4Mojo}.
 */
@ExtendWith(MockitoExtension.class)
class ErrorReporter4MojoTest {

    /** Maven plugin log (mocked). */
    @Mock
    private Log log;

    /** Plexus build context (mocked). */
    @Mock
    private BuildContext buildContext;

    // ------------------------------------------------------------------ error

    /**
     * Test that {@code error()} increments the error counter.
     */
    @Test
    void testError_incrementsErrorCount() {
        final var reporter = new ErrorReporter4Mojo(log, true, buildContext);
        reporter.error("msg", "src.js", 1, "lineSource", 0);
        Assertions.assertEquals(1, reporter.getErrorCnt());
    }

    /**
     * Test that {@code error()} delegates to the Maven log.
     */
    @Test
    void testError_logsToMavenLog() {
        final var reporter = new ErrorReporter4Mojo(log, true, buildContext);
        reporter.error("msg", "src.js", 2, "lineSource", 5);
        Mockito.verify(log).error(Mockito.anyString());
    }

    /**
     * Test that {@code error()} notifies the build context with SEVERITY_ERROR.
     */
    @Test
    void testError_addsBuildContextMessage() {
        final var file = new File("src.js");
        final var reporter = new ErrorReporter4Mojo(log, true, buildContext);
        reporter.setFile(file);
        reporter.error("msg", "src.js", 3, "lineSource", 1);
        Mockito.verify(buildContext).addMessage(file, 3, 1, "msg", BuildContext.SEVERITY_ERROR, null);
    }

    /**
     * Test that multiple {@code error()} calls accumulate the error count.
     */
    @Test
    void testError_multipleCallsAccumulateCount() {
        final var reporter = new ErrorReporter4Mojo(log, true, buildContext);
        reporter.error("e1", null, 1, null, 0);
        reporter.error("e2", null, 2, null, 0);
        reporter.error("e3", null, 3, null, 0);
        Assertions.assertEquals(3, reporter.getErrorCnt());
    }

    // --------------------------------------------------------------- warning

    /**
     * Test that {@code warning()} increments the warning counter when warnings are accepted.
     */
    @Test
    void testWarning_whenAcceptWarnTrue_incrementsWarningCount() {
        final var reporter = new ErrorReporter4Mojo(log, true, buildContext);
        reporter.warning("warn", "src.js", 1, "lineSource", 0);
        Assertions.assertEquals(1, reporter.getWarningCnt());
    }

    /**
     * Test that {@code warning()} notifies the log when warnings are accepted.
     */
    @Test
    void testWarning_whenAcceptWarnTrue_logsWarning() {
        final var reporter = new ErrorReporter4Mojo(log, true, buildContext);
        reporter.warning("warn", "src.js", 1, "lineSource", 0);
        Mockito.verify(log).warn(Mockito.anyString());
    }

    /**
     * Test that {@code warning()} notifies the build context when warnings are accepted.
     */
    @Test
    void testWarning_whenAcceptWarnTrue_addsBuildContextMessage() {
        final var file = new File("src.js");
        final var reporter = new ErrorReporter4Mojo(log, true, buildContext);
        reporter.setFile(file);
        reporter.warning("warn", "src.js", 7, "lineSource", 2);
        Mockito.verify(buildContext).addMessage(file, 7, 2, "warn", BuildContext.SEVERITY_WARNING, null);
    }

    /**
     * Test that {@code warning()} does NOT increment the counter when warnings are suppressed.
     */
    @Test
    void testWarning_whenAcceptWarnFalse_doesNotIncrementCount() {
        final var reporter = new ErrorReporter4Mojo(log, false, buildContext);
        reporter.warning("warn", "src.js", 1, "lineSource", 0);
        Assertions.assertEquals(0, reporter.getWarningCnt());
    }

    /**
     * Test that {@code warning()} does NOT log anything when warnings are suppressed.
     */
    @Test
    void testWarning_whenAcceptWarnFalse_doesNotLog() {
        final var reporter = new ErrorReporter4Mojo(log, false, buildContext);
        reporter.warning("warn", "src.js", 1, "lineSource", 0);
        Mockito.verify(log, Mockito.never()).warn(Mockito.anyString());
    }

    /**
     * Test that {@code warning()} does NOT notify the build context when warnings are suppressed.
     */
    @Test
    void testWarning_whenAcceptWarnFalse_doesNotNotifyBuildContext() {
        final var reporter = new ErrorReporter4Mojo(log, false, buildContext);
        reporter.warning("warn", "src.js", 1, "lineSource", 0);
        Mockito.verify(buildContext, Mockito.never()).addMessage(Mockito.any(), Mockito.anyInt(), Mockito.anyInt(),
                Mockito.anyString(), Mockito.anyInt(), Mockito.any());
    }

    // ---------------------------------------------------------- runtimeError

    /**
     * Test that {@code runtimeError()} throws an {@link EvaluatorException}.
     */
    @Test
    void testRuntimeError_throwsEvaluatorException() {
        final var reporter = new ErrorReporter4Mojo(log, true, buildContext);
        Assertions.assertThrows(EvaluatorException.class,
                () -> reporter.runtimeError("runtime", "src.js", 1, "lineSource", 0));
    }

    /**
     * Test that {@code runtimeError()} also increments the error counter before throwing.
     */
    @Test
    void testRuntimeError_incrementsErrorCount() {
        final var reporter = new ErrorReporter4Mojo(log, true, buildContext);
        try {
            reporter.runtimeError("runtime", "src.js", 1, "lineSource", 0);
        } catch (EvaluatorException ignored) {
            // expected
        }
        Assertions.assertEquals(1, reporter.getErrorCnt());
    }

    // ---------------------------------------------------- setDefaultFileName

    /**
     * Test that {@code setDefaultFileName()} retains a non-empty name.
     */
    @Test
    void testSetDefaultFileName_nonEmpty_usedInMessage() {
        final var reporter = new ErrorReporter4Mojo(log, true, buildContext);
        reporter.setDefaultFileName("myFile.js");
        // trigger an error with null sourceName so the default is used
        final var captor = ArgumentCaptor.forClass(String.class);
        reporter.error("oops", null, 1, null, 0);
        Mockito.verify(log).error(captor.capture());
        Assertions.assertTrue(captor.getValue().contains("myFile.js"),
                "Expected default filename to appear in the error message");
    }

    /**
     * Test that {@code setDefaultFileName("")} effectively clears the filename.
     */
    @Test
    void testSetDefaultFileName_empty_clearsDefaultName() {
        final var reporter = new ErrorReporter4Mojo(log, true, buildContext);
        reporter.setDefaultFileName("myFile.js");
        reporter.setDefaultFileName(""); // empty should set to null
        final var captor = ArgumentCaptor.forClass(String.class);
        reporter.error("oops", null, 1, null, 0);
        Mockito.verify(log).error(captor.capture());
        // With null default filename the message contains only the error text
        Assertions.assertFalse(captor.getValue().contains("myFile.js"),
                "Expected cleared filename to not appear in the error message");
    }

    // -------------------------------------------------------- message format

    /**
     * Test that the formatted message includes sourceName, line, column and message text.
     */
    @Test
    void testMessageFormat_includesSourceLineColumn() {
        final var reporter = new ErrorReporter4Mojo(log, true, buildContext);
        final var captor = ArgumentCaptor.forClass(String.class);
        reporter.error("bad syntax", "app.js", 10, "var x =", 4);
        Mockito.verify(log).error(captor.capture());
        final var msg = captor.getValue();
        Assertions.assertTrue(msg.contains("app.js"), "Expected source name in message");
        Assertions.assertTrue(msg.contains("10"), "Expected line number in message");
        Assertions.assertTrue(msg.contains("4"), "Expected column offset in message");
        Assertions.assertTrue(msg.contains("bad syntax"), "Expected error text in message");
    }

    /**
     * Test that the formatted message includes the lineSource on a new line when non-empty.
     */
    @Test
    void testMessageFormat_includesLineSource() {
        final var reporter = new ErrorReporter4Mojo(log, true, buildContext);
        final var captor = ArgumentCaptor.forClass(String.class);
        reporter.error("msg", "a.js", 1, "var x = $bad;", 0);
        Mockito.verify(log).error(captor.capture());
        Assertions.assertTrue(captor.getValue().contains("var x = $bad;"),
                "Expected lineSource to be appended to message");
    }

    /**
     * Test that a null or empty message is replaced with "unknown error".
     */
    @Test
    void testMessageFormat_nullMessage_showsUnknownError() {
        final var reporter = new ErrorReporter4Mojo(log, true, buildContext);
        final var captor = ArgumentCaptor.forClass(String.class);
        reporter.error(null, "a.js", 1, null, 0);
        Mockito.verify(log).error(captor.capture());
        Assertions.assertTrue(captor.getValue().contains("unknown error"),
                "Expected 'unknown error' placeholder for null message");
    }

    /**
     * Test that a null sourceName falls back to the default filename (if set).
     */
    @Test
    void testMessageFormat_nullSourceName_usesDefaultFilename() {
        final var reporter = new ErrorReporter4Mojo(log, true, buildContext);
        reporter.setDefaultFileName("fallback.js");
        final var captor = ArgumentCaptor.forClass(String.class);
        reporter.error("err", null, 5, null, 0);
        Mockito.verify(log).error(captor.capture());
        Assertions.assertTrue(captor.getValue().contains("fallback.js"),
                "Expected default filename to be used when sourceName is null");
    }

    /**
     * Test that when both sourceName and defaultFilename are null/empty, no location prefix is added.
     */
    @Test
    void testMessageFormat_noSourceAtAll_onlyMessageText() {
        final var reporter = new ErrorReporter4Mojo(log, true, buildContext);
        final var captor = ArgumentCaptor.forClass(String.class);
        reporter.error("just the error", null, 1, null, 0);
        Mockito.verify(log).error(captor.capture());
        // The message should just be the error text; no "null" string
        Assertions.assertFalse(captor.getValue().contains("null"),
                "Message should not contain the literal string 'null'");
        Assertions.assertTrue(captor.getValue().contains("just the error"), "Expected the error text to be present");
    }

    // ---------------------------------------------------------------- setFile

    /**
     * Test that {@code setFile()} updates the source file used when calling {@code addMessage}.
     */
    @Test
    void testSetFile_updatesSourceFileForBuildContext() {
        final var file1 = new File("first.js");
        final var file2 = new File("second.js");
        final var reporter = new ErrorReporter4Mojo(log, true, buildContext);

        reporter.setFile(file1);
        reporter.error("e", null, 1, null, 0);
        Mockito.verify(buildContext).addMessage(Mockito.eq(file1), Mockito.anyInt(), Mockito.anyInt(),
                Mockito.anyString(), Mockito.anyInt(), Mockito.isNull());

        reporter.setFile(file2);
        reporter.error("e", null, 2, null, 0);
        Mockito.verify(buildContext).addMessage(Mockito.eq(file2), Mockito.anyInt(), Mockito.anyInt(),
                Mockito.anyString(), Mockito.anyInt(), Mockito.isNull());
    }

    // --------------------------------------------------------------- counters

    /**
     * Test that both error and warning counts start at zero.
     */
    @Test
    void testInitialCountersAreZero() {
        final var reporter = new ErrorReporter4Mojo(log, true, buildContext);
        Assertions.assertEquals(0, reporter.getErrorCnt());
        Assertions.assertEquals(0, reporter.getWarningCnt());
    }

    /**
     * Test that error and warning counters are independent.
     */
    @Test
    void testErrorAndWarningCountersAreIndependent() {
        final var reporter = new ErrorReporter4Mojo(log, true, buildContext);
        reporter.error("e", null, 1, null, 0);
        reporter.warning("w", null, 2, null, 0);
        Assertions.assertEquals(1, reporter.getErrorCnt());
        Assertions.assertEquals(1, reporter.getWarningCnt());
    }
}
