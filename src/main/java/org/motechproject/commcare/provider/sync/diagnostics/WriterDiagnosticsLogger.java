package org.motechproject.commcare.provider.sync.diagnostics;

import org.apache.commons.lang.exception.ExceptionUtils;

import java.io.IOException;
import java.io.Writer;

public class WriterDiagnosticsLogger implements DiagnosticsLogger {
    private Writer writer;

    public WriterDiagnosticsLogger(Writer writer) {
        setWriter(writer);
    }

    public void setWriter(Writer writer) {
        this.writer = writer;
    }

    public void log(String message) {
        try {
            writer.write(message);
            writer.write("\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void log(Exception e) {
        log("EXCEPTION: " + ExceptionUtils.getFullStackTrace(e));
    }
}
