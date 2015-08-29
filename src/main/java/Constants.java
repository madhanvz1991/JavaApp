package main.java.com.cts.uw;

public class Constants
{
  public static final String DB_DRIVER = "oracle.jdbc.driver.OracleDriver";
  public static final String DB_CONNECTION = "jdbc:oracle:thin:@10.237.213.153:1521:OPTIMAWRITED";
  public static final String DB_USER = "SQConfigs";
  public static final String DB_PASSWORD = "sqconfigs";
  public static final String NULL_STRING = "";
  public static final String PATH_NAME = "D:/TEMP/";
  public static final String UPDATE_QUERY = "UPDATE config_xmls set XML=?, LASTUPDATEDTIME = ? , LASTUPDATEDBY = ? WHERE XML_NAME = ? AND KEY = ?";
  public static final String DECISION = "y";
  public static final String PRODUCT_KEY = "L1_P1";
  public static final String DEF_KEY = "def_def";
}
