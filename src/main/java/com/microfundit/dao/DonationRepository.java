package com.microfundit.dao;

import com.microfundit.model.Brand;
import com.microfundit.model.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

/**
 * Created by Kevin Kimaru Chege on 3/21/2018.
 */
public interface DonationRepository extends CrudRepository<Donation, Long> {

    @PreAuthorize("hasRole('ROLE_USER')")
    @Query("select d from Donation d where d.donor.username = :#{principal}")
    List<Donation> findByDonor();

}
