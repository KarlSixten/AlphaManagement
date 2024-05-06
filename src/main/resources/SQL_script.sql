DROP SCHEMA IF EXISTS alphaManagement;
CREATE SCHEMA alphaManagement;
USE alphaManagement;

CREATE TABLE jobType
(
    jobTypeID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    jobTypeName VARCHAR(45) NOT NULL
);

CREATE TABLE emp
(
    username VARCHAR(45) NOT NULL PRIMARY KEY,
    password VARCHAR(45) NOT NULL,
    firstName VARCHAR(45) NOT NULL,
    lastName VARCHAR(45) NOT NULL,
    jobTypeID INT NOT NULL,
    FOREIGN KEY (jobTypeID) REFERENCES jobType(jobTypeID)
);

CREATE TABLE project
(
    projectID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    projectName VARCHAR(45) NOT NULL,
    startDate DATE NOT NULL,
    endDate DATE NOT NULL,
    parentProjectID INT,
    FOREIGN KEY (parentProjectID) REFERENCES project(projectID)
);

CREATE TABLE project_emp
(
    projectID INT NOT NULL,
    username VARCHAR(45) NOT NULL,
    FOREIGN KEY (projectID) REFERENCES project(projectID),
    FOREIGN KEY (username) REFERENCES emp(username)
);

CREATE TABLE category
(
    categoryID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    categoryName VARCHAR(45) NOT NULL
);

CREATE TABLE task
(
    taskID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    taskName VARCHAR(45) NOT NULL,
    projectID INT NOT NULL,
    categoryID INT NOT NULL,
    description VARCHAR(255),
    estimate INT,
    startDate DATE NOT NULL,
    endDate DATE NOT NULL,
    FOREIGN KEY (projectID) REFERENCES project(projectID),
    FOREIGN KEY (categoryID) REFERENCES category(categoryID)
);

CREATE TABLE emp_task
(
    username varchar(45) NOT NULL,
    taskID INT NOT NULL,
    FOREIGN KEY (username) REFERENCES emp(username),
    FOREIGN KEY (taskID) REFERENCES task(taskID)
);

CREATE TABLE skill
(
    skillID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    skillName VARCHAR(45) NOT NULL
);

CREATE TABLE emp_skill
(
    username varchar(45) NOT NULL,
    skillID INT NOT NULL,
    FOREIGN KEY (username) REFERENCES emp(username),
    FOREIGN KEY (skillID) REFERENCES skill(skillID)
);


INSERT INTO jobType (jobTypeName) VALUES ('Employee');
INSERT INTO jobType (jobTypeName) VALUES ('Project Manager');
INSERT INTO jobType (jobTypeName) VALUES ('System Administrator');

INSERT INTO category (categoryName) VALUES ('User story');
INSERT INTO category (categoryName) VALUES ('Technical story');

INSERT INTO skill (skillName) VALUES ('Java');
INSERT INTO skill (skillName) VALUES ('SQL');
INSERT INTO skill (skillName) VALUES ('Python');
INSERT INTO skill (skillName) VALUES ('C#');
INSERT INTO skill (skillName) VALUES ('C++');
INSERT INTO skill (skillName) VALUES ('HTML');

INSERT INTO emp (firstName, lastName, username, password, jobTypeID) VALUES ('test', 'test', 'test', 'test', 3);