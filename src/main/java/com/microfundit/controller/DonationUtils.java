package com.microfundit.controller;

import com.microfundit.dao.*;
import com.microfundit.model.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.microfundit.Application.RANDOM;

/**
 * Created by Kevin Kimaru Chege on 4/10/2018.
 */
@Component
public class DonationUtils {
    @Autowired
    private StoryRepository stories;
    @Autowired
    private FundingRepository fundings;
    @Autowired
    private TransactionRepository transactions;
    @Autowired
    private DonorRepository donors;
    @Autowired
    private DonationRepository donations;

    static Logger logger = Logger.getLogger(DonationUtils.class);
    private List<Transaction> donationTransactions = new ArrayList<>();
    Random random = new Random(System.currentTimeMillis());

    @Autowired
    public DonationUtils(StoryRepository stories, FundingRepository fundings, TransactionRepository transactions,
                         DonorRepository donors, DonationRepository donations) {
        this.stories = stories;
        this.fundings = fundings;
        this.transactions = transactions;
        this.donors = donors;
        this.donations = donations;
        logger.info("+++++++++++++++\n+++++++++++\n++++++++++++\n+++++++++++++\n\n");
    }

    public Donation donate(Donation donation) {
        logger.info("\n\n+++++++++++++++STARTING DONATION PROCESS++++++++++++++++++++++..........");

        logger.info("\n\nSetting donation date..");
        donation.setDateAdded(new Date());
        logger.info("Succefully set Donation date");

        Donor donor = donors.findOne();
        logger.info("\n\nSetting donor...: " + donor);
        if (donor != null) {
            donation.setDonor(donor);
            logger.info("Donor succefully set. " + donor);
        } else {
            logger.info("Unsuccesful: // Error: This donor does not exist.");
            throw new RuntimeException("Error: Donation from unregistered Donor");
        }

        logger.info("\n\nDoing amount checks and validations.....");
        //Amount donated be it points or cash converted into cash in dollars
        int simpleDonatedAmount;
        simpleDonatedAmount = doAmountChecks(donation);
        logger.info("Successfully done amount checks and validations.");

        logger.info("\n\nDoing story checks and validations....");
        Story story = donation.getStory();
        doStoryChecks(story);
        logger.info("Successfully done story checks and validations.");

        logger.info("\n\nChecking available funds.........");
        ensureFundsAvailable(donation);
        logger.info("Success: There are enough funds to match this donation.");

        logger.info("\n\nStarting bank Transactions.....");
        //TODO: BANK TRANSACTIONS OR POINTS TRANSACTIONS
        logger.info("Successfully completed bank transactions");

        postBankOperations(donation, simpleDonatedAmount, story);

        logger.info("\n\n*************DONATION PROCESS SUCCESSFULLY COMPLETED.**********\n\n");
        return donation;
    }

    private synchronized void postBankOperations(Donation donation, int simpleDonatedAmount, Story story) {
        try {
            logger.info("\n\nChecking if there are any fundings available that can wholly match the donation.......");
            List<Funding> fundingsGtEDAmountTimesRatio =
                    getFundingsGreaterThanOrEqualToDonationAmountTimesFundingRatio(simpleDonatedAmount);

            if (fundingsGtEDAmountTimesRatio.size() > 0) {
                logger.info("\n\nThere sure are fundings whose  current amount is greater than or equal to the " +
                        "donated amount * their ratios");
                logger.info("\n\nRandomly selecting a funding and performing all necessary operations...........");
                Funding selectedFunding = operationsOnSelectingFundingCase1(donation, simpleDonatedAmount, fundingsGtEDAmountTimesRatio);

                logger.info("\n\nBeginning money transactions...");
                double storyAmount = doDonationTransactionsCase1(donation, simpleDonatedAmount, selectedFunding);

                logger.info("\n\nUpdating story amount after the transactions..." + story.toString());
                updateStoryAmount(story, storyAmount);
                logger.info("Successfully updated current amount.");
            } else {
                logger.info("\n\nThere not exists any funding to serve this donation wholly");

                logger.info("\n\nMatching brands and perfoming necessary operations.............");
                Map<Funding, Double> selectedFundings = operationsOnSelectingFundingsCase2(donation, simpleDonatedAmount);
                logger.info("Successfully finished operations....");

                logger.info("\n\nBeginning money transaction...");
                double grossStoryAmount = doDonationTransactionsCase2(donation, selectedFundings);
                logger.info("Successfuly completed money transaction.");

                logger.info("\n\nUpdating story current amount...");
                updateStoryAmount(story, grossStoryAmount);
                logger.info("Successfully updated current amount.");
            }
            logger.info("\n\nSaving this donation.....");
            donations.save(donation);
            logger.info("Successfully saved this donation.");

            logger.info("\n\nSetting this donation's transactions to this donation.");
            addTransactionsToSavedDonations(donation);
            logger.info("Successfully set the transaction's donation to this donation.");
        }catch(Exception e) {
            //TODO: FIND A WAY OF NOTIFYING ADMIN IF SUCH A THING HAPPENS
            logger.info("=======OOps post bank transactions failed======THIS WILL CAUSE DATA INCONCISTENCY.!!!!!!!!");
            e.printStackTrace();
        }
    }

