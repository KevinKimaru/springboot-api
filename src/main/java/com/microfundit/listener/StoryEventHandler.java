package com.microfundit.listener;

import com.microfundit.dao.StoryRepository;
import com.microfundit.model.Story;
import com.microfundit.service.StoryCloseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.*;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by Kevin Kimaru Chege on 3/21/2018.
 */
@Component
@RepositoryEventHandler(Story.class)
public class StoryEventHandler {
    @Autowired
    private StoryCloseService storyCloseService;
    @Autowired
    private TaskScheduler taskScheduler;
    @Autowired
    private StoryRepository stories;

    @HandleBeforeCreate
    private void setDefaultsBeforeAddingStory(Story story) {
        story.setDateAdded(new Date());
        story.setStatus(1);
    }

    @HandleAfterCreate
    private void afterCreatingActions(Story story) {
        storyCloseService = new StoryCloseService(taskScheduler, stories);
        storyCloseService.schedule(story);
    }

    @HandleAfterSave
    private void afterSavngActions(Story story) {
        storyCloseService = new StoryCloseService(taskScheduler, stories);
        storyCloseService.schedule(story);
    }
}
