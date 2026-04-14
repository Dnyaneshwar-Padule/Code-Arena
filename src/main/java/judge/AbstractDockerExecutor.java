package judge;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.TimeUnit;

/**
 * Base executor that runs user code in Docker containers.
 */
public abstract class AbstractDockerExecutor implements CodeExecutor {

    private static final long EXECUTION_TIMEOUT_SECONDS = 10L;
    private static final int MAX_OUTPUT_BYTES = 10 * 1024;
    private static final int EXIT_CODE_COMPILATION_ERROR = 11;
    private static final int EXIT_CODE_RUNTIME_ERROR = 12;

    @Override
    public ExecutionResult execute(String code, String input) {
        Path submissionDir = null;
        try {
            Path submissionsRoot = Path.of("/tmp/codearena");
            Files.createDirectories(submissionsRoot);
            submissionDir = Files.createTempDirectory(submissionsRoot, "submission_");
            Path sourceFile = submissionDir.resolve(getSourceFileName());
            Files.writeString(
                    sourceFile,
                    code == null ? "" : code,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE
            );

            ProcessBuilder processBuilder = new ProcessBuilder(
                    "docker",
                    "run",
                    "--rm",
                    "-i",
                    "--network",
                    "none",
                    "--pids-limit",
                    "64",
                    "--memory",
                    "256m",
                    "--cpus",
                    "0.5",
                    "-v",
                    submissionDir.toAbsolutePath() + ":/app",
                    "-w",
                    "/app",
                    getDockerImage(),
                    "bash",
                    "-c",
                    getCompileAndRunCommand()
            );

            long startedAt = System.currentTimeMillis();
            Process process = processBuilder.start();

            StreamCollector stdoutCollector = new StreamCollector(process.getInputStream());
            StreamCollector stderrCollector = new StreamCollector(process.getErrorStream());
            Thread stdoutThread = new Thread(stdoutCollector);
            Thread stderrThread = new Thread(stderrCollector);
            stdoutThread.start();
            stderrThread.start();

            try (OutputStream stdin = process.getOutputStream()) {
                stdin.write((input == null ? "" : input).getBytes(StandardCharsets.UTF_8));
                stdin.flush();
            }

            boolean finishedInTime = process.waitFor(EXECUTION_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (!finishedInTime) {
                process.destroyForcibly();
                return new ExecutionResult(
                        stdoutCollector.content(),
                        "Execution timed out.",
                        elapsed(startedAt),
                        ExecutionStatus.TIME_LIMIT_EXCEEDED
                );
            }

            stdoutThread.join();
            stderrThread.join();
            if (stdoutCollector.exceededLimit() || stderrCollector.exceededLimit()) {
                return new ExecutionResult(
                        stdoutCollector.content(),
                        "Output limit exceeded (10KB).",
                        elapsed(startedAt),
                        ExecutionStatus.OUTPUT_LIMIT_EXCEEDED
                );
            }
            int exitCode = process.exitValue();
            ExecutionStatus status = resolveStatus(exitCode, stderrCollector.content());
            return new ExecutionResult(
                    stdoutCollector.content(),
                    stderrCollector.content(),
                    elapsed(startedAt),
                    status
            );
        } catch (Exception ex) {
            return new ExecutionResult("", ex.getMessage(), 0L, ExecutionStatus.RUNTIME_ERROR);
        } finally {
            deleteDirectoryQuietly(submissionDir);
        }
    }

    protected abstract String getSourceFileName();

    protected abstract String getDockerImage();

    protected abstract String getCompileAndRunCommand();

    protected ExecutionStatus resolveStatus(int exitCode, String stderr) {
        if (exitCode == 0) {
            return ExecutionStatus.ACCEPTED;
        }
        if (exitCode == EXIT_CODE_COMPILATION_ERROR) {
            return ExecutionStatus.COMPILATION_ERROR;
        }
        if (exitCode == EXIT_CODE_RUNTIME_ERROR) {
            return ExecutionStatus.RUNTIME_ERROR;
        }
        if (exitCode == 137 || containsAny(stderr, "out of memory", "killed")) {
            return ExecutionStatus.MEMORY_LIMIT_EXCEEDED;
        }
        return ExecutionStatus.RUNTIME_ERROR;
    }

    private long elapsed(long startedAt) {
        return Math.max(1L, System.currentTimeMillis() - startedAt);
    }

    private boolean containsAny(String value, String... tokens) {
        String normalized = value == null ? "" : value.toLowerCase();
        for (String token : tokens) {
            if (normalized.contains(token)) {
                return true;
            }
        }
        return false;
    }

    private void deleteDirectoryQuietly(Path directory) {
        if (directory == null || !Files.exists(directory)) {
            return;
        }
        try {
            Files.walk(directory)
                    .sorted((first, second) -> second.getNameCount() - first.getNameCount())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException ignored) {
                            // Best effort cleanup.
                        }
                    });
        } catch (IOException ignored) {
            // Best effort cleanup.
        }
    }

    private static final class StreamCollector implements Runnable {
        private final InputStream inputStream;
        private final StringBuilder builder = new StringBuilder();
        private boolean exceededLimit;
        private int currentBytes;

        private StreamCollector(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public void run() {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (exceededLimit) {
                        continue;
                    }
                    String candidate = builder.isEmpty() ? line : ("\n" + line);
                    int nextBytes = currentBytes + candidate.getBytes(StandardCharsets.UTF_8).length;
                    if (nextBytes > MAX_OUTPUT_BYTES) {
                        exceededLimit = true;
                        continue;
                    }
                    builder.append(candidate);
                    currentBytes = nextBytes;
                }
            } catch (IOException ignored) {
                // Keep collector resilient to stream close races.
            }
        }

        private String content() {
            return builder.toString();
        }

        private boolean exceededLimit() {
            return exceededLimit;
        }
    }
}
