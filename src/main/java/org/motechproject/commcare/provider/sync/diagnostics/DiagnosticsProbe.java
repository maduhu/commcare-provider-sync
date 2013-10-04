package org.motechproject.commcare.provider.sync.diagnostics;

import org.motechproject.commcare.provider.sync.diagnostics.scheduler.DiagnosticsContext;

public interface DiagnosticsProbe {
    DiagnosticsStatus diagnose(DiagnosticsContext diagnosticsContext, DiagnosticsLogger diagnosticsLogger);

    String getName();
}
