package org.motechproject.commcare.provider.sync.diagnostics.scheduler;

import org.motechproject.commcare.provider.sync.diagnostics.DiagnosticsLogger;
import org.motechproject.commcare.provider.sync.diagnostics.DiagnosticsStatus;
import org.quartz.SchedulerException;

import java.util.List;

public interface SchedulerDiagnosticsService {

    public DiagnosticsStatus diagnoseSchedules(List<String> schedules, DiagnosticsLogger diagnosticsLogger) throws SchedulerException;
}
