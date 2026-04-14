package judge;

/**
 * Docker-based Java executor.
 */
public class JavaExecutor extends AbstractDockerExecutor {

    @Override
    protected String getSourceFileName() {
        return "Main.java";
    }

    @Override
    protected String getDockerImage() {
        return "eclipse-temurin:17";
    }

    @Override
    protected String getCompileAndRunCommand() {
        return "javac Main.java && java Main";
    }
}
