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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;

import org.apache.maven.api.plugin.testing.MojoExtension;
import org.codehaus.plexus.build.DefaultBuildContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonatype.plexus.build.incremental.BuildContext;

/**
 * Tests for {@link JSLintMojo} using direct instantiation.
 */
// Note: public here needed for javadocs to work so don't remove it
public class JSLintMojoTest {

    /** Temporary directory for test output files. */
    @TempDir
    File tempDir;

    /**
     * Test that executing the JSLintMojo with a valid JS file completes without throwing.
     *
     * @throws Exception
     *             if test setup or execution fails
     */
    @Test
    void testMojoExecute_withValidJsFile_doesNotThrow() throws Exception {
        final File webappDir = new File(tempDir, "webapp-jslint");
        webappDir.mkdirs();
        final File jsFile = new File(webappDir, "simple.js");
        Files.write(jsFile.toPath(), "var x = 1;".getBytes(StandardCharsets.UTF_8));

        final var mojo = createAndConfigureMojo(webappDir);
        Assertions.assertDoesNotThrow(mojo::execute, "JSLintMojo.execute() should not throw for valid JS");
    }

    /**
     * Test that mojo execution is skipped when {@code skip} is set to true.
     *
     * @throws Exception
     *             if test setup or execution fails
     */
    @Test
    void testMojoExecute_skipTrue_doesNotProcess() throws Exception {
        final File webappDir = new File(tempDir, "webapp-jslint-skip");
        webappDir.mkdirs();
        final var mojo = createAndConfigureMojo(webappDir);
        MojoExtension.setVariableValueToObject(mojo, "skip", true);

        Assertions.assertDoesNotThrow(mojo::execute, "JSLintMojo.execute() with skip=true should not throw");
    }

    /**
     * Test that the default includes for JSLintMojo return the expected JS glob.
     *
     * @throws Exception
     *             if test fails
     */
    @Test
    void testGetDefaultIncludes_returnsJsGlob() throws Exception {
        final var mojo = new JSLintMojo();
        final String[] includes = mojo.getDefaultIncludes();
        Assertions.assertNotNull(includes, "Default includes should not be null");
        Assertions.assertEquals(1, includes.length, "Expected exactly one default include pattern");
        Assertions.assertEquals("**/**.js", includes[0], "Default include should match '**/**.js'");
    }

    /**
     * Test that afterProcess() completes without throwing (it is a no-op).
     *
     * @throws Exception
     *             if test fails
     */
    @Test
    void testAfterProcess_doesNotThrow() throws Exception {
        final var mojo = new JSLintMojo();
        Assertions.assertDoesNotThrow(mojo::afterProcess, "afterProcess() should be a no-op");
    }

    // ----------------------------------------------------------------------- helpers

    /**
     * Creates and configures a {@link JSLintMojo} with the given webapp directory.
     *
     * @param warSourceDirectory
     *            the webapp source directory containing JS files
     *
     * @return configured mojo
     *
     * @throws Exception
     *             if field injection fails
     */
    private JSLintMojo createAndConfigureMojo(File warSourceDirectory) throws Exception {
        final var mojo = new JSLintMojo();
        final BuildContext legacyCtx = Mockito.mock(BuildContext.class);
        Mockito.lenient().when(legacyCtx.newFileOutputStream(ArgumentMatchers.any(File.class)))
                .thenAnswer(inv -> Files.newOutputStream(((File) inv.getArgument(0)).toPath()));
        final var buildContext = new DefaultBuildContext(legacyCtx);
        MojoExtension.setVariableValueToObject(mojo, "buildContext", buildContext);
        MojoExtension.setVariableValueToObject(mojo, "skip", false);
        MojoExtension.setVariableValueToObject(mojo, "jswarn", false);
        MojoExtension.setVariableValueToObject(mojo, "failOnWarning", false);
        MojoExtension.setVariableValueToObject(mojo, "excludeResources", true);
        MojoExtension.setVariableValueToObject(mojo, "excludeWarSourceDirectory", false);
        MojoExtension.setVariableValueToObject(mojo, "warSourceDirectory", warSourceDirectory);
        MojoExtension.setVariableValueToObject(mojo, "webappDirectory", new File(tempDir, "webapp-output"));
        MojoExtension.setVariableValueToObject(mojo, "outputDirectory", new File(tempDir, "classes"));
        MojoExtension.setVariableValueToObject(mojo, "sourceDirectory", new File(tempDir, "nonexistent-src"));
        MojoExtension.setVariableValueToObject(mojo, "resources", Collections.emptyList());
        return mojo;
    }
}
