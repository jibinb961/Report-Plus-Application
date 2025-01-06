package com.inhaler.report;

public class Classes {
    String classname,classteacher,subjects,classstrength,classsection;

    public Classes() {
    }
    @Override
    public String toString() {
        return "Subjects{" +
                "classname='" + classname + '\'' +
                ", classteacher='" + classteacher + '\'' +
                ", subjects='" + subjects + '\'' +
                ", classstrength='" + classstrength + '\'' +
                ", classsection='" + classsection + '\'' +
                '}';
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getClassteacher() {
        return classteacher;
    }

    public void setClassteacher(String classteacher) {
        this.classteacher = classteacher;
    }

    public String getSubjects() {
        return subjects;
    }

    public void setSubjects(String subjects) {
        this.subjects = subjects;
    }

    public String getClassstrength() {
        return classstrength;
    }

    public void setClassstrength(String classstrength) {
        this.classstrength = classstrength;
    }

    public String getClasssection() {
        return classsection;
    }

    public void setClasssection(String classsection) {
        this.classsection = classsection;
    }
}
