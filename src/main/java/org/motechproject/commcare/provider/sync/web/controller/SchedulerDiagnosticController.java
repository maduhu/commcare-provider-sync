package org.motechproject.commcare.provider.sync.web.controller;

import org.motechproject.commcare.provider.sync.diagnostics.DiagnosticsResult;
import org.motechproject.commcare.provider.sync.diagnostics.DiagnosticsStatus;
import org.motechproject.commcare.provider.sync.diagnostics.SchedulerDiagnosticService;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/web-api")
public class SchedulerDiagnosticController {

    private SchedulerDiagnosticService schedulerDiagnosticService;

    @Autowired
    public SchedulerDiagnosticController(SchedulerDiagnosticService schedulerDiagnosticService) {
        this.schedulerDiagnosticService = schedulerDiagnosticService;
    }

    @RequestMapping(value = "/diagnostics/scheduler", method = RequestMethod.GET)
    @ResponseBody
    public String commcareSyncSchedulerStatus() throws SchedulerException {
        DiagnosticsResult diagnosticsResult = schedulerDiagnosticService.diagnoseAllSchedules();
        return diagnosticsResult.getStatus().equals(DiagnosticsStatus.PASS)
                ? "SUCCESS"
                : String.format("FAILURE\n%s", diagnosticsResult.getMessage());
    }
}