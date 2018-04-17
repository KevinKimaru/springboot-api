package com.microfundit.listener;

import com.microfundit.dao.DonationRepository;
import com.microfundit.dao.DonorRepository;
import com.microfundit.dao.StoryRepository;
import com.microfundit.dao.SubscriptionRepository;
import com.microfundit.model.*;
import com.microfundit.service.SubscriptionSchedulingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

/**
 * Created by Kevin Kimaru Chege on 3/29/2018.
 */
@Component
@RepositoryEventHandler(Subscription.class)
public class SubscriptionEventHandler {
    @Autowired
    private SubscriptionRepository subscriptions;
    @Autowired
    private TaskScheduler taskScheduler;
    @Autowired
    private DonorRepository donors;
    @Autowired
    private StoryRepository stories;
    @Autowired
    private DonationRepository donations;

    @HandleBeforeCreate
    private void setDefaultsBeforeAddingSubscription(Subscription subscription) {
        subscription.setStatus(1);
        subscription.setSetTransactions((subscription.getSetTransactions() == 0) ? 5 : subscription.getSetTransactions());
    }

    @HandleAfterCreate
    private void scheduleTheSubscription(Subscription subscription) {
        SubscriptionSchedulingService subscriptionSchedulingService =
                new SubscriptionSchedulingService(subscriptions, taskScheduler, donors, stories, donations);
        //Donate first then the rest will follow
        Story story = stories.findOne(subscription.getStory().getId());
        Donor donor = donors.findOne(subscription.getDonor().getId());
        Donation donation = new Donation(donor, story, subscription.getAmount(), subscription.getType(),
                subscription.getPointsCompany());
        try {
            donations.save(donation);
        } catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Subscription error: " + e.getMessage());
        }
        //0 = DAY 1 = WEEK 2 = 2WEEKS 3 = 1 MONTH
        // TODO: UNCOMMENT THESE PIECE OF CODE FOR THE ACTUAL THING. REMOVE THE ONE BELOW THE SWITCH
//        switch(subscription.getPeriod()) {
//            case 0:
//                subscriptionSchedulingService.schedule(subscription, "* * * */" + 1 + " * *");
//                break;
//            case 1:
//                subscriptionSchedulingService.schedule(subscription, "* * * */" + 7 + " * *");
//                break;
//            case 2:
//                subscriptionSchedulingService.schedule(subscription, "* * * */" + 14 + " * *");
//                break;
//            case 3:
//                subscriptionSchedulingService.schedule(subscription, "* * * * */" + 1 + " *");
//                break;
//            default:
//                throw new RuntimeException("Invalid period Interval. Schedule cannot therefore take place");
//        }
        //TODO: TO BE REMOVED. THIS IS JUST FOR TESTING
        subscriptionSchedulingService.schedule(subscription, "*/" + 20 + " * * * * *");
    }
}
