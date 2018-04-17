package com.microfundit.controller;

import java.util.List;

/**
 * Created by Kevin Kimaru Chege on 4/10/2018.
 */
public class DonationImpl {
    private long story;
    private int amount;
    private int type;
    private long pointsCompany;

    private String stStory;
    private List<String> matchedBrands;
    private double matchedAmount;

    public DonationImpl() {

    }

    public DonationImpl(long story, int amount, int type, int pointsCompany) {
        this.story = story;
        this.amount = amount;
        this.type = type;
        this.pointsCompany = pointsCompany;
    }

    public DonationImpl(int amount, int type, String stStory, long storyId, List<String> matchedBrands, double matchedAmount) {
        this.amount = amount;
        this.type = type;
        this.stStory = stStory;
        this.matchedBrands = matchedBrands;
        this.matchedAmount = matchedAmount;
    }

    public String getStStory() {
        return stStory;
    }

    public void setStStory(String stStory) {
        this.stStory = stStory;
    }

    public List<String> getMatchedBrands() {
        return matchedBrands;
    }

    public void setMatchedBrands(List<String> matchedBrands) {
        this.matchedBrands = matchedBrands;
    }

    public double getMatchedAmount() {
        return matchedAmount;
    }

    public void setMatchedAmount(double matchedAmount) {
        this.matchedAmount = matchedAmount;
    }

    public long getStory() {
        return story;
    }

    public void setStory(long story) {
        this.story = story;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getPointsCompany() {
        return pointsCompany;
    }

    public void setPointsCompany(long pointsCompany) {
        this.pointsCompany = pointsCompany;
    }
}
