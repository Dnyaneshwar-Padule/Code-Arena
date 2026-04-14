<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Problem Detail | CP Portal" scope="request"/>
<%@ include file="components/header.jsp" %>

<main class="flex-grow-1 py-5 bg-light">
    <section class="container">
        <a class="btn btn-outline-secondary btn-sm mb-4" href="${pageContext.request.contextPath}/problems">Back to Problems</a>

        <c:if test="${not empty error}">
            <div class="alert alert-danger" role="alert">${error}</div>
        </c:if>

        <c:if test="${not empty problem}">
            <article class="card border-0 shadow-sm">
                <div class="card-body p-3 p-md-4">
                    <div class="row g-4 problem-detail-layout">
                        <div class="col-12 col-lg-6 problem-detail-column">
                            <ul class="nav nav-tabs mb-3" role="tablist">
                                <li class="nav-item" role="presentation">
                                    <button class="nav-link ${activeTab == 'submissions' ? '' : 'active'}"
                                            id="problem-tab"
                                            data-bs-toggle="tab"
                                            data-bs-target="#problem-pane"
                                            type="button"
                                            role="tab">
                                        Description
                                    </button>
                                </li>
                                <li class="nav-item" role="presentation">
                                    <button class="nav-link ${activeTab == 'submissions' ? 'active' : ''}"
                                            id="submissions-tab"
                                            data-bs-toggle="tab"
                                            data-bs-target="#submissions-pane"
                                            type="button"
                                            role="tab">
                                        Submissions
                                    </button>
                                </li>
                            </ul>

                            <div class="tab-content">
                                <div class="tab-pane fade ${activeTab == 'submissions' ? '' : 'show active'}"
                                     id="problem-pane"
                                     role="tabpanel">
                                    <section class="problem-pane pe-lg-3">
                                <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-2 mb-3">
                                    <h1 class="h3 fw-bold mb-0">${problem.title}</h1>
                                    <span class="badge text-bg-primary">${problem.difficulty}</span>
                                </div>
                                <div class="row g-2 mb-3">
                                    <div class="col-6">
                                        <div class="border rounded-3 p-2 bg-white">
                                            <small class="fw-semibold d-block">Time Limit</small>
                                            <span class="text-secondary">${problem.timeLimit} ms</span>
                                        </div>
                                    </div>
                                    <div class="col-6">
                                        <div class="border rounded-3 p-2 bg-white">
                                            <small class="fw-semibold d-block">Memory Limit</small>
                                            <span class="text-secondary">${problem.memoryLimit} KB</span>
                                        </div>
                                    </div>
                                </div>
                                <section class="mb-4">
                                    <h2 class="h6 fw-semibold">Description</h2>
                                    <p class="text-secondary mb-0 section-pre">${problem.description}</p>
                                </section>
                                <section class="mb-4">
                                    <h2 class="h6 fw-semibold">Input Format</h2>
                                    <pre class="bg-light border rounded p-3 mb-0 section-pre">${not empty problem.inputFormat ? fn:trim(problem.inputFormat) : 'Not specified.'}</pre>
                                </section>
                                <section class="mb-4">
                                    <h2 class="h6 fw-semibold">Output Format</h2>
                                    <pre class="bg-light border rounded p-3 mb-0 section-pre">${not empty problem.outputFormat ? fn:trim(problem.outputFormat) : 'Not specified.'}</pre>
                                </section>
                                <section class="mb-4">
                                    <h2 class="h6 fw-semibold">Constraints</h2>
                                    <pre class="bg-light border rounded p-3 mb-0 section-pre">${not empty problem.constraints ? fn:trim(problem.constraints) : 'Not specified.'}</pre>
                                </section>
                                <section>
                                    <h2 class="h5 fw-semibold mb-3">Sample Test Cases</h2>
                                    <c:choose>
                                        <c:when test="${empty sampleTestCases}">
                                            <p class="text-secondary mb-0">No sample test cases available for this problem.</p>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="row g-3">
                                                <c:forEach var="testCase" items="${sampleTestCases}" varStatus="status">
                                                    <div class="col-12">
                                                        <div class="border rounded-3 p-3 bg-white">
                                                            <h3 class="h6 fw-bold mb-3">Sample ${status.index + 1}</h3>
                                                            <p class="fw-semibold mb-1">Input</p>
                                                            <pre class="bg-light border rounded p-3 mb-3 section-pre"><code>${testCase.input}</code></pre>
                                                            <p class="fw-semibold mb-1">Output</p>
                                                            <pre class="bg-light border rounded p-3 mb-0 section-pre"><code>${testCase.expectedOutput}</code></pre>
                                                        </div>
                                                    </div>
                                                </c:forEach>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                </section>
                                    </section>
                                </div>

                                <div class="tab-pane fade ${activeTab == 'submissions' ? 'show active' : ''}"
                                     id="submissions-pane"
                                     role="tabpanel">
                                    <section class="pe-lg-3">
                                        <div class="d-flex justify-content-between align-items-center mb-3">
                                            <h2 class="h5 fw-semibold mb-0">Your Submission History</h2>
                                            <a class="btn btn-outline-primary btn-sm"
                                               href="${pageContext.request.contextPath}/submissions?problemId=${problem.id}">
                                                Refresh
                                            </a>
                                        </div>
                                        <c:choose>
                                            <c:when test="${empty submissions}">
                                                <div class="alert alert-secondary mb-0">No submissions yet for this problem.</div>
                                            </c:when>
                                            <c:otherwise>
                                                <div class="table-responsive">
                                                    <table class="table table-striped align-middle">
                                                        <thead class="table-light">
                                                        <tr>
                                                            <th>Status</th>
                                                            <th>Language</th>
                                                            <th>Execution Time</th>
                                                            <th>Passed</th>
                                                            <th>Submitted At</th>
                                                            <th class="text-end">Action</th>
                                                        </tr>
                                                        </thead>
                                                        <tbody>
                                                        <c:forEach var="submissionItem" items="${submissions}">
                                                            <tr>
                                                                <td>
                                                                    <span class="badge ${submissionItem.status == 'ACCEPTED' ? 'text-bg-success'
                                                                            : submissionItem.status == 'WRONG' ? 'text-bg-danger'
                                                                            : submissionItem.status == 'TIME_LIMIT_EXCEEDED' ? 'text-bg-warning'
                                                                            : submissionItem.status == 'RUNTIME_ERROR' ? 'text-bg-danger'
                                                                            : submissionItem.status == 'COMPILATION_ERROR' ? 'text-bg-dark'
                                                                            : submissionItem.status == 'PENDING' ? 'text-bg-info'
                                                                            : 'text-bg-secondary'}">${submissionItem.status}</span>
                                                                </td>
                                                                <td>${submissionItem.language}</td>
                                                                <td>${empty submissionItem.executionTime ? '-' : submissionItem.executionTime} ms</td>
                                                                <td>${empty submissionItem.passedCount ? '-' : submissionItem.passedCount}/${empty submissionItem.totalCount ? '-' : submissionItem.totalCount}</td>
                                                                <td>${submissionItem.createdAt}</td>
                                                                <td class="text-end">
                                                                    <button type="button"
                                                                            class="btn btn-sm btn-outline-primary"
                                                                            data-bs-toggle="modal"
                                                                            data-bs-target="#submissionModal-${submissionItem.id}">
                                                                        View
                                                                    </button>
                                                                </td>
                                                            </tr>
                                                        </c:forEach>
                                                        </tbody>
                                                    </table>
                                                </div>
                                                <c:forEach var="submissionItem" items="${submissions}">
                                                    <div class="modal fade" id="submissionModal-${submissionItem.id}" tabindex="-1" aria-hidden="true">
                                                        <div class="modal-dialog modal-lg modal-dialog-scrollable">
                                                            <div class="modal-content">
                                                                <div class="modal-header">
                                                                    <h5 class="modal-title">Submission #${submissionItem.id}</h5>
                                                                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                                                </div>
                                                                <div class="modal-body">
                                                                    <p class="mb-2"><strong>Status:</strong> ${submissionItem.status}</p>
                                                                    <p class="mb-2"><strong>Language:</strong> ${submissionItem.language}</p>
                                                                    <p class="mb-2"><strong>Execution Time:</strong> ${empty submissionItem.executionTime ? '-' : submissionItem.executionTime} ms</p>
                                                                    <p class="mb-2"><strong>Passed:</strong> ${empty submissionItem.passedCount ? '-' : submissionItem.passedCount}/${empty submissionItem.totalCount ? '-' : submissionItem.totalCount}</p>
                                                                    <p class="mb-1"><strong>Code:</strong></p>
                                                                    <pre class="bg-light border rounded p-3 mb-3 section-pre"><code>${submissionItem.code}</code></pre>
                                                                    <p class="mb-1"><strong>Output:</strong></p>
                                                                    <pre class="bg-light border rounded p-3 mb-3 section-pre"><code>${empty submissionItem.output ? '-' : submissionItem.output}</code></pre>
                                                                    <p class="mb-1"><strong>Error:</strong></p>
                                                                    <pre class="bg-light border rounded p-3 mb-0 section-pre"><code>${empty submissionItem.errorMessage ? '-' : submissionItem.errorMessage}</code></pre>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </c:forEach>
                                            </c:otherwise>
                                        </c:choose>
                                    </section>
                                </div>
                            </div>
                        </div>

                        <div class="col-12 col-lg-6 editor-column-divider problem-detail-column">
                            <section class="editor-pane ps-lg-3 d-flex flex-column">
                                <form id="submitForm" method="post" action="${pageContext.request.contextPath}/submit" class="d-flex flex-column h-100">
                                    <input type="hidden" name="problemId" value="${problem.id}">

                                    <div class="alert alert-info mb-3" role="alert">
                                        Read input from standard input (stdin) and print output to standard output (stdout).
                                    </div>

                                    <div class="card mb-3">
                                        <div class="card-body py-3">
                                            <h2 class="h6 fw-semibold mb-2">Input Format</h2>
                                            <pre class="bg-light border rounded p-2 mb-0 section-pre">${not empty problem.inputFormat ? fn:trim(problem.inputFormat) : 'Not specified.'}</pre>
                                        </div>
                                    </div>

                                    <div class="mb-3">
                                        <div class="d-flex justify-content-between align-items-center mb-1">
                                            <label for="language" class="form-label fw-semibold mb-0">Language</label>
                                            <button type="button" id="editorThemeToggle" class="btn btn-outline-secondary btn-sm">
                                                Light Mode
                                            </button>
                                        </div>
                                        <select id="language" name="language" class="form-select">
                                            <option value="C">C</option>
                                            <option value="CPP">C++</option>
                                            <option value="JAVA" selected>Java</option>
                                            <option value="PYTHON">Python</option>
                                        </select>
                                    </div>

                                    <div class="mb-3 flex-grow-1 d-flex flex-column editor-workspace">
                                        <label for="editor" class="form-label fw-semibold">Code Editor</label>
                                        <div id="editor" class="border rounded editor-monaco"></div>
                                        <input type="hidden" id="code" name="code">
                                    </div>

                                    <div class="mb-3">
                                        <label for="customInput" class="form-label fw-semibold">Custom Input (optional)</label>
                                        <textarea id="customInput" class="form-control" rows="4" placeholder="Provide stdin for Run. Use empty input or NA for no input."></textarea>
                                    </div>

                                    <div class="d-flex gap-2 mt-auto">
                                        <button type="button" id="runButton" class="btn btn-outline-secondary">Run</button>
                                        <button type="submit" id="submitButton" name="action" value="submit" class="btn btn-primary">Submit</button>
                                    </div>
                                </form>

                                <div class="card mt-3">
                                    <div class="card-body">
                                        <h2 class="h6 fw-semibold mb-3">Output / Result</h2>
                                        <div class="d-flex align-items-center gap-2 mb-3">
                                            <strong class="mb-0">Status:</strong>
                                            <span id="resultStatusBadge" class="badge text-bg-secondary">${empty submissionStatus ? '-' : submissionStatus}</span>
                                        </div>

                                        <p class="mb-2"><strong>Execution Time:</strong> <span id="resultExecutionTime">${empty submissionExecutionTime ? '-' : submissionExecutionTime}</span> ms</p>
                                        <p class="mb-2"><strong>Passed:</strong> <span id="resultPassedCount">${empty submissionPassedCount ? '-' : submissionPassedCount}</span>/<span id="resultTotalCount">${empty submissionTotalCount ? '-' : submissionTotalCount}</span></p>

                                        <p class="mb-2"><strong>Output:</strong></p>
                                        <pre class="bg-light border rounded p-3 mb-3 section-pre"><code id="resultOutput">${empty submissionOutput ? '' : submissionOutput}</code></pre>

                                        <p class="mb-2"><strong>Error:</strong></p>
                                        <pre class="bg-light border rounded p-3 mb-0 section-pre"><code id="resultError">${empty submissionError ? '' : submissionError}</code></pre>

                                        <div id="failedCaseContainer" class="mt-3 d-none">
                                            <h3 class="h6 fw-semibold mb-2">First Failed Sample Test Case</h3>
                                            <p class="mb-1"><strong>Input:</strong></p>
                                            <pre class="bg-light border rounded p-2 mb-2 section-pre"><code id="failedInput"></code></pre>
                                            <p class="mb-1"><strong>Expected Output:</strong></p>
                                            <pre class="bg-light border rounded p-2 mb-2 section-pre"><code id="failedExpectedOutput"></code></pre>
                                            <p class="mb-1"><strong>Actual Output:</strong></p>
                                            <pre class="bg-light border rounded p-2 mb-0 section-pre"><code id="failedActualOutput"></code></pre>
                                        </div>
                                    </div>
                                </div>
                            </section>
                        </div>
                    </div>
                </div>
            </article>
        </c:if>
    </section>
