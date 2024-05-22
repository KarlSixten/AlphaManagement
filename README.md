# AlphaManagement System

## Overview
The Alpha Management System is a robust application developed using Spring Boot, designed to manage both employees and projects within an organization. It features capabilities such as adding, updating, and deleting employee records and projects, all secured with role-based access controls.

## Features
- **Employee Management**: Enables administrative users to manage employee records, including creating, updating, and deleting entries.
- **Project Management**: Allows users to manage project details including creation, updates, and deletion, with projects listed by start date.
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
   git clone https://github.com/yourgithub/alpha-management-system.git
   cd alpha-management-system
