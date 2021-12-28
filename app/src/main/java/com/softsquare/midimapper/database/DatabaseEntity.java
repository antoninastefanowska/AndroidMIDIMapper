package com.softsquare.midimapper.database;

public abstract class DatabaseEntity {
    private Long id;
    private Long parentId;

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }
}
