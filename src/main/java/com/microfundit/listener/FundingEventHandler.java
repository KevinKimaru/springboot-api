package com.microfundit.listener;

import com.microfundit.dao.FundingRepository;
import com.microfundit.model.Funding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by Kevin Kimaru Chege on 3/21/2018.
 */
@Component
@RepositoryEventHandler(Funding.class)
public class FundingEventHandler {

    private final FundingRepository fundings;

    @Autowired
    public FundingEventHandler(FundingRepository fundings) {
        this.fundings = fundings;
    }

    @HandleBeforeCreate
    private void setDefaultsBeforeAddingFunding(Funding funding) {
        if (funding.getPlacedAmount() % funding.getRatio() != 0) {
            throw new RuntimeException("Amount funded has to be a multiple of the ratio assigned");
        }
        funding.setDateAdded(new Date());
        funding.setStatus(1);
        funding.setCurrentAmount(funding.getPlacedAmount());
    }

    @HandleBeforeSave
    private void setCurrentAmount(Funding funding) {
        Funding f = fundings.findOne(funding.getId());
        //Since admin does not set funding current amount once updated, lets do it here
        if (f != null)
            funding.setCurrentAmount(f.getCurrentAmount());
        else
            throw new RuntimeException("This funding does not exist.");
    }


}

