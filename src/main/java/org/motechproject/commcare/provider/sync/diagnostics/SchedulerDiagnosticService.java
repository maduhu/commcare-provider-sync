package org.motechproject.commcare.provider.sync.diagnostics;

import org.quartz.SchedulerException;

import java.util.List;

public interface SchedulerDiagnosticService {

    public DiagnosticsResult diagnoseSchedules(List<String> schedules) throws SchedulerException;
}
