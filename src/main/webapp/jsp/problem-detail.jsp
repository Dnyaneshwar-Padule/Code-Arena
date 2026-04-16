<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Problem Detail | CP Portal" scope="request"/>
<%@ include file="components/header.jsp" %>

<main class="flex-grow-1 d-flex flex-column py-4 bg-light">
    <section class="container-fluid flex-grow-1 d-flex flex-column px-3 px-md-4">
        <a class="btn btn-outline-secondary btn-sm mb-4 align-self-start" href="${pageContext.request.contextPath}/problems">Back to Problems</a>

        <c:if test="${not empty error}">
            <div class="alert alert-danger" role="alert">${error}</div>
        </c:if>

        <c:if test="${not empty problem}">
            <div class="row g-4 flex-grow-1">
                <div class="col-12 col-lg-6 d-flex flex-column">
                    <article class="card border-0 shadow-sm flex-grow-1 d-flex flex-column">
                        <div class="card-body p-3 p-md-4 d-flex flex-column flex-grow-1">
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

                                <div class="tab-content flex-grow-1 d-flex flex-column">
                                <div class="tab-pane fade ${activeTab == 'submissions' ? '' : 'show active'}"
                                     id="problem-pane"
                                     role="tabpanel">
                                    <!-- 
                                        <section class="problem-pane pe-lg-3 h-100 overflow-auto d-flex flex-column">
                                    -->
                                    <section class="problem-pane pe-lg-3 h-100 d-flex flex-column">
                                        <div class="problem-pane-scroll">     
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
                                            <section class="sample-testcases-section d-flex flex-column">
                                                <h2 class="h5 fw-semibold mb-3 flex-shrink-0">Sample Test Cases</h2>

                                                <div class="sample-testcases-scroll">
                                                    <c:choose>
                                                        <c:when test="${empty sampleTestCases}">
                                                            <p class="text-secondary mb-0">No sample test cases available for this problem.</p>
                                                        </c:when>

                                                        <c:otherwise>
                                                            <div class="d-flex flex-column gap-3">
                                                                <c:forEach var="testCase" items="${sampleTestCases}" varStatus="status">
                                                                    <div class="border rounded-3 p-3 bg-white">
                                                                        <h3 class="h6 fw-bold mb-3">Sample ${status.index + 1}</h3>

                                                                        <p class="fw-semibold mb-1">Input</p>
                                                                        <pre class="bg-light border rounded p-3 mb-3 section-pre"><code>${testCase.input}</code></pre>

                                                                        <p class="fw-semibold mb-1">Output</p>
                                                                        <pre class="bg-light border rounded p-3 mb-0 section-pre"><code>${testCase.expectedOutput}</code></pre>
                                                                    </div>
                                                                </c:forEach>
                                                            </div>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </section>
                                        </div>
                                    </section>
                                </div>

                                <div class="tab-pane fade ${activeTab == 'submissions' ? 'show active' : ''}"
                                    id="submissions-pane"
                                    role="tabpanel">
                                    <section class="submission-pane h-100 d-flex flex-column">
                                        <div class="submission-pane-scroll">
                                            <div class="submission-container">
                                                <div class="submission-history">
                                                    <%@ include file="components/submission-table.jsp" %>
                                                </div>

                                                <div class="submission-details">
                                                    <%@ include file="components/submission-details.jsp" %>
                                                </div>
                                            </div>
                                        </div>
                                    </section>
                                </div>
                            </div>
                        </div>
                    </article>
                </div>

                <div class="col-12 col-lg-6 d-flex flex-column">
                    <article class="card border-0 shadow-sm flex-grow-1 d-flex flex-column">
                        <div class="card-body p-3 p-md-4 d-flex flex-column flex-grow-1 editor-column-divider">
                            <section class="editor-pane ps-lg-3 d-flex flex-column flex-grow-1">
                                <form id="submitForm" method="post" action="${pageContext.request.contextPath}/submit" class="d-flex flex-column h-100">
                                    <input type="hidden" name="problemId" value="${problem.id}">
                                    <input type="hidden" name="contestId" value="${contestId}">

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
                    </article>
                </div>
            </div>
        </c:if>
    </section>
