package com.zibran.newsapp.models;

import java.io.Serializable;

public class NewsSource implements Serializable {

    private Object id;
    private String name;

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
