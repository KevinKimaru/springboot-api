package com.microfundit.dao;

import com.microfundit.model.Brand;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;

/**
 * Created by Kevin Kimaru Chege on 3/21/2018.
 */
public interface BrandRepository extends CrudRepository<Brand, Long>{
}
