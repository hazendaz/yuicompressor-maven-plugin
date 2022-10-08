package net_alchim31_maven_yuicompressor;

import org.apache.maven.plugin.logging.Log;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
import org.sonatype.plexus.build.incremental.BuildContext;

import java.io.File;

/**
 * The Class ErrorReporter4Mojo.
 */
public class ErrorReporter4Mojo implements ErrorReporter {

    /** The default filename. */
    private String defaultFilename_;

    /** The accept warn. */
    private boolean acceptWarn_;

    /** The log. */
    private Log log_;

    /** The warning cnt. */
    private int warningCnt_;

    /** The error cnt. */
    private int errorCnt_;

    /** The build context. */
    private BuildContext buildContext_;

    /** The source file. */
    private File sourceFile_;

    /**
     * Instantiates a new error reporter 4 mojo.
     *
     * @param log the log
     * @param jswarn the jswarn
     * @param buildContext the build context
     */
    public ErrorReporter4Mojo(Log log, boolean jswarn, BuildContext buildContext) {
        log_ = log;
        acceptWarn_ = jswarn;
        buildContext_ = buildContext;
    }

    /**
     * Sets the default file name.
     *
     * @param v the new default file name
     */
    public void setDefaultFileName(String v) {
        if (v.length() == 0) {
            v = null;
        }
        defaultFilename_ = v;
    }

    /**
     * Gets the error cnt.
     *
     * @return the error cnt
     */
    public int getErrorCnt() {
        return errorCnt_;
    }

    /**
     * Gets the warning cnt.
     *
     * @return the warning cnt
     */
    public int getWarningCnt() {
        return warningCnt_;
    }

    public void error(String message, String sourceName, int line, String lineSource, int lineOffset) {
        String fullMessage = newMessage(message, sourceName, line, lineSource, lineOffset);
        buildContext_.addMessage(sourceFile_, line, lineOffset, message, BuildContext.SEVERITY_ERROR, null);
        log_.error(fullMessage);
        errorCnt_++;
    }

    public EvaluatorException runtimeError(String message, String sourceName, int line, String lineSource, int lineOffset) {
        error(message, sourceName, line, lineSource, lineOffset);
        throw new EvaluatorException(message, sourceName, line, lineSource, lineOffset);
    }

    public void warning(String message, String sourceName, int line, String lineSource, int lineOffset) {
        if (acceptWarn_) {
            String fullMessage = newMessage(message, sourceName, line, lineSource, lineOffset);
            buildContext_.addMessage(sourceFile_, line, lineOffset, message, BuildContext.SEVERITY_WARNING, null);
            log_.warn(fullMessage);
            warningCnt_++;
        }
    }

    /**
     * New message.
     *
     * @param message the message
     * @param sourceName the source name
     * @param line the line
     * @param lineSource the line source
     * @param lineOffset the line offset
     * @return the string
     */
    private String newMessage(String message, String sourceName, int line, String lineSource, int lineOffset) {
        StringBuilder back = new StringBuilder();
        if ((sourceName == null) || (sourceName.length() == 0)) {
            sourceName = defaultFilename_;
        }
        if (sourceName != null) {
            back.append(sourceName)
                    .append(":line ")
                    .append(line)
                    .append(":column ")
                    .append(lineOffset)
                    .append(':')
            ;
        }
        if ((message != null) && (message.length() != 0)) {
            back.append(message);
        } else {
            back.append("unknown error");
        }
        if ((lineSource != null) && (lineSource.length() != 0)) {
            back.append("\n\t")
                    .append(lineSource)
            ;
        }
        return back.toString();
    }

    /**
     * Sets the file.
     *
     * @param file the new file
     */
    public void setFile(File file) {
        sourceFile_ = file;
    }

}
