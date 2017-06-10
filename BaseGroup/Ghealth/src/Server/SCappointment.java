/**
 * TODO This is the class description
 */


package Server;

import java.sql.ResultSet;
import java.sql.SQLException;


import java.util.ArrayList;

import java.util.Collections;
import java.util.List;

import models.*;
import support.DBManager;
import enums.*;

/**
 * @author G5 lab group
 * The Class SCappointment.
 */
public class SCappointment {

	/**
	 * Gets the clinic doctor list.
	 *
	 * @param pt the patient
	 * @param sp the Specialty
	 * @return the envelope
	 */
	final static String CONSARR = "ARRIVED";
	final static String CONSSCH = "SCHEDULED";
	static String did = "dID";
	static String ucid = "ucID";
	public static Envelope GetClinicDoctorList(String pt,String sp)
	{
		DBManager mysql = DBManager.getInstance();
		ResultSet rs = null;
		String querystr_a,querystr_b,querystr_c;
		User us = null;
		Envelope en = new Envelope();
		
		/* Return patient row if exist */
		System.out.println(sp);
		
		
		
		querystr_a="CREATE OR REPLACE VIEW AMIR AS"
				+ " SELECT * "
				+ " FROM appointmentsettings a "
				+ " WHERE a.apsPtID= ? AND a.apsStatus= ? ;";
		
		querystr_b="SELECT COUNT(*) AS COUNT"
				+ " FROM appointmentsettings a,doctor d"
				+ " WHERE a.apsPtID= ? AND a.apsStatus= ? AND d.dSpeciality= ? AND d.dID= ? ;";
		
		querystr_c="SELECT DISTINCT uID,uFirstName,uLastName,cLocation,cName "
				+ " FROM user,clinic,doctor LEFT JOIN AMIR on AMIR.apsDocID = doctor.dID "
				+ " WHERE dSpeciality= ? AND uID = ? AND cID = ?"
				+ " ORDER BY apsDate DESC;";
	
		
		try 
		{

			rs = mysql.querySelect(querystr_a, pt, CONSARR );
			System.out.println("after first query");
			
			AppointmentSettings as = new AppointmentSettings ();

			
			rs = mysql.querySelect(querystr_b, pt, CONSSCH , sp, as.getApsDocID());
			System.out.println("after second query");
			rs = mysql.querySelect(querystr_c, sp, did, ucid);
			rs.next();
			if(rs.getInt("COUNT") > 0)
			{
				System.out.println("There is SCHEDUELD appointment to "+sp+" Cant ceate more appointment to same doctor type!");
				en.setStatus(Status.SCHEDUELD);
			}
			else {
				rs = mysql.querySelect(querystr_c, sp, did, ucid);
				
				while (rs.next())
	            {
					/* Get & Create the exist user from DB */
					us = new User();
					Clinic cl = new Clinic();
					us.setuID(rs.getString("uID"));
					us.setuFirstName(rs.getString("uFirstName"));
					us.setuLastName(rs.getString("uLastName"));
					cl.setcLocation(rs.getString("cLocation"));
					cl.setcName(rs.getString("cName"));
					us.setuClinic(cl);
					System.out.println(us);
					en.addobjList(us);
					
				}
				
				en.setStatus(Status.ARRIVED);
			}
			en.setType(task.GET_DOCTORS_IN_CLINIC_BY_TYPE);
		}
		catch (SQLException ex) 
   	    {/* handle any errors*/
          System.out.println("SQLException: " + ex.getMessage());
          System.out.println("SQLState: " + ex.getSQLState());
          System.out.println("VendorError: " + ex.getErrorCode());
          return null;
        }
		
		
		return en;
	}
	
	
	/**
	 * Gets the available doctor hours.
	 *
	 * @param date the date
	 * @param uID the user id
	 * @return the envelope
	 */
	public static Envelope GetAvailibleDoctorHours(String date,String uID)
	{
		 
		String querystr;
		Envelope en = new Envelope();
		
		String []hours = new String[]{"8:00:00","8:30:00",
						"9:00:00","9:30:00",
						"10:00:00","10:30:00",
						"11:00:00","11:30:00",
						"12:00:00","12:30:00",
						"13:00:00","13:30:00",
						"14:00:00","14:30:00",
						"15:00:00","15:30:00",
						"16:00:00","16:30:00"};
		
		
		List<String> hoursList =  new ArrayList<String>();
	    Collections.addAll(hoursList, hours); 
	    
		querystr="SELECT apsTime FROM appointmentsettings"
				+ " WHERE apsDocID = ? AND apsDate = ?;";
	
		
		System.out.println(querystr);
		try 
		{
			User u = new User();
			AppointmentSettings app = new AppointmentSettings();
			DBManager mysql = DBManager.getInstance();
			ResultSet rs = mysql.querySelect(querystr, u.getuID(), app.getApsDate());
			while (rs.next())
            {
				System.out.println(rs.getString("apsTime"));
			//	String hourRes = rs.getString("apsTime");
				hoursList.remove(rs.getString("apsTime"));

			}
			
			List<Object> timeList = new ArrayList<Object>(hoursList);

			en.setobjList(timeList);
			en.setType(task.GET_AVAILIBLE_DOCTOR_HOURS);
			
		
		}
		catch (SQLException ex) 
   	    {/* handle any errors*/
          System.out.println("SQLException: " + ex.getMessage());
          System.out.println("SQLState: " + ex.getSQLState());
          System.out.println("VendorError: " + ex.getErrorCode());
          return null;
        }
		
		
		return en;
	}
	
	
	/**
	 * Creates the appointment.
	 *
	 * @param as the Appointment Settings
	 * @return the status
	 */
	public static Status CreateAppointment(AppointmentSettings as)
	{
	
		
		String querystr="INSERT INTO appointmentsettings " + " (apsPtID,apsDate,apsTime,apsCreateDate,apsCreateTime,apsStatus,apsDocID) "
				+ "VALUES ('"+as.getApsPtID()+"','"+as.getApsDate()+"','"+as.getApsTime()+"', '"
		+as.getCreateDate()+"', '"+as.getCreateTime()+"', '"+as.getApsStatus().toString()+"', '"+as.getApsDocID()+"')";
		
		DBManager mysql = DBManager.getInstance();
		mysql.query(querystr);
		
		return Status.CREATED;

	}
	
	
	/**
	 * Gets the scheduled appointments.
	 *
	 * @param ptID the patient id
	 * @return the envelope
	 */
	public static Envelope GetSCHEDUELDAppointments(String ptID)
	{
		String querystr;
		Envelope en = new Envelope();
		AppointmentSettings as;
		Doctor doctor;
		
		querystr="SELECT  apsID,apsPtID,apsDate,apsTime,apsCreateDate,apsCreateTime,apsStatus,apsDocID,uFirstName,uLastName,cID,cName,cLocation,dSpeciality "
				+ "FROM appointmentsettings,user,clinic,doctor "
				+ "WHERE apsPtID= ? AND apsStatus= ? AND uID= ? AND cID= ? AND dID= ?";
		
		try 
		{
			AppointmentSettings aset = new AppointmentSettings();
			User ut = new User();
			DBManager mysql = DBManager.getInstance();
			ResultSet rs = mysql.querySelect(querystr, ptID, Status.SCHEDUELD, aset.getApsDocID(), ucid, ut.getuID() );
			
			en.setStatus(Status.NOT_EXIST);
			while (rs.next())
            {
				Status st =  Status.valueOf(rs.getString(7));
				as = new AppointmentSettings(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4),
						rs.getString(5),rs.getString(6),st,rs.getString("apsDocID"));
				
				
				Clinic clinic = new Clinic(rs.getInt("cID"),rs.getString("cName"),rs.getString("cLocation"));
				DoctorSpeciallity ds = DoctorSpeciallity.valueOf(rs.getString("dSpeciality"));
				doctor = new Doctor(rs.getString("apsDocID"),rs.getString("uFirstName"),rs.getString("uLastName"),clinic,ds);
				as.setDoctor(doctor);
				en.addobjList(as);
				System.out.println(as);
				en.setStatus(Status.EXIST);
            }   
			
			en.setType(task.GET_OPEN_APPOINTMENTS);
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
	 * Cancel appointment.
	 *
	 * @param apsID the Appointment id
	 * @return the status
	 */
	public static Status CancelAppointment(int apsID)
	{
		
		String querystr;
		
		
		querystr="UPDATE appointmentsettings "
				+ "SET apsStatus= ? "
				+ "WHERE apsID= ?";
		
		DBManager mysql = DBManager.getInstance();
		mysql.querySelect(querystr, Status.CANCELED, apsID);
		
		return Status.CANCELED;

	}
	
}
