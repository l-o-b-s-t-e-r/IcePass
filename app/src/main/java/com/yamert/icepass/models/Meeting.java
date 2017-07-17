package com.yamert.icepass.models;

import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Lobster on 17.02.17.
 */

public class Meeting extends RealmObject {

    @PrimaryKey
    private Long id;

    private String title;
    private String description;
    private Date starts;
    private Date ends;
    private int count;
    private RealmList<Visitor> visitors = new RealmList<>();

    public Meeting() {

    }

    public Meeting(Long id) {
        this.id = id;
    }

    public Meeting(MeetingDto dto) {
        id = dto.getId();
        setData(dto);
    }

    public void setData(MeetingDto dto) {
        title = dto.getName();
        description = dto.getDetails();
        starts = dto.getCreatedAt();
        ends = dto.getUpdatedAt();
    }

    public void setData(MeetingWrapper wrapper) {
        title = wrapper.getName();
        description = wrapper.getDetails();
        count = wrapper.getCount();
    }

    public static Meeting getMeetingById(Long meetingId, Realm realm) {
        return realm.where(Meeting.class)
                .equalTo("id", meetingId)
                .findFirst();
    }

    public static void saveOrUpdate(List<Meeting> meetings, Realm realm) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(meetings);
        realm.commitTransaction();
    }

    public static void update(Meeting meeting, MeetingDto dto, Realm realm) {
        realm.beginTransaction();
        meeting.setData(dto);
        realm.commitTransaction();
    }

    public static void update(Meeting meeting, MeetingWrapper wrapper, Realm realm) {
        realm.beginTransaction();
        meeting.setData(wrapper);
        realm.commitTransaction();
    }

    public static void saveOrUpdate(Meeting meeting, Realm realm) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(meeting);
        realm.commitTransaction();
    }

    public static void updateCount(Meeting meeting, int count, Realm realm) {
        realm.beginTransaction();
        meeting.setCount(count);
        realm.commitTransaction();
    }

    public static void updateVisitors(Meeting meeting, List<Visitor> visitors, Realm realm) {
        realm.beginTransaction();
        meeting.getVisitors().clear();
        meeting.getVisitors().addAll(visitors);
        realm.commitTransaction();
    }

    public static void addVisitor(Meeting meeting, Visitor visitor, Realm realm) {
        realm.beginTransaction();
        meeting.getVisitors().add(visitor);
        realm.commitTransaction();
    }

    public static void addVisitor(Meeting meeting, Visitor visitor, int pos, Realm realm) {
        realm.beginTransaction();
        meeting.getVisitors().add(pos, visitor);
        realm.commitTransaction();
    }

    public static List<Meeting> getAllPast(Long id, Realm realm) {
        return realm.where(Meeting.class)
                .lessThan("recordId", id)
                .findAll();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public RealmList<Visitor> getVisitors() {
        return visitors;
    }

    public void setVisitors(RealmList<Visitor> visitors) {
        this.visitors = visitors;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStarts() {
        return starts;
    }

    public void setStarts(Date starts) {
        this.starts = starts;
    }

    public Date getEnds() {
        return ends;
    }

    public void setEnds(Date ends) {
        this.ends = ends;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "Meeting{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", starts=" + starts +
                ", visitors=" + visitors +
                '}';
    }
}
