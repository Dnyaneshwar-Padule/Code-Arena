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
    protected String getCompileAndRunCommand() {
        return "gcc main.c -o main || exit 11; ./main || exit 12";
    }
}
