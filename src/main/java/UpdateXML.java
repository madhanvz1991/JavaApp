package main.java.com.cts.uw;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Scanner;

public class UpdateConfigXML
{
  public static void main(String[] args)
  {
    String filePath = "";
    String fileName = "";
    String fileKey = "";
    String decision = "";

    Scanner scanner = new Scanner(System.in);

    System.out.println("\nEnter the XML file name and its path for updating");
    filePath = scanner.nextLine();

    fileName = filePath.replace("\\", "/");

    fileName = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());

    System.out.println("\nFile name path is " + fileName);

    System.out.println("\nEnter the Key for the entered " + fileName);
    fileKey = scanner.nextLine();

    System.out.println("\nXML file name entered is " + fileName + " and the key for the xml is " + fileKey);
    System.out.println("\nProceed updating... Please enter y/n");
    decision = scanner.nextLine();

    if (decision.equalsIgnoreCase("y")) {
      if ((fileKey != null) && ((fileKey.equalsIgnoreCase("L1_P1")) || (fileKey.equalsIgnoreCase("def_def"))))
      {
        Connection dbConnection = null;
        PreparedStatement preparedStatement = null;
        try {
          dbConnection = getDBConnection();
          InputStream instream = new FileInputStream(filePath);
          preparedStatement = dbConnection.prepareStatement("UPDATE config_xmls set XML=?, LASTUPDATEDTIME = ? , LASTUPDATEDBY = ? WHERE XML_NAME = ? AND KEY = ?");

          preparedStatement.setBlob(1, instream);
          preparedStatement.setTimestamp(2, getCurrentTimeStamp());
          preparedStatement.setString(3, "SQConfigs");

          preparedStatement.setString(4, fileName);
          preparedStatement.setString(5, fileKey);
          preparedStatement.executeUpdate();
          dbConnection.commit();
          System.out.println("\n" + fileName + " file updated successfully");
        }
        catch (IOException io) {
          System.out.println("\nXML file to be updated is not present in the path");
        } catch (Exception e) {
          e.printStackTrace();
        }
      } else {
        System.out.println("\nPlease enter the correct key for the entered " + fileName);
      }
    }
    else System.out.println("\nXML file updation cancelled");
  }

  private static Connection getDBConnection()
  {
    Connection dbConnection = null;
    try {
      Class.forName("oracle.jdbc.driver.OracleDriver");
    } catch (ClassNotFoundException e) {
      System.out.println(e.getMessage());
    }
    try {
      return DriverManager.getConnection(
        "jdbc:oracle:thin:@10.237.213.153:1521:OPTIMAWRITED", "SQConfigs", "sqconfigs");
    }
    catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return dbConnection;
  }

  private static Timestamp getCurrentTimeStamp()
  {
    Timestamp currTime = new Timestamp(new Date().getTime());
    return currTime;
  }
}
