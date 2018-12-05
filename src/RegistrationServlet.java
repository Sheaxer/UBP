

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import javax.persistence.PersistenceException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.passay.PasswordValidator;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.eclipse.persistence.sessions.Project;
import org.passay.DictionarySubstringRule;
import org.passay.PasswordData;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import org.passay.dictionary.WordLists;
import org.passay.dictionary.WordListDictionary;
import org.passay.RuleResult;

import org.passay.dictionary.ArrayWordList;
/**
 * Servlet implementation class RegistrationServlet
 */
@WebServlet("/Registration")
public class RegistrationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegistrationServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// get registration credentials
		String username = request.getParameter("username");
		String enteredPassword = request.getParameter("password");
		String repeatPassword = request.getParameter("passwordRepeat");
		
		if(username.equals("") || enteredPassword.equals("") || repeatPassword.equals("")) {
			// some credentials were not entered
			request.setAttribute("message", "You must enter all credentials.");
			request.getRequestDispatcher("/register.jsp").forward(request, response);
			return;
		}
		
		// check if the passwords match
		if( !enteredPassword.equals(repeatPassword)) {
			request.setAttribute("message", "Entered passwords must match.");
			request.getRequestDispatcher("/register.jsp").forward(request, response);
			return;
		}

		// check if password is legit
		try {
			if( !checkPassword(enteredPassword)) {
				request.setAttribute("message", "Password must be at least 8 characters long and contain a lowercase letter, an uppercase letter, a number and a non-alphanumeric character. It also must not contain easily guessable substrings (like whole words or parts of words)");
				request.getRequestDispatcher("/register.jsp").forward(request, response);
				return;
			}
		} catch (Exception e) {
			request.setAttribute("message", "An internal error has occured. We are sorry for your incovenience.");
			request.getRequestDispatcher("/register.jsp").forward(request, response);
			e.printStackTrace();
			return;
		}
		
		
		// TODO Checks for dictionary attacks
		
		try {
			// create new user
			UserData userData = new UserData();
			userData.changeName(username);
			userData.changePassword(enteredPassword);
			
			
			userData.setKeys(CryptoUtils.generateKeyPair());
			Long id = DatabaseManager.addNewUser(userData); // fails if user with the same username exists
			
			// redirect user to the login page
			request.setAttribute("message", "Registration successful");
			request.getRequestDispatcher("/index.jsp").forward(request, response);
			return;
			
		} catch (PersistenceException pe) {
			// user already exists (probably)
			request.setAttribute("message", "This username is already taken.");
			request.getRequestDispatcher("/register.jsp").forward(request, response);
			return;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static boolean checkPassword(String pass) throws FileNotFoundException, IOException {
		char ch;
		boolean capitalFlag = false;
		boolean lowerCaseFlag = false;
		boolean numberFlag = false;
		boolean nonalphanumericFlag = false;
		
		// check for password length
		if(pass.length() < 8) {
			return false;
		}
		
		//check for dictionary passwords
		
		try {
			InputStream is = RegistrationServlet.class.getResourceAsStream("resources" + File.separator+"cain.txt");
			
			String line = null;
			
			ArrayList<String> lines = new ArrayList<String>();
			
			try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, Charset.forName(StandardCharsets.UTF_8.name())))) {	
				while ((line = bufferedReader.readLine()) != null) {
					lines.add(line);
				}
			}
			lines.sort(String::compareToIgnoreCase);
			String[] linesArray = lines.toArray(new String[0]).clone();
			
			ArrayWordList wl = new ArrayWordList(linesArray, false);
			WordListDictionary wld = new WordListDictionary(wl);
			PasswordValidator passwordValidator = new PasswordValidator(new DictionarySubstringRule(wld));
			PasswordData passwordData = new PasswordData(pass);
			RuleResult validate = passwordValidator.validate(passwordData);
			if(!(validate.isValid())) {
				System.out.println(validate.getDetails());
				System.out.println(validate.getMetadata());
				return false;
			}
		} catch(FileNotFoundException ex) {
			throw ex;
		} catch (IOException ex) {
			throw ex;
		}
		
		// check for character diversity
		for(int i=0;i < pass.length();i++) {
			ch = pass.charAt(i);
			if( Character.isDigit(ch)) {
				numberFlag = true;
			}
			else if (Character.isUpperCase(ch)) {
				capitalFlag = true;
			} else if (Character.isLowerCase(ch)) {
				lowerCaseFlag = true;
			} else {
				nonalphanumericFlag = true;
			}
			if(numberFlag && capitalFlag && lowerCaseFlag && nonalphanumericFlag)
				return true;
		}
		
		return false;
	}

}