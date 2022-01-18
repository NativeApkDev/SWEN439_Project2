/*
 * LibraryModel.java
 * Author: Dominic Tjiptono (Student Number: 300502615)
 * Created on: Friday, 28 May 2021
 */



import javax.swing.*;
import java.sql.*;


public class LibraryModel {

    // For use in creating dialogs and making them modal
    private final JFrame dialogParent;

    // A connection to the database to be opened
    private Connection connection = null;

    // A Statement class object to help with dealing with SQL queries.
    private Statement statement = null; // initial value

    public LibraryModel(JFrame parent, String userid, String password)  {
        // Setting the values of the class attributes
        dialogParent = parent;

        // Registering a PostgreSQL Driver
        try {
            Class.forName("org.postgresql.Driver");
        }
        catch (ClassNotFoundException cnfe) {
            // Showing a message dialog telling that the driver class for PostgreSQL cannot be found.
            JOptionPane.showMessageDialog(dialogParent, "Cannot find the driver class! Either " +
                    "it is not installed yet or postgresql.jar is not in CLASSPATH");

            // Closing the connection to the database
            closeDBConnection();

            // Terminating the program safely
            System.exit(0);
        }

        // Establishing a connection to the database
        String url = "jdbc:postgresql://db.ecs.vuw.ac.nz/" + userid + "_jdbc";

        try {
            connection = DriverManager.getConnection(url,
                    userid, password);
        }
        catch (SQLException sqlex) {
            // Showing a message dialog telling that connection to the database fails.
            JOptionPane.showMessageDialog(dialogParent, "Cannot connect!\n" + sqlex.getMessage());

            // Closing the connection to the database
            closeDBConnection();

            // Terminating the program safely
            System.exit(0);
        }
    }

    public String bookLookup(int isbn) {
        String res = "Book Lookup:\n"; // initial value
        try {
            // Initialising a statement object
            this.statement = connection.createStatement();

            // Executing the Statement object
            ResultSet resultSet = statement.executeQuery("SELECT isbn, title, edition_no, " +
                    "numofcop, numleft, name, authorseqno FROM book " +
                    "NATURAL JOIN book_author NATURAL JOIN author WHERE isbn = " + isbn + " ORDER BY authorseqno");

            // Handling query answer in ResultSet object
            while (resultSet.next()){
                res += resultSet.getString(1) + ": " + resultSet.getString(2) +
                        "\nEdition: " + resultSet.getString(3) +
                        " - Number of copies: " + resultSet.getString(4) +
                        " - Copies left: " + resultSet.getString(5) +
                        "\nAuthor: " + resultSet.getString(6)
                        + "\nAuthor Seq No: " + resultSet.getString(7) + "\n\n";
            }
        }
        catch (SQLException sqlex) {
            JOptionPane.showMessageDialog(dialogParent, "Cannot find book with isbn = " + isbn + "!\n" +
                    sqlex.getMessage());
            return null;
        }

        return res;
    }

    public String showCatalogue() {
        String res = "Show Catalogue:\n\n"; // initial value
        try {
            // Initialising a statement object
            this.statement = connection.createStatement();

            // Executing the Statement object
            ResultSet resultSet = statement.executeQuery("SELECT isbn, title, edition_no, " +
                    "numofcop, numleft, name, authorseqno FROM book " +
                    "NATURAL JOIN book_author NATURAL JOIN author ORDER BY authorseqno");

            // Handling query answer in ResultSet object
            while (resultSet.next()){
                res += resultSet.getString(1) + ": " + resultSet.getString(2) +
                        "\nEdition: " + resultSet.getString(3) +
                        " - Number of copies: " + resultSet.getString(4) +
                        " - Copies left: " + resultSet.getString(5) +
                        "\nAuthor: " + resultSet.getString(6)
                        + "\nAuthor Seq No: " + resultSet.getString(7) + "\n\n";
            }
        }
        catch (SQLException sqlex) {
            JOptionPane.showMessageDialog(dialogParent, "Cannot show book catalogue!\n" +
                    sqlex.getMessage());
            return null;
        }

        return res;
    }