    private double doDonationTransactionsCase2(Donation donation, Map<Funding, Double> selectedFundings) {
        //TODO: TRANSACTIONS
        logger.info("Beginning Looping all the " + selectedFundings.size() + " matched fundings to calculate money transactions......");
        double grossStoryAmount = 0;
        for (Map.Entry<Funding, Double> entry : selectedFundings.entrySet()) {
            logger.info("Looping....transacting for " + entry.getKey().getBrand().getName());
            //The brand amount to be used in this transaction
            double brandAmount = entry.getValue() * entry.getKey().getRatio();
            double microfunditAmount = (brandAmount /100.0) * 10.0;
            //Story amount to display to people
            double storyAmount = entry.getValue() + brandAmount;
            //The real story amount after microfundit gets its share
            double realStoryAmount = storyAmount - microfunditAmount;
            grossStoryAmount += storyAmount;
            Transaction transaction = new Transaction(brandAmount, microfunditAmount, storyAmount, realStoryAmount);
            transaction.setFunding(entry.getKey());
            transaction.setStory(donation.getStory());
            logger.info("Looping..Calculated transaction....microfunditAmount => " + microfunditAmount +
                    " brandAmount => " + brandAmount +
                    " storyAmount => " + storyAmount +
                    " realStoryAmount => " + realStoryAmount +
                    "story => " + transaction.getStory().getDescription().substring(0, 60) + "..." +
                    " funding => " + transaction.getFunding().getBrand().getName() + " == " + transaction.getFunding().getPlacedAmount());
            logger.info("Looping...Saving this transaction......");
            transactions.save(transaction);
            logger.info("Looping...Successfully saved transaction.");

            logger.info("Looping...Adding this transaction to this funding's list of transactions...");
            Funding funding = fundings.findOne(entry.getKey().getId());
            if(funding != null) {
                funding.getTransactions().add(transaction);
                fundings.save(funding);
            } else {
                logger.info("OOPs!! Error. This funding does not exist");
                //TODO:
            }
            logger.info("Looping.. Succesfully added transaction to this funding and persisted it.");

            logger.info("Looping...Adding this transaction to this story's list of transactions...");
            Story story = stories.findOne(donation.getStory().getId());
            if(story != null) {
                story.getTransactions().add(transaction);
                stories.save(story);
            } else {
                logger.info("Looping...OOPs!! Error. This Story does not exist");
                //TODO:
            }
            logger.info("Looping.. Succesfully added transaction to this story's and persisted it.");


            //Add it to the list of transactions. so it is this transaction donation is set to this donation after
            //this donation is saved
            donationTransactions.add(transaction);

            logger.info("Looping...Adding this transaction to donation");
            donation.getTransactions().add(transaction);

            logger.info("Looping...Current gross story amount " + grossStoryAmount + "\n");
        }
        logger.info("Total gross story amount. " + grossStoryAmount);
        return grossStoryAmount;
    }

