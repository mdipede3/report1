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
 * The Class SCdocAppointment.
 */
public class SCdocAppointment {
	
	/**
	 * Gets the recorded appointments.
	 *
	 * @param ptID the id
	 * @return the envelope
	 */
	
	static String ucid = "ucID";
	static String pd = "pd.PersonalDoctorID";
	
	public static Envelope GetRecordedAppointments(String ptID)
	{
		
		String querystr;
		ResultSet rs;
		Envelope en = new Envelope();
		AppointmentSettings as;
		Doctor doctor;
		
		querystr="SELECT  apsID,apsPtID,apsDate,apsTime,apsCreateDate,apsCreateTime,apsStatus,apsDocID,uFirstName,uLastName,cID,cName,cLocation,dSpeciality,apsSummery "
				+ "FROM appointmentsettings,user,clinic,doctor "
				+ "WHERE apsPtID= ? AND apsStatus= ?  AND uID= ? AND cID=ucID AND dID=uID"
						+ " ORDER BY apsDate DESC; ";
		
		try 
		{
			
			AppointmentSettings asp = new AppointmentSettings();
			User u = new User();
			DBManager mysql = DBManager.getInstance();
			rs = mysql.querySelect(querystr, ptID, Status.ARRIVED, asp.getApsDocID(), ucid , u.getuID());
			en.setStatus(Status.NOT_EXIST);
			while (rs.next())
            {
				System.out.println(rs.getString(15));// printing the summery
				
				
				Status st =  Status.valueOf(rs.getString(7));
				as = new AppointmentSettings(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4),
						rs.getString(5),rs.getString(6),st,rs.getString("apsDocID"));
				
				as.setApsSummery(rs.getString(15));
				
				Clinic clinic = new Clinic(rs.getInt("cID"),rs.getString("cName"),rs.getString("cLocation"));
				DoctorSpeciallity ds = DoctorSpeciallity.valueOf(rs.getString("dSpeciality"));
				doctor = new Doctor(rs.getString("apsDocID"),rs.getString("uFirstName"),rs.getString("uLastName"),clinic,ds);
				as.setDoctor(doctor);
				en.addobjList(as);
				System.out.println(as);
				en.setStatus(Status.EXIST);
            }   
			
			en.setType(task.GET_ARRIVED_APPOINTMENTS);
			
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
	 * Gets the current appointment.
	 *
	 * @param ptID the pt id
	 * @param apsDocID the aps doc id
	 * @return the envelope
	 */
	public static Envelope GetCurrentAppointment(String ptID, String apsDocID)
	{
		
		String querystr;
		ResultSet rs;
		Envelope en = new Envelope();
	
		querystr="SELECT apsID"
				+ " FROM appointmentsettings"
				+ " WHERE apsPtID = ? AND apsStatus = ? AND apsDocID =  ? ;";
		
		try 
		{
			DBManager mysql = DBManager.getInstance();
			rs = mysql.querySelect(querystr,ptID, Status.SCHEDUELD, apsDocID);
			en.setStatus(Status.NOT_EXIST);
			
			while (rs.next())
            {
				int appointementID = rs.getInt(1);
				
				en.addobjList(appointementID);
				System.out.println("the appointement id received from DB was:" + appointementID);
				en.setStatus(Status.EXIST);
            }   
			
			en.setType(task.GET_CURRENT_APPOINTMENT_ID);

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
	 * Gets the p doc mail.
	 *
	 * @param nt the nt
	 * @return the p doc mail
	 */
	public static Notification getPDocMail(Notification nt){
		
		String querystr,querystr2;
		ResultSet rs;
		
		querystr= ""
				+ "SELECT pd.PersonalDoctorEmail "
				+ "FROM ghealth.personaldoctor pd, ghealth.patient p "
				+ "WHERE p.ptID = ? AND p.ptDoctorID = ? ;";
		querystr2= ""
				+ "SELECT pd.PersonalDoctorName "
				+ "FROM ghealth.personaldoctor pd, ghealth.patient p "
				+ "WHERE p.ptID = ? AND p.ptDoctorID = ? ;";
		
		try {
			DBManager mysql = DBManager.getInstance();
			rs = mysql.querySelect(querystr, nt.patient.getpID(),pd );
			rs.next();
			nt.mail = rs.getString(1);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			DBManager mysql = DBManager.getInstance();
			rs = mysql.querySelect(querystr2, nt.patient.getpID(), pd);
			rs.next();
			nt.docName = rs.getString(1);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return nt;
	}
	
	
	/**
	 * Record appointment.
	 *
	 * @param apsID the aps id
	 * @param summery the summery
	 * @return the status
	 */
	public static Status RecordAppointment(String apsID, String summery)
	{
		
		
		String querystr;
		
	
		SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm:ss");
		String createdHour = hourFormat.format(new Date());
		
		querystr="UPDATE appointmentsettings "
				+ "SET apsStatus = ? , apsSummery = ? , apsStartTime = ? "
				+ "WHERE apsID = ? ;";
		
		DBManager mysql = DBManager.getInstance();
		mysql.query(querystr, Status.ARRIVED, summery, createdHour, apsID);
		
		return Status.ARRIVED;

	}
	
}
