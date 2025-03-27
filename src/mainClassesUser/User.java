package mainClassesUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * The User class represents a user entity in the system.
 * It contains the user's details such as userName, password, and roles.
 */
public class User {
    private String userName;
    private String password;
    private List<String> roles; //Multiple role
    private String realName;
    private String email;

    //Construct User
    public User() {
        this.roles = new ArrayList<>();
    }

    // Sets the data to this user.
    public User(String userName, String password, List<String> roles, String realName, String email) {
        this.userName = userName;
        this.password = password;
        this.roles = (roles != null) ? new ArrayList<>(roles) : new ArrayList<>();
        this.realName = realName;
        this.email = email;
    }

    // password Getter & Setter
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    // userName Getter & Setter
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    // roles Getter & Setter (List<String>)
    public List<String> getRoles() { return new ArrayList<>(roles); }
    public void setRoles(List<String> roles) { this.roles = new ArrayList<>(roles); }

    // Add role
    public void addRole(String role) {
        if (!roles.contains(role)) {
            roles.add(role);
        }
    }

    // Remove role
    public void removeRole(String role) {
        roles.remove(role);
    }

    // Check role
    public boolean hasRole(String role) {
        return roles.contains(role);
    }

    // Multiple role to one string (ex: "admin,student")
    public String getRoleString() {
        return String.join(",", roles);
    }

    // One string to multiple roles split by ,
    public static List<String> parseRoles(String roleString) {
        return (roleString == null || roleString.isEmpty()) 
               ? new ArrayList<>() 
               : new ArrayList<>(Arrays.asList(roleString.split(","))); 
    }

    // realName Getter & Setter
    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }

    // email Getter & Setter
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
