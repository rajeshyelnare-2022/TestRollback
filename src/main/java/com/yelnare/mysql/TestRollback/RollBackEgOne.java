package com.yelnare.mysql.TestRollback;

import java.sql.*;
import java.io.*;
import java.util.*;

/**
 *  
 * MariaDB [finance]> show columns from testThread;
+----------+--------------+------+-----+---------+-------+
| Field    | Type         | Null | Key | Default | Extra |
+----------+--------------+------+-----+---------+-------+
| id       | int(11)      | YES  |     | NULL    |       |
| name     | varchar(100) | YES  |     | NULL    |       |
| priority | int(11)      | YES  |     | NULL    |       |
+----------+--------------+------+-----+---------+-------+
 * @author rajye
 *
 */


public class RollBackEgOne implements Runnable
{
	  public static Thread t1;
	  public static RollBackEgOne o1;
	  public static Connection conn = null;
    public static void main( String[] args )
    {
    	RollBackEgOne r1 = new RollBackEgOne();
    	// get the connection 
     	Connection conn = r1.getConnection();
        // Thread rollback
     	
        Thread t1 = new Thread(r1, "Thread One");
        t1.start();
        Thread t2 = new Thread(r1, "Thread Two");
        t2.start();
     
        try {
			t1.join();
		     t2.join();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
        r1.PrintValues("Before Rollback");
        try {
			conn.rollback();
		} catch (SQLException e) {
			System.out.println("ERROR in Run Metod"+e);
		}
        r1.PrintValues("After Rollback");
        
        try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    public  void PrintValues(String str) {
    	System.out.println("PrintValues() Start "+str);
    	try {
        Statement stmt = null;
        stmt = (Statement) conn.createStatement();
    	 ResultSet rs = stmt.executeQuery(" select id,name,priority from TestThread");
         rs.beforeFirst();
         while(rs.next()){
            System.out.print("ID: " + rs.getInt("id")+
            ", Name: " + rs.getString("name")+
            ", priority: " + rs.getInt("priority"));
         }
         System.out.println();
         System.out.println("Printing done ");
    	}
    	catch (SQLException e) {
			System.out.println("ERROR in PrintValues Metod"+e);
		}
    	
    }
    public  Connection getConnection () {
   
    	try {
        conn = (Connection) DriverManager.getConnection("jdbc:mariadb://localhost/finance", "root", "finance123");
        conn.setAutoCommit(false);
        Thread.sleep(5000);
    	}
     catch (Exception e) {
        System.out.println("ERROR in getConnection "+e);
     }
    	  return conn;
    	
    }
    public  void insertaRow(long id, String name,int priority) {
   	 System.out.println( "In method insertaRow Using thread priority for priority" );
       Statement stmt = null;
       try {
            if(conn == null )
            {
            	System.out.println(" connection is null");
            }
             stmt = (Statement) conn.createStatement();
             String query1 = "INSERT INTO testThread VALUES ("+id+",'"+name+"',"+priority+")";
             System.out.println("Query is:"+query1);
             stmt.executeUpdate(query1);
             System.out.println("Record is inserted in the table successfully");
          } catch (Exception e) {
             System.out.println("ERROR in insertaRow"+e);
          }
   	
   }
    public void run()
    {
        System.out.println(
            "Thread is created and running ...NAME"+ Thread.currentThread().getName()+"ID-> "+Thread.currentThread().getId());
        RollBackEgOne RollBackEgOne = new RollBackEgOne();
        RollBackEgOne.insertaRow(Thread.currentThread().getId(),Thread.currentThread().getName(),Thread.currentThread().getPriority());
       System.out.println(
                 Thread.currentThread().getName() +" is Completed"); 
    }
}
