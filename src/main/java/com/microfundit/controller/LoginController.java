package com.microfundit.controller;

import com.microfundit.model.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Kevin Kimaru Chege on 4/6/2018.
 */
@Api(tags = "Log in into the API")
@RestController
public class LoginController {

    @ApiOperation("Log in into the API")
    @RequestMapping(value = "/login", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public ResponseEntity<Object> login(@RequestBody User user) {
        return new ResponseEntity<Object>(HttpStatus.OK);
    }
}
