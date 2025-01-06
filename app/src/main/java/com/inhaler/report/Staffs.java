package com.inhaler.report;

public class Staffs {
    String staffname,staffid,staffsalary,classes;

    public Staffs()
    {

    }
    @Override
    public String toString() {
        return "Staffs{" +
                "staffname='" + staffname + '\'' +
                ", staffid='" + staffid + '\'' +
                ", staffsalary='" + staffsalary + '\'' +
                ", classes='" + classes + '\'' +
                '}';
    }

    public String getClasses() {
        return classes;
    }

    public void setClasses(String classes) {
        this.classes = classes;
    }

    public String getStaffsalary() {
        return staffsalary;
    }

    public void setStaffsalary(String staffsalary) {
        this.staffsalary = staffsalary;
    }

    public String getStaffname() {
        return staffname;
    }

    public void setStaffname(String staffname) {
        this.staffname = staffname;
    }

    public String getStaffid() {
        return staffid;
    }

    public void setStaffid(String staffid) {
        this.staffid = staffid;
    }
}
