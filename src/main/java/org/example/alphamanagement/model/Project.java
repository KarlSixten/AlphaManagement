package org.example.alphamanagement.model;

import java.time.LocalDate;
import java.util.Date;

public class Project {
    private String projectName;
    private LocalDate startDate;
    private LocalDate endDate;
    private int projectID;
    private int parentProjectID;


    public Project(String projectName, LocalDate startDate, LocalDate endDate) {
        this.projectName = projectName;
        this.startDate = startDate;
        this.endDate = endDate;
    }


    public Project() {
    }

    public int getParentProjectID() {
        return parentProjectID;
    }

    public void setParentProjectID(int parentProjectID) {
        this.parentProjectID = parentProjectID;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public int getProjectID() {
        return projectID;
    }

    public void setProjectID(int projectID) {
        this.projectID = projectID;
    }

}
