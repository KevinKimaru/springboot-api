package com.microfundit.service;

import com.microfundit.dao.StoryRepository;
import com.microfundit.model.Story;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;

/**
 * Created by Kevin Kimaru Chege on 4/1/2018.
 */
@Service
public class StoryCloseService {
    private Logger logger = Logger.getLogger(StoryCloseService.class);

    private final TaskScheduler taskScheduler;
    private final StoryRepository stories;
    private ScheduledFuture scheduledFuture;

    @Autowired
    public StoryCloseService(TaskScheduler taskScheduler, StoryRepository stories) {
        this.taskScheduler = taskScheduler;
        this.stories = stories;
    }

    public void schedule(Story story) {
        scheduledFuture = taskScheduler.schedule(new Runnable() {
            @Override
            public void run() {
                logger.info("Closing story... " + "\n" + story.toString() + "\n" + new Date());
                //Get the story and check if it has been updated since scheduling. If so cancel the task scheduler
                //as the update triggered another schedule as well and return from this method
                Story story1 = stories.findOne(story.getId());
                if(!Objects.equals(story1.getVersion(), story.getVersion())) {
                    logger.info("This story has been updated since last access. It shall therefore be closed with another" +
                            " task schduler. Closing this task scheduller.........\n" + story.toString());
                    scheduledFuture.cancel(true);
                    return;
                }
                //Else close the story
                story.setStatus(0);
                stories.save(story);
                logger.info("Successfully closed story. " + "\n" + story.toString() + "\n" + new Date());
                scheduledFuture.cancel(true);
            }
        }, new Trigger() {
            @Override
            public Date nextExecutionTime(TriggerContext triggerContext) {
                Calendar c = Calendar.getInstance();
                c.setTime(story.getDateAdded());
                //TODO: THIS IS FOR TESTING PURPOSES CHANGE THE CALENDAR.MINUTE TO CALENDAR.DAY
                c.add(Calendar.MINUTE, story.getTimeAllocated());
                return c.getTime();
            }
        });
    }
}