    public String showLoanedBooks() {
        String res = "Show Loaned Books:\n\n"; // initial value
        try {
            // Initialising a statement object
            this.statement = connection.createStatement();

            // Executing the Statement object
            ResultSet resultSet = statement.executeQuery("SELECT isbn, title, customerid, " +
                    "l_name, f_name FROM book NATURAL JOIN " +
                    "cust_book NATURAL JOIN customer");

            // Handling query answer in ResultSet object
            while (resultSet.next()){
                res += resultSet.getString(1) + ": " + resultSet.getString(2) +
                        "\nBorrower: " + resultSet.getString(3) + " (" + resultSet.getString(4)
                        + ", " + resultSet.getString(5)
                        + ")\n\n";
            }
        }
        catch (SQLException sqlex) {
            JOptionPane.showMessageDialog(dialogParent, "Cannot show loaned books!\n" + sqlex.getMessage());
            return null;
        }

        return res;
    }

    public String showAuthor(int authorID) {
        String res = "Show Author:\n"; // initial value
        try {
            // Initialising a statement object
            this.statement = connection.createStatement();

            // Executing the Statement object
            ResultSet resultSet = statement.executeQuery("SELECT * FROM author WHERE authorid = " + authorID);

            // Handling query answer in ResultSet object
            while (resultSet.next()){
                res += resultSet.getString(1) + ": " + resultSet.getString(3) + ", " + resultSet.getString(2) + "\n\n";
            }
        }
        catch (SQLException sqlex) {
            JOptionPane.showMessageDialog(dialogParent, "Cannot find author with authorid = " +
                    authorID + "!\n" + sqlex.getMessage());
            return null;
        }

        return res;
    }

    public String showAllAuthors() {
        String res = "Show All Authors:\n"; // initial value
        try {
            // Initialising a statement object
            this.statement = connection.createStatement();

            // Executing the Statement object
            ResultSet resultSet = statement.executeQuery("SELECT * FROM author");

            // Handling query answer in ResultSet object
            while (resultSet.next()){
                res += resultSet.getString(1) + ": " + resultSet.getString(3) + ", " + resultSet.getString(2) + "\n\n";
            }
        }
        catch (SQLException sqlex) {
            JOptionPane.showMessageDialog(dialogParent, "Cannot show list of authors!\n" +
                    sqlex.getMessage());
            return null;
        }

        return res;
    }

    public String showCustomer(int customerID) {
        String res = "Show Customer:\n"; // initial value
        try {
            // Initialising a statement object
            this.statement = connection.createStatement();

            // Executing the Statement object
            ResultSet resultSet = statement.executeQuery("SELECT * FROM customer WHERE customerid = "
                    + customerID);

            // Handling query answer in ResultSet object
            while (resultSet.next()){
                res += resultSet.getString(1) + ": " + resultSet.getString(2) + ", " +
                        resultSet.getString(3) + " - ";
                if (resultSet.getString(4) == null){
                    res += "(no city)\n\n";
                }
                else {
                    res += resultSet.getString(4) + "\n\n";
                }
            }
        }
        catch (SQLException sqlex) {
            JOptionPane.showMessageDialog(dialogParent, "Cannot find customer with customerid = " +
                    customerID + "!\n" + sqlex.getMessage());
            return null;
        }

        return res;
    }

    public String showAllCustomers() {
        String res = "Show All Customers:\n"; // initial value
        try {
            // Initialising a statement object
            this.statement = connection.createStatement();

            // Executing the Statement object
            ResultSet resultSet = statement.executeQuery("SELECT * FROM customer");

            // Handling query answer in ResultSet object
            while (resultSet.next()){
                res += resultSet.getString(1) + ": " + resultSet.getString(2) + ", " +
                        resultSet.getString(3) + " - ";
                if (resultSet.getString(4) == null){
                    res += "(no city)\n\n";
                }
                else {
                    res += resultSet.getString(4) + "\n\n";
                }
            }
        }
        catch (SQLException sqlex) {
            JOptionPane.showMessageDialog(dialogParent, "Cannot show list of customers!\n" +
                    sqlex.getMessage());
            return null;
        }

        return res;
    }

