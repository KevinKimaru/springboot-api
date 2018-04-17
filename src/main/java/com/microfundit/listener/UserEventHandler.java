package com.microfundit.listener;

import com.microfundit.dao.UserRepository;
import com.microfundit.model.Donor;
import com.microfundit.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Created by Kevin Kimaru Chege on 4/4/2018.
 */
@Component
@RepositoryEventHandler(User.class)
public class UserEventHandler {
    public static final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    private final UserRepository users;

    @Autowired
    public UserEventHandler(UserRepository users) {
        this.users = users;
    }

    @HandleBeforeCreate
    public void encodePassword(User user) {
        if(user.getUsername() != null && user.getPassword() != null) {
            String password = bCryptPasswordEncoder.encode(user.getPassword());
            user.setPassword(password);
            if(user instanceof Donor)
                user.setRole("ROLE_USER");
            else
                user.setRole("ROLE_DONOR");
        }
    }

    @HandleBeforeSave
    public void setPassword(User user) {
        User u = users.findOne(user.getId());
        if(u != null)
        user.setPassword(u.getPassword());
        else
            throw new RuntimeException("This user does not exist");
    }
}
