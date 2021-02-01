package com.timer.quartztest.playground;

import com.timer.quartztest.info.TimerInfo;
import com.timer.quartztest.jobs.HelloWorldJob;
import com.timer.quartztest.timerservice.SchedulerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlaygroundService {

    private static final Logger LOG = LoggerFactory.getLogger(PlaygroundService.class);

    private final SchedulerService scheduler;

    @Autowired
    public PlaygroundService(SchedulerService scheduler) {
        this.scheduler = scheduler;
    }

    public void runHelloWorldJob(){
        final TimerInfo info = new TimerInfo();
        info.setTotalFireCount(5);
        info.setRemainingFireCount(info.getTotalFireCount());
        info.setRepeatIntervalMs(5000);
        info.setInitialOffsetMs(1000);
        info.setCallbackData("My callback data");

        scheduler.schedule(HelloWorldJob.class, info);
    }

    public Boolean deleteTimer(final String timerId){
        return scheduler.deleteTimer(timerId);
    }

    public List<TimerInfo> getAllRunningTimers(){
        return scheduler.getAllRunningTimers();
    }

    public TimerInfo getRunningTimer(String timerId){
        return scheduler.getRunningTimer(timerId);
    }
}
