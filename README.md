# Employee Management System

## Database Setup

1. Create the `employees` Table

```sql
CREATE TABLE employees (
    id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    department VARCHAR(100) NOT NULL,
    salary DOUBLE NOT NULL,
    join_date DATE NOT NULL
);
```
2. Create the `login` Table
```sql
CREATE TABLE login (
    username VARCHAR(100) PRIMARY KEY,
    password VARCHAR(100) NOT NULL
);
```
3. View the Table Structure
```sql
DESCRIBE employees;
DESCRIBE login;
```
4. View All Data in the `employees` Table
```sql
SELECT * FROM employees;
```
5. View All Data in the `login` Table
```sql
SELECT * FROM login;
```

### Technologies Used
- Java
- Swing (for GUI)
- MySQL (for database)
