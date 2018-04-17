package com.microfundit.controller;

import com.microfundit.dao.*;
import com.microfundit.model.Brand;
import com.microfundit.model.Donation;
import com.microfundit.model.PointsCompany;
import com.microfundit.model.Story;
import io.swagger.annotations.Api;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Kevin Kimaru Chege on 4/10/2018.
 */
@Api(tags = "Donate ")
@RestController
public class DonationController {
    @Autowired
    private StoryRepository stories;
    @Autowired
    private PointsCompanyRepository pointsCompaies;

    Logger logger = Logger.getLogger(LoginController.class);

    @Autowired
    DonationUtils donationUtils;

    @RequestMapping(value = "/donate", method = RequestMethod.POST)
    @ResponseBody
    public DonationImpl donate(@RequestBody DonationImpl donationImpl) {
        Story story = stories.findOne(donationImpl.getStory());
        if (story == null) {
            throw new RuntimeException("The selected story does not exist");
        }
        Donation donation = new Donation(story, donationImpl.getAmount(), donationImpl.getType());
        if (donationImpl.getType() == 0) {
            PointsCompany pointsCompany = pointsCompaies.findOne(donationImpl.getPointsCompany());
            if (pointsCompany == null) {
                throw new RuntimeException("The selected points company does not exist.");
            }
            donation.setPointsCompany(pointsCompany);
        }

        Donation d1 = donationUtils.donate(donation);

        List<String> brandNames = new ArrayList<>();
        for (Brand b : d1.getMatchedBrands()) {
            brandNames.add(b.getName());
        }
        DonationImpl d2 = new DonationImpl(d1.getAmount(), d1.getType(), d1.getStory().getDescription(), d1.getStory().getId(),
                brandNames, d1.getMatchedAmount());
        return d2;
    }

}
