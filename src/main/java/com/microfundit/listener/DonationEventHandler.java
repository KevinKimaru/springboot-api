package com.microfundit.listener;

import com.microfundit.dao.DonorRepository;
import com.microfundit.dao.FundingRepository;
import com.microfundit.dao.StoryRepository;
import com.microfundit.dao.TransactionRepository;
import com.microfundit.model.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by Kevin Kimaru Chege on 3/21/2018.
 */
@Component
@RepositoryEventHandler(Donation.class)
public class DonationEventHandler {
    private final StoryRepository stories;
    private final FundingRepository fundings;
    private final TransactionRepository transactions;
    private final DonorRepository donors;

    Logger logger = Logger.getLogger(DonationEventHandler.class);
    private List<Transaction> donationTransactions = new ArrayList<>();
    Random random = new Random(System.currentTimeMillis());

    @Autowired
    public DonationEventHandler(StoryRepository stories, FundingRepository fundings, TransactionRepository transactions, DonorRepository donors) {
        this.stories = stories;
        this.fundings = fundings;
        this.transactions = transactions;
        this.donors = donors;
    }

    @HandleBeforeCreate
    public void addDonationTransaction(Donation donation) throws Exception {
        logger.info("Starting donation process...");

        logger.info("Setting donation date..");
        donation.setDateAdded(new Date());
        logger.info("Succefully set Donation date");

        Donor donor = donors.findOne();
        logger.info("Setting donor...: " + donor);
        if(donor != null) {
            donation.setDonor(donor);
            logger.info("Donor succefully set. " + donor );
        } else {
            logger.info("Unsuccesful: // Error: This donor does not exist.");
            throw new RuntimeException("Error: Donation from unregistered Donor");
        }

        logger.info("Doing amount checks and validations.....");
        //Amount donated be it points or cash converted into cash in dollars
        int simpleDonatedAmount;
        simpleDonatedAmount = doAmountChecks(donation);
        logger.info("Successfully done amount checks and validations.");

        logger.info("Doing story checks and validations....");
        Story story = donation.getStory();
        doStoryChecks(story);
        logger.info("Successfully done story checks and validations.");

        logger.info("Checking available funds.........");
        ensureFundsAvailable(donation);
        logger.info("Success: There are enough funds to match this donation.");

        logger.info("Starting bank Transactions.....");
        //TODO: BANK TRANSACTIONS OR POINTS TRANSACTIONS
        logger.info("Successfully completed bank transactions");

        logger.info("Checking if there are any fundings available that can wholly match the donation.......");
        List<Funding> fundingsGtEDAmountTimesRatio =
                getFundingsGreaterThanOrEqualToDonationAmountTimesFundingRatio(simpleDonatedAmount);

        if(fundingsGtEDAmountTimesRatio.size() > 0) {
            logger.info("There sure are fundings whose  current amount is greater than or equal to the " +
                            "donated amount * their ratios");
            logger.info("Randomly selecting a funding and performing all necessary operations...........");
            Funding selectedFunding = operationsOnSelectingFundingCase1(donation, simpleDonatedAmount, fundingsGtEDAmountTimesRatio);

            logger.info("Beginning money transactions...");
            double storyAmount = doDonationTransactionsCase1(donation, simpleDonatedAmount, selectedFunding);

            logger.info("Updating story amount after the transactions..." + story.toString());
            updateStoryAmount(story, storyAmount);
        } else {
            logger.info("There not exists any funding to serve this donation wholly");
            logger.info("Matching brands and perfoming necessary operations.............");
            Map<Brand, Integer> selectedBrands = operationsOnSelectingFundingsCase2(donation, simpleDonatedAmount);
            double grossStoryAmount = doDonationTransactionsCase2(donation, selectedBrands);
            updateStoryAmount(story, grossStoryAmount);
        }

    }

