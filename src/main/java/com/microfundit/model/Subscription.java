package com.microfundit.model;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by Kevin Kimaru Chege on 3/29/2018.
 */
@Entity
public class Subscription extends BaseEntity {

    //0 = DAY 1 = WEEK 2 = 2WEEKS 3 = 1 MONTH
    @NotNull
    private int period;
    //Amount to cut
    @NotNull
    private int amount;
    @OneToMany
    @ElementCollection
    private List<Donation> donations;
    @NotNull
    @ManyToOne
    private Donor donor;
    //Number of times set to cut before it is closed
    @NotNull
    private int setTransactions;
    //The number of times it has currently cut
    @NotNull
    private int currentTransactions;
    //0 = CLOSED 1 = OPEN
    @NotNull
    private int status;
    @NotNull
    @ManyToOne
    private Story story;
    //CASH = 1 or POINTS = 0
    @NotNull
    private int type;
    @ManyToOne
    private PointsCompany pointsCompany;

    protected Subscription() {
        super();
    }

    public Subscription(int time, int amount, Donor donor, int type, PointsCompany pointsCompany) {
        this.period = time;
        this.amount = amount;
        this.donor = donor;
        this.type = type;
        this.pointsCompany = pointsCompany;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public PointsCompany getPointsCompany() {
        return pointsCompany;
    }

    public void setPointsCompany(PointsCompany pointsCompany) {
        this.pointsCompany = pointsCompany;
    }

    public Story getStory() {
        return story;
    }

    public void setStory(Story story) {
        this.story = story;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public List<Donation> getDonations() {
        return donations;
    }

    public void setDonations(List<Donation> donations) {
        this.donations = donations;
    }

    public Donor getDonor() {
        return donor;
    }

    public void setDonor(Donor donor) {
        this.donor = donor;
    }

    public int getSetTransactions() {
        return setTransactions;
    }

    public void setSetTransactions(int setTransactions) {
        this.setTransactions = setTransactions;
    }

    public int getCurrentTransactions() {
        return currentTransactions;
    }

    public void setCurrentTransactions(int currentTransactions) {
        this.currentTransactions = currentTransactions;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
