package net_alchim31_maven_yuicompressor;

import org.codehaus.plexus.util.IOUtil;
import org.mozilla.javascript.ErrorReporter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * The Class JSLintChecker.
 */
//TODO: use MojoErrorReporter
class JSLintChecker {

    /** The jslint path. */
    private String jslintPath_;

    /**
     * Instantiates a new JS lint checker.
     *
     * @throws Exception the exception
     */
    public JSLintChecker() throws Exception {
        FileOutputStream out = null;
        InputStream in = null;
        try {
            File jslint = File.createTempFile("jslint", ".js");
            jslint.deleteOnExit();
            in = getClass().getResourceAsStream("/jslint.js");
            out = new FileOutputStream(jslint);
            IOUtil.copy(in, out);
            jslintPath_ = jslint.getAbsolutePath();
        } finally {
            IOUtil.close(in);
            IOUtil.close(out);
        }
    }

    /**
     * Check.
     *
     * @param jsFile the js file
     * @param reporter the reporter
     */
    public void check(File jsFile, ErrorReporter reporter) {
        String[] args = new String[2];
        args[0] = jslintPath_;
        args[1] = jsFile.getAbsolutePath();
        BasicRhinoShell.exec(args, reporter);
    }
}
