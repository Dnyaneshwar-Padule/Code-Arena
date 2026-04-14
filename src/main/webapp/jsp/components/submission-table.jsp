<section class="submission-split-top pe-lg-3">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h2 class="h5 fw-semibold mb-0">Your Submission History</h2>
        <button type="button" id="refreshSubmissionsButton" class="btn btn-outline-primary btn-sm">
            Refresh
        </button>
    </div>
    <div id="submissionsLoading" class="alert alert-info mb-3 d-none">Loading submissions...</div>
    <div id="submissionsError" class="alert alert-danger mb-3 d-none"></div>
    <div id="submissionsEmptyState" class="alert alert-secondary mb-0 d-none">No submissions yet for this problem.</div>
    <div class="submission-table-wrap">
        <table class="table table-striped align-middle mb-0" id="submissionsTable">
            <thead class="table-light">
            <tr>
                <th>Status</th>
                <th>Language</th>
                <th>Execution Time</th>
                <th>Submitted At</th>
            </tr>
            </thead>
            <tbody id="submissionsTableBody"></tbody>
        </table>
    </div>
</section>
