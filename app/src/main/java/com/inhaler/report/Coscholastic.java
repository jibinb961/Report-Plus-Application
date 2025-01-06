package com.inhaler.report;

public class Coscholastic {
    String activityname,activitygrade;

    public Coscholastic() {
    }

    @Override
    public String toString() {
        return "Coscholastic{" +
                "activityname='" + activityname + '\'' +
                ", activitygrade='" + activitygrade + '\'' +

                '}';
    }

    public String getActivityname() {
        return activityname;
    }

    public void setActivityname(String acivityname) {
        this.activityname = acivityname;
    }

    public String getActivitygrade() {
        return activitygrade;
    }

    public void setActivitygrade(String activitygrade) {
        this.activitygrade = activitygrade;
    }
}
