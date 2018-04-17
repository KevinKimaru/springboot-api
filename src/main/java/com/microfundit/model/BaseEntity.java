package com.microfundit.model;

import javax.persistence.*;

/**
 * Created by Kevin Kimaru Chege on 3/21/2018.
 */
@MappedSuperclass
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private final Long id;
    @Version
    private Long version;

    protected BaseEntity() {
        id = null;
    }

    public Long getId() {
        return id;
    }

    public Long getVersion() {
        return version;
    }
}
