package validationEvaluators;

public class EvaluatorPassword { // FSM for password evaluation.

	public static String passwordErrorMsg = ""; // Error messages to display if password does not pass validation
	public static String passwordInput = ""; // The user input that is being processed
	public static String passwordConfirmation = ""; // The user password confirmation input
	public static int passwordIndexOfError = -1; // Index of the error
	public static boolean foundUpperCase = false;
	public static boolean foundLowerCase = false; 
	public static boolean foundNumber = false;
	public static boolean foundSpecialChar = false;
	public static boolean passwordLongEnough = false; // Check if the password is at least 8 characters long
	public static boolean passwordTooLong = false; // Check if the password is at most 16 characters long
	private static char currentChar;
	private static int currentCharIndex = 0;
	private static boolean running = false;

	public static String evaluatePassword(String input, String confirmation) {
		
		if (input.length() <= 0) return "You must enter a password!";
		
		// Access the first character of the password
		currentChar = input.charAt(0);

		passwordInput = input;					// Save a copy of the input
		passwordConfirmation = confirmation;	// Save a copy of the confirmation
		foundUpperCase = false;					// Reset the Boolean flag
		foundLowerCase = false;					// Reset the Boolean flag
		foundNumber = false;					// Reset the Boolean flag
		foundSpecialChar = false;				// Reset the Boolean flag
		passwordLongEnough = false;				// Reset the Boolean flag
		passwordTooLong = false;				// Reset the Boolean flag
		passwordErrorMsg = "";					// Reset the error message
		passwordIndexOfError = -1;				// Reset index of error
		currentCharIndex = 0;					// Reset char index
		running = true;							// Start the loop

		// The Directed Graph simulation continues until the end of the input is reached or at some 
		// state the current character does not match any valid transition
		while (running) {
		// The cascading if statement sequentially tries the current character against all of the
		// valid transitions
			if (currentChar >= 'A' && currentChar <= 'Z') {
				System.out.println("Upper case letter found");
				foundUpperCase = true;
			} else if (currentChar >= 'a' && currentChar <= 'z') {
				System.out.println("Lower case letter found");
				foundLowerCase = true;
			} else if (currentChar >= '0' && currentChar <= '9') {
				System.out.println("Digit found");
				foundNumber = true;
			} else if ("!@#$%^&*()_-+=".indexOf(currentChar) >= 0) {
				System.out.println("Special character found");
				foundSpecialChar = true;
			} else {
				passwordIndexOfError = currentCharIndex;
				return "An invalid character has been found!";
			}
			if (currentCharIndex >= 7) {
				System.out.println("At least 8 characters found");
				passwordLongEnough = true;
			}
			if (currentCharIndex > 15) {
				System.out.println("More than 16 characters found");
				passwordTooLong = true;
			}
			
			
			// Go to the next character if there is one
			currentCharIndex++;
			if (currentCharIndex >= passwordInput.length())
				running = false;
			else
				currentChar = input.charAt(currentCharIndex);
			
			System.out.println();
		}
			
		String listBullet = "    \u2023    ";

		if (!foundUpperCase) {
			passwordErrorMsg += listBullet + "An uppercase letter.\n";
		}
		if (!foundLowerCase) {
			passwordErrorMsg += listBullet + "A lowercase letter.\n";
		}
		if (!foundNumber) {
			passwordErrorMsg += listBullet + "A number.\n";
		}
		if (!foundSpecialChar) {
			passwordErrorMsg += listBullet + "A special character.\n";
		}
		if (!passwordLongEnough) {
			passwordErrorMsg += listBullet + "At least 8 characters total.\n";
		}
		if (passwordTooLong) {
				passwordErrorMsg += listBullet + "At most 16 characters total.\n";
		}
		if (passwordErrorMsg.equals("")) {
			if (!passwordInput.equals(passwordConfirmation)) {
				return "Passwords do not match.";
			} else {
				// Passed validation
				return "";
			}	
		} else {
			return "Your password is missing:\n" + passwordErrorMsg;
		}
	}
}