    private double doDonationTransactionsCase2(Donation donation, Map<Brand, Integer> selectedBrands) {
        //TODO: TRANSACTIONS
        double grossStoryAmount = 0;
        for (Map.Entry<Brand, Integer> entry: selectedBrands.entrySet()) {
            logger.info("Making transactions.....");
            System.out.println("Making transactions......");
            double microfunditAmount = entry.getValue() / 10;
            double brandAmount = entry.getValue() - microfunditAmount;
            double storyAmount = entry.getValue() + brandAmount;
            double realStoryAmount = storyAmount - microfunditAmount;
            grossStoryAmount += storyAmount;
            Transaction transaction = new Transaction(brandAmount, microfunditAmount, storyAmount, realStoryAmount);
            transactions.save(transaction);
            donation.getTransactions().add(transaction);
        }
        return grossStoryAmount;
    }

    private Map<Brand, Integer> operationsOnSelectingFundingsCase2(Donation donation, int simpleDonatedAmount) {
        logger.info("There not exists any funding to serve this donation wholly");
        Map<Brand, Integer> selectedBrands = new HashMap<>();
        //fundings with status open
        List<Funding> openFundings = fundings.findByStatus(1);
        //You had already confirmed that there is enough cash in the fundings. So no need of rechecking.
        //However other donors might have caused the data to be updated and thus invalid. Here is the big
        //question. todo; can be modified
        Funding selectedOpenFunding;
        int requiredAmount = simpleDonatedAmount;
        int counter = 0;
        //Break out of the loop only when all the donated amount has been matched OR all the open fundings have
        //been tested for matching. Due to modification by other donors this it is possible that all the donated
        // amount is not matched
        while(requiredAmount > 0 && counter < (openFundings.size() - 1)) {
            logger.info("Matching........");
            selectedOpenFunding = openFundings.get(counter);
            int matchesCount = 0;
            //Break the the required amount into 1s
            int fundingCurrAmount = selectedOpenFunding.getCurrentAmount();
            int fundingRatio = selectedOpenFunding.getRatio();
            while(fundingCurrAmount >= fundingRatio) {
                logger.info("Looping.....");
                requiredAmount -= 1;
                logger.info("Looping2.....");
                selectedOpenFunding.setCurrentAmount(selectedOpenFunding.getCurrentAmount()
                        - selectedOpenFunding.getRatio());
                logger.info("Looping3.....");
                fundings.save(selectedOpenFunding);
                logger.info("Looping4.....");
                matchesCount++;
                logger.info("Looping5.....");
                fundingCurrAmount--;
            }
            logger.info("Out of the loop");
            if(selectedOpenFunding.getCurrentAmount() == 0) {
                selectedOpenFunding.setStatus(0);
                fundings.save(selectedOpenFunding);
            }
            selectedBrands.put(selectedOpenFunding.getBrand(), matchesCount);
            counter++;
        }
        List<Brand> selectedBrandssList = new ArrayList<>();
        selectedBrandssList.addAll(selectedBrands.keySet());
        donation.setMatchedBrands(selectedBrandssList);
        donation.setMatchedAmount(simpleDonatedAmount - requiredAmount);
        return selectedBrands;
    }

    private void updateStoryAmount(Story story, double storyAmount) {
        //Update story's current amount
        story.setCurrentAmount(story.getCurrentAmount() + storyAmount);
        stories.save(story);
        logger.info("Successfully updated story " + story.toString());
    }

    private double doDonationTransactionsCase1(Donation donation, int simpleDonatedAmount, Funding selectedFunding) {
        //The brand amount to be used in this transaction
        double brandAmount = simpleDonatedAmount * selectedFunding.getRatio();
        double microfunditAmount = (brandAmount / 100.00) * 10.00;
        //Story amount to display to people
        double storyAmount = simpleDonatedAmount + brandAmount;
        //The real story amount after microfundit gets its share
        double realStoryAmount = storyAmount - microfunditAmount;
        Transaction transaction = new Transaction(brandAmount, microfunditAmount, storyAmount, realStoryAmount);
        logger.info("Creating transaction: " + transaction.toString());
        transactions.save(transaction);
        logger.info("Successfully saved transaction.");

        logger.info("Adding this transaction to this donation transactions list. Each of the donation field of each" +
                " transaction field will be set to this donation after it has been saved.");
        donationTransactions.add(transaction);

        logger.info("Setting the transactions for this donation");
        donation.setTransactions(Arrays.asList(transaction));

        return storyAmount;
    }

