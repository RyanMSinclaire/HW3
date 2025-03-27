package validationEvaluators;

public class InappropriateChecker { // FSM for InappropriateChecker evaluator.

	public static String userNameRecognizerErrorMessage = "";	// The error message text
	public static String userNameRecognizerInput = "";			// The input being processed
	public static int userNameRecognizerIndexofError = -1;		// The index of error location
	private static int state = 0;						// The current state value
	private static int nextState = 0;					// The next state value
	private static boolean finalState = false;			// Is this state a final state?
	private static String inputLine = "";				// The input line
	private static char currentChar;					// The current character in the line
	private static int currentCharNdx;					// The index of the current character
	private static boolean running;						// The flag that specifies if the FSM is 
														// running
	private static int userNameSize = 0;				// A numeric value may not exceed 16 characters



	
	// Private method to move to the next character within the limits of the input line
	private static void moveToNextCharacter() {
		currentCharNdx++;
		if (currentCharNdx < inputLine.length())
			currentChar = inputLine.charAt(currentCharNdx);
		else {
			currentChar = ' ';
			running = false;
		}
	}

	/*
	 * This method for implementing the FSM
	 *
	 * PARAMETERS String input	-->	The input string for the Finite State Machine
	 *
	 * RETURN		-->				An output string that is empty if every things is okay or it is a String
	 * 								with a helpful description of the error
	 */

	public static String checkForValidContents(String input) {
		// Check to ensure that there is input to process
		if(input.length() <= 0) {
			userNameRecognizerIndexofError = 0;	// Error at first character;
			return "\nYour text entry can't be empty!";
		}
		
		// The local variables used to perform the Finite State Machine simulation
		state = 0;							// This is the FSM state number
		inputLine = input;					// Save the reference to the input line as a global
		currentCharNdx = 0;					// The index of the current character
		currentChar = input.charAt(0);		// The current character from above indexed position

		// The Finite State Machines continues until the end of the input is reached or at some 
		// state the current character does not match any valid transition to a next state

		userNameRecognizerInput = input;	// Save a copy of the input
		running = true;						// Start the loop
		nextState = -1;						// There is no next state
		//System.out.println("\nCurrent Final Input  Next  Date\nState   State Char  State  Size");
		
		// This is the place where semantic actions for a transition to the initial state occur
		
		userNameSize = 0;					// Initialize the UserName size

		// The Finite State Machines continues until the end of the input is reached or at some 
		// state the current character does not match any valid transition to a next state
		while (running) {
			// The switch statement takes the execution to the code for the current state, where
			// that code sees whether or not the current character is valid to transition to a
			// next state
			switch (state) {
			case 0: 
				// State 0 has 1 valid transition that is addressed by an if statement.
				
				// The current character is checked against A-Z and a-z. If any are matched
				// the FSM goes to state 1
				
				// A-Z, a-z -> State 1
				if (!Character.toString(currentChar).equals("") ) {	//if not empty
					
					nextState = 1;
					
					// Count the character 
					userNameSize++;
					
					// This only occurs once, so there is no need to check for the size getting
					// too large.
				}
				// If it is none of those characters, the FSM halts
				else 
					running = false;
				
				// The execution of this state is finished
				break;
			
			case 1: 
				// State 1 has two valid transitions, 
				//	1: a A-Z, a-z, 0-9 that transitions back to state 1
				//  2: a period, minus, or underscore that transitions to state 2 

				
				// A-Z, a-z, 0-9 -> State 1
				if (!Character.toString(currentChar).equals("") ) {	//if not empty
					
					nextState = 1;
					
					// Count the character
					userNameSize++;
				}

							
				// If it is none of those characters, the FSM halts
				else
					running = false;
				
				// The execution of this state is finished
				// If the size is larger than 16, the loop must stop
				if (userNameSize > 16)
					running = false;
				break;			
						
			}
			
			if (running) {
				//displayDebuggingInfo();
				/* When the processing of a state has finished, the FSM proceeds to the next
				character in the input and if there is one, it fetches that character and
				updates the currentChar.  If there is no next character the currentChar is
				set to a blank. */
				moveToNextCharacter();

				// Move to the next state
				state = nextState;
				
				// Is the new state a final state?  If so, signal this fact.
				if (state == 1) finalState = true;

				// Ensure that one of the cases sets this to a valid value
				nextState = -1;
			}
			// Should the FSM get here, the loop starts again

		}

		
		userNameRecognizerIndexofError = currentCharNdx;	// Set index of a possible error;
		
		// The following code is a slight variation to support just console output.
		switch (state) {
		case 0:
			// State 0 is not a final state, so we can return a very specific error message
			userNameRecognizerErrorMessage += "Can't input empty.\\n";
			return userNameRecognizerErrorMessage;

		case 1:
			// State 1 is a final state.  Check to see if the UserName length is valid.  If so we
			// we must ensure the whole string has been consumed.

			if (currentCharNdx < input.length()) {
				// There are characters remaining in the input, so the input is not valid
				userNameRecognizerErrorMessage +="";
			}
			else if (userNameSize < 1) {
				// UserName is too small
				userNameRecognizerErrorMessage += "Can't input empty.\n";
			}
			else if (userNameSize > 2147483647) {
				// UserName is too long
				userNameRecognizerErrorMessage += "Max size is 2,147,483,647 character.\n";
			}
			
			else {
				// UserName is valid
				userNameRecognizerIndexofError = -1;
				userNameRecognizerErrorMessage = banWordCheck(input);
			}
			
			return userNameRecognizerErrorMessage;

			
		default:

			    return "";
		}

			
		
		
	}
	public static String banWordCheck(String input) {
	    // Change to lower case to compare.
	    String lowerInput = input.toLowerCase();

	    // Ban list
	    String[] offensiveWords = {"fuck", "shit", "bitch", "asshole", "cunt"};
	    String[] hateSpeechWords = {"retard", "nigger","nigga", "faggot", "chink", "kike"};
	    String[] sexualContentWords = {"sex", "porn", "cock", "vagina", "dick", "boobs"};
	    String[] illegalActivitiesWords = {"hack", "drugs", "gambling", "torrent", "suicide"};
	    String[] spamScamWords = {"click here", "free gift", "cash app", "bitcoin"};
	    String[] circumventionWords = {"f.u.c.k", "s h * t", "a$$", "b!tch", "d4mn"};


	    if (containsBannedWord(lowerInput, offensiveWords)) {
	        return "You can't use Offensive Language.";
	    } else if (containsBannedWord(lowerInput, hateSpeechWords)) {
	        return "You can't use Hate Speech & Discriminatory Terms.";
	    } else if (containsBannedWord(lowerInput, sexualContentWords)) {
	        return "You can't use Sexual Content & Inappropriate Language.";
	    } else if (containsBannedWord(lowerInput, illegalActivitiesWords)) {
	        return "You can't use Illegal Activities & Harmful Behavior.";
	    } else if (containsBannedWord(lowerInput, spamScamWords)) {
	        return "You can't use Spam & Scam-Related Words.";
	    } else if (containsBannedWord(lowerInput, circumventionWords)) {
	        return "You can't use Circumvention Attempts Using Variants Language.";
	    } else {
	        return "";
	    }
	}


	private static boolean containsBannedWord(String input, String[] bannedWords) {
	    for (String word : bannedWords) {
	        if (input.contains(word)) {
	            return true;
	        }
	    }
	    return false;
	}

	

}
