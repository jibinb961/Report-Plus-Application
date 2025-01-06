package com.inhaler.report;

public class Students {
    String studentname,rollnumber,fathername,classid,studentweight,studentheight;

    public Students()
    {

    }
    @Override
    public String toString() {
        return "Subjects{" +
                "studentname='" + studentname + '\'' +
                ", rollnumber='" + rollnumber + '\'' +
                ", fathername='" + fathername + '\'' +
                ", classid='" + classid + '\'' +
                ", studentweight='" + studentweight + '\'' +
                ", studentheight='" + studentheight + '\'' +
                '}';
    }

    public String getStudentweight() {
        return studentweight;
    }

    public void setStudentweight(String studentweight) {
        this.studentweight = studentweight;
    }

    public String getStudentheight() {
        return studentheight;
    }

    public void setStudentheight(String studentheight) {
        this.studentheight = studentheight;
    }

    public String getStudentname() {
        return studentname;
    }

    public void setStudentname(String studentname) {
        this.studentname = studentname;
    }

    public String getRollnumber() {
        return rollnumber;
    }

    public void setRollnumber(String rollnumber) {
        this.rollnumber = rollnumber;
    }

    public String getFathername() {
        return fathername;
    }

    public void setFathername(String fathername) {
        this.fathername = fathername;
    }

    public String getClassid() {
        return classid;
    }

    public void setClassid(String classid) {
        this.classid = classid;
    }
}