</main>

<script src="https://cdnjs.cloudflare.com/ajax/libs/monaco-editor/0.52.2/min/vs/loader.min.js"></script>
<script>
    (function () {
        const languageSelect = document.getElementById('language');
        const codeInput = document.getElementById('code');
        const editorContainer = document.getElementById('editor');
        // const submitForm = document.querySelector('form[action$="/submit"]');
        const submitForm = document.getElementById('submitForm');
        const themeToggleButton = document.getElementById('editorThemeToggle');
        const runButton = document.getElementById('runButton');
        const submitButton = document.getElementById('submitButton');
        const customInput = document.getElementById('customInput');
        const resultStatusBadge = document.getElementById('resultStatusBadge');
        const resultExecutionTime = document.getElementById('resultExecutionTime');
        const resultOutput = document.getElementById('resultOutput');
        const resultError = document.getElementById('resultError');
        const resultPassedCount = document.getElementById('resultPassedCount');
        const resultTotalCount = document.getElementById('resultTotalCount');
        const failedCaseContainer = document.getElementById('failedCaseContainer');
        const failedInput = document.getElementById('failedInput');
        const failedExpectedOutput = document.getElementById('failedExpectedOutput');
        const failedActualOutput = document.getElementById('failedActualOutput');
        const problemIdInput = submitForm.querySelector('input[name="problemId"]');
        const problemTabButton = document.getElementById('problem-tab');
        const submissionsTabButton = document.getElementById('submissions-tab');
        const problemPane = document.getElementById('problem-pane');
        const submissionsPane = document.getElementById('submissions-pane');
        if (!languageSelect || !codeInput || !editorContainer || !submitForm || !themeToggleButton
                || !runButton || !submitButton || !customInput
                || !resultStatusBadge || !resultExecutionTime || !resultOutput || !resultError
                || !resultPassedCount || !resultTotalCount || !failedCaseContainer
                || !failedInput || !failedExpectedOutput || !failedActualOutput || !problemIdInput
                || !problemTabButton || !submissionsTabButton || !problemPane || !submissionsPane
                || typeof require === 'undefined') {
            return;
        }

        const templates = {
            C: `#include <stdio.h>

int main() {
    int n;
    if (scanf("%d", &n) != 1) {
        return 0;
    }

    // read remaining input

    // logic

    // print output
    return 0;
}
`,
            CPP: `#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    if (!(cin >> n)) {
        return 0;
    }

    // read remaining input

    // logic

    // print output
    return 0;
}
`,
            JAVA: `import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        if (!sc.hasNextInt()) {
            return;
        }
        int n = sc.nextInt();

        // read remaining input

        // logic

        // print output
    }
}
`,
            PYTHON: `import sys

def main():
    data = sys.stdin.read().strip().split()
    if not data:
        return

    n = int(data[0])

    # read remaining input

    # logic

    # print output

if __name__ == "__main__":
    main()
`
        };

        const monacoLanguages = {
            C: 'c',
            CPP: 'cpp',
            JAVA: 'java',
            PYTHON: 'python'
        };

        require.config({
            paths: {
                vs: 'https://cdnjs.cloudflare.com/ajax/libs/monaco-editor/0.52.2/min/vs'
            }
        });

        require(['vs/editor/editor.main'], function () {
            const defaultLanguage = languageSelect.value || 'JAVA';
            let isDarkTheme = true;
            const editor = monaco.editor.create(editorContainer, {
                value: templates[defaultLanguage] || '',
                language: monacoLanguages[defaultLanguage] || 'java',
                theme: 'vs-dark',
                automaticLayout: true,
                minimap: {enabled: false}
            });

            languageSelect.addEventListener('change', function () {
                const selectedLanguage = this.value;
                const model = editor.getModel();
                if (model) {
                    monaco.editor.setModelLanguage(model, monacoLanguages[selectedLanguage] || 'java');
                }
                editor.setValue(templates[selectedLanguage] || '');
            });

            submitForm.addEventListener('submit', async function (event) {
                event.preventDefault();
                const code = editor.getValue();
                const language = languageSelect.value;
                codeInput.value = code;
                lockButtons('Submitting...', true);
                try {
                    const body = new URLSearchParams();
                    body.set('code', code);
                    body.set('language', language);
                    body.set('problemId', problemIdInput.value);

                    const response = await fetch('${pageContext.request.contextPath}/submit', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                        },
                        body: body.toString()
                    });
                    const payload = await response.json();
                    if (!response.ok) {
                        throw new Error(payload.error || 'Unable to process submission right now.');
                    }
                    applyResult(payload);
                } catch (error) {
                    applyResult({
                        status: 'ERROR',
                        output: '',
                        error: error.message || 'Unable to process submission right now.',
                        executionTime: '-',
                        passedCount: 0,
                        totalCount: 0
                    });
                } finally {
                    unlockButtons();
                }
            });

            themeToggleButton.addEventListener('click', function () {
                isDarkTheme = !isDarkTheme;
                monaco.editor.setTheme(isDarkTheme ? 'vs-dark' : 'vs');
                themeToggleButton.textContent = isDarkTheme ? 'Light Mode' : 'Dark Mode';
            });

            runButton.addEventListener('click', async function () {
                const code = editor.getValue();
                const language = languageSelect.value;
                const input = customInput.value;
                lockButtons('Running...', false);
                try {
                    const body = new URLSearchParams();
                    body.set('code', code);
                    body.set('language', language);
                    body.set('input', input);
                    body.set('problemId', problemIdInput.value);

                    const response = await fetch('${pageContext.request.contextPath}/run', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                        },
                        body: body.toString()
                    });
                    const payload = await response.json();
                    if (!response.ok) {
                        throw new Error(payload.error || 'Unable to run code right now.');
                    }
                    applyResult(payload);
                } catch (error) {
                    applyResult({
                        status: 'ERROR',
                        output: '',
                        error: error.message || 'Unable to run code right now.',
                        executionTime: '-',
                        passedCount: 0,
                        totalCount: 0
                    });
                } finally {
                    unlockButtons();
                }
            });
        });

        function lockButtons(primaryText, submitting) {
            runButton.disabled = true;
            submitButton.disabled = true;
            runButton.textContent = submitting ? 'Run' : primaryText;
            submitButton.textContent = submitting ? primaryText : 'Submit';
        }

        function unlockButtons() {
            runButton.disabled = false;
            submitButton.disabled = false;
            runButton.textContent = 'Run';
            submitButton.textContent = 'Submit';
        }

        function applyResult(payload) {
            const status = payload.status || '-';
            const badgeClass = badgeClassForStatus(status);
            resultStatusBadge.className = 'badge ' + badgeClass;
            resultStatusBadge.textContent = status;
            resultOutput.textContent = payload.output || '';
            resultError.textContent = payload.error || '';
            resultExecutionTime.textContent = payload.executionTime === undefined || payload.executionTime === null ? '-' : payload.executionTime;
            resultPassedCount.textContent = payload.passedCount === undefined || payload.passedCount === null ? '-' : payload.passedCount;
            resultTotalCount.textContent = payload.totalCount === undefined || payload.totalCount === null ? '-' : payload.totalCount;

            const hasFailedCase = !!(payload.failedInput || payload.failedExpectedOutput || payload.failedActualOutput);
            if (hasFailedCase) {
                failedCaseContainer.classList.remove('d-none');
                failedInput.textContent = payload.failedInput || '';
                failedExpectedOutput.textContent = payload.failedExpectedOutput || '';
                failedActualOutput.textContent = payload.failedActualOutput || '';
            } else {
                failedCaseContainer.classList.add('d-none');
                failedInput.textContent = '';
                failedExpectedOutput.textContent = '';
                failedActualOutput.textContent = '';
            }
        }

        function setLeftTab(tabName) {
            const showSubmissions = tabName === 'submissions';
            problemTabButton.classList.toggle('active', !showSubmissions);
            submissionsTabButton.classList.toggle('active', showSubmissions);
            problemPane.classList.toggle('show', !showSubmissions);
            problemPane.classList.toggle('active', !showSubmissions);
            submissionsPane.classList.toggle('show', showSubmissions);
            submissionsPane.classList.toggle('active', showSubmissions);
        }

        function badgeClassForStatus(status) {
            switch (status) {
                case 'ACCEPTED':
                    return 'text-bg-success';
                case 'WRONG':
                    return 'text-bg-danger';
                case 'TLE':
                case 'TIME_LIMIT_EXCEEDED':
                    return 'text-bg-warning';
                case 'RUNTIME_ERROR':
                    return 'text-bg-danger';
                case 'COMPILATION_ERROR':
                    return 'text-bg-dark';
                case 'ERROR':
                    return 'text-bg-dark';
                default:
                    return 'text-bg-secondary';
            }
        }

        applyResult({
            status: resultStatusBadge.textContent.trim(),
            output: resultOutput.textContent,
            error: resultError.textContent,
            executionTime: resultExecutionTime.textContent.trim(),
            passedCount: resultPassedCount.textContent.trim(),
            totalCount: resultTotalCount.textContent.trim()
        });

        problemTabButton.addEventListener('click', function (event) {
            event.preventDefault();
            setLeftTab('problem');
        });
        submissionsTabButton.addEventListener('click', function (event) {
            event.preventDefault();
            setLeftTab('submissions');
        });
    })();
</script>

<%@ include file="components/footer.jsp" %>
