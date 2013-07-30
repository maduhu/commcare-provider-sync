package org.motechproject.commcare.provider.sync.diagnostics;

import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SchedulerDiagnosticController {

    private SchedulerDiagnosticService schedulerDiagnosticService;

    @Autowired
    public SchedulerDiagnosticController(SchedulerDiagnosticService schedulerDiagnosticService) {
        this.schedulerDiagnosticService = schedulerDiagnosticService;
    }

    @RequestMapping(value = "/commcare/diagnostics/scheduler", method = RequestMethod.GET)
    @ResponseBody
    public String commcareSyncSchedulerStatus() throws SchedulerException {
        DiagnosticsResult diagnosticsResult = schedulerDiagnosticService.diagnoseAllSchedules();
        return diagnosticsResult.getStatus().equals(DiagnosticsStatus.PASS)
                ? "SUCCESS"
                : String.format("FAILURE\n%s", diagnosticsResult.getMessage());
    }
}