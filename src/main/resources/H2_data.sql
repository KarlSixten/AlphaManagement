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
    hoursDone DOUBLE DEFAULT 0,
    isDone BOOLEAN DEFAULT FALSE,
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

--------------------------------------------------------------------------------------------------------------------------------------------------
-- TESTDATA -- TESTDATA -- TESTDATA -- TESTDATA -- TESTDATA -- TESTDATA -- TESTDATA -- TESTDATA -- TESTDATA -- TESTDATA -- TESTDATA -- TESTDATA --
--------------------------------------------------------------------------------------------------------------------------------------------------

-- Insert emps
INSERT INTO emp (firstName, lastName, username, password, jobTypeID) VALUES ('Employee', 'Worker', 'emp', 'emp', 1);
INSERT INTO emp (firstName, lastName, username, password, jobTypeID) VALUES ('Project', 'Manager', 'pm', 'pm', 2);
INSERT INTO emp (firstName, lastName, username, password, jobTypeID) VALUES ('System', 'Admin', 'admin', 'admin', 3);

-- Insert emp skills
INSERT INTO emp_skill (username,skillID) values ('emp',2);
INSERT INTO emp_skill (username,skillID) values ('emp',3);
INSERT INTO emp_skill (username,skillID) values ('emp',5);
INSERT INTO emp_skill (username,skillID) values ('pm',1);
INSERT INTO emp_skill (username,skillID) values ('pm',3);
INSERT INTO emp_skill (username,skillID) values ('admin',4);
INSERT INTO emp_skill (username,skillID) values ('admin',3);
INSERT INTO emp_skill (username,skillID) values ('admin',6);

-- Insert parent projects
INSERT INTO project(projectName, startDate, endDate) VALUES ('Testprojekt', '2024-01-01', '2024-12-31');
INSERT INTO project(projectName, startDate, endDate) VALUES ('Hesteprojekt', '2024-02-04', '2024-06-19');
INSERT INTO project(projectName, startDate, endDate) VALUES ('Festeprojekt', '2024-07-22', '2024-10-08');

-- Insert subprojects (4 for each parent project)
INSERT INTO project(projectName, startDate, endDate, parentProjectID) VALUES ('TestSprint 1', '2024-01-01', '2024-03-01', 1);
INSERT INTO project(projectName, startDate, endDate, parentProjectID) VALUES ('TestSprint 2', '2024-03-01', '2024-05-01', 1);
INSERT INTO project(projectName, startDate, endDate, parentProjectID) VALUES ('TestSprint 3', '2024-05-01', '2024-08-01', 1);
INSERT INTO project(projectName, startDate, endDate, parentProjectID) VALUES ('TestSprint 4', '2024-08-01', '2024-12-31', 1);
INSERT INTO project(projectName, startDate, endDate, parentProjectID) VALUES ('HestSprint 1', '2024-02-04', '2024-03-04', 2);
INSERT INTO project(projectName, startDate, endDate, parentProjectID) VALUES ('HestSprint 2', '2024-03-04', '2024-04-04', 2);
INSERT INTO project(projectName, startDate, endDate, parentProjectID) VALUES ('HestSprint 3', '2024-04-04', '2024-05-04', 2);
INSERT INTO project(projectName, startDate, endDate, parentProjectID) VALUES ('HestSprint 4', '2024-05-04', '2024-06-19', 2);
INSERT INTO project(projectName, startDate, endDate, parentProjectID) VALUES ('FestSprint 1', '2024-07-22', '2024-08-22', 3);
INSERT INTO project(projectName, startDate, endDate, parentProjectID) VALUES ('FestSprint 2', '2024-08-22', '2024-09-22', 3);
INSERT INTO project(projectName, startDate, endDate, parentProjectID) VALUES ('FestSprint 3', '2024-09-22', '2024-09-25', 3);
INSERT INTO project(projectName, startDate, endDate, parentProjectID) VALUES ('FestSprint 4', '2024-09-25', '2024-10-08', 3);

-- Insert tasks (TestSprint 1 through 4)
INSERT INTO task(taskName, projectID, categoryID, description, estimate, startDate, endDate) VALUES ('TASK 1', 4, 2, 'DETTE ER EN TASK', 12, '2024-01-01', '2024-02-20');
INSERT INTO task(taskName, projectID, categoryID, description, estimate, startDate, endDate, hoursDone) VALUES ('TASK 2', 4, 1, 'DETTE ER EN TASK', 20, '2024-01-01', '2024-02-15', 3);
INSERT INTO task(taskName, projectID, categoryID, description, estimate, startDate, endDate) VALUES ('TASK 3', 4, 2, 'DETTE ER EN TASK', 8, '2024-01-20', '2024-02-10');
INSERT INTO task(taskName, projectID, categoryID, description, estimate, startDate, endDate, hoursDone) VALUES ('TASK 4', 4, 2, 'DETTE ER EN TASK', 16, '2024-02-10', '2024-03-01', 10);

