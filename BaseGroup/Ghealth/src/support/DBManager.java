package support;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.swing.JOptionPane;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

/**
 * The Class DBManager.
 */
public class DBManager {
	
	/** The name prop. */
	private final String NAME_PROP = "DBSettings.properties";
	
	private Connection conn;
	
	/**
	 * The instance.
	 *
	 * @INSTANCE 
	 */
	public static DBManager INSTANCE = DBManager.getInstance();
	
	/**
	 * Instantiates a new DB manager.
	 */
	private DBManager() {
		this.conn = getConnection();
	}
	
	/**
	 * Gets the single instance of DBManager.
	 *
	 * @return single instance of DBManager
	 */
	public static DBManager getInstance() {
		if(INSTANCE == null)
			INSTANCE = new DBManager();
		return INSTANCE;
	}
		

	/**
	 * Gets the connection.
	 *
	 * @return the connection
	 */
	private Connection getConnection() {
		Properties prop = new Properties();
		FileInputStream stream = null;
		try {
			stream = new FileInputStream(new File(NAME_PROP));
			prop.load(stream);
		} catch (IOException e) {
			e.getMessage();
		}
		finally {
			if(stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, e.getMessage());
				}
			}
		}
		
		Connection conn = null;
		
		MysqlDataSource mysql = new MysqlDataSource();
		mysql.setServerName(prop.getProperty("hostname"));
		mysql.setUser(prop.getProperty("username"));
		mysql.setPassword(prop.getProperty("password"));
		mysql.setDatabaseName(prop.getProperty("dbname"));
		try {
			conn = mysql.getConnection();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		return conn;
	}
	
	/**
	 * Assign values.
	 *
	 * @param stmt the stmt
	 * @param objects the objects
	 * @throws SQLException the SQL exception
	 */
	private void assignValues(PreparedStatement stmt, Object...objects) throws SQLException {
		int idx = 0;
		for(Object oElem : objects) {
			stmt.setObject(++idx, oElem);
		}
	}
	
	/**
	 * Query.
	 *
	 * @param sql the sql
	 */
	public void query(String sql) {
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.executeUpdate();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}
	
	/**
	 * Query.
	 *
	 * @param sql the sql
	 * @param values the values
	 */
	public void query(String sql, Object...values) {
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			assignValues(stmt, values);
			stmt.executeUpdate();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}
	
	/**
	 * Query select.
	 *
	 * @param sql the sql
	 * @return the result set
	 */
	public ResultSet querySelect(String sql) {
		ResultSet rs = null;
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		return rs;
	} 

	/**
	 * Query select.
	 *
	 * @param sql the sql
	 * @param values the values
	 * @return the result set
	 */
	public ResultSet querySelect(String sql, Object...values) {
		ResultSet rs = null;
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			assignValues(stmt, values);
			rs = stmt.executeQuery();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		return rs;
	}
		

}
