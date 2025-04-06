# ATM Management System

A Java-based ATM Management System with a modern graphical user interface and secure banking operations.

## Features

- 🔐 Secure user authentication with PIN hashing
- 💰 Basic banking operations:
  - Cash deposit
  - Cash withdrawal
  - Balance inquiry
  - PIN change
  - Mini statement
  - Fast cash options
- 📝 New account registration
- 🎨 Modern UI with responsive design
- 🔒 Session management and security features

## Technologies Used

- Java Swing for GUI
- JDBC for database connectivity
- MySQL for data storage
- SHA-256 for PIN hashing

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

4. Configure database connection:
   - Update the connection details in `src/Atm/management/Connn.java`

5. Add required libraries to your project classpath:
   - mysql-connector-java.jar
   - jcalendar-1.4.jar

## Database Schema

The system uses three main tables:
- `login` - Stores user credentials and card information
- `bank` - Stores transaction records
- `signup` - Stores user account details

## Usage

1. Run the application:
```bash
java -cp ".:lib/*" Atm.management.Login
```

2. For new users:
   - Click "SIGN UP" to create a new account
   - Fill in the required details
   - Note down your card number and PIN

3. For existing users:
   - Enter your card number and PIN
   - Access various banking operations from the main menu

## Security Features

- PIN hashing using SHA-256
- Session management
- Input validation
- Error handling
- Maximum login attempts
- Secure database operations

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
