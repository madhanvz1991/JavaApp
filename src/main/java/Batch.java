package com.cts.uw.uwd;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;




public class SummaryBatch {

	/**
	 * @param args
	 * @throws SQLException 
	 */
		
	public static void main(String[] args) throws ParseException, SQLException {
		
		
		byte[] submissionXml;
	    String lobId = "";
	    String productId = "";
	    String stage = "";
	    String submissionId ="";
	    String tiv="";
	    String decision = "";
	    String lossHistory = "";
	    String naicCode = "";
	    String basePrice = "";
	    Timestamp created =null;
	    String uwName = "";
	    String insuredName = "";
	    Timestamp receivedDate = null;
	    String agencyName = "";
	    String insuredState="";
	    String premium = "";
	    String naicDesc = "";
	    String noOfYears=""; 
	    String riskAppetite = "";
	    Timestamp quoteDate = null;
	    Scanner scanner = new Scanner(System.in);

	    System.out.println("\nEnter the years in number for updating Submission Details ");
	    noOfYears = scanner.nextLine();

	    System.out.println("\nDo you want to update all submission details within "+noOfYears+"Year/Years??...Please Enter Y/N");
	    decision = scanner.nextLine();
	    

	    if (decision.equalsIgnoreCase("Y")) {
	      
	        Connection dbConnection = null;
	        PreparedStatement preparedStatement1 = null;
	        PreparedStatement preparedStatement2 = null;
	        PreparedStatement preparedStatement3 = null;


	        Map<String, Object> dataMap;
      
	          try {
		          
	        	 dbConnection = getDBConnection();
	        	 String deleteTable="DELETE FROM SUBMISSIONSUMMARY";
	        	 preparedStatement3= dbConnection.prepareStatement(deleteTable);
	        	 preparedStatement3.executeUpdate();
	        	 String selectSQL = "SELECT SUBMISSIONXML,LOBID,PRODUCTID,UWSTAGE,SUBMISSIONID,CREATED,UWNAME,INSUREDNAME,RECEIVEDDT,AGENCYNAME FROM SUBMISSION  WHERE created between (?-?) and ? ORDER BY CREATED DESC";
	        	 preparedStatement1 = dbConnection.prepareStatement(selectSQL);
	             preparedStatement1.setTimestamp(1, getCurrentTimeStamp());
	             //System.out.println("yeaaaaaaaarrrs"+Integer.parseInt(noOfYears)*365);
	             preparedStatement1.setInt(2, Integer.parseInt(noOfYears)*365);
	             preparedStatement1.setTimestamp(3, getCurrentTimeStamp());
	             //System.out.println(getCurrentTimeStamp()+"staaamp");


	        	 ResultSet rs = preparedStatement1.executeQuery();
	        	 int count=0;
	        	 while (rs.next()) {
	        		 count=count+1;
	        		submissionXml = rs.getBytes("SUBMISSIONXML");
	        		lobId=rs.getString("LOBID");
	        		productId=rs.getString("PRODUCTID");
	        		stage=rs.getString("UWSTAGE");
	        		submissionId = rs.getString("SUBMISSIONID");
	        		created = rs.getTimestamp("CREATED");
	        		uwName = rs.getString("UWNAME");
	        		insuredName = rs.getString("INSUREDNAME");
	        		receivedDate = rs.getTimestamp("RECEIVEDDT");
	        		agencyName = rs.getString("AGENCYNAME");
				 	Map policyMap = null;
				 	Map<String,String> lossAmountMap=new HashMap<String,String>();

	        		 dataMap=createMapFromXml(submissionXml);
	        		 if((String) dataMap.get("naicCodequalify")!=""){
	        			 naicCode = (String) dataMap.get("naicCodequalify");
	        		 }else{
	        			 naicCode ="";
	        		 }
	        		 
	        		 if((String) dataMap.get("insuredState")!=""){
	        			 insuredState = (String) dataMap.get("insuredState");
	        		 }else{
	        			 insuredState ="";
	        		 }
	        		 
	        		 if((String) dataMap.get("Premium")!=""){
	        			 premium = (String) dataMap.get("Premium");
	        		 }else{
	        			 premium ="";
	        		 }
	        		 
	        		 if((String) dataMap.get("naicCode")!=""){
	        			 naicDesc = (String) dataMap.get("naicCode");
	        		 }else{
	        			 naicDesc ="";
	        		 }
	        		 
	        		 if((String) dataMap.get("naiscClassifScore")!=""){
	        			 riskAppetite = (String) dataMap.get("naiscClassifScore");
	        		 }else{
	        			 riskAppetite ="";
	        		 }
	        		 
	        		 if(((String) dataMap.get("firstQuoteDate"))!=null){
	        			 String qDateString = (String) dataMap.get("firstQuoteDate");
	        			   SimpleDateFormat sdf=new SimpleDateFormat("MM/dd/yyyy");
	        			   Date qDate = sdf.parse(qDateString);
	        			   quoteDate = new java.sql.Timestamp(qDate.getTime());
	        		 }else{
	        			 quoteDate =null;
	        		 }
	        		 
	        			if(dataMap.get("coverages")!=null){
	    					Map<String,Object> map1=(Map<String, Object>) dataMap.get("coverages");
	    					policyMap= (Map) map1.get("policyCoverage");
	    					}	
	    				if(policyMap != null){
	    					 if((String) policyMap.get("tiv")!="" && (String) policyMap.get("tiv")!=null){
	                             tiv=(String) policyMap.get("tiv");
	                             }else{
	        	        			 tiv ="";
	        	        		 }
	    					}
	    				
	    				 if((String)dataMap.get("basePrice")!=null && dataMap.get("basePrice").toString().length()>0){
	    					 basePrice= (String) dataMap.get("basePrice");
		        		 }else{
		        			 basePrice ="";
		        		 }
	    				
	    				if(dataMap.get("lossInfo")!=null){
	    					//System.out.println(dataMap.get("lossInfo")+"lossss");
	    					Map<String,Object> carrierMap=(Map<String, Object>) dataMap.get("lossInfo");
	    					List<Map> lossListMap=(List<Map>) carrierMap.get("lossHistories");
	    					
	    					if(lossListMap!=null && !lossListMap.isEmpty()){
	    					for (Map carrier : lossListMap) {
	    						Map<String,String> lossValues=(Map<String,String>) carrier.get("lossHistory");
	    						if(lossValues!=null && !lossValues.isEmpty() ){
	    							if((String) lossValues.get("dateOfOccurence")!="" && (String) lossValues.get("dateOfOccurence")!=null
	    									&&(String) lossValues.get("amountPaid")!="" && (String) lossValues.get("amountPaid")!=null ){
	    							lossAmountMap.put(lossValues.get("dateOfOccurence"),lossValues.get("amountPaid"));
	    							}
	    						 }
	    				      }
	    				   }
	    				}
	    			int totalLoss=0;
	    			if(!lossAmountMap.isEmpty() && lossAmountMap!=null && lossAmountMap.size()>0){
	    				for (Map.Entry<String, String> entry : lossAmountMap.entrySet())
	    				{
	    					if(entry.getKey().toString().matches("(0?[1-9]|1[012])/(0?[1-9]|[12][0-9]|3[01])/((19|20)\\d\\d)")){
	    					SimpleDateFormat sdf=new SimpleDateFormat("MM/dd/yyyy");
	    					Date d1=sdf.parse((String) entry.getKey()) ;
	    					Date d2=new Date();
	    					Calendar c1=Calendar.getInstance();
	    					Calendar c2=Calendar.getInstance();
	    					c1.setTime(d1);
	    					c2.setTime(d2);
	    					c2.add(c2.YEAR, -3);
	    					System.out.println(c2.getTime());
	    					if(c1.after(c2)){
	    						String tempVal = entry.getValue();
	    						if(!tempVal.isEmpty()){
	    						totalLoss=totalLoss+Integer.valueOf((String) entry.getValue());
	    						}
	    					}
	    					//totalLoss=totalLoss+Integer.valueOf(entry.getValue());
	    				     }
	    				}
		        		 lossHistory=String.valueOf(totalLoss);
	    			}else{
	    				lossHistory="";
	    			}

	    			//System.out.println(totalLoss+"sss");
	        		//System.out.println(lossAmountMap+"sss");
	        		//System.out.println(submissionXml+"---jjjjj");
	        		 System.out.println(submissionId+"--subId");
	        		 System.out.println(lobId+"--lob");
	        		 System.out.println(productId+"--product");
	        		 System.out.println(basePrice+"--basePrice");
	        		 System.out.println(lossHistory+"--loss");
	        		 System.out.println(naicCode+"--naic");
	        		 System.out.println(tiv+"--tiv");
	        		 System.out.println(stage+"--stage"); 
	        		 System.out.println(created+"--created");
	        		 System.out.println("--------------------");
	        		 //System.out.println(dataMap+"--map");
	        		 
	        				 
	        		 System.out.println(tiv.length()+"------tiv length");
	        		//insert query for Updating the Submission Summary Details	
	        	     String summaryUpdate = "INSERT INTO SUBMISSIONSUMMARY"
	        					+ "(SUBMISSIONID, LOBID, PRODUCTID,BASEPRICE,LOSSHISTORY,NAICCODE,TIV,STAGE,CREATED,UWNAME,INSUREDNAME,RECEIVEDDT,AGENCYNAME,INSUREDSTATE,PREMIUM,NAICDESC,RISKAPPETITE,QUOTEDATE) VALUES"
	        					+ "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	        		 preparedStatement2 = dbConnection.prepareStatement(summaryUpdate);	

	        		 preparedStatement2.setString(1, submissionId);
	                 preparedStatement2.setString(2, lobId);
	                 preparedStatement2.setString(3, productId);
	                 preparedStatement2.setString(4, basePrice);
	                 preparedStatement2.setString(5, lossHistory);
	                 preparedStatement2.setString(6, naicCode);
	                 preparedStatement2.setString(7, tiv);
	                 preparedStatement2.setString(8, stage);
	                 preparedStatement2.setTimestamp(9, created);
	                 preparedStatement2.setString(10, uwName);
	                 preparedStatement2.setString(11, insuredName);
	                 preparedStatement2.setTimestamp(12, receivedDate);
	                 preparedStatement2.setString(13, agencyName);
	                 preparedStatement2.setString(14, insuredState);
	                 preparedStatement2.setString(15, premium);
	                 preparedStatement2.setString(16, naicDesc);
	                 preparedStatement2.setString(17, riskAppetite);
	                 preparedStatement2.setTimestamp(18, quoteDate );


	                 
		        	 preparedStatement2.addBatch();
		        	 preparedStatement2.executeBatch();
		        	 
					
		        	
	        		}
	         
	   	 	 	 dbConnection.commit();
	 			 System.out.println("RecordS are inserted into SUBMISSIONSUMMARY table!");

	        	 
	  
	        	 
			} catch ( SQLException | JDOMException | IOException e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
			}

	
	
	    }else{
	    	System.out.println("\n Updation of Submission Details cancelled");
	    }

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
	        "jdbc:oracle:thin:@10.237.213.153:1521:OPTIMAWRITED", "SQdev", "SQdev");
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
	
