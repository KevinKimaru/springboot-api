package com.microfundit.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

/**
 * Created by Kevin Kimaru Chege on 3/21/2018.
 */
@Entity
public class Transaction extends BaseEntity {
//    @NotNull
    @ManyToOne
    private Donation donation;
    @NotNull
    @Digits(integer = 10, fraction = 2)
    private double brandAmount;
    @NotNull
    @Digits(integer = 10, fraction = 2)
    private double microfunditAmount;
    @NotNull
    @Digits(integer = 10, fraction = 2)
    private double storyAmount;
    @NotNull
    @Digits(integer = 10, fraction = 2)
    private double realStoryAmount;
    @ManyToOne
    private Funding funding;
    @ManyToOne
    private Story story;

    protected Transaction() {
        super();
    }

    public Transaction(double brandAmount, double microfunditAmount, double storyAmount, double realStoryAmount) {
        this();
        this.brandAmount = brandAmount;
        this.microfunditAmount = microfunditAmount;
        this.storyAmount = storyAmount;
        this.realStoryAmount = realStoryAmount;
    }

    public Funding getFunding() {
        return funding;
    }

    public void setFunding(Funding funding) {
        this.funding = funding;
    }

    public Story getStory() {
        return story;
    }

    public void setStory(Story story) {
        this.story = story;
    }

    public Donation getDonation() {
        return donation;
    }

    public double getRealStoryAmount() {
        return realStoryAmount;
    }

    public void setRealStoryAmount(double realStoryAmount) {
        this.realStoryAmount = realStoryAmount;
    }

    public void setDonation(Donation donation) {
        this.donation = donation;
    }

    public double getBrandAmount() {
        return brandAmount;
    }

    public void setBrandAmount(double brandAmount) {
        this.brandAmount = brandAmount;
    }

    public double getMicrofunditAmount() {
        return microfunditAmount;
    }

    public void setMicrofunditAmount(double microfunditAmount) {
        this.microfunditAmount = microfunditAmount;
    }

    public double getStoryAmount() {
        return storyAmount;
    }

    public void setStoryAmount(double storyAmount) {
        this.storyAmount = storyAmount;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                ", brandAmount=" + brandAmount +
                ", microfunditAmount=" + microfunditAmount +
                ", storyAmount=" + storyAmount +
                ", realStoryAmount=" + realStoryAmount +
                '}';
    }
}
