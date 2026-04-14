<section class="submission-split-bottom pe-lg-3 mt-3">
    <h3 class="h6 fw-semibold mb-3">Submission Details</h3>
    <div id="submissionDetailsLoading" class="alert alert-info mb-3 d-none">Loading submission details...</div>
    <div id="submissionDetailsEmpty" class="alert alert-secondary mb-3">Select a submission to view details.</div>
    <div id="submissionDetailsCard" class="card d-none">
        <div class="card-body submission-details-body">
            <div class="d-flex align-items-center gap-2 mb-3">
                <strong class="mb-0">Status:</strong>
                <span id="detailsStatusBadge" class="badge text-bg-secondary">-</span>
            </div>
            <p class="mb-2"><strong>Language:</strong> <span id="detailsLanguage">-</span></p>
            <p class="mb-2"><strong>Execution Time:</strong> <span id="detailsExecutionTime">-</span></p>
            <p class="mb-2"><strong>Submitted At:</strong> <span id="detailsCreatedAt">-</span></p>
            <p class="mb-1"><strong>Code:</strong></p>
            <pre class="bg-light border rounded p-3 mb-3 section-pre submission-pre"><code id="detailsCode"></code></pre>
            <p class="mb-1"><strong>Output:</strong></p>
            <pre class="bg-light border rounded p-3 mb-3 section-pre submission-pre"><code id="detailsOutput"></code></pre>
            <p class="mb-1"><strong>Error:</strong></p>
            <pre class="bg-light border rounded p-3 mb-0 section-pre submission-pre"><code id="detailsError"></code></pre>
        </div>
    </div>
</section>
