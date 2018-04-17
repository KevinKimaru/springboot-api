package com.microfundit.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Kevin Kimaru Chege on 3/21/2018.
 */
@Entity
public class Story extends BaseEntity {
    @NotNull
    @Size(min = 2)
    private String description;
    @NotNull
    @ManyToOne
    private Organisation organisation;
    @NotNull
    private double targetAmount;
    @NotNull
    private double currentAmount;
    @NotNull
    private int timeAllocated;
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date dateAdded;
    @OneToMany(mappedBy = "story")
    private List<Donation> donations;
    //either OPEN = 1 or CLOSED = 0
    @NotNull
    private int status;
    @ElementCollection(targetClass = String.class)
    private List<String> images;
    @OneToMany
    private List<Transaction> transactions;

    protected Story() {
        super();
        donations = new ArrayList<>();
        dateAdded = new Date();
        status = 1;
        images = new ArrayList<>();
        transactions = new ArrayList<>();
    }

    public Story(String description, Organisation organisation, int targetAmount, int timeAllocated) {
        this();
        this.description = description;
        this.organisation = organisation;
        this.targetAmount = targetAmount;
        this.timeAllocated = timeAllocated;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void setDonations(List<Donation> donations) {
        this.donations = donations;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public double getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(double targetAmount) {
        this.targetAmount = targetAmount;
    }

    public double getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(double currentAmount) {
        this.currentAmount = currentAmount;
    }

    public int getTimeAllocated() {
        return timeAllocated;
    }

    public void setTimeAllocated(int timeAllocated) {
        this.timeAllocated = timeAllocated;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public List<Donation> getDonations() {
        return donations;
    }

    public void addDonation(Donation donation) {
        donation.setStory(this);
        donations.add(donation);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Story{" +
                "description='" + description + '\'' +
                ", organisation=" + organisation +
                ", targetAmount=" + targetAmount +
                ", currentAmount=" + currentAmount +
                ", timeAllocated=" + timeAllocated +
                ", dateAdded=" + dateAdded +
                ", status=" + status +
                '}';
    }
}
