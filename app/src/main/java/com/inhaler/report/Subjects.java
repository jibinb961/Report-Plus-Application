package com.inhaler.report;

public class Subjects {
    String subjectname,modified;

    public Subjects()
    {

    }
    @Override
    public String toString() {
        return "Subjects{" +
                "subjectname='" + subjectname + '\'' +
                ", modified='" + modified + '\'' +
                '}';
    }

    public String getSubjectname() {
        return subjectname;
    }

    public void setSubjectname(String subjectname) {
        this.subjectname = subjectname;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }
}
