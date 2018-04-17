package com.microfundit.listener;

import com.microfundit.model.Brand;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by Kevin Kimaru Chege on 3/21/2018.
 */
@Component
@RepositoryEventHandler(Brand.class)
public class BrandEventHandler {
    @HandleBeforeCreate
    private void setDefaultsBeforeAddingBrand(Brand brand) {
        brand.setDateAdded(new Date());
    }
}
