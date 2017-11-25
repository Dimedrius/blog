import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.database.*;
import org.session.SessionClass;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SignServlet extends HttpServlet 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5197480783437505197L;

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
        response.setContentType("text/html");
		
		try 
		{
			if(!SessionClass.existSession(request))
				request.getRequestDispatcher("sign.jsp").forward(request, response);
			else
				response.sendRedirect(request.getContextPath() + "/main");
				//request.getRequestDispatcher("/main").forward(request, response);
		} catch (Exception e) {
			System.out.println(e);
		}
    }
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	{
		StringBuilder jb = new StringBuilder();
        String line = new String();
        
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                jb.append(line);
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        try {
        	JsonParser parser = new JsonParser();
        	parser.parse(jb.toString());
        	JsonObject rootObject = parser.parse(jb.toString()).getAsJsonObject();

        	response.setContentType("application/json");
        	response.setCharacterEncoding("utf-8");
            PrintWriter out = response.getWriter();
			
			String login = rootObject.get("login").getAsString();
			String password = rootObject.get("password").getAsString();
			String email = rootObject.get("email").getAsString();

			if(MySQLClass.addUser(login, password, email))
				out.println("error");
			else
				out.println("success");
        } catch (Exception e) {
            System.out.println(e.toString());
        }
	}
}