    private Map<Funding, Double> operationsOnSelectingFundingsCase2(Donation donation, int simpleDonatedAmount) {
        Map<Funding, Double> selectedFundingsMap = new HashMap<>();
        logger.info("Finding all open fundings...");
        //fundings with status open
        List<Funding> openFundings = fundings.findByStatus(1);
        logger.info("Found " + openFundings.size() + " open fundigs.");
        //You had already confirmed that there is enough cash in the fundings. So no need of rechecking.
        //However other donors might have caused the data to be updated and thus invalid. Here is the big
        //question. todo; can be modified
        Funding selectedOpenFunding;
        int requiredAmount = simpleDonatedAmount;
        int counter = 0;
        //Break out of the loop only when all the donated amount has been matched OR all the open fundings have
        //been tested for matching. Due to modification by other donors this it is possible that all the donated
        // amount is not matched
        logger.info("Beginning loop1...... This loop loops until all the donated amount has been matched AND" +
                "as long as there are available fundings to deduct from.");
        while (requiredAmount > 0 && counter < openFundings.size()) {
            logger.info("Looping1........Getting funding " + counter);
            selectedOpenFunding = openFundings.get(counter);
            logger.info("Looping1........Funding found.Its current amount is " + selectedOpenFunding.getCurrentAmount());
            int matchesCount = 0;
            //Break the the required amount into 1s
            int fundingCurrAmount = selectedOpenFunding.getCurrentAmount();
            int fundingRatio = selectedOpenFunding.getRatio();
            logger.info("Looping1........Beginning loop 1.1.....This will loop as long as the funding current amount" +
                    "is greater than or equal to its ratio AND all the donated amount is not matched");
            while (fundingCurrAmount >= fundingRatio && requiredAmount > 0) {
                requiredAmount -= 1;
                logger.info("Looping 1.1....Deducting this funding current amount which currently is "
                        + fundingCurrAmount + " by 1(its ratio)");
                selectedOpenFunding.setCurrentAmount(selectedOpenFunding.getCurrentAmount()
                        - selectedOpenFunding.getRatio());
                logger.info("Looping 1.1....Deducted this funding current amount. Its current amount now is "
                        + selectedOpenFunding.getCurrentAmount() +"\n");
                matchesCount++;
                fundingCurrAmount--;
            }
            logger.info("Looping1...Finished deducting from this funding. Its current amount is " +
                    selectedOpenFunding.getCurrentAmount());
            if (selectedOpenFunding.getCurrentAmount() <= 0) {
                logger.info("Looping1...The amount of this funding has droppped to 0. Closing the story thererfore....");
                selectedOpenFunding.setStatus(0);
            }
            logger.info("Looping1...Saving changes made to this funding " + selectedOpenFunding.getBrand().getName() +
                    "-> Placed ammount:" + selectedOpenFunding.getPlacedAmount());
            fundings.save(selectedOpenFunding);
            logger.info("Looping1....Successsfully persisted the changes.");

            logger.info("Looping 1...Generally, this funding from " + selectedOpenFunding.getBrand().getName() +
                    " has matched $" + matchesCount);
            selectedFundingsMap.put(selectedOpenFunding, (double) matchesCount);
            logger.info("Currently matched fundings: " + selectedFundingsMap.size());
            logger.info("Looping1...End of loop " + counter + "\n");
            counter++;
        }

        List<Brand> selectedBrandssList = new ArrayList<>();
        selectedFundingsMap.forEach((f, i)-> {
            selectedBrandssList.add(f.getBrand());
        });
        logger.info("Selected matched brands are " + selectedBrandssList + " -> " + selectedBrandssList.size());
        logger.info("Donated amount is...." + donation.getAmount() + " and matched amount is " +
                (simpleDonatedAmount - requiredAmount));
        logger.info("Setting the matched brands and matched amount.");
        donation.setMatchedBrands(selectedBrandssList);
        donation.setMatchedAmount(simpleDonatedAmount - requiredAmount);
        return selectedFundingsMap;
    }

