(function () {
    function init(config) {
        if (!config || !config.contextPath || !config.problemId) {
            return null;
        }

        var elements = {
            refreshButton: document.getElementById('refreshSubmissionsButton'),
            loading: document.getElementById('submissionsLoading'),
            error: document.getElementById('submissionsError'),
            empty: document.getElementById('submissionsEmptyState'),
            table: document.getElementById('submissionsTable'),
            tableBody: document.getElementById('submissionsTableBody'),
            detailsLoading: document.getElementById('submissionDetailsLoading'),
            detailsEmpty: document.getElementById('submissionDetailsEmpty'),
            detailsCard: document.getElementById('submissionDetailsCard'),
            detailsStatusBadge: document.getElementById('detailsStatusBadge'),
            detailsLanguage: document.getElementById('detailsLanguage'),
            detailsExecutionTime: document.getElementById('detailsExecutionTime'),
            detailsCreatedAt: document.getElementById('detailsCreatedAt'),
            detailsCode: document.getElementById('detailsCode'),
            detailsOutput: document.getElementById('detailsOutput'),
            detailsError: document.getElementById('detailsError')
        };

        if (!elements.refreshButton || !elements.tableBody || !elements.table) {
            return null;
        }

        var state = {
            selectedSubmissionId: null
        };

        elements.refreshButton.addEventListener('click', function () {
            loadSubmissions();
        });

        async function loadSubmissions() {
            setTableLoading(true);
            clearTableError();
            try {
                var response = await fetch(
                    config.contextPath + '/submissions?problemId=' + encodeURIComponent(config.problemId),
                    {method: 'GET'}
                );
                var payload = await response.json();
                if (!response.ok) {
                    throw new Error(payload.error || 'Unable to load submissions right now.');
                }
                renderSubmissionTable(payload.submissions || []);
            } catch (error) {
                renderSubmissionTable([]);
                showTableError(error.message || 'Unable to load submissions right now.');
            } finally {
                setTableLoading(false);
            }
        }

        function renderSubmissionTable(submissions) {
            elements.tableBody.innerHTML = '';
            if (!Array.isArray(submissions) || submissions.length === 0) {
                elements.empty.classList.remove('d-none');
                elements.table.classList.add('d-none');
                clearDetails();
                return;
            }

            elements.empty.classList.add('d-none');
            elements.table.classList.remove('d-none');

            submissions.forEach(function (submission) {
                var row = document.createElement('tr');
                row.dataset.submissionId = String(submission.id);
                row.classList.add('submission-row');
                if (state.selectedSubmissionId === submission.id) {
                    row.classList.add('table-primary');
                }
                row.innerHTML = ''
                    + '<td><span class="badge ' + badgeClassForStatus(submission.status || '-') + '">'
                    + escapeHtml(submission.status || '-') + '</span></td>'
                    + '<td>' + escapeHtml(submission.language || '-') + '</td>'
                    + '<td>' + formatExecutionTime(submission.executionTime) + '</td>'
                    + '<td>' + escapeHtml(submission.createdAt || '-') + '</td>';
                row.addEventListener('click', function () {
                    handleRowClick(submission.id);
                });
                elements.tableBody.appendChild(row);
            });

            if (state.selectedSubmissionId === null && submissions.length > 0) {
                handleRowClick(submissions[0].id);
            }
        }

        async function handleRowClick(submissionId) {
            state.selectedSubmissionId = submissionId;
            highlightSelectedRow();
            await loadSubmissionDetails(submissionId);
        }

        async function loadSubmissionDetails(submissionId) {
            setDetailsLoading(true);
            try {
                var response = await fetch(
                    config.contextPath + '/submission?id=' + encodeURIComponent(submissionId),
                    {method: 'GET'}
                );
                var payload = await response.json();
                if (!response.ok) {
                    throw new Error(payload.error || 'Unable to load submission details right now.');
                }
                updateDetailsSection(payload);
            } catch (error) {
                clearDetails(error.message || 'Unable to load submission details right now.');
            } finally {
                setDetailsLoading(false);
            }
        }

        function updateDetailsSection(payload) {
            elements.detailsCard.classList.remove('d-none');
            elements.detailsEmpty.classList.add('d-none');
            elements.detailsStatusBadge.className = 'badge ' + badgeClassForStatus(payload.status || '-');
            elements.detailsStatusBadge.textContent = payload.status || '-';
            elements.detailsLanguage.textContent = payload.language || '-';
            elements.detailsExecutionTime.textContent = formatExecutionTime(payload.executionTime);
            elements.detailsCreatedAt.textContent = payload.createdAt || '-';
            elements.detailsCode.textContent = payload.code || '';
            elements.detailsOutput.textContent = payload.output || '';
            elements.detailsError.textContent = payload.error || '';
        }

        function clearDetails(message) {
            elements.detailsCard.classList.add('d-none');
            elements.detailsEmpty.classList.remove('d-none');
            elements.detailsEmpty.textContent = message || 'Select a submission to view details.';
            elements.detailsStatusBadge.className = 'badge text-bg-secondary';
            elements.detailsStatusBadge.textContent = '-';
            elements.detailsLanguage.textContent = '-';
            elements.detailsExecutionTime.textContent = '-';
            elements.detailsCreatedAt.textContent = '-';
            elements.detailsCode.textContent = '';
            elements.detailsOutput.textContent = '';
            elements.detailsError.textContent = '';
        }

        function setTableLoading(isLoading) {
            elements.loading.classList.toggle('d-none', !isLoading);
            elements.refreshButton.disabled = isLoading;
        }

        function setDetailsLoading(isLoading) {
            if (!elements.detailsLoading) {
                return;
            }
            elements.detailsLoading.classList.toggle('d-none', !isLoading);
        }

        function clearTableError() {
            elements.error.classList.add('d-none');
            elements.error.textContent = '';
        }

        function showTableError(message) {
            elements.error.classList.remove('d-none');
            elements.error.textContent = message;
        }

        function highlightSelectedRow() {
            var rows = elements.tableBody.querySelectorAll('tr.submission-row');
            rows.forEach(function (row) {
                var isSelected = row.dataset.submissionId === String(state.selectedSubmissionId);
                row.classList.toggle('table-primary', isSelected);
            });
        }

        function formatExecutionTime(value) {
            if (value === undefined || value === null || value === '') {
                return '-';
            }
            return escapeHtml(String(value)) + ' ms';
        }

        function badgeClassForStatus(status) {
            switch (status) {
                case 'ACCEPTED':
                    return 'text-bg-success';
                case 'WRONG':
                    return 'text-bg-danger';
                case 'TIME_LIMIT_EXCEEDED':
                case 'TLE':
                    return 'text-bg-warning';
                case 'RUNTIME_ERROR':
                    return 'text-bg-danger';
                case 'COMPILATION_ERROR':
                    return 'text-bg-dark';
                default:
                    return 'text-bg-secondary';
            }
        }

        function escapeHtml(value) {
            return String(value)
                .replaceAll('&', '&amp;')
                .replaceAll('<', '&lt;')
                .replaceAll('>', '&gt;')
                .replaceAll('"', '&quot;')
                .replaceAll("'", '&#39;');
        }

        return {
            loadSubmissions: loadSubmissions,
            renderSubmissionTable: renderSubmissionTable,
            handleRowClick: handleRowClick,
            loadSubmissionDetails: loadSubmissionDetails,
            updateDetailsSection: updateDetailsSection
        };
    }

    window.SubmissionsUI = {
        init: init
    };
})();