    // Creating a method to check whether a customer exists or not
    public boolean customerExists(int customerID){
        try {
            // Initialising a statement object
            this.statement = connection.createStatement();

            // Try to get a customer with customerid 'customerID'
            statement.executeQuery("SELECT * FROM customer WHERE customerid " +
                    "= " + customerID);
            return true;
        }
        catch (SQLException sqlex){
            return false;
        }
    }

    // Creating a method to check whether a book with a particular ISBN has copies or not
    public boolean bookExistsAndHasCopies(int isbn){
        try {
            // Initialising a statement object
            this.statement = connection.createStatement();

            // Try to get a book with ISBN 'isbn'
            ResultSet resultSet = statement.executeQuery("SELECT numleft FROM book WHERE isbn = "
            + isbn);
            int numleft = resultSet.getInt(1);
            return numleft > 0;
        }
        catch (SQLException sqlex) {
            return false;
        }
    }

    public String borrowBook(int isbn, int customerID,
                             int day, int month, int year) {
        String res = "Borrow Book:\n"; // initial value
        try {
            // Initialising a statement object
            this.statement = connection.createStatement();

            // Getting the due date of borrowing the book.
            String dueDate = "'" + year + "-" + (month + 1) + "-" + day + "'";

            // Below is a rough structure of how the SQL query for 'borrowBook' method looks like.

            /**
             * IF EXISTS (
             * SELECT 1 FROM customer WHERE customerid = customerID
             * )
             * BEGIN
             * LOCK TABLE customer IN SHARE MODE;
             * END
             *
             * IF EXISTS (
             * SELECT 1 FROM book WHERE isbn = isbn AND numleft > 0
             * )
             * BEGIN
             * LOCK TABLE book IN SHARE MODE;
             * END
             *
             * BEGIN;
             * INSERT INTO cust_book (isbn, due_date, customerID) VALUES (isbn, dueDate, customerID);
             * COMMIT;
             *
             * BEGIN;
             * UPDATE book SET numleft = numleft - 1 WHERE isbn = isbn;
             * COMMIT;
             * */

            // Executing queries to the database
            String[] months = {"January", "February", "March", "April", "May", "June", "July", "August",
            "September", "October", "November", "December"};

            // If there is a customer with customerid 'customerID', lock customer table
            if (customerExists(customerID)){
                statement.executeUpdate("""
                        BEGIN;
                        LOCK TABLE customer IN SHARE MODE;;
                        END;
                        """);
            }

            // If there is a book with ISBN 'isbn' with copies available
            if (bookExistsAndHasCopies(isbn)){
                statement.executeUpdate("""
                        BEGIN;
                        LOCK TABLE book IN SHARE MODE;
                        END;
                        """);
            }

            statement.executeUpdate("BEGIN;");
            statement.executeUpdate("INSERT INTO cust_book (isbn, duedate, customerID) " +
                    "VALUES(" + isbn + ", " + dueDate + ", " + customerID + ");");
            statement.executeUpdate("COMMIT;");

            // Creating an interaction command to check that my program works correctly
            JOptionPane.showMessageDialog(dialogParent, "Locked the tuple(s), ready to update. " +
                    "Click OK to continue!");
            
            ResultSet bookTitleSet = statement.executeQuery("SELECT title FROM book NATURAL JOIN " +
                    "book_author WHERE isbn = " + isbn);
            String bookTitle = ""; // initial value
            while (bookTitleSet.next()) {
                bookTitle = bookTitleSet.getString(1);
            }

            ResultSet customerNameSet = statement.executeQuery("SELECT f_name, l_name FROM customer WHERE " +
                    "customerid = " + customerID);
            String customerFName = ""; // initial value
            String customerLName = ""; // initial value
            while (customerNameSet.next()) {
                customerFName = customerNameSet.getString(1);
                customerLName = customerNameSet.getString(2);
            }

            // Generating confirmation message
            res += "Book: " + isbn + " (" + bookTitle + ")\nLoaned to: " +
                    customerID + " (" + customerFName + " " + customerLName + ")\nDue Date: " +
                    day + " " + months[month] + " " + year + "\n\n";

            // Updating book table
            statement.executeUpdate("BEGIN;");
            statement.executeUpdate("UPDATE book SET numleft = numleft - 1 WHERE isbn = " + isbn + ";");
            statement.executeUpdate("COMMIT;");
        }
        catch (SQLException sqlex) {
            JOptionPane.showMessageDialog(dialogParent, "Customer with Customer ID = " + customerID
                    + " cannot borrow book with ISBN = " + isbn + "!\n" + sqlex.getMessage());
            return null;
        }

        return res;
    }

