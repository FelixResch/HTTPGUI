package at.resch.html.test.auth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;

import at.resch.html.server.Session;

public class DBBackend {

	private static Connection mysqlDB;
	private static SecureRandom random;
	
	public static void init() {
		try {
			Properties p = new Properties();
			if(!new File("auth_server.conf").exists()) {
				p.setProperty("mysql-host", "localhost");
				p.setProperty("mysql-port", "3306");
				p.setProperty("mysql-db", "user_services");
				p.setProperty("mysql-password", "");
				p.setProperty("mysql-user", "root");
				System.out.println("Writing initial config");
				try {
					File f = new File("auth_server.conf");
					//System.out.println(f.getAbsolutePath());
					p.store(new FileOutputStream(f), "");
				} catch (IOException e) {
					System.err.println("Couldn't store Configuration!");
				}
			} else {
				try {
					p.load(new FileInputStream("auth_server.conf"));
				} catch (IOException e) {
					System.err.println("Couldn't load configuration file. Please restart!");
					System.exit(-1);
				}
				
			}
			String host = p.getProperty("mysql-host");
			String port = p.getProperty("mysql-port");
			String db = p.getProperty("mysql-db");
			String password = p.getProperty("mysql-password");
			String user = p.getProperty("mysql-user");
			Class.forName("com.mysql.jdbc.Driver");
			mysqlDB = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + db, user, password);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Session.logger.info("DB Init Finished");
		random = new SecureRandom();
	}
	
	public static void close() throws SQLException {
		mysqlDB.close();
	}
	
	public static String getUserInfo(String username) throws SQLException {
		Statement statement = mysqlDB.createStatement();
		ResultSet result = statement.executeQuery("select * from v_user_info where Username = '" + username + "'");
		String[] columns = new String[] {"Username", "Display", "Token", "Start", "End"};
		result.first();
		if(!(result.getDate("Start").before(new Date()) && result.getDate("End").after(new Date()))) {
			//System.out.println("[INFO] User token expired generating new one");
			String token = new BigInteger(130, random).toString(32).substring(0, 15);
			//System.out.println("[INFO] New token: " + token);
			ResultSet res = statement.executeQuery("select max(t_id) as Max from t_token");
			res.first();
			int new_id = res.getInt("Max") + 1;
			GregorianCalendar cal = new GregorianCalendar();
			String start, end;
			start = cal.get(GregorianCalendar.YEAR) + "-" + String.format("%02d", cal.get(GregorianCalendar.MONTH) + 1) + "-01";
			end = cal.get(GregorianCalendar.YEAR) + "-" + String.format("%02d", cal.get(GregorianCalendar.MONTH) + 2) + "-02";
			statement.execute("insert into t_token set t_token = '" + token + "', t_id = " + new_id + ", t_start = '" + start + "', t_end = '" + end + "'");
			statement.execute("update u_user set u_t_token = " + new_id + " where u_id = (select l_u_user from l_login where l_uname = '" + username + "')");
			result = statement.executeQuery("select * from v_user_info where Username = '" + username + "'");
		}
		result.first();
		String ret = "{\"updates\":[{\"Id\":\"0\", \"Name\":\"User\", \"update\":[";
		for(int i = 0; i < columns.length; i++) {
			String json = "{\"Name\":\"" + columns[i] + "\", \"Value\":\"" + result.getString(columns[i]) +"\", \"Type\":\"STRING\"}";
			ret += json + (i == columns.length - 1 ? "" : ",");
		}
		ret += "]}]}}";
		statement.close();
		return ret;
	}
	
	public static boolean registerUser(String username, String password, String email, String display) {
		try {
			Statement statement = mysqlDB.createStatement();
			try {
				getUserInfo(username);
				return false;
			} catch (SQLException | NullPointerException e) {}
			Session.logger.info("Registering new User");
			String token = new BigInteger(130, random).toString(32).substring(0, 15);
			ResultSet res = statement.executeQuery("select max(t_id) as Max from t_token");
			res.first();
			int new_id = res.getInt("Max") + 1;
			GregorianCalendar cal = new GregorianCalendar();
			String start, end;
			start = cal.get(GregorianCalendar.YEAR) + "-" + String.format("%02d", cal.get(GregorianCalendar.MONTH) + 1) + "-01";
			end = cal.get(GregorianCalendar.YEAR) + "-" + String.format("%02d", cal.get(GregorianCalendar.MONTH) + 2) + "-02";
			statement.execute("insert into t_token set t_token = '" + token + "', t_id = " + new_id + ", t_start = '" + start + "', t_end = '" + end + "'");
			Session.logger.info("Created new Token");
			res = statement.executeQuery("select max(u_id) as Max from u_user");
			res.first();
			int new_uid = res.getInt("Max") + 1;
			statement.execute("insert into u_user set u_id = " + new_uid + ", u_display='" + display + "', u_t_token=" + new_id + ", u_email='" + email + "'");
			Session.logger.info("Created new User");
			statement.execute("insert into l_login set l_uname='" + username + "', l_pass='" + password + "', l_u_user=" + new_uid);
			Session.logger.info("Registration complete");
			statement.close();
		} catch (SQLException e) {
			Session.logger.warn("User Registration Error", e);
			return false;
		}
		return true;
	}
	
	public static String getPassword(String username) throws SQLException {
		Statement statement = mysqlDB.createStatement();
		ResultSet result = statement.executeQuery("select * from v_user_info where Username = '" + username + "'");
		String[] columns = new String[] {"Password"};
		result.first();
		String ret = "";
		for(int i = 0; i < columns.length; i++) {
			ret = result.getString(columns[i]);
		}
		statement.close();
		return ret;
	}
	
	public static String authenticateToken(String username, String token) throws SQLException {
		String auth_pass = getPassword(username);
		String plain_token = UserServices.decrypt(token, UserServices.generateKeys(auth_pass));
		Statement statement = mysqlDB.createStatement();
		ResultSet result = statement.executeQuery("select * from v_user_info where Username = '" + username + "'");
		String[] columns = new String[] {"Token"};
		result.first();
		String ret = "";
		for(int i = 0; i < columns.length; i++) {
			ret = result.getString(columns[i]);
		}
		if(plain_token.equals(ret)) {
			if(result.getDate("Start").before(new Date()) && result.getDate("End").after(new Date())) {
				return "1";
			}
		}
		statement.close();
		return "0";
	}
}