INSERT INTO task(taskName, projectID, categoryID, description, estimate, startDate, endDate) VALUES ('TASK 5', 5, 2, 'DETTE ER EN TASK', 35, '2024-03-05', '2024-04-15');
INSERT INTO task(taskName, projectID, categoryID, description, estimate, startDate, endDate, hoursDone) VALUES ('TASK 6', 5, 1, 'DETTE ER EN TASK', 25, '2024-03-10', '2024-04-20', 15);
INSERT INTO task(taskName, projectID, categoryID, description, estimate, startDate, endDate) VALUES ('TASK 7', 5, 2, 'DETTE ER EN TASK', 12, '2024-03-15', '2024-04-05');
INSERT INTO task(taskName, projectID, categoryID, description, estimate, startDate, endDate, hoursDone) VALUES ('TASK 8', 5, 2, 'DETTE ER EN TASK', 28, '2024-03-25', '2024-04-25', 20);

INSERT INTO task(taskName, projectID, categoryID, description, estimate, startDate, endDate) VALUES ('TASK 9', 6, 2, 'DETTE ER EN TASK', 22, '2024-05-05', '2024-06-10');
INSERT INTO task(taskName, projectID, categoryID, description, estimate, startDate, endDate, hoursDone) VALUES ('TASK 10', 6, 1, 'DETTE ER EN TASK', 30, '2024-05-15', '2024-07-01', 18);
INSERT INTO task(taskName, projectID, categoryID, description, estimate, startDate, endDate) VALUES ('TASK 11', 6, 1, 'DETTE ER EN TASK', 14, '2024-06-01', '2024-07-05');
INSERT INTO task(taskName, projectID, categoryID, description, estimate, startDate, endDate, hoursDone) VALUES ('TASK 12', 6, 2, 'DETTE ER EN TASK', 36, '2024-07-01', '2024-08-01', 25);

INSERT INTO task(taskName, projectID, categoryID, description, estimate, startDate, endDate) VALUES ('TASK 13', 7, 1, 'DETTE ER EN TASK', 18, '2024-08-05', '2024-09-15');
INSERT INTO task(taskName, projectID, categoryID, description, estimate, startDate, endDate, hoursDone) VALUES ('TASK 14', 7, 1, 'DETTE ER EN TASK', 10, '2024-08-10', '2024-10-01', 5);
INSERT INTO task(taskName, projectID, categoryID, description, estimate, startDate, endDate) VALUES ('TASK 15', 7, 1, 'DETTE ER EN TASK', 28, '2024-09-01', '2024-10-05');
INSERT INTO task(taskName, projectID, categoryID, description, estimate, startDate, endDate, hoursDone) VALUES ('TASK 16', 7, 2, 'DETTE ER EN TASK', 40, '2024-09-15', '2024-12-01', 30);



-- Insert emps to parent-projects
INSERT INTO project_emp (projectID, username) VALUES (1, 'emp');
INSERT INTO project_emp (projectID, username) VALUES (2, 'emp');
INSERT INTO project_emp (projectID, username) VALUES (3, 'emp');
INSERT INTO project_emp (projectID, username) VALUES (1, 'pm');
INSERT INTO project_emp (projectID, username) VALUES (2, 'pm');
INSERT INTO project_emp (projectID, username) VALUES (3, 'pm');
INSERT INTO project_emp (projectID, username) VALUES (1, 'admin');
INSERT INTO project_emp (projectID, username) VALUES (2, 'admin');
INSERT INTO project_emp (projectID, username) VALUES (3, 'admin');


-- Insert emps to sub-projects
INSERT INTO project_emp (projectID, username) VALUES (4, 'emp');
INSERT INTO project_emp (projectID, username) VALUES (4, 'pm');
INSERT INTO project_emp (projectID, username) VALUES (4, 'admin');
INSERT INTO project_emp (projectID, username) VALUES (5, 'pm');
INSERT INTO project_emp (projectID, username) VALUES (5, 'emp');
INSERT INTO project_emp (projectID, username) VALUES (6, 'admin');
INSERT INTO project_emp (projectID, username) VALUES (6, 'pm');
INSERT INTO project_emp (projectID, username) VALUES (7, 'pm');
INSERT INTO project_emp (projectID, username) VALUES (8, 'emp');
INSERT INTO project_emp (projectID, username) VALUES (8, 'admin');
INSERT INTO project_emp (projectID, username) VALUES (9, 'admin');
INSERT INTO project_emp (projectID, username) VALUES (10, 'emp');
INSERT INTO project_emp (projectID, username) VALUES (12, 'pm');
INSERT INTO project_emp (projectID, username) VALUES (12, 'admin');
INSERT INTO project_emp (projectID, username) VALUES (14, 'admin');
INSERT INTO project_emp (projectID, username) VALUES (15, 'admin');
INSERT INTO project_emp (projectID, username) VALUES (5, 'admin');
INSERT INTO project_emp (projectID, username) VALUES (6, 'emp');
INSERT INTO project_emp (projectID, username) VALUES (8, 'pm');
INSERT INTO project_emp (projectID, username) VALUES (9, 'emp');


-- Insert emps to tasks
INSERT INTO emp_task(username, taskID) VALUES ('emp', 4);
INSERT INTO emp_task(username, taskID) VALUES ('pm', 4);
INSERT INTO emp_task(username, taskID) VALUES ('admin', 4);
