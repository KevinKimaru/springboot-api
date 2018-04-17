package com.microfundit.controller;

import com.microfundit.dao.StoryRepository;
import com.microfundit.model.Story;
import com.microfundit.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Kevin Kimaru Chege on 4/16/2018.
 */
@RestController
public class StoryController {
    private final StoryRepository stories;
    private final StorageService store;

    @Autowired
    public StoryController(StoryRepository stories, StorageService store) {
        this.stories = stories;
        this.store = store;
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/deleteStory/{storyId}")
    public ResponseEntity<?> deleteStory(@PathVariable Long storyId) {
        Story story = stories.findOne(storyId);
        if(story != null) {
            stories.delete(storyId);
            store.deleteAll(story.getId());
            return ResponseEntity.ok("Successfully deleted story");
        }
        return ResponseEntity.notFound().build();
    }
}
