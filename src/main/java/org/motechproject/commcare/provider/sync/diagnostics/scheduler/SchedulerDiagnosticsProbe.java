package org.motechproject.commcare.provider.sync.diagnostics.scheduler;

import org.motechproject.commcare.provider.sync.diagnostics.DiagnosticsLogger;
import org.motechproject.commcare.provider.sync.diagnostics.DiagnosticsProbe;
import org.motechproject.commcare.provider.sync.diagnostics.DiagnosticsStatus;
import org.quartz.SchedulerException;

import java.util.List;

public class SchedulerDiagnosticsProbe implements DiagnosticsProbe {

    private final SchedulerDiagnosticsService schedulerDiagnosticsService;

    public static final String SCHEDULE_LIST_TO_PROBE = "SCHEDULE_LIST_TO_PROBE";

    public SchedulerDiagnosticsProbe(SchedulerDiagnosticsService schedulerDiagnosticsService) {
        this.schedulerDiagnosticsService = schedulerDiagnosticsService;
    }

    @Override
    public DiagnosticsStatus diagnose(DiagnosticsContext diagnosticsContext, DiagnosticsLogger diagnosticsLogger) {
        try {
            return schedulerDiagnosticsService.diagnoseSchedules((List<String>) diagnosticsContext.get(SCHEDULE_LIST_TO_PROBE), diagnosticsLogger);
        } catch (SchedulerException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public String getName() {
        return "Scheduler Probe";
    }
}
