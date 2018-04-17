package com.microfundit.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Kevin Kimaru Chege on 3/21/2018.
 */
@Entity
public class Donor extends User {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfBirth;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateAdded;
    //Either MALE = 1 or FEMALE = 0
    private int gender;
    @OneToMany(mappedBy = "donor")
    private List<Donation> donations;
    @OneToMany(mappedBy = "donor")
    private List<Subscription> subscriptions;

    protected Donor() {
        super();
        dateOfBirth = new Date();
        donations = new ArrayList<>();
        subscriptions = new ArrayList<>();
    }

    public Donor(String firstName, String lastName,
                String email, String phoneNumber, int gender) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
    }

    @Override
    public Long getId() {
        return super.getId();
    }

    public void setDonations(List<Donation> donations) {
        this.donations = donations;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public List<Donation> getDonations() {
        return donations;
    }

    public void addDonation(Donation donation) {
        donation.setDonor(this);
        donations.add(donation);
    }
}
