package com.microfundit.listener;

import com.microfundit.model.PointsCompany;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by Kevin Kimaru Chege on 3/21/2018.
 */
@Component
@RepositoryEventHandler(PointsCompany.class)
public class PointsCompanyEventHandler {
    @HandleBeforeCreate
    private void setDefaultsBeforeAddingPointsCompany(PointsCompany pointsCompany) {
        pointsCompany.setDateAdded(new Date());
    }
}
