/**
 * TODO This is the class description
 */


package Server;

import java.sql.ResultSet;
import java.sql.SQLException;


import java.text.SimpleDateFormat;
import java.util.Date;

import models.*;
import support.DBManager;
import enums.*;

/**
 * @author G5 lab group
 * The Class SClab.
 */
public class SClab {

	
	
	/**
	 * Get_ scheduel d_labs.
	 *
	 * @param ptID the pt id
	 * @return the envelope
	 */
	
	static String ucid =  "ucID";
	
	public static Envelope Get_SCHEDUELD_labs(String ptID)
	{
		
		
		ResultSet rs;
		Envelope en = new Envelope();
		LabSettings ls;
		User doctor = new User();
		Clinic cl;
		
		String querystr="SELECT * "
				+ "FROM labsettings,user,clinic  "
				+ "WHERE labDocID= ? AND labPtID= ? AND labStatus= ? AND cID = ?"
				+ " ORDER BY labCreateDate DESC ;";
		
		try 
		{
			DBManager mysql = DBManager.getInstance();
			rs = mysql.querySelect(querystr, doctor.getuID(), ptID, Status.SCHEDUELD, ucid);
			en.setStatus(Status.NOT_EXIST);
			while (rs.next())
            {
				Status st =  Status.valueOf(rs.getString("labStatus"));
				
				ls = new LabSettings(rs.getInt("labID"),rs.getString("labPtID"), rs.getString("labCreateDate"), rs.getString("labCreateTime"), st,
						rs.getString("labDocID"), rs.getString("labDocReq"));
				
				
				doctor = new User();
				doctor.setuID(rs.getString("labDocID"));
				doctor.setuFirstName(rs.getString("uFirstName"));
				doctor.setuLastName(rs.getString("uLastName"));
				
				cl = new Clinic();
				cl.setcID(rs.getInt("cID"));
				cl.setcLocation(rs.getString("cLocation"));
				cl.setcName("cName");
				doctor.setuClinic(cl);
				ls.setLabWorker(doctor);
				
				en.addobjList(ls);
				System.out.println(ls.toStringOpenLabs());
				en.setStatus(Status.EXIST);
            }   
			
			en.setType(task.GET_SCHEDUELD_LAB);
			
		}
		catch (SQLException ex) 
   	    {/* handle any errors*/
          System.out.println("SQLException: " + ex.getMessage());
          System.out.println("SQLState: " + ex.getSQLState());
          System.out.println("VendorError: " + ex.getErrorCode());
          en.setStatus(Status.FAILED_EXCEPTION);
          return en;
        }
		
		return en;

	}

	
	/**
	 * Get_ arrive d_labs.
	 *
	 * @param ptID the pt id
	 * @return the envelope
	 */
	public static Envelope Get_ARRIVED_labs(String ptID)
	{
		
		ResultSet rs;
		Envelope en = new Envelope();
		LabSettings ls;
		User doctor = new User();
		Clinic cl;
		
		String querystr="SELECT * "
				+ "FROM labsettings,user,clinic  "
				+ "WHERE labDocID= ? AND labPtID= ? AND labStatus= ? AND cID = ?"
				+ " ORDER BY labCreateDate DESC;";
		
		try 
		{
			DBManager mysql = DBManager.getInstance();
			rs = mysql.querySelect(querystr, doctor.getuID(), ptID, Status.ARRIVED, ucid);
			
			en.setStatus(Status.NOT_EXIST);
			while (rs.next())
            {
				Status st =  Status.valueOf(rs.getString("labStatus"));
				
				ls = new LabSettings(rs.getInt("labID"),rs.getString("labPtID"), rs.getString("labCreateDate"), rs.getString("labCreateTime"), st,
						rs.getString("labDocID"), rs.getString("labDocReq"));
				
				
				doctor = new User();
				doctor.setuID(rs.getString("labDocID"));
				doctor.setuFirstName(rs.getString("uFirstName"));
				doctor.setuLastName(rs.getString("uLastName"));
				
				cl = new Clinic();
				cl.setcID(rs.getInt("cID"));
				cl.setcLocation(rs.getString("cLocation"));
				cl.setcName("cName");
				doctor.setuClinic(cl);
				ls.setLabWorker(doctor);
				ls.setLabWorkerSummery(rs.getString("labWorkerSummery"));
				
				String filePath = rs.getString("labPhotoPath");
				ls.setFilePath(filePath);
				if(!filePath.equals("NO FILE"))
				{
					String extension = filePath;
				    int index=extension.indexOf(".");
				    //get the extension of the file
				    extension=extension.substring(index+1, extension.length());
				    ls.setFileExt(extension);
				}
				
				en.addobjList(ls);
				System.out.println(ls.toStringOpenLabs());
				en.setStatus(Status.EXIST);
            }   
			
			en.setType(task.GET_SCHEDUELD_LAB);
		
		}
		catch (SQLException ex) 
   	    {/* handle any errors*/
          System.out.println("SQLException: " + ex.getMessage());
          System.out.println("SQLState: " + ex.getSQLState());
          System.out.println("VendorError: " + ex.getErrorCode());
          en.setStatus(Status.FAILED_EXCEPTION);
          return en;
        }
		
		return en;

	}
	
	
	
	
	/**
	 * Update lab record.
	 *
	 * @param labID the lab id
	 * @param record the record
	 * @param labworkerID the labworker id
	 */
	public static void UpdateLabRecord(int labID,String record,String labworkerID)
	{
		
		
		String querystr="UPDATE labsettings "
						+ "SET labStatus = ? ,labWorkerSummery = ? ,labworkerID = ? "
						+ "WHERE labID = ? ;";
		
		DBManager mysql = DBManager.getInstance();
		mysql.query(querystr, Status.ARRIVED, record, labworkerID, labID);
		

	}




