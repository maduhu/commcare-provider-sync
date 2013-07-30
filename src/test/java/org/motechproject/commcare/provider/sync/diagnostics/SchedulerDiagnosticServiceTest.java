package org.motechproject.commcare.provider.sync.diagnostics;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SchedulerDiagnosticServiceTest {
    @Mock
    private Scheduler motechScheduler;
    @Mock
    private SchedulerFactoryBean schedulerFactoryBean;
    @Mock
    Trigger mockTrigger1, mockTrigger2;

    String providerSync = "commcare.provider.sync.schedule";
    String groupSync = "commcare.group.sync.schedule";
    private final String schedulerDiagnosticsFormat = "Job : %s\nPrevious Fire Time : %s\nNext Fire Time : %s\nHas job run in previous day : %s";
    private SchedulerDiagnosticService schedulerDiagnosticService;

    @Before
    public void setUp() {
        when(schedulerFactoryBean.getScheduler()).thenReturn(motechScheduler);
        schedulerDiagnosticService = new SchedulerDiagnosticService(schedulerFactoryBean, new ArrayList<String>() {{
            add(providerSync);
            add(groupSync);
        }});
    }

    private void initializeTriggers() throws SchedulerException{
        Set<TriggerKey> triggerKeys = new HashSet<>();
        TriggerKey triggerKey1 = new TriggerKey("name1", "default");
        TriggerKey triggerKey2 = new TriggerKey("name2", "default");
        triggerKeys.add(triggerKey1);
        triggerKeys.add(triggerKey2);
        when(motechScheduler.getTriggerKeys(GroupMatcher.triggerGroupContains(anyString()))).thenReturn(triggerKeys);

        when(motechScheduler.getTrigger(triggerKey1)).thenReturn(mockTrigger1);
        when(motechScheduler.getTrigger(triggerKey2)).thenReturn(mockTrigger2);
        when(mockTrigger1.getJobKey()).thenReturn(new JobKey(providerSync));
        when(mockTrigger2.getJobKey()).thenReturn(new JobKey(groupSync));

        ArrayList jobKeyList = mock(ArrayList.class);
        when(motechScheduler.getTriggersOfJob(any(JobKey.class))).thenReturn(jobKeyList);
        when(jobKeyList.size()).thenReturn(2);

    }

    @Test
    public void shouldPrintJobHasNotRunWhenJobHasNotPreviouslyRun() throws SchedulerException {
        initializeTriggers();
        when(mockTrigger1.getPreviousFireTime()).thenReturn(null);
        when(mockTrigger1.getNextFireTime()).thenReturn(DateTime.now().plusMonths(4).toDate());
        when(mockTrigger2.getPreviousFireTime()).thenReturn(null);
        when(mockTrigger2.getNextFireTime()).thenReturn(DateTime.now().plusMonths(4).toDate());

        DiagnosticsResult diagnosticsResult = schedulerDiagnosticService.diagnoseAllSchedules();

        assertTrue(diagnosticsResult.getMessage().contains(String.format(schedulerDiagnosticsFormat, providerSync, "This job has not yet run", mockTrigger1.getNextFireTime(), "N/A")));
        assertTrue(diagnosticsResult.getMessage().contains(String.format(schedulerDiagnosticsFormat, groupSync, "This job has not yet run", mockTrigger2.getNextFireTime(), "N/A")));
        assertEquals(DiagnosticsStatus.PASS, diagnosticsResult.getStatus());
    }

    @Test
    public void shouldFailIfAnyOfTheJobsHasNotRunWithinLastWeek() throws SchedulerException {
        initializeTriggers();
        when(mockTrigger1.getPreviousFireTime()).thenReturn(DateTime.now().minusWeeks(1).toDate());
        when(mockTrigger1.getNextFireTime()).thenReturn(DateTime.now().plusMonths(4).toDate());
        when(mockTrigger2.getPreviousFireTime()).thenReturn(DateTime.now().minusDays(5).toDate());
        when(mockTrigger2.getNextFireTime()).thenReturn(DateTime.now().plusMonths(4).toDate());

        DiagnosticsResult diagnosticsResult = schedulerDiagnosticService.diagnoseAllSchedules();

        assertTrue(diagnosticsResult.getMessage().contains(String.format(schedulerDiagnosticsFormat, providerSync, mockTrigger1.getPreviousFireTime(), mockTrigger1.getNextFireTime(), "No")));
        assertTrue(diagnosticsResult.getMessage().contains(String.format(schedulerDiagnosticsFormat, groupSync, mockTrigger2.getPreviousFireTime(), mockTrigger2.getNextFireTime(), "Yes")));
        assertEquals(DiagnosticsStatus.FAIL, diagnosticsResult.getStatus());
    }

    @Test
    public void shouldFailDiagnosticsIfJobHasNotRunForGivenInterval() throws SchedulerException {
        initializeTriggers();
        when(mockTrigger1.getPreviousFireTime()).thenReturn(DateTime.now().minusWeeks(3).toDate());
        when(mockTrigger1.getNextFireTime()).thenReturn(DateTime.now().plusMonths(4).toDate());
        when(mockTrigger2.getPreviousFireTime()).thenReturn(DateTime.now().minusDays(4).toDate());
        when(mockTrigger2.getNextFireTime()).thenReturn(DateTime.now().plusMonths(4).toDate());

        DiagnosticsResult diagnosticsResult = schedulerDiagnosticService.diagnoseAllSchedules();

        assertTrue(diagnosticsResult.getMessage().contains(String.format(schedulerDiagnosticsFormat, providerSync, mockTrigger1.getPreviousFireTime(), mockTrigger1.getNextFireTime(), "No")));
        assertTrue(diagnosticsResult.getMessage().contains(String.format(schedulerDiagnosticsFormat, groupSync, mockTrigger2.getPreviousFireTime(), mockTrigger2.getNextFireTime(), "Yes")));
        assertEquals(DiagnosticsStatus.FAIL, diagnosticsResult.getStatus());
    }

    @Test
    public void shouldPassIfTheJobHasRunThePreviousWeek() throws SchedulerException {
        initializeTriggers();
        when(mockTrigger1.getPreviousFireTime()).thenReturn(DateTime.now().minusDays(6).toDate());
        when(mockTrigger1.getNextFireTime()).thenReturn(DateTime.now().plusHours(1).toDate());
        when(mockTrigger2.getPreviousFireTime()).thenReturn(DateTime.now().minusDays(6).toDate());
        when(mockTrigger2.getNextFireTime()).thenReturn(DateTime.now().plusHours(1).toDate());

        DiagnosticsResult diagnosticsResult = schedulerDiagnosticService.diagnoseAllSchedules();

        assertEquals(DiagnosticsStatus.PASS, diagnosticsResult.getStatus());
    }

    @Test
    public void shouldNotFailIfJobHasNotRunYet() throws SchedulerException {
        initializeTriggers();
        when(mockTrigger1.getPreviousFireTime()).thenReturn(null);
        when(mockTrigger1.getNextFireTime()).thenReturn(DateTime.now().plusHours(4).toDate());

        DiagnosticsResult diagnosticsResult = schedulerDiagnosticService.diagnoseAllSchedules();

        assertEquals(DiagnosticsStatus.PASS, diagnosticsResult.getStatus());
    }

    @Test
    public void shouldFailIfAllTheJobsAreNotScheduled() throws SchedulerException {
        schedulerDiagnosticService = new SchedulerDiagnosticService(schedulerFactoryBean, Arrays.asList("unscheduled.job"));

        DiagnosticsResult diagnosticsResult = schedulerDiagnosticService.diagnoseAllSchedules();

        System.out.println(diagnosticsResult.getMessage());
        assertTrue(diagnosticsResult.getMessage().contains(String.format("Unscheduled Job: unscheduled.job")));
        assertEquals(DiagnosticsStatus.FAIL, diagnosticsResult.getStatus());
    }
}