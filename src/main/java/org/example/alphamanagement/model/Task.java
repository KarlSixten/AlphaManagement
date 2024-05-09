package org.example.alphamanagement.model;

import java.time.LocalDate;

public class Task {
    private int taskID;
    private String taskName;
    private int projectID;
    private int categoryID;
    private String description;
    private int estimate;
    private LocalDate startDate;
    private LocalDate endDate;

    public Task(int taskID, String taskName, int projectID, int categoryID, String description, int estimate, LocalDate startDate, LocalDate endDate) {
        this.taskID = taskID;
        this.taskName = taskName;
        this.projectID = projectID;
        this.categoryID = categoryID;
        this.description = description;
        this.estimate = estimate;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getHoursDone() {
        return hoursDone;
    }

    public void setHoursDone(int hoursDone) {
        this.hoursDone = hoursDone;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public Task(){

    }
    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getProjectID() {
        return projectID;
    }

    public void setProjectID(int projectID) {
        this.projectID = projectID;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getEstimate() {
        return estimate;
    }

    public void setEstimate(int estimate) {
        this.estimate = estimate;
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
}
