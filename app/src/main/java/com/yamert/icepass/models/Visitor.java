package com.yamert.icepass.models;

import io.realm.Realm;
import io.realm.RealmObject;
import timber.log.Timber;

/**
 * Created by Lobster on 17.02.17.
 */

public class Visitor extends RealmObject {

    private String name;

    private boolean status;

    public Visitor() {

    }

    public Visitor(VisitorDtoPrivacy dto) {
        this.name = dto.getName();
        this.status = dto.isActive();
    }

    public Visitor(VisitorDtoUsage dto) {
        this.name = dto.getToken();
        this.status = dto.isActive();
    }

    public static Visitor getUser(Realm realm) {
        return realm.where(Visitor.class)
                .findFirst();
    }

    public static void saveOrUpdate(Visitor visitor, Realm realm) {
        realm.executeTransactionAsync(realm1 -> realm1.copyToRealmOrUpdate(visitor));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof Visitor)) return false;

        Visitor visitor = (Visitor) o;

        return name != null ? name.equals(visitor.name) : visitor.name == null;

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Visitor{" +
                "name='" + name + '\'' +
                ", status=" + status +
                '}';
    }
}
