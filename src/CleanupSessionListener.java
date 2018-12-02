import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@WebListener
public class CleanupSessionListener implements HttpSessionListener {

	@Override
    public void sessionDestroyed(HttpSessionEvent event) {
        System.out.println("Session is getting destroooyed");
        String name = (String) event.getSession().getAttribute("username");
        System.out.println("Testing which user was deleted? " + name);
        String loginHash = (String) event.getSession().getAttribute("loginHash");
        
        DatabaseManager.logoutUser(loginHash);
        
    }
}