    private Funding operationsOnSelectingFundingCase1(Donation donation, int simpleDonatedAmount, List<Funding> fundingsGtEDAmountTimesRatio) {
        Funding selectedFunding;
        logger.info("Randomly selecting a funding...");
        //randomise the funding to select
        selectedFunding = fundingsGtEDAmountTimesRatio.get(random.nextInt(fundingsGtEDAmountTimesRatio.size()));
        logger.info("Successfully selected a funding. " + selectedFunding.getBrand() + " -> Current Amount " + selectedFunding.getCurrentAmount());

        logger.info("Decrementing the fundings current amount.....");
        //Decrement the funding's current amount
        selectedFunding.setCurrentAmount(selectedFunding.getCurrentAmount() - (simpleDonatedAmount * selectedFunding.getRatio()));
        logger.info("Successfully decremented funding. Changes to be persisted later." + selectedFunding.getBrand() + " -> Current Amount " +
                selectedFunding.getCurrentAmount());

        //check if the funding,s current amount is zero. If so close it
        if(selectedFunding.getCurrentAmount() <= 0) {
            logger.info("This funding current amount has dropped to 0. Closing funding..." + selectedFunding.getId());
            selectedFunding.setStatus(0);
            logger.info("Successfully closed funding. Changes to be persisted later.");
        }

        //save the updated brand
        logger.info("Starting Persisting this updated funding process....");
        logger.info("Finding the latest version of this funding...");
        Funding f = fundings.findOne(selectedFunding.getId());
        logger.info("Successfully found the latest version of this funding.");
        logger.info("Checking if the versions are the same..");
        if(f.getVersion() == selectedFunding.getVersion()) {
            logger.info("The versions of the two fundings are same. Persisting this funding...");
            fundings.save(selectedFunding);
            logger.info("Successfully persisted this funding update.");
        } else {
            logger.info("OOps. Versions of the 2 fundings are not same. It has must have been modified by another transaction.");
            //TODO: WHAT HAPPENS IF VERSIONS ARE NOT SAME. 1.==
            logger.info("");
        }

        //TODO: YOU MAY WANT TO RELOCATE THESE CODES TO THE IF BLOCK ABOVE CONSIDERING THE 1.== TODO CONFITION
        logger.info("Setting matched brands for donation to this fundings brand");
        //set the matched brand of the donation to this fundings brand
        donation.setMatchedBrands(Arrays.asList(selectedFunding.getBrand()));

        logger.info("Setting matched amount to the wholly donated amount.");
        donation.setMatchedAmount(simpleDonatedAmount);

        return selectedFunding;
    }

    private List<Funding> getFundingsGreaterThanOrEqualToDonationAmountTimesFundingRatio(int simpleDonatedAmount) {
        logger.info("Fetching all fundings whose current amount is gretaer than or equal to the donated amount");
        //fundings with current amount greater than or equal to amount donated
        List<Funding> fundingsGtEDAmount = fundings.findBycurrentAmountGreaterThan(simpleDonatedAmount - 1);
        logger.info("Found " + fundingsGtEDAmount.size() + " fundings whose amount is greater than or equal to " +
                "the donated amount.");

        logger.info("Looping through the " + fundingsGtEDAmount.size() + " donations with current amount greater than" +
                " or equal to donated amount, to determine which ones have an amount greater than or equal to the amount" +
                " donated * the funding ratio.");
        //Fundings with current amounnt greater than or equal to amount donated * ratio of funding
        List<Funding> fundingsGtEDAmountTimesRatio = new ArrayList<>();
        for(Funding funding: fundingsGtEDAmount) {
            logger.info("Looping....Comparing (funding.getRatio() * simpleDonatedAmount) ="
                    +(funding.getRatio() * simpleDonatedAmount) + " with (funding.getCurrentAmount()) =" + funding.getCurrentAmount()  );
            if((funding.getRatio() * simpleDonatedAmount) <= funding.getCurrentAmount()) {
                logger.info("Added this funding to fundings whose current amount is greater than or equal to the " +
                        "donated amount * its ratio");
                fundingsGtEDAmountTimesRatio.add(funding);
            }
        }
        logger.info(fundingsGtEDAmountTimesRatio + " fundings added to fundings list with fundings  whose" +
                " current amount is greater than or equal to the donated amount * its ratio");
        return fundingsGtEDAmountTimesRatio;
    }

