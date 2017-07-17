package com.yamert.icepass.models;

import java.util.Date;
import java.util.List;

/**
 * Created by Lobster on 15.05.17.
 */

public class MeetingWrapper {

    private Long id;

    private String name;

    private String details;

    private int count;

    private List<VisitorDtoUsage> present;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<VisitorDtoUsage> getPresent() {
        return present;
    }

    public void setPresent(List<VisitorDtoUsage> present) {
        this.present = present;
    }
}