</main>


<style>
/* ---------- Main Height Chain ---------- */

main,
.container-fluid,
.row.g-4.flex-grow-1,
.col-12.col-lg-6.d-flex.flex-column,
.card.border-0.shadow-sm.flex-grow-1.d-flex.flex-column,
.card-body.p-3.p-md-4.d-flex.flex-column.flex-grow-1 {
    min-height: 0;
}

/* ---------- Tab Containers ---------- */

.tab-content {
    display: flex;
    flex-direction: column;
    flex: 1 1 auto;
    min-height: 0;
    height: 0;
}

#problem-pane,
#submissions-pane {
    flex: 1 1 auto;
    min-height: 0;
    overflow: hidden;
}

#problem-pane.show.active,
#submissions-pane.show.active {
    display: flex !important;
    flex-direction: column;
    flex: 1 1 auto;
    min-height: 0;
    height: 0;
    overflow: hidden;
}

/* ---------- Description Tab ---------- */

.problem-pane {
    display: flex;
    flex-direction: column;
    flex: 1 1 auto;
    min-height: 0;
    height: 100%;
    overflow: hidden;
}

.problem-pane-scroll {
    flex: 1 1 auto;
    min-height: 0;
    height: 0;
    overflow-y: auto;
    overflow-x: hidden;
    padding-right: 0.5rem;
}

/* ---------- Sample Test Cases ---------- */

.sample-testcases-section {
    display: flex;
    flex-direction: column;
    flex: 1 1 auto;
    min-height: 0;
    overflow: hidden;
}

.sample-testcases-scroll {
    flex: 1 1 80%;
    min-height: 0;
    max-height: 80%;
    overflow-y: auto;
    overflow-x: hidden;
    padding-right: 0.25rem;
}

/* ---------- Submission Tab ---------- */

.submission-pane {
    display: flex;
    flex-direction: column;
    flex: 1;
    min-height: 0;
    overflow: hidden;
}

.submission-pane-scroll {
    flex: 1;
    min-height: 0;
    overflow-y: auto;
    overflow-x: hidden;
    padding-right: 0.5rem;
}

.submission-container {
    display: flex;
    flex-direction: column;
    gap: 1rem;
}

.submission-history {
    display: flex;
    flex-direction: column;
    min-height: 300px;
}

.submission-table-wrap {
    flex: 1;
    min-height: 0;
    max-height: 300px;
    overflow-y: auto;
    overflow-x: auto;
}

.submission-details {
    display: flex;
    flex-direction: column;
    min-height: 300px;
}

.submission-details-body {
    flex: 1;
    min-height: 0;
}

/* Keep submission details container unchanged */
.submission-details-body {
    flex: 1 1 auto;
    min-height: 0;
}

.submission-split-top,
.submission-split-bottom {
    display: flex;
    flex-direction: column;
    flex: 1 1 auto;
    min-height: 0;
}

/* ---------- Utility Styles ---------- */

.submission-row {
    cursor: pointer;
}

.submission-pre,
.section-pre {
    white-space: pre-wrap;
    word-break: break-word;
    overflow-x: auto;
}

.problem-pane-scroll,
.sample-testcases-scroll,
.submission-pane-scroll,
.submission-table-wrap {
    scrollbar-width: thin;
}
</style>
<script src="https://cdnjs.cloudflare.com/ajax/libs/monaco-editor/0.52.2/min/vs/loader.min.js"></script>
<script src="${pageContext.request.contextPath}/static/js/submissions.js"></script>
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
        const contestIdInput = submitForm.querySelector('input[name="contestId"]');
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

        const submissionsUI = window.SubmissionsUI
            ? window.SubmissionsUI.init({
                contextPath: '${pageContext.request.contextPath}',
                problemId: problemIdInput.value
            })
            : null;

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
                    if (contestIdInput && contestIdInput.value) {
                        body.set('contestId', contestIdInput.value);
                    }

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
                    if (submissionsUI) {
                        await submissionsUI.loadSubmissions();
                    }
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
            if (submissionsUI) {
                submissionsUI.loadSubmissions();
            }
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
