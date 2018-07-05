package com.subscription.service.impl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.subscription.service.MySchedulerService;
import com.subscription.service.task.TaskExport;
import com.subscription.util.ScheduleJob;

@Service
public class MySchedulerServiceImpl implements MySchedulerService {

  @Autowired
  private Scheduler scheduler;

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @Override
  public void scheduleTask(String key, String name, String description, String emails, String url,
      String cron, LocalDate dateStart, LocalDate dateEnd) {

    Date startDate = Date.from(dateStart.atStartOfDay(ZoneId.systemDefault()).toInstant());
    Date endDate = Date.from(dateEnd.atStartOfDay(ZoneId.systemDefault()).toInstant());

    Job task = new TaskExport();
    // Create Quartz Job detail
    JobDetail jobDetail = ScheduleJob.getJobDetail(task, key, "Task Export Job");
    // create Quartz trigger
    Trigger trigger = ScheduleJob.getTrigger(name, startDate, endDate, cron);
    // get job data map to use as parameters. we can pass any type of values to the task through the
    // JobDataMap
    JobDataMap jdm = jobDetail.getJobDataMap();
    jdm.put("url", url);
    jdm.put("name", name);
    jdm.put("description", description);
    jdm.put("emails", emails);

    // Schedule task
    try {
      ScheduleJob.scheduleJob(scheduler, jobDetail, trigger);
    } catch (SchedulerException e) {
      log.error("scheduleTask:", e);
    }

  }

  @Override
  public void unScheduleTask(String id) {
    try {
      if (id != null) {
        ScheduleJob.deleteJob(scheduler, id);
      }

    } catch (SchedulerException e) {
      log.error("unScheduleTask:", e);
    }

  }



}