	/**
	 * Update lab file path.
	 *
	 * @param filename the filename
	 * @param labID the lab id
	 */
	public static void UpdateLabFilePath(String filename,int labID) {
		

		String querystr="UPDATE labsettings "
				+ "SET labPhotoPath='"+filename+"' "
				+ "WHERE labID='"+labID+"'";
		
		DBManager mysql = DBManager.getInstance();
		mysql.query(querystr, filename, labID);
		
	}
	
	
	/**
	 * Gets the lab file path.
	 *
	 * @param labID the lab id
	 * @return the string
	 */
	public static String GetLabFilePath(int labID) {
		
		
		ResultSet rs;
		String filePath = "";
		String querystr="SELECT * labsettings "
				+ "WHERE labID= ?;";
		
		try 
		{
			DBManager mysql = DBManager.getInstance();
			rs = mysql.querySelect(querystr, labID);
			rs.next();
			filePath=rs.getString("labPhotoPath");
			
		}
		catch (SQLException ex) 
   	    {/* handle any errors*/
          System.out.println("SQLException: " + ex.getMessage());
          System.out.println("SQLState: " + ex.getSQLState());
          System.out.println("VendorError: " + ex.getErrorCode());
          //return Status.FAILED_EXCEPTION;
        }
		
		return filePath;
	}

	/**
	 * Creaet lab ref.
	 *
	 * @param lb the lb
	 * @return the status
	 */
	public static Status CreaetLabRef(LabSettings lb) {
	
		String querystr;

		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm:ss");
		String createdDate = formatter.format(new Date());
		String createdHour = hourFormat.format(new Date());
		
		
		querystr="INSERT INTO labsettings " + " (labPtID,labCreateDate,labCreateTime,labStatus,labDocID,labDocReq) "
				+ "VALUES ('"+lb.getLabPtID()+"','"+createdDate+"','"+createdHour+"','SCHEDUELD','"+lb.getLabDocID()+"'"
				+",'"+lb.getLabDoctorReq()+"')";

		DBManager mysql = DBManager.getInstance();
		mysql.querySelect(querystr);
		
		return Status.CREATED;
	}
}