    private void updateStoryAmount(Story story, double storyAmount) {
        //Update story's current amount
        story.setCurrentAmount(story.getCurrentAmount() + storyAmount);
        if(story.getCurrentAmount() >= story.getTargetAmount()) {
            logger.info("This story has reached its target amount. Closing story..");
            story.setStatus(0);
        }
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
        transaction.setFunding(selectedFunding);
        transaction.setStory(donation.getStory());
        logger.info("Creating transaction: " + transaction.toString());
        transactions.save(transaction);
        logger.info("Successfully saved transaction.");

        logger.info("Adding this transaction to this funding's list of transactions...");
        Funding funding = fundings.findOne(selectedFunding.getId());
        if(funding != null) {
            funding.getTransactions().add(transaction);
            fundings.save(funding);
        } else {
            logger.info("OOPs!! Error. This funding does not exist");
            //TODO:
        }
        logger.info("Succesfully added transaction to this funding and persisted it.");

        logger.info("Adding this transaction to this story's list of transactions...");
        Story story = stories.findOne(donation.getStory().getId());
        if(story != null) {
            story.getTransactions().add(transaction);
            stories.save(story);
        } else {
            logger.info("OOPs!! Error. This Story does not exist");
            //TODO:
        }
        logger.info("Looping.. Succesfully added transaction to this story's and persisted it.");


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
        selectedFunding = fundingsGtEDAmountTimesRatio.get(RANDOM.nextInt(fundingsGtEDAmountTimesRatio.size()));
        logger.info("Successfully selected a funding. " + selectedFunding.getBrand() + " -> Current Amount " + selectedFunding.getCurrentAmount());

        logger.info("Decrementing the fundings current amount.....");
        //Decrement the funding's current amount
        selectedFunding.setCurrentAmount(selectedFunding.getCurrentAmount() - (simpleDonatedAmount * selectedFunding.getRatio()));
        logger.info("Successfully decremented funding. Changes to be persisted later." + selectedFunding.getBrand() + " -> Current Amount " +
                selectedFunding.getCurrentAmount());

        //check if the funding,s current amount is zero. If so close it
        if (selectedFunding.getCurrentAmount() <= 0) {
            logger.info("This funding current amount has dropped to 0. Closing funding..." + selectedFunding.getId());
            selectedFunding.setStatus(0);
            logger.info("Successfully closed funding. Changes to be persisted later.");
        }

        //save the updated brand
        logger.info("Persisting this updated funding ....");
        fundings.save(selectedFunding);
        logger.info("Successfully persisted this funding update.");

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
        for (Funding funding : fundingsGtEDAmount) {
            logger.info("Looping....Comparing (funding.getRatio() * simpleDonatedAmount) ="
                    + (funding.getRatio() * simpleDonatedAmount) + " with (funding.getCurrentAmount()) =" + funding.getCurrentAmount());
            if ((funding.getRatio() * simpleDonatedAmount) <= funding.getCurrentAmount()) {
                logger.info("Added this funding to fundings whose current amount is greater than or equal to the " +
                        "donated amount * its ratio");
                fundingsGtEDAmountTimesRatio.add(funding);
            }
            logger.info("\n");
        }
        logger.info(fundingsGtEDAmountTimesRatio.size() + " fundings added to fundings list with fundings  whose" +
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
        if (availableFunds < 1) {
            logger.error("OOps. Unsuccessful. There are currently no funds from brands for matching.");
            throw new RuntimeException("There are currently no funds from brands for matching.");
        } else if (availableFunds < donation.getAmount()) {
            //The client can use this message to know how much they status of the fundings and how much they
            // can therefore donate.
            logger.error("OOps. Unsuccessful. The available funds allow you to only donate " + availableFunds);
            throw new RuntimeException("There are currently is no enough funds to match your donation. Please donate an amount" +
                    " less than or equal to: " + availableFunds);
        }
    }

    private void doStoryChecks(Story story) {
        if (story.getStatus() == 0) {
            logger.error("Story checks failed. This story has been closed.");
            throw new UnsupportedOperationException("This story has been closed.");
        }
        if (isDateExpired(story.getDateAdded(), story.getTimeAllocated())) {
            logger.info("Time allocated to this story has elapsed. Closing story....." + story.getDescription().substring(0, 100) + "... ");
            story.setStatus(0);
            stories.save(story);
            logger.info("Story successfully closed.");
            logger.error("Story checks failed. This story has been closed. Time allocated to it has elapsed.");
            throw new UnsupportedOperationException("This story has been closed. Time allocated to it has elapsed.");
        }
        if (story.getCurrentAmount() >= story.getTargetAmount()) {
            logger.info("The target amount set for this story has been hit. Closing story......" + story.getDescription().substring(0, 100) + "... ");
            story.setStatus(0);
            stories.save(story);
            throw new UnsupportedOperationException("This story has already been closed. The target amount has been hit");
        }
    }

    private int doAmountChecks(Donation donation) {
        int simpleDonatedAmount;//Donation is only of 2 types.  1 == CASH or 0 == POINTS
        if (donation.getType() == 1) {
            logger.info("Donation is in form of cash.");
            //Cash Donation amount maximum is 5
            donation.setPointsCompany(null);
            if (donation.getAmount() > 5) {
                logger.error("Donation Amount checks failed. You cannot donate more than 5 dollars");
                throw new RuntimeException("You cannot donate more than 5 dollars");
            } else {
                simpleDonatedAmount = donation.getAmount();
            }
        } else if (donation.getType() == 0) {
            logger.info("Donation is in form of points.");
            //Points Donation maximum is 2500 and must be a multiple of 500
            if ((donation.getAmount() > 2500)) {
                logger.error("Donation Amount checks failed. You cannot donate more than 2500 points");
                throw new RuntimeException("You cannot donate more than 2500 points");
            } else if (donation.getAmount() % 500 != 0) {
                logger.error("Donation Amount checks failed. Donation in points must be a multiple of 500");
                throw new RuntimeException("Donation in points must be a multiple of 500");
            } else {
                simpleDonatedAmount = donation.getAmount() / 500;
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

    public void addTransactionsToSavedDonations(Donation donation) {
        logger.info("Beginning looping all donation transactions....How many ->" + donationTransactions.size());
        for (Transaction t : donationTransactions) {
            logger.info("Looping...setting donation for this transaction");
            t.setDonation(donation);
            logger.info("Looping...saving the changes for this transaction....");
            transactions.save(t);
            logger.info("Looping...Successfully saved the changes\n");
        }
        donationTransactions = new ArrayList<>();
    }
}
