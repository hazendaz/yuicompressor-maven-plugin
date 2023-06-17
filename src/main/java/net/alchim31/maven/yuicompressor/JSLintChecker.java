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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
                FileOutputStream out = new FileOutputStream(jslint)) {
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
