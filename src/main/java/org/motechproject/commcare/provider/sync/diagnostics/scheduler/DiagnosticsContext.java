package org.motechproject.commcare.provider.sync.diagnostics.scheduler;

import java.util.HashMap;

public class DiagnosticsContext {
    public HashMap<String, Object> context = new HashMap<>();

    public void set(String key, Object value) {
        context.put(key, value);
    }

    public Object get(String key) {
        return context.get(key);
    }
}
