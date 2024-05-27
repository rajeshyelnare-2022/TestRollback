package com.yelnare.mysql.TestRollback;

import java.sql.*;
import java.io.*;
import java.util.*;




public class App implements Runnable
{
	  public static Thread t1;
	    public static App o1;
    public static void main( String[] args )
    {
        System.out.println( "Test The SQL ROLLBACK!" );
        // simple rollback
        App app = new App();
        app.testSimpleInsertRollback();
        // Thread rollback
        Runnable r1 = new App();
        Thread t1 = new Thread(r1, "Thread One");
        t1.start();
        Thread t2 = new Thread(r1, "Thread Two");
        t2.start();
     
        String str = t1.getName();
        System.out.println(str);
        try {
			getConnection().rollback();
		} catch (SQLException e) {
			System.out.println("ERROR in Run Metod"+e);
		}
    }
    public static Connection getConnection () {
    	Connection conn = null;
    	try {
        conn = (Connection) DriverManager.getConnection("jdbc:mariadb://localhost/finance", "root", "finance123");
        conn.setAutoCommit(false);
    	}
     catch (Exception e) {
        System.out.println("ERROR in getConnection "+e);
     }
    	  return conn;
    	
    }
    public  void testSimpleInsertRollback() {
    	System.out.println( "In method testInsertRollback" );
    
        Statement stmt = null;
        try {
              System.out.println("Connection is created successfully:");
              stmt = (Statement) getConnection().createStatement();
              String query1 = "INSERT INTO account " + "VALUES (5, 'John', 34)";
              stmt.executeUpdate(query1);
              System.out.println("Record is inserted in the table successfully..................");
              ResultSet rs = stmt.executeQuery(" select id,name,balance from account");
              rs.beforeFirst();
              while(rs.next()){
                 System.out.print("ID: " + rs.getInt("id"));
                 System.out.print(", Name: " + rs.getString("name"));
                 System.out.print(", Balance: " + rs.getInt("balance"));
              }
              System.out.println();
              getConnection().rollback();
              	
           } catch (Exception e) {
              System.out.println("ERROR in testSimpleInsertRollback"+e);
           }
        
   
    	
    }
    public  void insertaRow(long id, String name,int priority) {
   	 System.out.println( "In method insertaRow Using thread priority for balance" );
   	if(Thread.currentThread().getName().equals("Thread Two")) {
     	try {
     		System.out.println(" Sleep for 1 seconds ");
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				  System.out.println("ERROR in sleeep"+e);
			}
     }
       Statement stmt = null;
       try {
            
             stmt = (Statement) getConnection().createStatement();
             String query1 = "INSERT INTO account VALUES ("+id+",'"+name+"',"+priority+")";
             System.out.println("Query is:"+query1);
             stmt.executeUpdate(query1);
             System.out.println("Record is inserted in the table successfully..................");
             ResultSet rs = stmt.executeQuery(" select id,name,balance from account");
             rs.beforeFirst();
             while(rs.next()){
                System.out.print("ID: " + rs.getInt("id"));
                System.out.print(", Name: " + rs.getString("name"));
                System.out.print(", Balance: " + rs.getInt("balance"));
             }
             System.out.println();
             
             	
          } catch (Exception e) {
             System.out.println("ERROR in testSimpleInsertRollback"+e);
          }
       
  
   	
   }
    public void run()
    {
        System.out.println(
            "Thread is created and running ...NAME"+ Thread.currentThread().getName()+"ID-> "+Thread.currentThread().getId());
        App app = new App();
        app.insertaRow(Thread.currentThread().getId(),Thread.currentThread().getName(),Thread.currentThread().getPriority());
        if(Thread.currentThread().getName().equals("Thread Two")) {
        	try {
        		System.out.println(" Sleep for 5 seconds ");
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				  System.out.println("ERROR in sleeep"+e);
			}
        }
        System.out.println(
                 Thread.currentThread().getName() +" is Completed"); 
    }
}
