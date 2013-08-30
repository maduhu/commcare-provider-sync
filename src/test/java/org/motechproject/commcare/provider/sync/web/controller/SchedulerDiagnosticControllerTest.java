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

public class SchedulerDiagnosticControllerTest {
    private SchedulerDiagnosticController schedulerDiagnosticController;

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
        schedulerDiagnosticController = new SchedulerDiagnosticController(schedulerDiagnosticService);
        when(schedulerDiagnosticService.diagnoseSchedules(anyList())).thenReturn(new DiagnosticsResult(DiagnosticsStatus.PASS, "message"));

        String status = schedulerDiagnosticController.commcareSyncSchedulerStatus();

        assertEquals("SUCCESS", status);
    }

    @Test
    public void shouldReturnFailureWhenObdSchedulesDoNotRunAsExpected() throws Exception {
        schedulerDiagnosticController = new SchedulerDiagnosticController(schedulerDiagnosticService);
        when(schedulerDiagnosticService.diagnoseSchedules(anyList())).thenReturn(new DiagnosticsResult(DiagnosticsStatus.FAIL, "Commcare sync Schedulers not running"));

        String status = schedulerDiagnosticController.commcareSyncSchedulerStatus();

        assertEquals("FAILURE\nCommcare sync Schedulers not running", status);
    }
}