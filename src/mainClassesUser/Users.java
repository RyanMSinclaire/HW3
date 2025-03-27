package mainClassesUser;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * The User class represents a user entity in the system.
 * It contains the user's details such as userName, password, and roles.
 */
public class Users {
   
    private List<User> userList; // Master user list
    private Connection connection;
    
    // Construct Users
    public Users(Connection connection) {
        this.userList = new ArrayList<>();
    }

    
}
