package com.microfundit.model;

import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by Kevin Kimaru Chege on 4/4/2018.
 */
@Entity
public class User extends BaseEntity {
    @Column(unique = true)
    @NotNull
    @Size(min = 2, max = 50)
    private String username;
    @NotNull
    @Size(min = 2)
    @RestResource(exported = false)
    private String password;
    private String role;

    protected User() {
        super();
    }

    public User(String username, String password, String role) {
        this();
        this.username = username;
        setPassword(password);
        this.role = role;
    }

    @Override
    public Long getId() {
        return super.getId();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

//    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
