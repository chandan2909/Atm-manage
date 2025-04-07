# ATM Management System

A Java-based ATM Management System with a modern graphical user interface and secure banking operations.

## Features

- 🔐 Enhanced security:
  - Secure user authentication with SHA-256 PIN hashing
  - Account lockout after multiple failed attempts
  - Security question verification for PIN reset
  - Strong PIN requirements and validation
- 💰 Basic banking operations:
  - Cash deposit
  - Cash withdrawal
  - Balance inquiry
  - PIN change
  - Mini statement
  - Fast cash options
- 📝 Improved account registration:
  - Comprehensive user information collection
  - Copiable credentials for new users
  - Security question setup during registration
- 🎨 Modern UI with responsive design:
  - Professional button styling with hover effects
  - Centered layout for better user experience
  - Clear visual feedback for user actions
- 🔒 Enhanced security infrastructure:
  - Account locking system
  - Failed attempts tracking
  - Proper PIN reset verification flow
  - Protection against sequential and repetitive PINs

## Technologies Used

- Java Swing for GUI
- JDBC for database connectivity
- MySQL for data storage
- SHA-256 for PIN hashing
- Custom security infrastructure

## Prerequisites

- Java Development Kit (JDK) 8 or higher
- MySQL Server
- MySQL Connector/J (JDBC driver)
- JCalendar library for date handling

## Installation

1. Clone the repository:
```bash
git clone https://github.com/chandan2909/Atm-manage
```

2. Import the project into your preferred IDE

3. Set up the MySQL database:
   - Create a new database named `atm`
   - Import the provided `atm_schema.sql` file using:
     ```bash
     mysql -u your_username -p atm < atm_schema.sql
     ```
   - This will create all necessary tables, indexes, and views
   - Add the required security columns to the login table:
     ```sql
     ALTER TABLE login 
     ADD COLUMN failed_attempts INT DEFAULT 0, 
     ADD COLUMN is_locked INT DEFAULT 0, 
     ADD COLUMN security_question VARCHAR(255), 
     ADD COLUMN security_answer VARCHAR(255);
     ```

4. Configure database connection:
   - Update the connection details in `src/Atm/management/Connn.java`

5. Add required libraries to your project classpath:
   - mysql-connector-java.jar
   - jcalendar-1.4.jar

## Database Schema

The system uses three main tables:
- `login` - Stores user credentials, card information, and security data
- `bank` - Stores transaction records
- `signup` - Stores user account details

## Usage

1. Run the application:
```bash
java -cp ".:lib/*" Atm.management.Login
```

2. For new users:
   - Click "SIGN UP" to create a new account
   - Fill in the required details including a security question and answer
   - After registration, easily copy your card number and PIN using the Copy buttons
   - Store these credentials securely for future login

3. For existing users:
   - Enter your card number and PIN
   - Access various banking operations from the main menu

4. If you forget your PIN:
   - Click "RESET PIN" on the login screen
   - Enter your card number
   - Answer your security question correctly
   - Choose a new PIN that meets security requirements

## Security Features

- Secure PIN handling:
  - SHA-256 hashing with salt for PIN storage
  - Strong PIN requirements (4-6 digits)
  - Prevention of sequential digits (like 1234)
  - Limits on repeating digits
  
- Account protection:
  - Maximum login attempts with account lockout
  - Security question verification for PIN reset
  - Tracking of failed attempts
  - Account locking after too many failed security verifications
  
- Data security:
  - Input validation for all user data
  - Proper error handling
  - Secure database operations
  - Protection against SQL injection

## Contributing

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a new Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Java Swing documentation
- MySQL documentation
- JCalendar library developers
