/**
 * TODO This is the class description
 */


package Server;

import java.sql.ResultSet;
import java.sql.SQLException;

import models.*;
import support.DBManager;


/**
 * @author G5 lab group
 * The Class SCclinic.
 */
public class SCclinic {



	/**
	 * Gets the our clinic list.
	 *
	 * @return the envelope
	 */
	public static Envelope GetOurClinicList()
	{
		Envelope en = new Envelope();
		DBManager mysql = DBManager.getInstance();
		String querystr;
		ResultSet rs = null;
		
		querystr="SELECT * "
				+ "FROM clinic";
		System.out.println(querystr);
		
		try 
		{
			rs = mysql.querySelect(querystr);
			while (rs.next())
            {
				en.addobjList(new Clinic(rs.getInt(1),rs.getString(2),rs.getString(3)));
				System.out.println(rs.getString(1)+" "+rs.getString(2));
            }   
			
		}
		catch (SQLException ex) 
   	    {/* handle any errors*/
          System.out.println("SQLException: " + ex.getMessage());
          System.out.println("SQLState: " + ex.getSQLState());
          System.out.println("VendorError: " + ex.getErrorCode());
         
        }
		return en;
		
	}
	
	
}
