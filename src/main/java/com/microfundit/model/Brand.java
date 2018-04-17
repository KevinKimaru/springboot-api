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
public class Brand extends BaseEntity {
    @NotNull
    @Size(min = 2, max = 80)
    private String name;
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull()
    private Date dateAdded;
    @OneToMany(mappedBy = "brand")
    private List<Funding> fundings;
    @ManyToMany(mappedBy = "matchedBrands")
    private List<Donation> donations;

    protected Brand() {
        super();
        dateAdded = new Date();
        fundings = new ArrayList<>();
        donations = new ArrayList<>();
    }

    public Brand(String name) {
        this();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public List<Funding> getFundings() {
        return fundings;
    }



    public List<Donation> getDonations() {
        return donations;
    }

    public void setFundings(List<Funding> fundings) {
        this.fundings = fundings;
    }

    public void setDonations(List<Donation> donations) {
        this.donations = donations;
    }
}
