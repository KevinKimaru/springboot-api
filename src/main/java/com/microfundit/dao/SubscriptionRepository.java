package com.microfundit.dao;

import com.microfundit.model.Subscription;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;

/**
 * Created by Kevin Kimaru Chege on 4/2/2018.
 */
public interface SubscriptionRepository extends CrudRepository<Subscription, Long> {
}
