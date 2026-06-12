/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * See LICENSE file for details.
 *
 * Copyright 2012-2026 Hazendaz.
 */
package net.alchim31.maven.yuicompressor;

import java.io.IOException;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Check JS files with jslint.
 */
@Mojo(name = "jslint", defaultPhase = LifecyclePhase.PROCESS_RESOURCES, requiresProject = true, threadSafe = true)
public class JSLintMojo extends MojoSupport {

    /** The jslint. */
    private JSLintChecker jslint;

    @Override
    protected String[] getDefaultIncludes() {
        return new String[] { "**/**.js" };
    }

    @Override
    public void beforeProcess() throws IOException {
        jslint = new JSLintChecker();
    }

    @Override
    public void afterProcess() {
        // Do nothing
    }

    @Override
    protected void processFile(SourceFile src) throws IOException {
        getLog().info("check file :" + src.toFile());
        jslint.check(src.toFile(), jsErrorReporter);
    }
}
