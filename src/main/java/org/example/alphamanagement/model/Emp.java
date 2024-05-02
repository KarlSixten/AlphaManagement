package org.example.alphamanagement.model;

public class Emp {
    private String username;
    private String password;
    private int jobType;

    public Emp(String username, String password, int jobType) {
        this.username = username;
        this.password = password;
        this.jobType = jobType;
    }

    public Emp() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getJobType() {
        return jobType;
    }

    public void setJobType(int jobType) {
        this.jobType = jobType;
    }
}
