package judge;

/**
 * Docker-based C executor.
 */
public class CExecutor extends AbstractDockerExecutor {

    @Override
    protected String getSourceFileName() {
        return "main.c";
    }

    @Override
    protected String getDockerImage() {
        return "gcc:latest";
    }

    @Override
    protected String getCompileCommand() {
        return "gcc main.c -o main";
    }

    @Override
    protected String getRunCommand() {
        return "./main";
    }
}
