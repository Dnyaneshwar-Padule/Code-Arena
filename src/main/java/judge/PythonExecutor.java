package judge;

/**
 * Docker-based Python executor.
 */
public class PythonExecutor extends AbstractDockerExecutor {

    @Override
    protected String getSourceFileName() {
        return "main.py";
    }

    @Override
    protected String getDockerImage() {
        return "python:3";
    }

    @Override
    protected String getCompileCommand() {
        return "true";
    }

    @Override
    protected String getRunCommand() {
        return "python3 main.py";
    }
}
