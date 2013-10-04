package org.motechproject.commcare.provider.sync.diagnostics.scheduler;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.motechproject.commcare.provider.sync.diagnostics.DiagnosticsLogger;
import org.motechproject.commcare.provider.sync.diagnostics.DiagnosticsStatus;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SchedulerDiagnosticsServiceImplTest {
    @Mock
    private Scheduler motechScheduler;
    @Mock
    private SchedulerFactoryBean schedulerFactoryBean;
    @Mock
    Trigger mockTrigger1, mockTrigger2;
    @Mock
    private DiagnosticsLogger diagnosticsLogger;

    String providerSync = "commcare.provider.sync.schedule";
    String groupSync = "commcare.group.sync.schedule";
    private final String schedulerDiagnosticsFormat = "Job: %s\nPrevious Fire Time: %s\nNext Fire Time: %s\nHas job run in previous day: %s";
    private SchedulerDiagnosticsServiceImpl schedulerDiagnosticsService;

    @Before
    public void setUp() {
        initMocks(this);
        when(schedulerFactoryBean.getScheduler()).thenReturn(motechScheduler);
        schedulerDiagnosticsService = new SchedulerDiagnosticsServiceImpl(schedulerFactoryBean);
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

        DiagnosticsStatus diagnosticsStatus = schedulerDiagnosticsService.diagnoseSchedules(new ArrayList<String>() {{
            add(providerSync);
            add(groupSync);
        }}, diagnosticsLogger);

        verifyLogEntry(diagnosticsLogger, providerSync, null, mockTrigger1.getNextFireTime(), "N/A");
        verifyLogEntry(diagnosticsLogger, groupSync, null, mockTrigger2.getNextFireTime(), "N/A");
        assertEquals(DiagnosticsStatus.PASS, diagnosticsStatus);
    }

    @Test
    public void shouldFailIfAnyOfTheJobsHasNotRunWithinLastWeek() throws SchedulerException {
        initializeTriggers();
        when(mockTrigger1.getPreviousFireTime()).thenReturn(DateTime.now().minusWeeks(1).toDate());
        when(mockTrigger1.getNextFireTime()).thenReturn(DateTime.now().plusMonths(4).toDate());
        when(mockTrigger2.getPreviousFireTime()).thenReturn(DateTime.now().minusDays(5).toDate());
        when(mockTrigger2.getNextFireTime()).thenReturn(DateTime.now().plusMonths(4).toDate());

        DiagnosticsStatus diagnosticsStatus = schedulerDiagnosticsService.diagnoseSchedules(new ArrayList<String>() {{
            add(providerSync);
            add(groupSync);
        }}, diagnosticsLogger);

        verifyLogEntry(diagnosticsLogger, providerSync, mockTrigger1.getPreviousFireTime(), mockTrigger1.getNextFireTime(), "No");
        verifyLogEntry(diagnosticsLogger, groupSync, mockTrigger2.getPreviousFireTime(), mockTrigger2.getNextFireTime(), "Yes");
        assertEquals(DiagnosticsStatus.FAIL, diagnosticsStatus);
    }

    @Test
    public void shouldFailDiagnosticsIfJobHasNotRunForGivenInterval() throws SchedulerException {
        initializeTriggers();
        when(mockTrigger1.getPreviousFireTime()).thenReturn(DateTime.now().minusWeeks(3).toDate());
        when(mockTrigger1.getNextFireTime()).thenReturn(DateTime.now().plusMonths(4).toDate());
        when(mockTrigger2.getPreviousFireTime()).thenReturn(DateTime.now().minusDays(4).toDate());
        when(mockTrigger2.getNextFireTime()).thenReturn(DateTime.now().plusMonths(4).toDate());

        DiagnosticsStatus diagnosticsStatus = schedulerDiagnosticsService.diagnoseSchedules(new ArrayList<String>() {{
            add(providerSync);
            add(groupSync);
        }}, diagnosticsLogger);

        verifyLogEntry(diagnosticsLogger, providerSync, mockTrigger1.getPreviousFireTime(), mockTrigger1.getNextFireTime(), "No");
        verifyLogEntry(diagnosticsLogger, groupSync, mockTrigger2.getPreviousFireTime(), mockTrigger2.getNextFireTime(), "Yes");
        assertEquals(DiagnosticsStatus.FAIL, diagnosticsStatus);
    }

    @Test
    public void shouldPassIfTheJobHasRunThePreviousWeek() throws SchedulerException {
        initializeTriggers();
        when(mockTrigger1.getPreviousFireTime()).thenReturn(DateTime.now().minusDays(6).toDate());
        when(mockTrigger1.getNextFireTime()).thenReturn(DateTime.now().plusHours(1).toDate());
        when(mockTrigger2.getPreviousFireTime()).thenReturn(DateTime.now().minusDays(6).toDate());
        when(mockTrigger2.getNextFireTime()).thenReturn(DateTime.now().plusHours(1).toDate());

        DiagnosticsStatus diagnosticsStatus = schedulerDiagnosticsService.diagnoseSchedules(new ArrayList<String>() {{
            add(providerSync);
            add(groupSync);
        }}, diagnosticsLogger);

        assertEquals(DiagnosticsStatus.PASS, diagnosticsStatus);
    }

    @Test
    public void shouldNotFailIfJobHasNotRunYet() throws SchedulerException {
        initializeTriggers();
        when(mockTrigger1.getPreviousFireTime()).thenReturn(null);
        when(mockTrigger1.getNextFireTime()).thenReturn(DateTime.now().plusHours(4).toDate());

        DiagnosticsStatus diagnosticsStatus = schedulerDiagnosticsService.diagnoseSchedules(new ArrayList<String>() {{
            add(providerSync);
            add(groupSync);
        }}, diagnosticsLogger);

        assertEquals(DiagnosticsStatus.PASS, diagnosticsStatus);
    }

    @Test
    public void shouldFailIfAllTheJobsAreNotScheduled() throws SchedulerException {
        schedulerDiagnosticsService = new SchedulerDiagnosticsServiceImpl(schedulerFactoryBean);

        DiagnosticsStatus diagnosticsStatus = schedulerDiagnosticsService.diagnoseSchedules(Arrays.asList("unscheduled.job"), diagnosticsLogger);

        verify(diagnosticsLogger).log(String.format("Unscheduled Job: unscheduled.job"));
        assertEquals(DiagnosticsStatus.FAIL, diagnosticsStatus);
    }

    private void verifyLogEntry(DiagnosticsLogger diagnosticsLogger, String jobName, Date previousFireTime, Date nextFireTime, String runStatusInPreviousWeek) {
        InOrder inOrder = inOrder(diagnosticsLogger);
        inOrder.verify(diagnosticsLogger).log("Job: " + jobName);
        inOrder.verify(diagnosticsLogger).log("Previous Fire Time: " + (previousFireTime == null ? "Has not yet run" : previousFireTime));
        inOrder.verify(diagnosticsLogger).log("Next Fire Time: " + (nextFireTime == null ? "Not scheduled" : nextFireTime));
        inOrder.verify(diagnosticsLogger).log("Has Run In Previous Week: " + runStatusInPreviousWeek);
    }
}