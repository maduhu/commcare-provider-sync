package org.motechproject.commcare.provider.sync.diagnostics;

import java.io.StringWriter;

public class StringDiagnosticsLogger extends WriterDiagnosticsLogger {
    private StringWriter stringWriter = new StringWriter();

    public StringDiagnosticsLogger() {
        super(null);
        this.setWriter(stringWriter);
    }

    @Override
    public String toString() {
        return stringWriter.toString();
    }
}
