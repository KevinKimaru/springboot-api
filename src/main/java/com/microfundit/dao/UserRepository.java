package com.microfundit.dao;

import com.microfundit.model.Donor;
import com.microfundit.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;

/**
 * Created by Kevin Kimaru Chege on 3/21/2018.
 */
@CrossOrigin(origins = "http://localhost:4200")
public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String username);

    @Query("select u from User u where u.username=?1")
    User findOne(String username);
}
