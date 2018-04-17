package com.microfundit.controller;

import com.microfundit.dao.StoryRepository;
import com.microfundit.model.Story;
import com.microfundit.service.StorageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Kevin Kimaru Chege on 3/27/2018.
 */
@Api(tags = "Upload and Download Image")
@RestController
public class UploadController {
    @Autowired
    StorageService storageService;

    @Autowired
    StoryRepository stories;


    @ApiOperation("Upload an image for the story with the id passed")
    @RequestMapping(value = "/image/{storyId}", method = RequestMethod.POST)
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") @ApiParam(value="The image to save for this story")
                                                               MultipartFile file,
                                                   @PathVariable @ApiParam(value = "The id of the story to add an image to.")
                                                           Long storyId) {
        String message = "";
        try {
            Story story = stories.findOne(storyId);
            String imageName = storageService.store(file, story.getId());
//            files.add(file.getOriginalFilename());

            message = "You successfully uploaded " + file.getOriginalFilename() + "! as" + imageName;

            story.getImages().add(imageName);
            stories.save(story);

            return ResponseEntity.status(HttpStatus.OK).body(message);
        } catch (Exception e) {
            message = "FAIL to upload " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
        }
    }

    @ApiOperation("Get all images for this story")
    @RequestMapping(value = "/getImages/{storyId}", method = RequestMethod.GET)
    public ResponseEntity<List<String>> getListFiles(Model model, @PathVariable
    @ApiParam(value = "The id of the story, whose images you are to get.")
            Long storyId) {
        Story story = stories.findOne(storyId);
        List<String> files = story.getImages();
        List<String> fileNames = files
                .stream().map(fileName -> MvcUriComponentsBuilder
                        .fromMethodName(UploadController.class, "getFile", fileName, story.getId()).build().toString())
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(fileNames);
    }

    @GetMapping("/files/{storyId}/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> getFile(@PathVariable String filename, @PathVariable Long storyId) {
        Resource file = storageService.loadFile(filename, storyId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    @RequestMapping(value = "/deleteImage/{storyId}/{imageName}", method = RequestMethod.PUT)
    public ResponseEntity<Object> deleteFile(@PathVariable Long storyId, @PathVariable String imageName) {
        Story story = stories.findOne(storyId);
        if (story != null) {
            story.getImages().removeIf(n -> n.contentEquals(imageName));
            stories.save(story);
            storageService.delete(storyId, imageName);
            return ResponseEntity.ok().body("Successully deleted image");
        }
        return ResponseEntity.notFound().build();
    }
}
