package com.microfundit.listener;

import com.microfundit.model.Organisation;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by Kevin Kimaru Chege on 3/21/2018.
 */
@Component
@RepositoryEventHandler(Organisation.class)
public class OrganisationEventHandler {
    @HandleBeforeCreate
    private void setDefaultsBeforeAddingOrganisation(Organisation organisation) {
        organisation.setDateAdded(new Date());
    }
}
