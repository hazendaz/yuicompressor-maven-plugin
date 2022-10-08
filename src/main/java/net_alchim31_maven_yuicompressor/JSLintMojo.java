package net_alchim31_maven_yuicompressor;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Check JS files with jslint.
 *
 * @author David Bernard
 * @since 2007-08-29
 */
@Mojo(name = "jslint", defaultPhase = LifecyclePhase.PROCESS_RESOURCES, requiresProject = true, threadSafe = true)
public class JSLintMojo extends MojoSupport {
    
    /** The jslint. */
    private JSLintChecker jslint_;

    @Override
    protected String[] getDefaultIncludes() throws Exception {
        return new String[]{"**/**.js"};
    }

    @Override
    public void beforeProcess() throws Exception {
        jslint_ = new JSLintChecker();
    }

    @Override
    public void afterProcess() throws Exception {
    }

    @Override
    protected void processFile(SourceFile src) throws Exception {
        getLog().info("check file :" + src.toFile());
        jslint_.check(src.toFile(), jsErrorReporter_);
    }
}
