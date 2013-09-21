package org.motechproject.commcare.provider.sync.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.commcare.provider.sync.diagnostics.DiagnosticsResult;
import org.motechproject.commcare.provider.sync.diagnostics.DiagnosticsStatus;
import org.motechproject.commcare.provider.sync.diagnostics.SchedulerDiagnosticService;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class DiagnosticControllerTest {
    private DiagnosticController diagnosticController;

    @Mock
    private SchedulerDiagnosticService schedulerDiagnosticService;

    @Mock
    private HttpServletResponse httpServletResponse;
    @Mock
    private ServletOutputStream servletOutputStream;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldReturnSuccessWhenObdSchedulesRunAsExpected() throws Exception {
        diagnosticController = new DiagnosticController(schedulerDiagnosticService);
        when(schedulerDiagnosticService.diagnoseSchedules(anyList())).thenReturn(new DiagnosticsResult(DiagnosticsStatus.PASS, "message"));

        String status = diagnosticController.diagnoseScheduler();

        assertEquals("SUCCESS", status);
    }

    @Test
    public void shouldReturnFailureWhenObdSchedulesDoNotRunAsExpected() throws Exception {
        diagnosticController = new DiagnosticController(schedulerDiagnosticService);
        when(schedulerDiagnosticService.diagnoseSchedules(anyList())).thenReturn(new DiagnosticsResult(DiagnosticsStatus.FAIL, "Commcare sync Schedulers not running"));

        String status = diagnosticController.diagnoseScheduler();

        assertEquals("FAILURE\nCommcare sync Schedulers not running", status);
    }
}