    public String returnBook(int isbn, int customerid) {
        String res = "Return Book:\n"; // initial value
        try {
            // Initialising a statement object
            this.statement = connection.createStatement();

            // If there is a customer with customerid 'customerid', lock customer table
            if (customerExists(customerid)){
                statement.executeUpdate("""
                        BEGIN;
                        LOCK TABLE customer IN SHARE MODE;;
                        END;
                        """);
            }

            // If there is a book with ISBN 'isbn' with copies available
            if (bookExistsAndHasCopies(isbn)){
                statement.executeUpdate("""
                        BEGIN;
                        LOCK TABLE book IN SHARE MODE;
                        END;
                        """);
            }

            // Executing a deletion of a record in cust_book with matching isbn and customerid
            statement.executeUpdate("BEGIN;");
            statement.executeUpdate("DELETE FROM cust_book WHERE isbn = " +
                    isbn + " AND customerid = " + customerid);
            statement.executeUpdate("COMMIT;");

            // Generating confirmation message
            res += "Book " + isbn + " returned for customer " + customerid + "\n\n";

            // Updating book table
            statement.executeUpdate("BEGIN;");
            statement.executeUpdate("UPDATE book SET numleft = numleft + 1 WHERE isbn = " + isbn + ";");
            statement.executeUpdate("COMMIT;");
        }
        catch (SQLException sqlex) {
            JOptionPane.showMessageDialog(dialogParent, "Customer with Customer ID = " + customerid
                    + " cannot return book with ISBN = " + isbn + "!\n" + sqlex.getMessage());
            return null;
        }

        return res;
    }

    public void closeDBConnection() {
        try{
            if (this.statement != null) {
                this.statement.close();
            }

            if (this.connection != null) {
                this.connection.close();
            }
        }
        catch (SQLException sqlex){
            JOptionPane.showMessageDialog(dialogParent, "Cannot close database connection!\n" +
                    sqlex.getMessage());
        }
    }

    public String deleteCus(int customerID) {
        String res = "Delete Customer:\n"; // initial value
        try {
            // Initialising a statement object
            this.statement = connection.createStatement();

            // Executing the Statement object
            statement.executeUpdate("DELETE FROM customer WHERE customerid = " + customerID);

            // Generating confirmation message
            res += "Customer " + customerID + " removed!\n\n";
        }
        catch (SQLException sqlex) {
            JOptionPane.showMessageDialog(dialogParent, "Cannot delete customer with customerid = "
                    + customerID + "!\n" + sqlex.getMessage());
            return null;
        }

        return res;
    }

    public String deleteAuthor(int authorID) {
        String res = "Delete Author:\n"; // initial value
        try {
            // Initialising a statement object
            this.statement = connection.createStatement();

            // Executing the Statement object
            statement.executeUpdate("DELETE FROM author WHERE authorid = " + authorID);

            // Generating confirmation message
            res += "Author " + authorID + " removed!\n\n";
        }
        catch (SQLException sqlex) {
            JOptionPane.showMessageDialog(dialogParent, "Cannot delete author with authorid = "
                    + authorID + "!\n" + sqlex.getMessage());
            return null;
        }

        return res;
    }

    public String deleteBook(int isbn) {
        String res = "Delete Book:\n"; // initial value
        try {
            // Initialising a statement object
            this.statement = connection.createStatement();

            // Executing the Statement object
            statement.executeUpdate("DELETE FROM book WHERE isbn = " + isbn);

            // Generating confirmation message
            res += "Book " + isbn + " removed!\n\n";
        }
        catch (SQLException sqlex) {
            JOptionPane.showMessageDialog(dialogParent, "Cannot delete book with isbn = "
                    + isbn + "!\n" + sqlex.getMessage());
            return null;
        }

        return res;
    }
}