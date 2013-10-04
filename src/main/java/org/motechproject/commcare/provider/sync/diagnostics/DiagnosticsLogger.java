package org.motechproject.commcare.provider.sync.diagnostics;

public interface DiagnosticsLogger {
    public void log(String message);

    public void log(Exception e);
}