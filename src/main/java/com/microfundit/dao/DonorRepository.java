package com.microfundit.dao;

import com.microfundit.model.Donor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

/**
 * Created by Kevin Kimaru Chege on 4/4/2018.
 */
public interface DonorRepository extends CrudRepository<Donor, Long> {

    @PreAuthorize("hasRole('ROLE_USER')")
    @Query("select d from Donor d where d.username=:#{principal}")
    Donor findOne();
}
