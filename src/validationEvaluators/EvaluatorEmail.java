package validationEvaluators;

public class EvaluatorEmail {
	
	public static String evaluateEmail(String emailInput) {

		String emailErrorMsg = "";
		int whereAtAt = emailInput.indexOf('@');
		int whereDotAt = emailInput.lastIndexOf('.');
		int emailLength = emailInput.length();
		
		if (emailLength == 0) {
			return "Your email cannot be blank!";
		}
		
		// Checking the length
		if ((emailLength > 256) || (emailLength < 6)) {
			return "Invalid email";
		}
	
		if (!(emailInput.contains("@"))){
			return "Please include @ in your email";
		}
		
		// Checking for a valid first character 
		if (!(("._-!#$%&\"\\=^'{}|".indexOf(emailInput.charAt(0)) >= 0) ||
				(emailInput.charAt(0) >= 'A' && emailInput.charAt(0) <= 'Z' ) ||
				(emailInput.charAt(0) >= 'a' && emailInput.charAt(0) <= 'z' ) ||		
				(emailInput.charAt(0) >= '0' && emailInput.charAt(0) <= '9' ))) {
		return "Please enter a proper Prefix";
		}
		
		// Checking for proper length and @ . positions in domain
		if ((whereAtAt > whereDotAt) ||
				(whereAtAt == (whereDotAt - 1)) ||
				((emailLength - whereDotAt) > 64) ||
				((emailLength - whereDotAt) < 3 ) ||
				((emailLength - whereAtAt) > 253) ) {
			return "Please enter a valid domain";
		}
		
		// Loop to check the characters for the address are valid 
		int i = 0;
		while (i < whereAtAt) {
			if (!(("._-!#$%&\"\\=^'{}|".indexOf(emailInput.charAt(i)) >= 0) ||
					(emailInput.charAt(i) >= 'A' && emailInput.charAt(i) <= 'Z' ) ||
					(emailInput.charAt(i) >= 'a' && emailInput.charAt(i) <= 'z' ) ||		
					(emailInput.charAt(i) >= '0' && emailInput.charAt(i) <= '9' ))) {
			return "Invalid character in Prefix";
			}
			else {
				i++;
			}
		}
		
		// Checking characters in domain before TLD
		int j = whereAtAt + 1;
		while (j < whereDotAt) {
			if (!((emailInput.charAt(j) >= 'a' && emailInput.charAt(j) <= 'z' ) ||		
					(emailInput.charAt(j) >= '0' && emailInput.charAt(j) <= '9' ))) {
			return "Invalid character in domain";
			}
			else {
				j++;
			}
		}
		
		//checking characters in TLD
		int p = whereDotAt + 1;
		while (p < emailLength) {
			if (!((emailInput.charAt(p) >= 'a' && emailInput.charAt(p) <= 'z') ||
					(emailInput.charAt(p) >= '0' && emailInput.charAt(p) <= '9' ) ||
					((emailInput.charAt(p) == '-') && 
					(p > whereDotAt + 1) && 
					(p < emailLength - 1)))) {
			return "Invalid character in TLD";
			}
			
			else {
				p++;
			}
		}


		return emailErrorMsg;
		
	}
	
}
