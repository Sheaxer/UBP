

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/Login")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// get user credentials
		String username = request.getParameter("username");
		String enteredPassword = request.getParameter("password");
		
		if(username.equals("") || enteredPassword.equals("")) {
			// some credentials were not entered
			request.setAttribute("message", "You must enter both login credentials.");
			request.getRequestDispatcher("/index.jsp").forward(request, response);
			return;
		}
		
		// TODO read user salt and password from DB
		//String referenceHash = "heslo";
		//String salt = "salt";
		//String enteredHash = UserUtils.hashPassword(enteredPassword, salt);
		
		// TODO add login delay
		// check password match
		Long id = DatabaseManager.getUserId(username, enteredPassword);
		
		if(id != null) {
			// login the user
			HttpSession session = request.getSession(); // create a session
			session.setAttribute("username", username);
			String loginHash = DatabaseManager.logInUser(id);
			session.setAttribute("loginHash", loginHash);
			
			session.setMaxInactiveInterval(5*60); // 5 min
			
			// redirect to encryption
			response.sendRedirect(response.encodeRedirectURL("encrypt.jsp"));
			return;
		}
		else {
			request.setAttribute("message", "Incorrect username or password");
			request.getRequestDispatcher("/index.jsp").forward(request, response);
			return;
		}
	}

}
