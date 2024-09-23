# Employee Management System

## Database Setup
### 1. Create the `employees` Table

```sql
CREATE TABLE employees (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    department VARCHAR(100) NOT NULL,
    salary DOUBLE NOT NULL,
    join_date DATE NOT NULL
);
```

2. View the Table Structure

```sql
DESCRIBE employees;
```

3. View All Data in the employees Table
```sql
SELECT * FROM employees;
```
