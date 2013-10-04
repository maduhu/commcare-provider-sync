package org.motechproject.commcare.provider.sync.diagnostics.scheduler;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.joda.time.DateTime;
import org.motechproject.commcare.provider.sync.diagnostics.DiagnosticsLogger;
import org.motechproject.commcare.provider.sync.diagnostics.DiagnosticsStatus;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.server.config.SettingsFacade;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@Service("schedulerDiagnosticsService")
public class SchedulerDiagnosticsServiceImpl implements SchedulerDiagnosticsService {

    private Scheduler scheduler;

    @Autowired
    public SchedulerDiagnosticsServiceImpl(ApplicationContext applicationContext, @Qualifier("quartzSettings") SettingsFacade quartzSettings) throws Exception {
        SchedulerFactoryBean schedulerFactoryBean = initializeSchedulerFactoryBean(applicationContext, quartzSettings.asProperties());
        scheduler = schedulerFactoryBean.getScheduler();
    }

    private SchedulerFactoryBean initializeSchedulerFactoryBean(ApplicationContext applicationContext, Properties quartzProperties) throws Exception {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setQuartzProperties(quartzProperties);
        schedulerFactoryBean.setApplicationContextSchedulerContextKey("applicationContext");
        schedulerFactoryBean.setApplicationContext(applicationContext);
        schedulerFactoryBean.setAutoStartup(false);
        schedulerFactoryBean.afterPropertiesSet();
        return schedulerFactoryBean;
    }

    public SchedulerDiagnosticsServiceImpl(SchedulerFactoryBean schedulerFactoryBean) {
        this.scheduler = schedulerFactoryBean.getScheduler();
    }

    public DiagnosticsStatus diagnoseSchedules(List<String> schedules, DiagnosticsLogger diagnosticsLogger) throws SchedulerException {

        List<JobDetails> jobDetailsList = getJobDetailsFor(schedules);

        DiagnosticsStatus diagnosticsStatus = checkIfAllJobsAreScheduled(schedules, jobDetailsList, diagnosticsLogger);
        return diagnosticsStatus.equals(DiagnosticsStatus.FAIL) ?
                diagnosticsStatus :
                checkIfJobsAreScheduledAtTheRightTime(jobDetailsList, diagnosticsLogger);
    }

    private DiagnosticsStatus checkIfAllJobsAreScheduled(List<String> jobs, List<JobDetails> jobDetailsList, DiagnosticsLogger diagnosticsLogger) {
        if (jobDetailsList.size() != jobs.size()) {
            ArrayList<String> unScheduledJobs = getUnscheduledJobs(jobs, jobDetailsList);
            for (String unScheduledJob : unScheduledJobs)
                diagnosticsLogger.log("Unscheduled Job: " + unScheduledJob);
            return DiagnosticsStatus.FAIL;
        }
        return DiagnosticsStatus.PASS;
    }

    private DiagnosticsStatus checkIfJobsAreScheduledAtTheRightTime(List<JobDetails> jobDetailsList, DiagnosticsLogger diagnosticsLogger) throws SchedulerException {
        DiagnosticsStatus status = DiagnosticsStatus.PASS;
        for (JobDetails jobDetails : jobDetailsList) {
            Date previousFireTime = jobDetails.getPreviousFireTime();
            Date nextFireTime = jobDetails.getNextFireTime();

            diagnosticsLogger.log("Job: " + jobDetails.getName());
            diagnosticsLogger.log("Previous Fire Time: " + (previousFireTime == null ? "Has not yet run" : previousFireTime.toString()));
            diagnosticsLogger.log("Next Fire Time: " + (nextFireTime == null ? "Not scheduled" : nextFireTime.toString()));

            String runStatusInPreviousWeek = "N/A";
            if(previousFireTime != null) {
                boolean hasJobRunInPreviousWeek = hasJobRunInPreviousWeek(jobDetails);
                if(hasJobRunInPreviousWeek) {
                    runStatusInPreviousWeek = "Yes";
                } else {
                    runStatusInPreviousWeek = "No";
                    status = DiagnosticsStatus.FAIL;
                }
            }

            diagnosticsLogger.log("Has Run In Previous Week: " + runStatusInPreviousWeek);
        }

        return status;
    }

    private ArrayList<String> getUnscheduledJobs(List<String> jobs, List<JobDetails> jobDetailsList) {
        ArrayList<String> jobDetailNamesList = (ArrayList<String>) CollectionUtils.collect(jobDetailsList, new Transformer() {
            @Override
            public Object transform(Object input) {
                JobDetails jobDetails = (JobDetails) input;
                return jobDetails.getName();
            }
        });
        return (ArrayList<String>) CollectionUtils.disjunction(jobs, jobDetailNamesList);
    }

    private boolean hasJobRunInPreviousWeek(JobDetails jobDetails) {
        Date previousFireTime = jobDetails.getPreviousFireTime();
        if (previousFireTime == null) {
            return false;
        }

        return !DateUtil.newDateTime(previousFireTime).isBefore(DateTime.now().minusWeeks(1));
    }

    private List<JobDetails> getJobDetailsFor(List<String> jobs) throws SchedulerException {
        List<TriggerKey> triggerKeys = new ArrayList<>(scheduler.getTriggerKeys(GroupMatcher.triggerGroupContains("default")));
        List<JobDetails> jobDetailsList = new ArrayList<>();
        for (TriggerKey triggerKey : triggerKeys) {
            Trigger trigger = scheduler.getTrigger(triggerKey);
            JobKey jobKey = trigger.getJobKey();
            if (scheduler.getTriggersOfJob(jobKey).size() > 0 && isForJob(jobKey.getName(), jobs)) {
                Date previousFireTime = trigger.getPreviousFireTime();
                jobDetailsList.add(new JobDetails(previousFireTime, jobKey.getName(), trigger.getNextFireTime()));
            }
        }
        return jobDetailsList;
    }

    private boolean isForJob(String name, List<String> jobs) {
        for (String job : jobs) {
            if (name.contains(job))
                return true;
        }
        return false;
    }
}