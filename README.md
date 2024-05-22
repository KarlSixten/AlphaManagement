# AlphaManagement System

## Overview
The Alpha Management System is a robust application developed using Spring Boot, designed to manage both employees and projects within an organization. It features capabilities such as adding, updating, and deleting employee records and projects, all secured with role-based access controls.

## Features
- **Employee Management**: Enables administrative users to manage employee records, including creating, updating, and deleting entries.
- **Project Management**: Allows users to manage project details including creation, updates, and deletion, with projects listed by end date.
- **Login System**: Secures the application via a login mechanism, ensuring that only authorized users can access the system.
- **Role-Based Access**: Restricts certain operations such as updating or deleting data to users with administrative roles.

## Technologies Used
- **Spring Boot**: Framework for building Java applications.
- **Thymeleaf**: Server-side Java template engine used for web interfaces.
- **Bootstrap**: Framework for designing responsive and mobile-first websites.
- **MySQL**: Database used for persistent storage.

## Installation

### Prerequisites
- Java JDK 11 or newer
- Maven 3.6 or later
- MySQL Server 8.0 or later

### Setup Instructions
1. **Clone the Repository**
   ```bash
   git clone https://github.com/KarlSixten/AlphaManagement.git
   cd Alphamanagement
2. **Make sure profile is set to Dev under application properties**
3. **Login with one of the predefined users. See them under the H2_data.sql**

## Usage

1. Creating a project, subproject and task
2. Assigning employees to various projects
3. Time management based on business days and amount of employees assigned
4. Progress overview with percentage of how much of the planned work is done

## License
- Distributed under the MIT license.

  MIT License

Copyright (c) [2024] [AlphaManagement]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

## Roles, permissions and functionality

### Employees
An employee role is the standard role in the program.
The employee will only see the projects they are assigned to, and cannot assign people to projects, nor create, update or delete projects and subprojects.

The following functionality is what they have access and permission to:

- Being able to see all projects they are assigned to
- Creating tasks
- Updating tasks
- Deleting tasks
- Being able to see the task-breakdown, timemanagement etc.'

### Projectmanagers

A project manager role has permission to edit the majority of the program, when it comes to the projectmanagement tool.

The following functionality is what they have acces and permission to: 

- Everything that the employee role can also do
- Creating projects and subprojects
- Updating projects and subprojects
- Deleting projects and subprojects
- Assigning employees to projects and subprojects
  

### System administrator

A system administrator has full acces to the program when it comes to managing users in the system.

The following functionality is what they have acces and permission to:

- Creating employees and assigning them roles and skills
- Updating employees
- Deleting employees
  

## Contact

- Aleksander Gregersen  alek3691@stud.kea.dk  @Aleksandergreg 
- Anders Ludvigsen  anlu0001@stud.kea.dk  @AndersBondeLudvigsen 
- Gustav Søderberg  guso0001@stud.kea.dk  @GustavSoederberg 
- Karl Bjarnø  kabj0001@stud.kea.dk  @KarlSixten





   
   
