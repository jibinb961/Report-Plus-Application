package com.inhaler.report;

public class Attendance {
    private String absentday;
    public Attendance()
    {

    }
    @Override
    public String toString() {
        return "Attendance{" +
                "absentday='" + absentday + '\'' +

                '}';
    }

    public String getAbsentday() {
        return absentday;
    }

    public void setAbsentday(String absentday) {
        this.absentday = absentday;
    }
}
