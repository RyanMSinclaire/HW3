package tests.generatedata;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import mainClassesUser.User;

public class generatorUsers {

    // Generates a list of random users for testing purposes.
    public static List<User> generateRandomUsers(int numberOfUsers) {
        List<User> Users = new ArrayList<>();
        Random random = new Random();

        // Sample usernames and roles
        String[] userNames = {"Admin","Student"};
        String[] roles = {"student", "instructor", "staff", "reviewer"};
        
        System.out.printf("%-15s %-15s %-15s %-15s %-15s\n", "UserName", "PW", "Roles", "Real Name", "Email");
        System.out.println("-----------------------------------------------------------");

        // Generate users
        for (int i = 0; i < numberOfUsers; i++) {
            // Random data for each question
            String userName = userNames[1] + Integer.toString(i + 1); // Student1, Student2 ...
            String password = userNames[1] + Integer.toString(i + 1);
            List<String> role = new ArrayList<>();
            int numberOfRoles = random.nextInt(3) + 1;
            Collections.shuffle(Arrays.asList(roles));
            for (int j = 0; j < numberOfRoles; j++) {
                role.add(roles[j]);
            }

            String realName = "Test Name" + Integer.toString(i + 1);
            String email = userNames[1] + Integer.toString(i + 1) + "@test.edu";
            
            User user = new User(userName, password, role, realName, email);

            System.out.printf("%-15s %-15s %-15s %-15s %-15s\n",
            		userName, password, role, realName, email);
            
            Users.add(user);
        }
        return Users;
    }
    public static User generateAdmin() {
    	String[] roles = {"admin","student", "reviewer"};        
        String userName = "Admin"; 
        String password = "Admin123!";
        List<String> role = new ArrayList<>();
        role.add(roles[0]);
        String realName = "Admins Test Name";
        String email = "admin@asu.edu";
        User admin = new User(userName, password, role, realName, email);
        return admin;
    }
}
