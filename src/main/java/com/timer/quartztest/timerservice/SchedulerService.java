package com.timer.quartztest.timerservice;

import com.timer.quartztest.info.TimerInfo;
import com.timer.quartztest.jobs.HelloWorldJob;
import com.timer.quartztest.util.TimerUtils;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.sql.Time;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class SchedulerService {
    private static final Logger LOG = LoggerFactory.getLogger(SchedulerService.class);

    // Scheduler - 등록된 Job과 Trigger를 관리. 연관된 Trigger의 발사시점을 보고 있다가 관련 Job을 실행시키는 역할
    private final Scheduler scheduler;

    @Autowired
    public SchedulerService(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void schedule(final Class jobClass, final TimerInfo info){
        final JobDetail jobDetial = TimerUtils.buildJobDetail(jobClass, info);
        final Trigger trigger = TimerUtils.buildTrigger(jobClass, info);


        try {
            scheduler.scheduleJob(jobDetial, trigger);
        } catch (SchedulerException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public List<TimerInfo> getAllRunningTimers(){
        try {
            return scheduler.getJobKeys(GroupMatcher.anyGroup())
                    .stream()
                    .map(jobKey -> {
                        try {
                            final JobDetail jobDetail = scheduler.getJobDetail(jobKey);
                            return (TimerInfo) jobDetail.getJobDataMap().get(jobKey.getName());
                        } catch (SchedulerException e) {
                            LOG.error(e.getMessage(), e);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (SchedulerException e) {
            LOG.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    public TimerInfo getRunningTimer(String timerId){
        try {
            final JobDetail jobDetail = scheduler.getJobDetail(new JobKey(timerId));
            if(jobDetail == null){
                LOG.error("Failed to find timer with ID '{}'", timerId);
                return null;
            }
            return (TimerInfo) jobDetail.getJobDataMap().get(timerId);
        } catch (SchedulerException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    public void updateTimer(final String timerId, final TimerInfo info){
        try {
            final JobDetail jobDetail = scheduler.getJobDetail(new JobKey(timerId));
            if (jobDetail == null){
                LOG.error("Failed to find timer with ID '{}'", timerId);
                return;
            }
            jobDetail.getJobDataMap().put(timerId, info);
            scheduler.addJob(jobDetail, true, true);
        } catch (SchedulerException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public Boolean deleteTimer(final String timerId){
        try {
            return scheduler.deleteJob(new JobKey(timerId));
        } catch (SchedulerException e) {
            LOG.error(e.getMessage(), e);
            return false;
        }
    }

    @PostConstruct  // 의존성 주입이 이루어진 후 초기화를 수행하는 메서드. service를 수행하기 전에 발생, 이 메서드는 다른 리소스에서 호출되지 않는다해도 수행된다.
    public void init() {
        try {
            scheduler.start();
            scheduler.getListenerManager().addTriggerListener(new SimpleTriggerListener(this));
        } catch (SchedulerException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @PreDestroy  // 마지막 소멸 단계
    public void preDestory() {
        try {
            scheduler.shutdown();
        } catch (SchedulerException e) {
            LOG.error(e.getMessage(), e);
        }
    }

}
