package com.microfundit.dao;

import com.microfundit.model.Brand;
import com.microfundit.model.Funding;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Kevin Kimaru Chege on 3/21/2018.
 */
public interface FundingRepository extends CrudRepository<Funding, Long> {
    
    List<Funding> findBycurrentAmountGreaterThan(@Param("currentAmount") int currentAmount);

    List<Funding> findByStatus(@Param("status") int status);
}
