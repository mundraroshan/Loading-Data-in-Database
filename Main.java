package com.higradius;

import java.io.BufferedReader;//This class  is used to read the text from a character-based input stream. 
import java.io.FileReader;//FileReader class is used to read data from the file. 
import java.io.IOException;//This is related to Input and Output operations in the Java code. 
import java.sql.Connection;// to Connect Java Application with mysql database
import java.sql.DriverManager;
import java.sql.PreparedStatement;//It is used to execute parameterized query.
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;//provides methods to format and parse date and time in java. 
import java.util.ArrayList; //import the ArrayList class

import java.sql.Date; //The java.sql.Date class represents only date in java used because it represents the date that can be stored in database. 

public class Main {
	//JDBC driver name and database URL
  static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
  static final String DB_URL = "jdbc:mysql://localhost/h2h_internship";

  static final String USER = "root";// user id for the database
  static final String PASS = "Root";//password for the database

  public static Date parseDate(String date, String dateFormat) { 
    if (date.isEmpty()) { // Checking  if the date is found to be empty it will return NULL.
      return null;
    } else {
      SimpleDateFormat format = new SimpleDateFormat(dateFormat);//creating instance of this concrete class for formatting and parsing date(string->data type) 
      java.util.Date ret; // ret is a variable in which parsed date is stored.
      try {
        ret = format.parse(date); // now date is being parsed(string to date type)and stored in ret.
        Date clearDate = new Date(ret.getTime()); //returns the no. of miliseconds, helps to assign a date and time to another Date object.
        return clearDate;
      } catch (ParseException e) { // catch if any parsed exception is found.
        e.printStackTrace();    // prints that exception.
        return null;
      }

    }
  }

