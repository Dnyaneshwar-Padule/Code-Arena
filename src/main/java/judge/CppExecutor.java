package judge;

/**
 * Docker-based C++ executor.
 */
public class CppExecutor extends AbstractDockerExecutor {

    @Override
    protected String getSourceFileName() {
        return "main.cpp";
    }

    @Override
    protected String getDockerImage() {
        return "gcc:latest";
    }

    @Override
    protected String getCompileAndRunCommand() {
        return "g++ main.cpp -o main && ./main";
    }
}
