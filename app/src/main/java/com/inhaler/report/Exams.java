package com.inhaler.report;

public class Exams {

    String examname,examstartdate,examenddate,coscholastic,classid,staffid,totalmarks,subjects,modifiedby;

    public Exams() {
    }
    @Override
    public String toString() {
        return "Exams{" +
                "examname='" + examname + '\'' +
                ", examstartdate='" + examstartdate + '\'' +
                ", examenddate='" + examenddate + '\'' +
                ", coscholastic='" + coscholastic + '\'' +
                ", classid='" + classid + '\'' +
                ", staffid='" + staffid + '\'' +
                ", totalmarks='" + totalmarks + '\'' +
                ", subjects='" + subjects + '\'' +
                ", modifiedby='" + modifiedby + '\'' +
                '}';
    }

    public String getModifiedby() {
        return modifiedby;
    }

    public void setModifiedby(String modifiedby) {
        this.modifiedby = modifiedby;
    }

    public String getClassid() {
        return classid;
    }

    public void setClassid(String classid) {
        this.classid = classid;
    }

    public String getStaffid() {
        return staffid;
    }

    public void setStaffid(String staffid) {
        this.staffid = staffid;
    }

    public String getTotalmarks() {
        return totalmarks;
    }

    public void setTotalmarks(String totalmarks) {
        this.totalmarks = totalmarks;
    }

    public String getSubjects() {
        return subjects;
    }

    public void setSubjects(String subjects) {
        this.subjects = subjects;
    }

    public String getCoscholastic() {
        return coscholastic;
    }

    public void setCoscholastic(String coscholastic) {
        this.coscholastic = coscholastic;
    }

    public String getExamname() {
        return examname;
    }

    public void setExamname(String examname) {
        this.examname = examname;
    }

    public String getExamstartdate() {
        return examstartdate;
    }

    public void setExamstartdate(String examstartdate) {
        this.examstartdate = examstartdate;
    }

    public String getExamenddate() {
        return examenddate;
    }

    public void setExamenddate(String examenddate) {
        this.examenddate = examenddate;
    }
}
