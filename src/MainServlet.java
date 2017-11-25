import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;

import org.database.*;
import org.session.SessionClass;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MainServlet extends HttpServlet 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3877285994037926685L;

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
        
        String page = new String();
        String id = new String();
        
        page = request.getParameter("page");
        if(page == null)
        	page = "1";
        else {
        	int ipage = Integer.parseInt(page);
        	if(ipage != 1 && ipage != 2 && ipage != 3)
        		page = "1";
        }
        request.setAttribute("page", page);
        
        id = request.getParameter("id");
        if(id == null)
        	id = "null";
        else {
        	int iid = Integer.parseInt(id);
        	if(iid < 0)
        		id = "null";
        }
        request.setAttribute("id", id);
        
		try 
		{
			if(SessionClass.existSession(request))
				request.getRequestDispatcher("main.jsp").forward(request, response);
			else
				response.sendRedirect(request.getContextPath() + "/welcome");
		} catch (Exception e) {
			System.out.println(e);
		}
    }
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	{
		StringBuilder jb = new StringBuilder();
        String line = new String();

        try 
        {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                jb.append(line);
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        try 
        {
        	Cookie[] cookies = request.getCookies();
        	String title = new String();
        	String body = new String();
        	String tags = new String();
        	String author = new String();
        	String email = new String();
        	int pid = 0, uid = 0, cid = 0;
        	JsonParser parser = new JsonParser();
        	Gson gson = new Gson();
        	parser.parse(jb.toString());
        	JsonObject rootObject = parser.parse(jb.toString()).getAsJsonObject();

            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            
            switch(rootObject.get("type").getAsInt())//1 - all posts, 2 - selected post, 3 - user
            {
            case 1:
            default:
            	ArrayList<BPost> posts = MySQLClass.getPostsList();
            	line = gson.toJson(posts);
            	out.println(line);
            	break;
            case 2:
            	BPost post = MySQLClass.getPost(request, rootObject.get("id").getAsInt());
            	line = gson.toJson(post);
            	out.println(line);
            	break;
            case 3:
            	BUser user = MySQLClass.getUser(rootObject.get("id").getAsInt());
            	line = gson.toJson(user);
            	out.println(line);
            	break;
            	//4 - add post, 5 - edit post, 6 - add comment
            case 4:
            	title = rootObject.get("title").getAsString();
            	body = rootObject.get("body").getAsString();
            	tags = rootObject.get("tags").getAsString();
            	
        		for(int i = 0; i<cookies.length; ++i)
        			if(cookies[i].getName().equals("uid"))
        				uid = Integer.parseInt(cookies[i].getValue());
        		MySQLClass.addPost(title, body, tags, uid);
            	break;
            case 5:
            	title = rootObject.get("title").getAsString();
            	body = rootObject.get("body").getAsString();
            	tags = rootObject.get("tags").getAsString();
            	pid = rootObject.get("pid").getAsInt();
            	
        		for(int i = 0; i<cookies.length; ++i)
        			if(cookies[i].getName().equals("uid"))
        				uid = Integer.parseInt(cookies[i].getValue());
        		if(uid == MySQLClass.getUid(pid))
        			MySQLClass.editPost(title, body, tags, pid);
            	break;
            case 6:
            	author = rootObject.get("author").getAsString();
            	email = rootObject.get("email").getAsString();
            	body = rootObject.get("text").getAsString();
            	pid = rootObject.get("pid").getAsInt();
            	
        		MySQLClass.addComment(pid, author, email, body);
            	break;
            	//7 - delete post, 8 - delete comment
            case 7:
            	pid = rootObject.get("pid").getAsInt();
            	
        		for(int i = 0; i<cookies.length; ++i)
        			if(cookies[i].getName().equals("uid"))
        				uid = Integer.parseInt(cookies[i].getValue());
        		if(uid == MySQLClass.getUid(pid))
        			MySQLClass.deletePost(pid);
            	break;
            case 8:
            	cid = rootObject.get("cid").getAsInt();
            	pid = rootObject.get("pid").getAsInt();
            	
        		for(int i = 0; i<cookies.length; ++i)
        			if(cookies[i].getName().equals("uid"))
        				uid = Integer.parseInt(cookies[i].getValue());
        		if(uid == MySQLClass.getUid(pid))
        			MySQLClass.deleteComment(cid);
            	break;
            	//logout
            case 9:
            	SessionClass.destroySession(request, response);
            	break;
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
	}
}