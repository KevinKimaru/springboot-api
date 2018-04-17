package com.microfundit.listener;

import com.microfundit.dao.DonorRepository;
import com.microfundit.model.Donor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by Kevin Kimaru Chege on 3/21/2018.
 */
@Component
@RepositoryEventHandler(Donor.class)
public class DonorEventHandler {

    private final DonorRepository donors;

    @Autowired
    public DonorEventHandler(DonorRepository donors) {
        this.donors = donors;
    }

    @HandleBeforeCreate
    private void setDefaultsBeforeAddingUser(Donor donor) {
        if(donor.getUsername() == null || donor.getPassword() == null) {
            throw new RuntimeException("Username or password cannot be null");
        }
        donor.setDateAdded(new Date());
        donor.setRole("ROLE_USER");
    }

    @HandleBeforeSave
    private void setPassword(Donor donor) {
        Donor d = donors.findOne(donor.getId());
        if(d != null)
        donor.setPassword(d.getPassword());
        else
            throw new RuntimeException("This donor does not exist.");
    }
}
