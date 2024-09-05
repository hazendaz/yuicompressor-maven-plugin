/*
 * YuiCompressor Maven plugin
 *
 * Copyright 2012-2024 Hazendaz.
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
