/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * See LICENSE file for details.
 *
 * Copyright 2012-2026 Hazendaz.
 */
package net.alchim31.maven.yuicompressor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

import org.codehaus.plexus.util.IOUtil;
import org.mozilla.javascript.ErrorReporter;

/**
 * The Class JSLintChecker.
 */
// TODO: use MojoErrorReporter
class JSLintChecker {

    /** The jslint path. */
    private String jslintPath;

    /**
     * Instantiates a new JS lint checker.
     *
     * @throws IOException
     *             the IO exception
     */
    public JSLintChecker() throws IOException {
        File jslint = File.createTempFile("jslint", ".js");
        jslint.deleteOnExit();
        try (InputStream in = getClass().getResourceAsStream("/jslint.js");
                OutputStream out = Files.newOutputStream(jslint.toPath())) {
            IOUtil.copy(in, out);
        }
        jslintPath = jslint.getCanonicalPath();
    }

    /**
     * Check.
     *
     * @param jsFile
     *            the js file
     * @param reporter
     *            the reporter
     *
     * @throws IOException
     *             the IO exception
     */
    public void check(File jsFile, ErrorReporter reporter) throws IOException {
        String[] args = new String[2];
        args[0] = jslintPath;
        args[1] = jsFile.getCanonicalPath();
        BasicRhinoShell.exec(args, reporter);
    }
}
