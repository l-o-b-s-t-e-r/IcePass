package com.yamert.icepass.misc;

import com.yamert.icepass.models.Meeting;

/**
 * Created by Lobster on 28.03.17.
 */

public class EventUpdateMeeting {

    private Meeting meeting;
    private int type;

    public EventUpdateMeeting(Meeting meeting, int type) {
        this.meeting = meeting;
        this.type = type;
    }

    public Meeting getMeeting() {
        return meeting;
    }

    public void setMeeting(Meeting meeting) {
        this.meeting = meeting;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
