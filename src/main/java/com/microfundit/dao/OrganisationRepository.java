package com.microfundit.dao;

import com.microfundit.model.Brand;
import com.microfundit.model.Organisation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;

/**
 * Created by Kevin Kimaru Chege on 3/21/2018.
 */
@CrossOrigin(origins = "http://localhost:4200")
public interface OrganisationRepository extends CrudRepository<Organisation, Long> {
}
