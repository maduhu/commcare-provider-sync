package org.motechproject.commcare.provider.sync.diagnostics;

import org.junit.Test;
import org.motechproject.commcare.provider.sync.diagnostics.scheduler.DiagnosticsContext;

import java.io.StringWriter;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AllDiagnosticsProbesTest {

    @Test
    public void shouldRunAllProbesAndReturnResult() {
        DiagnosticsProbe probe1 = mock(DiagnosticsProbe.class);
        DiagnosticsProbe probe2 = mock(DiagnosticsProbe.class);
        DiagnosticsProbe probe3 = mock(DiagnosticsProbe.class);

        DiagnosticsContext diagnosticsContext = mock(DiagnosticsContext.class);

        when(probe1.diagnose(eq(diagnosticsContext), any(DiagnosticsLogger.class))).thenReturn(DiagnosticsStatus.PASS);
        when(probe2.diagnose(eq(diagnosticsContext), any(DiagnosticsLogger.class))).thenReturn(DiagnosticsStatus.PASS);
        when(probe3.diagnose(eq(diagnosticsContext), any(DiagnosticsLogger.class))).thenReturn(DiagnosticsStatus.PASS);

        AllDiagnosticsProbes allDiagnosticsProbes = new AllDiagnosticsProbes(Arrays.asList(probe1, probe2, probe3));
        DiagnosticsStatus actualDiagnosticsStatus = allDiagnosticsProbes.diagnose(diagnosticsContext, new StringWriter());

        assertEquals(DiagnosticsStatus.PASS, actualDiagnosticsStatus);

        verify(probe1).diagnose(eq(diagnosticsContext), any(DiagnosticsLogger.class));
        verify(probe2).diagnose(eq(diagnosticsContext), any(DiagnosticsLogger.class));
        verify(probe3).diagnose(eq(diagnosticsContext), any(DiagnosticsLogger.class));
    }

    @Test
    public void shouldFailIfAnyProbeFails() {
        DiagnosticsProbe probe1 = mock(DiagnosticsProbe.class);
        DiagnosticsProbe probe2 = mock(DiagnosticsProbe.class);
        DiagnosticsProbe probe3 = mock(DiagnosticsProbe.class);

        DiagnosticsContext diagnosticsContext = mock(DiagnosticsContext.class);

        when(probe1.diagnose(eq(diagnosticsContext), any(DiagnosticsLogger.class))).thenReturn(DiagnosticsStatus.PASS);
        when(probe2.diagnose(eq(diagnosticsContext), any(DiagnosticsLogger.class))).thenReturn(DiagnosticsStatus.FAIL);
        when(probe3.diagnose(eq(diagnosticsContext), any(DiagnosticsLogger.class))).thenReturn(DiagnosticsStatus.PASS);

        AllDiagnosticsProbes allDiagnosticsProbes = new AllDiagnosticsProbes(Arrays.asList(probe1, probe2, probe3));
        DiagnosticsStatus actualDiagnosticsStatus = allDiagnosticsProbes.diagnose(diagnosticsContext, new StringWriter());

        assertEquals(DiagnosticsStatus.FAIL, actualDiagnosticsStatus);

        verify(probe1).diagnose(eq(diagnosticsContext), any(DiagnosticsLogger.class));
        verify(probe2).diagnose(eq(diagnosticsContext), any(DiagnosticsLogger.class));
        verify(probe3).diagnose(eq(diagnosticsContext), any(DiagnosticsLogger.class));
    }

    @Test
    public void shouldFailIfAnyProbeReturnUnknownStatus() {
        DiagnosticsProbe probe1 = mock(DiagnosticsProbe.class);
        DiagnosticsProbe probe2 = mock(DiagnosticsProbe.class);
        DiagnosticsProbe probe3 = mock(DiagnosticsProbe.class);

        DiagnosticsContext diagnosticsContext = mock(DiagnosticsContext.class);

        when(probe1.diagnose(eq(diagnosticsContext), any(DiagnosticsLogger.class))).thenReturn(DiagnosticsStatus.PASS);
        when(probe2.diagnose(eq(diagnosticsContext), any(DiagnosticsLogger.class))).thenReturn(DiagnosticsStatus.UNKNOWN);
        when(probe3.diagnose(eq(diagnosticsContext), any(DiagnosticsLogger.class))).thenReturn(DiagnosticsStatus.PASS);

        AllDiagnosticsProbes allDiagnosticsProbes = new AllDiagnosticsProbes(Arrays.asList(probe1, probe2, probe3));
        DiagnosticsStatus actualDiagnosticsStatus = allDiagnosticsProbes.diagnose(diagnosticsContext, new StringWriter());

        assertEquals(DiagnosticsStatus.FAIL, actualDiagnosticsStatus);

        verify(probe1).diagnose(eq(diagnosticsContext), any(DiagnosticsLogger.class));
        verify(probe2).diagnose(eq(diagnosticsContext), any(DiagnosticsLogger.class));
        verify(probe3).diagnose(eq(diagnosticsContext), any(DiagnosticsLogger.class));
    }

    @Test
    public void shouldFailIfAnyProbeReturnsWarnStatus() {
        DiagnosticsProbe probe1 = mock(DiagnosticsProbe.class);
        DiagnosticsProbe probe2 = mock(DiagnosticsProbe.class);
        DiagnosticsProbe probe3 = mock(DiagnosticsProbe.class);

        DiagnosticsContext diagnosticsContext = mock(DiagnosticsContext.class);

        when(probe1.diagnose(eq(diagnosticsContext), any(DiagnosticsLogger.class))).thenReturn(DiagnosticsStatus.PASS);
        when(probe2.diagnose(eq(diagnosticsContext), any(DiagnosticsLogger.class))).thenReturn(DiagnosticsStatus.WARN);
        when(probe3.diagnose(eq(diagnosticsContext), any(DiagnosticsLogger.class))).thenReturn(DiagnosticsStatus.PASS);

        AllDiagnosticsProbes allDiagnosticsProbes = new AllDiagnosticsProbes(Arrays.asList(probe1, probe2, probe3));
        DiagnosticsStatus actualDiagnosticsStatus = allDiagnosticsProbes.diagnose(diagnosticsContext, new StringWriter());

        assertEquals(DiagnosticsStatus.FAIL, actualDiagnosticsStatus);

        verify(probe1).diagnose(eq(diagnosticsContext), any(DiagnosticsLogger.class));
        verify(probe2).diagnose(eq(diagnosticsContext), any(DiagnosticsLogger.class));
        verify(probe3).diagnose(eq(diagnosticsContext), any(DiagnosticsLogger.class));
    }

    @Test
    public void shouldFailIfAnyProbeReturnsNullStatus() {
        DiagnosticsProbe probe1 = mock(DiagnosticsProbe.class);
        DiagnosticsProbe probe2 = mock(DiagnosticsProbe.class);
        DiagnosticsProbe probe3 = mock(DiagnosticsProbe.class);

        DiagnosticsContext diagnosticsContext = mock(DiagnosticsContext.class);

        when(probe1.diagnose(eq(diagnosticsContext), any(DiagnosticsLogger.class))).thenReturn(DiagnosticsStatus.PASS);
        when(probe2.diagnose(eq(diagnosticsContext), any(DiagnosticsLogger.class))).thenReturn(null);
        when(probe3.diagnose(eq(diagnosticsContext), any(DiagnosticsLogger.class))).thenReturn(DiagnosticsStatus.PASS);

        AllDiagnosticsProbes allDiagnosticsProbes = new AllDiagnosticsProbes(Arrays.asList(probe1, probe2, probe3));
        DiagnosticsStatus actualDiagnosticsStatus = allDiagnosticsProbes.diagnose(diagnosticsContext, new StringWriter());

        assertEquals(DiagnosticsStatus.FAIL, actualDiagnosticsStatus);

        verify(probe1).diagnose(eq(diagnosticsContext), any(DiagnosticsLogger.class));
        verify(probe2).diagnose(eq(diagnosticsContext), any(DiagnosticsLogger.class));
        verify(probe3).diagnose(eq(diagnosticsContext), any(DiagnosticsLogger.class));
    }

    @Test
    public void shouldFailIfAnyProbeThrowsException() {
        DiagnosticsProbe probe1 = mock(DiagnosticsProbe.class);
        DiagnosticsProbe probe2 = mock(DiagnosticsProbe.class);
        DiagnosticsProbe probe3 = mock(DiagnosticsProbe.class);

        DiagnosticsContext diagnosticsContext = mock(DiagnosticsContext.class);

        when(probe1.diagnose(eq(diagnosticsContext), any(DiagnosticsLogger.class))).thenReturn(DiagnosticsStatus.PASS);
        when(probe2.diagnose(eq(diagnosticsContext), any(DiagnosticsLogger.class))).thenThrow(new RuntimeException("some exception"));
        when(probe3.diagnose(eq(diagnosticsContext), any(DiagnosticsLogger.class))).thenReturn(DiagnosticsStatus.PASS);

        AllDiagnosticsProbes allDiagnosticsProbes = new AllDiagnosticsProbes(Arrays.asList(probe1, probe2, probe3));
        DiagnosticsStatus actualDiagnosticsStatus = allDiagnosticsProbes.diagnose(diagnosticsContext, new StringWriter());

        assertEquals(DiagnosticsStatus.FAIL, actualDiagnosticsStatus);

        verify(probe1).diagnose(eq(diagnosticsContext), any(DiagnosticsLogger.class));
        verify(probe2).diagnose(eq(diagnosticsContext), any(DiagnosticsLogger.class));
        verify(probe3).diagnose(eq(diagnosticsContext), any(DiagnosticsLogger.class));
    }

}
