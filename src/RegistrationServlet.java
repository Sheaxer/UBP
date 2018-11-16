

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
		
		// TODO Checks for password complexity and dictionary attacks
		// TODO Checks for users with same username
		// TODO Create the user in the DB
		// TODO generate RSA keypair for user and save to DB
		// TODO generate salt, hash user password and store in DB
		
		request.setAttribute("message", "Registration successful");
		request.getRequestDispatcher("/index.jsp").forward(request, response);
		return;
	}

}
