package com.timer.quartztest.playground;

import com.timer.quartztest.info.TimerInfo;
import com.timer.quartztest.timerservice.SchedulerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/timer")
public class PlaygroundController {

    private static final Logger LOG = LoggerFactory.getLogger(PlaygroundController.class);

    private PlaygroundService service;

    @Autowired
    public PlaygroundController(PlaygroundService service){
        this.service = service;
    }

    @PostMapping("/runhelloworld")
    public void runHelloWorldJob(){
        service.runHelloWorldJob();
    }

    @GetMapping
    public List<TimerInfo> getAllRunningTimers(){
        return service.getAllRunningTimers();
    }

    @GetMapping("/{timerId}")
    public TimerInfo getRunningTimer(@PathVariable String timerId){
        return service.getRunningTimer(timerId);
    }

    @DeleteMapping("/{timerId}")
    public Boolean deleteTimer(@PathVariable String timerId){
        return service.deleteTimer(timerId);
    }
}