	public static Map<String, Object> createMapFromXml(byte[] byteVal) throws JDOMException, IOException
	  {

	    InputStream inStream = new ByteArrayInputStream(byteVal);

	    SAXBuilder builder = new SAXBuilder();

	    Document doc = builder.build(inStream);
	    Element rootNode = doc.getRootElement();
	    List<Element> elementList = rootNode.getChildren();

	    Map mapObject = new UWDMap();

	    for (Element element : elementList)
	    {
	      updateMap(mapObject, element, element.getAttributeValue("type"));
	    }


	    return mapObject;
	  }
	 
	
	  public static void updateMap(Map<String, Object> mapObject, Element element, String type)
	  {
	    if ("complex".equalsIgnoreCase(type))
	    {
	      Map childObject = new UWDMap();
	      mapObject.put(element.getName(), childObject);

	      for (Element element1 : element.getChildren()) {
	        updateMap(childObject, element1, element1.getAttributeValue("type"));
	      }
	    }
	    else if ("simple".equalsIgnoreCase(type))
	    {
	      mapObject.put(element.getName(), element.getValue());
	    }
	    else if ("list".equalsIgnoreCase(type))
	    {
	      List list = new ArrayList();

	      mapObject.put(element.getName(), list);

	      for (Element element2 : element.getChildren())
	      {
	        Map child = new UWDMap();

	        updateMap(child, element2, element2.getAttributeValue("type"));

	        list.add(child);
	      }
	    }
	  }
	
	  
	  
	  
}
