package com.microfundit.service;

import com.microfundit.dao.*;
import com.microfundit.model.Donation;
import com.microfundit.model.Donor;
import com.microfundit.model.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;

/**
 * Created by Kevin Kimaru Chege on 4/2/2018.
 */
@Service
public class SubscriptionSchedulingService {
    Logger logger = LoggerFactory.getLogger(SubscriptionSchedulingService.class);
    private final SubscriptionRepository subscriptions;
    private ScheduledFuture scheduledFuture;
    private final TaskScheduler taskScheduler;
    private final DonorRepository donors;
    private final StoryRepository stories;
    private final DonationRepository donations;

    @Autowired
    public SubscriptionSchedulingService(SubscriptionRepository subscriptions, TaskScheduler taskScheduler, DonorRepository donors, StoryRepository stories, DonationRepository donations) {
        this.subscriptions = subscriptions;
        this.taskScheduler = taskScheduler;
        this.donors = donors;
        this.stories = stories;
        this.donations = donations;
    }

    public void schedule(Subscription subscription, String cronExpression) {
        scheduledFuture = taskScheduler.schedule(new Runnable() {
            @Override
            public void run() {

                logger.info("*****************+++++++++++++++++++++ {}", new Date());

                Subscription s = subscriptions.findOne(subscription.getId());

//                Story story = stories.findOne(s.getStory().getId());
                stories.findAll();
                Donor donor = donors.findOne(s.getDonor().getId());
                Donation donation = new Donation(donor, null, s.getAmount(), s.getType(),
                        s.getPointsCompany());
                try {
                    donations.save(donation);
                } catch(Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("Subscription error: " + e.getMessage());
                }

                s.setCurrentTransactions(s.getCurrentTransactions() + 1);
                if(s.getCurrentTransactions() >= s.getSetTransactions()) {
                    s.setStatus(0);
                    subscriptions.save(s);
                    scheduledFuture.cancel(true);
                    return;
                }
                subscriptions.save(s);
            }
        }, new CronTrigger(cronExpression));
    }
}
