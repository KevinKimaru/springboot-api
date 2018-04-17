package com.microfundit.dao;

import com.microfundit.model.Story;
import io.swagger.annotations.Api;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;

/**
 * Created by Kevin Kimaru Chege on 3/21/2018.
 */
@Api(tags = "Story Entity")
@CrossOrigin(origins = "http://localhost:4200")
public interface StoryRepository extends CrudRepository<Story, Long> {

}
