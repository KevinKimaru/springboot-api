package com.microfundit.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Kevin Kimaru Chege on 3/21/2018.
 */
@Entity
public class Funding extends BaseEntity {
    @NotNull
    @ManyToOne
    private Brand brand;
    @NotNull
    private int placedAmount;
    @NotNull
    private int currentAmount;
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date dateAdded;
    //Can either be CLOSED = 0 or OPENED = 1
    @NotNull
    private int status;
    @NotNull
    private int ratio;
    @OneToMany(mappedBy = "funding")
    private List<Transaction> transactions;


    protected Funding() {
        super();
        dateAdded = new Date();
        status = 1;
        transactions = new ArrayList<>();
    }

    public Funding(Brand brand, int placedAmount, int ratio) {
        this();
        this.brand = brand;
        this.placedAmount = placedAmount;
        this.ratio = ratio;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public int getRatio() {
        return ratio;
    }

    public void setRatio(int ratio) {
        this.ratio = ratio;
    }

    public int getPlacedAmount() {
        return placedAmount;
    }

    public void setPlacedAmount(int placedAmount) {
        this.placedAmount = placedAmount;
    }

    public int getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(int currentAmount) {
        this.currentAmount = currentAmount;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Funding{" +
                "brand=" + brand +
                ", placedAmount=" + placedAmount +
                ", currentAmount=" + currentAmount +
                ", dateAdded=" + dateAdded +
                ", status=" + status +
                ", ratio=" + ratio +
                '}';
    }
}
