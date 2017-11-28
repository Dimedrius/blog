package org.session;

import java.sql.SQLException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.database.MySQLClass;

public class SessionClass
{
	public static boolean existSession(HttpServletRequest request) throws ClassNotFoundException, SQLException
	{
		Cookie[] cookies = request.getCookies();
		String login = new String(), password = new String();

		if(cookies != null)
		for(int i = 0; i<cookies.length; ++i)
		{
			if(cookies[i].getName().equals("login"))
				login = cookies[i].getValue();
			if(cookies[i].getName().equals("password"))
				password = cookies[i].getValue();
		}
		
		return MySQLClass.checkUser(login, password);
	}
	
	public static void upSession(HttpServletResponse response, String _login, String _password) throws ClassNotFoundException, SQLException
	{
		Cookie login = new Cookie("login", _login);
		Cookie password = new Cookie("password", _password);
		Cookie uid = new Cookie("uid", "" + MySQLClass.getUid(_login, _password));
		login.setMaxAge(60*60*24);
		password.setMaxAge(60*60*24);
		uid.setMaxAge(60*60*24);
		response.addCookie(login);
		response.addCookie(password);
		response.addCookie(uid);
	}
	
	public static void destroySession(HttpServletRequest request, HttpServletResponse response)
	{
		Cookie[] cookies = request.getCookies();
		
		for(int i = 0; i<cookies.length; ++i)
		{
			cookies[i].setMaxAge(0);
			response.addCookie(cookies[i]);
		}
	}
}