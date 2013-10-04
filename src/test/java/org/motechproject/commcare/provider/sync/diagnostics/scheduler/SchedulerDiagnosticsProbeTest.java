package org.motechproject.commcare.provider.sync.diagnostics.scheduler;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.motechproject.commcare.provider.sync.diagnostics.DiagnosticsLogger;
import org.motechproject.commcare.provider.sync.diagnostics.DiagnosticsStatus;
import org.quartz.SchedulerException;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SchedulerDiagnosticsProbeTest {

    @Mock
    private SchedulerDiagnosticsService schedulerDiagnosticsService;

    @Mock
    private DiagnosticsContext diagnosticsContext;

    @Mock
    private DiagnosticsLogger diagnosticsLogger;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private SchedulerDiagnosticsProbe schedulerProbe;

    @Before
    public void setUp() {
        initMocks(this);
        schedulerProbe = new SchedulerDiagnosticsProbe(schedulerDiagnosticsService);
    }

    @Test
    public void shouldInvokeServiceWithSchedulesToProbe() throws SchedulerException {
        List<String> schedulesToProbe = Arrays.asList("schedule1", "schedule2");
        when(diagnosticsContext.get("SCHEDULE_LIST_TO_PROBE")).thenReturn(schedulesToProbe);

        when(schedulerDiagnosticsService.diagnoseSchedules(schedulesToProbe, diagnosticsLogger)).thenReturn(DiagnosticsStatus.PASS);

        assertEquals(DiagnosticsStatus.PASS, schedulerProbe.diagnose(diagnosticsContext, diagnosticsLogger));

        when(schedulerDiagnosticsService.diagnoseSchedules(schedulesToProbe, diagnosticsLogger)).thenReturn(DiagnosticsStatus.FAIL);

        assertEquals(DiagnosticsStatus.FAIL, schedulerProbe.diagnose(diagnosticsContext, diagnosticsLogger));

        when(schedulerDiagnosticsService.diagnoseSchedules(schedulesToProbe, diagnosticsLogger)).thenReturn(DiagnosticsStatus.UNKNOWN);

        assertEquals(DiagnosticsStatus.UNKNOWN, schedulerProbe.diagnose(diagnosticsContext, diagnosticsLogger));
    }

    @Test
    public void shouldHandleSchedulerException() throws SchedulerException {
        List<String> schedulesToProbe = Arrays.asList("schedule1", "schedule2");
        when(diagnosticsContext.get("SCHEDULE_LIST_TO_PROBE")).thenReturn(schedulesToProbe);

        when(schedulerDiagnosticsService.diagnoseSchedules(schedulesToProbe, diagnosticsLogger)).thenThrow(new SchedulerException("my scheduler exception"));

        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("my scheduler exception");

        schedulerProbe.diagnose(diagnosticsContext, diagnosticsLogger);
    }
}