    private void ensureFundsAvailable(Donation donation) {
        int availableFunds = 0;
        logger.info("Looping though all OPEN fundings...");
        for (Funding funding : fundings.findByStatus(1)) {
            //Divide by ratio to ensure you are comparing in 1:1 ratio
            availableFunds += (funding.getCurrentAmount() / funding.getRatio());
            logger.info("Looping... " + availableFunds);
        }
        if (availableFunds <= 0) {
            logger.error("OOps. Unsuccessful. There are currently no funds from brands for matching.");
            throw new RuntimeException("There are currently no funds from brands for matching.");
        } else if(availableFunds < donation.getAmount()) {
            //The client can use this message to know how much they status of the fundings and how much they
            // can therefore donate.
            logger.error("OOps. Unsuccessful. The available funds allow you to only donate " + availableFunds);
            throw new RuntimeException("There are currently is no enough funds to match your donation. Please donate an amount" +
                    "less than or equal to: " + availableFunds);
        }
    }

    private void doStoryChecks(Story story) {
        if(story.getStatus() == 0) {
            logger.error("Story checks failed. This story has been closed.");
            throw new UnsupportedOperationException("This story has been closed.");
        }
        if (isDateExpired(story.getDateAdded(), story.getTimeAllocated())) {
            logger.info("Time allocated to this story has elapsed. Closing story....." + story.getDescription().substring(0, 100) +"... ");
            story.setStatus(0);
            stories.save(story);
            logger.info("Story successfully closed.");
            logger.error("Story checks failed. This story has been closed. Time allocated to it has elapsed.");
            throw new UnsupportedOperationException("This story has been closed. Time allocated to it has elapsed.");
        }
        if (story.getCurrentAmount() >= story.getTargetAmount()) {
            logger.info("The target amount set for this story has been hit. Closing story......" + story.getDescription().substring(0, 100) +"... ");
            story.setStatus(0);
            stories.save(story);
            throw new UnsupportedOperationException("This story has already been closed. The target amount has been hit");
        }
    }

    private int doAmountChecks(Donation donation) {
        int simpleDonatedAmount;//Donation is only of 2 types.  1 == CASH or 0 == POINTS
        if(donation.getType() == 1) {
            logger.info("Donation is in form of cash.");
            //Cash Donation amount maximum is 5
            donation.setPointsCompany(null);
            if(donation.getAmount() > 5) {
                logger.error("Donation Amount checks failed. You cannot donate more than 5 dollars");
                throw new RuntimeException("You cannot donate more than 5 dollars");
            } else {
                simpleDonatedAmount = donation.getAmount();
            }
        } else if(donation.getType() == 0) {
            logger.info("Donation is in form of points.");
            //Points Donation maximum is 2500 and must be a multiple of 500
            if((donation.getAmount() > 2500)) {
                logger.error("Donation Amount checks failed. You cannot donate more than 2500 points");
                throw new RuntimeException("You cannot donate more than 2500 points");
            } else if(donation.getAmount() % 500 != 0) {
                logger.error("Donation Amount checks failed. Donation in points must be a multiple of 500");
                throw new RuntimeException("Donation in points must be a multiple of 500");
            } else {
                simpleDonatedAmount = donation.getAmount()/500;
            }
        } else {
            logger.error("Donation Amount checks failed. Invalid transaction type: Types can only be 1 = CASH or 0 = POINTS");
            throw new RuntimeException("Invalid transaction type: Types can only be 1 = CASH or 0 = POINTS");
        }
        return simpleDonatedAmount;
    }

    private boolean isDateExpired(Date date, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, days);
        return Calendar.getInstance().after(c);
    }

    @HandleAfterCreate
    public void addTransactionsToSavedDonations(Donation donation) {
        for (Transaction t: donationTransactions) {
            t.setDonation(donation);
            transactions.save(t);
        }
        donationTransactions = new ArrayList<>();
    }
}

