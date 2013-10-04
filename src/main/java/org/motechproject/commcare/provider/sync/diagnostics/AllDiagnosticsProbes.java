package org.motechproject.commcare.provider.sync.diagnostics;

import org.motechproject.commcare.provider.sync.diagnostics.scheduler.DiagnosticsContext;

import java.io.Writer;
import java.util.List;

public class AllDiagnosticsProbes {
    private List<DiagnosticsProbe> diagnosticsProbes;

    public AllDiagnosticsProbes(List<DiagnosticsProbe> diagnosticsProbes) {
        this.diagnosticsProbes = diagnosticsProbes;
    }

    public List<DiagnosticsProbe> getAllProbes() {
        return diagnosticsProbes;
    }

    public DiagnosticsStatus diagnose(DiagnosticsContext diagnosticsContext, Writer writer) {


        DiagnosticsStatus finalStatus = DiagnosticsStatus.PASS;
        DiagnosticsLogger summaryLogger = new StringDiagnosticsLogger();
        DiagnosticsLogger detailsLogger = new StringDiagnosticsLogger();

        detailsLogger.log("Details: ");

        summaryLogger.log("Summary: ");
        summaryLogger.log("=========================================================================");
        for (DiagnosticsProbe diagnosticsProbe : diagnosticsProbes) {
            DiagnosticsStatus diagnosticsStatus = runProbe(diagnosticsProbe, detailsLogger, diagnosticsContext);
            detailsLogger.log("\n");

            summaryLogger.log(String.format("%s: %s", diagnosticsProbe.getName(), diagnosticsStatus));

            if(diagnosticsStatus != DiagnosticsStatus.PASS) {
                finalStatus = diagnosticsStatus.FAIL;
            }
        }
        summaryLogger.log("-------------------------------------------------------------------------");
        summaryLogger.log(String.format("Final Result: %s", finalStatus));
        summaryLogger.log("=========================================================================");

        DiagnosticsLogger diagnosticsLogger = new WriterDiagnosticsLogger(writer);
        diagnosticsLogger.log(summaryLogger.toString());
        diagnosticsLogger.log("\n");
        diagnosticsLogger.log(detailsLogger.toString());
        diagnosticsLogger.log("\n");

        return finalStatus;
    }

    private DiagnosticsStatus runProbe(DiagnosticsProbe probe, DiagnosticsLogger diagnosticsLogger, DiagnosticsContext diagnosticsContext) {
        diagnosticsLogger.log("=========================================================================");
        diagnosticsLogger.log(String.format("Probe: %s", probe.getName()));
        diagnosticsLogger.log("-------------------------------------------------------------------------");
        DiagnosticsStatus diagnosticsStatus;
        try {
            diagnosticsStatus = probe.diagnose(diagnosticsContext, diagnosticsLogger);
        } catch (Exception e) {
            diagnosticsLogger.log(e);
            diagnosticsStatus = DiagnosticsStatus.FAIL;
        }
        diagnosticsLogger.log("------------------------------------------------------------------------");
        diagnosticsLogger.log(String.format("Result: %s", diagnosticsStatus));
        diagnosticsLogger.log("=========================================================================");
        return diagnosticsStatus;
    }
}
