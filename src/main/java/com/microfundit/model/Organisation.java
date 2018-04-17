package com.microfundit.model;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Kevin Kimaru Chege on 3/21/2018.
 */
@Entity
public class Organisation extends BaseEntity {
    @NotNull
    @Size(min = 2, max = 80)
    private String name;
    private String description;
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateAdded;
    @OneToMany(mappedBy = "organisation")
    private List<Story> stories;

    protected Organisation() {
        super();
        dateAdded = new Date();
        stories = new ArrayList<>();
    }

    public Organisation(String name, String description) {
        this();
        this.name = name;
        this.description = description;
    }

    @Override
    public Long getId() {
        return super.getId();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public List<Story> getStories() {
        return stories;
    }

    public void addStory(Story story) {
       story.setOrganisation(this);
       stories.add(story);
    }
}