  public static ArrayList<Invoice> readinvoices() throws Exception {
    String line = "";
    String splitBy = ",";
    ArrayList<Invoice> invoices = new ArrayList<Invoice>();//Creating arraylist of another class(pojo class)
    // parsing a CSV file into BufferedReader class constructor
    BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\KIIT\\Desktop\\High Radius\\1806507.csv"));// Assuming we have the csv file in the desired location it loads the csv file in br object. 
    br.readLine(); // .readline() can be used to read data line by line.

    try {
           while ((line = br.readLine()) != null) // returns a Boolean value
      {
        String[] row = line.split(splitBy); // use comma as separator
        String businessCode = row[0]; // Datatype of businesscode is string.
        String custNumber = row[1];
        String nameCustomer = row[2];
        Date clearDate = parseDate(row[3], "yyyy-MM-dd hh:mm:ss");//parseDate parses a string to determine if it contains a date value, and returns a standard date in the format yyyy-MM-ddTkk:mm:ss
        int businessYear = (int) Float.parseFloat(row[4]);// converting float datatype of businessYear to int.
        long docId = (long) Double.parseDouble(row[5]);// the datatype of docID is long.
        Date postingDate = parseDate(row[6], "yyyy-MM-dd");
        Date documentCreateDate = parseDate(row[8], "yyyyMMdd");
        Date dueInDate = parseDate(row[9], "yyyyMMdd");
        String invoiceCurrency = row[10];
        String documentType = row[11];
        boolean postingId = Boolean.parseBoolean(row[12]);// postingID returns 0 or 1.
        String areaBusiness = row[13];
        Double totalOpenAmount = Double.parseDouble(row[14]);// datatype of totalopenAmount is double. 
        Date baselineCreateDate = parseDate(row[15], "yyyyMMdd");
        String custPaymentTerms = row[16];
        long invoiceId = row[17] != null && !row[17].isEmpty() ? (long) Double.parseDouble(row[17]) : 0;// it checks the invoice id if it is not null and not empty then invoice id has long datatype or else it returns 0.
        boolean isOpen = Boolean.parseBoolean(row[18]);
        Invoice invoice = new Invoice(businessCode, custNumber, nameCustomer, clearDate, businessYear, docId,
            postingDate, documentCreateDate, dueInDate, invoiceCurrency, documentType, postingId,
            areaBusiness, totalOpenAmount, baselineCreateDate, custPaymentTerms, invoiceId, isOpen);//All the columns are stored in invoice instance.
        invoices.add(invoice); // Adding the columns in invoices variable.
      }
    } catch (IOException e) {
      e.printStackTrace();// if any error found it prints the detail about the error.
    }
   // System.out.println(invoices.get(0));
    return invoices;
  }
  public static void insert(ArrayList<Invoice> invoices) {
	    String query = "INSERT into invoice_details (business_code,cust_number,name_customer,clear_date,business_year,doc_id,posting_date,document_create_date,due_in_date,invoice_currency,document_type,posting_id,area_business,total_open_amount,baseline_create_date,cust_payment_terms,invoice_id,isOpen) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	    Connection conn = null;
	    PreparedStatement stmt = null;
	    try {
	      Class.forName("com.mysql.jdbc.Driver");
	      conn = DriverManager.getConnection(DB_URL, USER, PASS);//Establishes the connection with the database.
	      stmt = conn.prepareStatement(query);
	      for (int i = 0; i < invoices.size(); ++i) { // inserting the data into its specific columns.
	        Invoice invoice = invoices.get(i);
	        stmt.setString(1, invoice.getBusinessCode());
	        stmt.setString(2, invoice.getCustNumber());
	        stmt.setString(3, invoice.getNameCustomer());
	        stmt.setDate(4, invoice.getClearDate());
	        stmt.setInt(5, invoice.getBusinessYear());
	        stmt.setLong(6, invoice.getDocId());
	        stmt.setDate(7, invoice.getPostingDate());
	        stmt.setDate(8, invoice.getDocumentCreateDate());
	        stmt.setDate(9, invoice.getDueInDate());
	        stmt.setString(10, invoice.getInvoiceCurrency());
	        stmt.setString(11, invoice.getDocumentType());
	        stmt.setBoolean(12, invoice.postingId);
	        stmt.setString(13, invoice.getAreaBusiness());
	        stmt.setDouble(14, invoice.getTotalOpenAmount());
	        stmt.setDate(15, invoice.getBaselineCreateDate());
	        stmt.setString(16, invoice.getCustPaymentTerms());
	        stmt.setLong(17, invoice.getInvoiceId());
	        stmt.setBoolean(18, invoice.isOpen());
	        stmt.addBatch(); //The addBatch() method is used to add individual statements to the batch.
	        if (i % 5000 == 0){ // batchsize = 5000
	          stmt.executeBatch(); //The executeBatch() is used to start the execution of all the statements grouped together.
	        }
	      }
	      stmt.executeBatch(); //if any statement will be left to execute in the above line will be executed from here.
	    } 
	    catch (SQLException se) // Handle errors for JDBC
	    {	
	      se.printStackTrace(); 
	    }
	    catch (Exception e)		// Handle errors for class.forName
	    {
	      e.printStackTrace();
	    } 
	    finally 				// finally block is used to close resources 
	    {
	      try {
	        if (stmt != null)	
	          stmt.close();			//it will close all ResulSets that were created by that statement.
	      } 
	      catch (SQLException se2) { // Handles the sql exceptions if any.
	      }
	      try {
	        if (conn != null) // it will check for conn if not equal to null then close.
	          conn.close();
	      } 
	      catch (SQLException se) 
	      {
	        se.printStackTrace();
	      }
	    }
	  }

	  public static void main(String args[]) throws Exception {
		ArrayList<Invoice> invoices = readinvoices(); //reads the invoices and stores as a arraylist object invoice
	    System.out.println("The total number of rows present in the dataset is :");
	    System.out.println(invoices.size());// It will print the total number of rows in the given dataset.
	    insert(invoices);// calls the insert function for inserting invoices in the database.
	    System.out.println("Finally all the data have been successfully entered into the database");

	  }
	}
