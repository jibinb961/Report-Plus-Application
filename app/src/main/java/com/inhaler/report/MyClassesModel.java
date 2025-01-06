package com.inhaler.report;

public class MyClassesModel {

    String className,classTeacher,classStrength,classSection,classId;

    public MyClassesModel(String className, String classTeacher, String classStrength, String classSection, String classId) {
        this.className = className;
        this.classTeacher = classTeacher;
        this.classStrength = classStrength;
        this.classSection = classSection;
        this.classId = classId;
    }

    public String getClassId() {
        return classId;
    }

    public String getClassName() {
        return className;
    }


    public String getClassTeacher() {
        return classTeacher;
    }

    public String getClassStrength() {
        return classStrength;
    }

    public String getClassSection() {
        return classSection;
    }
}
