package databasePart1;

import java.sql.*;
import java.util.UUID;

import mainClassesUser.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * The DatabaseHelper class is responsible for managing the connection to the database,
 * performing operations such as user registration, login validation, and handling invitation codes.
 */
public class DatabaseHelper {

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "org.h2.Driver";
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase";

	// Database credentials
	static final String USER = "sa";
	static final String PASS = "";

	private User loggedInUser = null;
	private Connection connection = null;
	private Statement statement = null;

	// Global Max OTP Length
	public static final Integer MAX_OTP_LENGTH = 4;

	// PreparedStatement pstmt
	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement();
			// You can use this command to clear the database and restart from fresh.
			 statement.execute("DROP ALL OBJECTS");

			createTables(); // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	public Connection getConnection() throws SQLException {
		if (this.connection == null || this.connection.isClosed()) {
			System.out.println("Connection was closed. Reconnecting...");
			connectToDatabase();
		}
		return this.connection;
	}

	private void createTables() throws SQLException {
		String userTable = "CREATE TABLE IF NOT EXISTS cse360users (" + "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255) UNIQUE, " + "password VARCHAR(255), " + "role TEXT, " // Multiple roles are
																								// saved in a single
																								// text which is used
																								// later.
				+ "realName VARCHAR(255), " + "email VARCHAR(255), " + "otp VARCHAR(255) NULL)";
		statement.execute(userTable);

		// ✅ Create Questions table
		String questionsTable = "CREATE TABLE IF NOT EXISTS Questions (" 
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255), " 
				+ "title VARCHAR(255) NOT NULL, " 
				+ "category VARCHAR(255) NOT NULL, "
				+ "content TEXT NOT NULL, " 
				+ "isSolved BOOLEAN DEFAULT FALSE, "
				+ "status ENUM('open', 'closed', 'under_review') DEFAULT 'open', " 
				+ "relatedQuestionID INT NULL, "
				+ "unreadAnswersCount INT DEFAULT 0, " 
				+ "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
				+ "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, "
				+ "FOREIGN KEY (userName) REFERENCES cse360users(userName))";

		statement.execute(questionsTable);

		// Create Answers table
		String answersTable = "CREATE TABLE IF NOT EXISTS Answers (" + "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255), " + "answerText TEXT NOT NULL, " + "isCorrect BOOLEAN DEFAULT FALSE, "
				+ "masterQuestionId INT NULL, " + "relatedAnswerID INT NULL, "
				+ "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
				+ "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, "
				+ "FOREIGN KEY (userName) REFERENCES cse360users(userName), "
				+ "FOREIGN KEY (masterQuestionId) REFERENCES Questions(id))";
		statement.execute(answersTable);

		// Create Message table
		String massegesTable = "CREATE TABLE IF NOT EXISTS Massages (" + "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName TEXT, " + "reciverUserID TEXT, " + "massegeText TEXT, " + "relatedQuestionLinkID TEXT, "
				+ "isReaden BOOLEAN)";

		statement.execute(massegesTable);

		// Create the invitation codes table
		String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes (" + "code VARCHAR(10) PRIMARY KEY, "
				+ "isUsed BOOLEAN DEFAULT FALSE, " + "expiration_date DATE, " + "role TEXT)"; // Multiple roles are
																								// saved in a single
																								// text which is used
																								// later.

		statement.execute(invitationCodesTable);
	}

	public List<User> getUserList() {
		List<User> userList = new ArrayList<>();
		String query = "SELECT userName, role, realName, email FROM cse360users";

		try (PreparedStatement pstmt = connection.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {

			while (rs.next()) {
				String userName = rs.getString("userName");
				String roleString = rs.getString("role");
				List<String> roles = roleString != null && !roleString.isEmpty() ? Arrays.asList(roleString.split(","))
						: new ArrayList<>();
				String realName = rs.getString("realName");
				String email = rs.getString("email");

				// Creates the user
				User user = new User(userName, "", roles, realName, email);

				userList.add(user);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return userList;
	}

	public String getEmail(String userName) {
		String query = "SELECT email FROM cse360users WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, userName);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getString("email");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getRealName(String userName) {
		String query = "SELECT realName FROM cse360users WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, userName);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getString("realName");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	// Check if the database is empty
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM cse360users";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}

	// Check if the question database is empty
	public boolean isQuestionDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM Questions";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}

	// Check if the answers database is empty
	public boolean isAnswerDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM Answers";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}

	// Registers a new user in the database.
	public void register(User user, List<String> roles) throws SQLException {
		String rolesString = String.join(",", roles); // multiple roles to one text ("admin,student")

		String query = "INSERT INTO cse360users (userName, password, role, realName, email) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			pstmt.setString(3, rolesString);
			pstmt.setString(4, user.getRealName());
			pstmt.setString(5, user.getEmail());
			pstmt.executeUpdate();
		}
	}

	public void setOneTimePassword(String userName, String otp) {
		String query = "UPDATE cse360users SET otp = ? WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, otp);
			pstmt.setString(2, userName);
			pstmt.executeUpdate();
			System.out.println("One-time password set for user: " + userName);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean hasOTP(String userName) {
		String query = "SELECT otp FROM cse360users WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, userName);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getString("otp") != null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean loginWithOTP(String userName, String otp) {
		String query = "SELECT * FROM cse360users WHERE userName = ? AND otp = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, userName);
			pstmt.setString(2, otp);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					clearOTP(userName); // Reset after use OTP
					return true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	private void clearOTP(String userName) {
		String query = "UPDATE cse360users SET otp = NULL WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, userName);
			pstmt.executeUpdate();
			System.out.println("OTP cleared for user: " + userName);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void setNewPassword(String userName, String newPassword) {
		String query = "UPDATE cse360users SET password = ? WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, newPassword);
			pstmt.setString(2, userName);
			pstmt.executeUpdate();
			System.out.println("Password updated for user: " + userName);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Validates a user's login credentials.
	public boolean login(User user) throws SQLException {
		String query = "SELECT * FROM cse360users WHERE userName = ? AND password = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					loggedInUser = user;
					System.out.println("Login successful: " + loggedInUser);
					return true;
				}
			}
		}
		return false;
	}

	public User getLoggedInUser() {
		return loggedInUser;
	}

	public String getLoggedInUsername() {
		return loggedInUser.getUserName();
	}

	public void logout() {
		loggedInUser = null;
		System.out.println("User logged out.");
	}

	public int countAdmins() {
		String query = "SELECT COUNT(*) FROM cse360users WHERE role LIKE '%admin%'";
		try (PreparedStatement pstmt = connection.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {
			if (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	// Checks if a user already exists in the database based on their userName.
	public boolean doesUserExist(String userName) {
		String query = "SELECT COUNT(*) FROM cse360users WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {

			pstmt.setString(1, userName);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				// If the count is greater than 0, the user exists
				return rs.getInt(1) > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false; // If an error occurs, assume user doesn't exist
	}

	// Retrieves the role of a user from the database using their UserName.
	public List<String> getUserRoles(String userName) {
		String query = "SELECT role FROM cse360users WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, userName);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				String rolesString = rs.getString("role");
				return parseRoles(rolesString);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

//About Role
	private List<String> parseRoles(String rolesString) {
		return List.of(rolesString.split(",")); // "admin,student" → ["admin", "student"]
	}

	public boolean hasRole(String userName, String role) {
		List<String> roles = getUserRoles(userName);
		return roles.contains(role);
	}

	public boolean isAdmin(String userName) {
		return hasRole(userName, "admin");
	}

	public void addRoleToUser(String userName, String newRole) {
		List<String> roles = getUserRoles(userName);
		if (!roles.contains(newRole)) {
			roles.add(newRole);
			updateUserRoles(userName, roles);
		}
	}

	public void removeRoleFromUser(String userName, String roleToRemove) {
		List<String> roles = getUserRoles(userName);
		roles.remove(roleToRemove);
		updateUserRoles(userName, roles);
	}

	public void updateUserRoles(String userName, List<String> roles) {
		if (roles == null || roles.isEmpty()) {
			System.out.println("Error: Roles list cannot be null or empty.");
			return;
		}

		// multiple roles to one text ("admin,student")
		String rolesString = String.join(",", roles);

		String query = "UPDATE cse360users SET role = ? WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, rolesString);
			pstmt.setString(2, userName);

			int rowsUpdated = pstmt.executeUpdate();
			if (rowsUpdated > 0) {
				System.out.println("Roles updated successfully for user: " + userName);
			} else {
				System.out.println("Warning: No user found with userName: " + userName);
			}
		} catch (SQLException e) {
			System.err.println("Database error while updating roles for user: " + userName);
			e.printStackTrace();
		}
	}

	public void deleteUser(String userName) {
		if (isAdmin(userName) && countAdmins() <= 1) {
			System.out.println("Cannot delete the last admin!");
			return;
		}

		String query = "DELETE FROM cse360users WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, userName);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String generateInvitationCode(Date expirationDate, List<String> roles) {
		if (roles == null || roles.isEmpty())
			return null;

		// OTP Code is random UUID of length MAX_OTP_LENGTH
		String code = UUID.randomUUID().toString().substring(0, MAX_OTP_LENGTH);

		// Multiple roles to one text ("admin,student")
		String rolesString = String.join(",", roles);

		String query = "INSERT INTO InvitationCodes (code, isUsed, expiration_date, role) VALUES (?, ?, ?, ?)";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, code);
			pstmt.setBoolean(2, false);
			pstmt.setDate(3, expirationDate);
			pstmt.setString(4, rolesString);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return code;
	}

	public List<String> getRolesFromInvitationCode(String code) {
		String query = "SELECT role FROM InvitationCodes WHERE code = ? AND isUsed = FALSE AND expiration_date >= CURRENT_DATE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, code);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				String roleString = rs.getString("role");
				return User.parseRoles(roleString); // parse one text to List<String>("admin,student" → ["admin",
													// "student"])
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return new ArrayList<>(); // if invitation code is empty return null
	}

	// Validates an invitation code to check if it is unused.
	public boolean validateInvitationCode(String code) {
		String query = "SELECT * FROM InvitationCodes WHERE code = ? AND isUsed = FALSE AND expiration_date >= CURRENT_DATE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, code);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				// Mark the code as used
				markInvitationCodeAsUsed(code);
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	// Marks the invitation code as used in the database.
	private void markInvitationCodeAsUsed(String code) {
		String query = "UPDATE InvitationCodes SET isUsed = TRUE WHERE code = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, code);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// RYAN! trying to get the created time with this method 
	public String getCreationTime(int id) {
		String query = "SELECT created_at FROM questions WHERE id = ?"; //WHERE userName = ?
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, id);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getString("created_at");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	// Closes the database connection and statement.
	public void closeConnection() {
		try {
			if (statement != null)
				statement.close();
		} catch (SQLException se2) {
			se2.printStackTrace();
		}
		try {
			if (connection != null)
				connection.close();
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}

}