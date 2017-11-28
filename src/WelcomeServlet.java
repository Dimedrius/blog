import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.database.MySQLClass;
import org.session.SessionClass;

public class WelcomeServlet extends HttpServlet 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3148297183136363310L;

	public void init()
	{
		try 
		{
			MySQLClass.connect();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
        response.setContentType("text/html;charset=UTF-8");
        
		try 
		{
			if(!SessionClass.existSession(request))
				request.getRequestDispatcher("welcome.jsp").forward(request, response);
			else
				response.sendRedirect(request.getContextPath() + "/main");
		} catch (Exception e) {
			System.out.println(e);
		}
    }
}