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

import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.build.BuildContext;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

/**
 * The Class ErrorReporter4Mojo.
 */
public class ErrorReporter4Mojo implements ErrorReporter {

    /** The default filename. */
    private String defaultFilename;

    /** The accept warn. */
    private boolean acceptWarn;

    /** The log. */
    private Log log;

    /** The warning cnt. */
    private int warningCnt;

    /** The error cnt. */
    private int errorCnt;

    /** The build context. */
    private BuildContext buildContext;

    /** The source file. */
    private File sourceFile;

    /**
     * Instantiates a new error reporter 4 mojo.
     *
     * @param log
     *            the log
     * @param jswarn
     *            the jswarn
     * @param buildContext
     *            the build context
     */
    public ErrorReporter4Mojo(Log log, boolean jswarn, BuildContext buildContext) {
        this.log = log;
        this.acceptWarn = jswarn;
        this.buildContext = buildContext;
    }

    /**
     * Sets the default file name.
     *
     * @param v
     *            the new default file name
     */
    public void setDefaultFileName(String v) {
        if (v.length() == 0) {
            v = null;
        }
        defaultFilename = v;
    }

    /**
     * Gets the error cnt.
     *
     * @return the error cnt
     */
    public int getErrorCnt() {
        return errorCnt;
    }

    /**
     * Gets the warning cnt.
     *
     * @return the warning cnt
     */
    public int getWarningCnt() {
        return warningCnt;
    }

    @Override
    public void error(String message, String sourceName, int line, String lineSource, int lineOffset) {
        String fullMessage = newMessage(message, sourceName, line, lineSource, lineOffset);
        buildContext.addMessage(sourceFile, line, lineOffset, message, BuildContext.SEVERITY_ERROR, null);
        log.error(fullMessage);
        errorCnt++;
    }

    @Override
    public EvaluatorException runtimeError(String message, String sourceName, int line, String lineSource,
            int lineOffset) {
        error(message, sourceName, line, lineSource, lineOffset);
        throw new EvaluatorException(message, sourceName, line, lineSource, lineOffset);
    }

    @Override
    public void warning(String message, String sourceName, int line, String lineSource, int lineOffset) {
        if (acceptWarn) {
            String fullMessage = newMessage(message, sourceName, line, lineSource, lineOffset);
            buildContext.addMessage(sourceFile, line, lineOffset, message, BuildContext.SEVERITY_WARNING, null);
            log.warn(fullMessage);
            warningCnt++;
        }
    }

    /**
     * New message.
     *
     * @param message
     *            the message
     * @param sourceName
     *            the source name
     * @param line
     *            the line
     * @param lineSource
     *            the line source
     * @param lineOffset
     *            the line offset
     *
     * @return the string
     */
    private String newMessage(String message, String sourceName, int line, String lineSource, int lineOffset) {
        StringBuilder back = new StringBuilder();
        if (sourceName == null || sourceName.length() == 0) {
            sourceName = defaultFilename;
        }
        if (sourceName != null) {
            back.append(sourceName).append(":line ").append(line).append(":column ").append(lineOffset).append(':');
        }
        if (message != null && message.length() != 0) {
            back.append(message);
        } else {
            back.append("unknown error");
        }
        if (lineSource != null && lineSource.length() != 0) {
            back.append("\n\t").append(lineSource);
        }
        return back.toString();
    }

    /**
     * Sets the file.
     *
     * @param file
     *            the new file
     */
    public void setFile(File file) {
        sourceFile = file;
    }

}
