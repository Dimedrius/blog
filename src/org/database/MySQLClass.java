package org.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class MySQLClass 
{
    public static Connection connection = null;

    public static void connect() throws ClassNotFoundException, SQLException
	{
        if (connection == null || connection.isClosed())
		{
			try
			{
				Class.forName("com.mysql.jdbc.Driver");
				String url = "jdbc:mysql://localhost:3306/";
				String db = "blog";
				String user = "blog";
				String password = "blogpassword";
				connection = DriverManager.getConnection(url + db, user, password);
			} catch (Exception e) {
				System.out.println(e);
			}
		}
    }

    public static boolean addUser(String login, String password, String email) throws ClassNotFoundException, SQLException
	{
		if(!checkUser(login))
		{
			try 
			{
				connect();
				Statement statement = connection.createStatement();
				statement.executeUpdate("INSERT INTO `users` (`login`, `password`, `email`)VALUES('" + login + "', PASSWORD('" + password + "'), '" + email + "')");
				statement.close();
			} catch (Exception e) {
				System.out.println(e);
			}
			return true;
		}else{
			return false;
		}
    }
	
    public static void addPost(String title, String body, String tags, int uid) throws ClassNotFoundException, SQLException
	{
        try 
		{
        	connect();
            Statement statement = connection.createStatement();
            statement.addBatch("INSERT INTO `posts` (`uid`, `title`, `body`, `tags`)VALUES('" + uid + "', '" + title + "', '" + body + "', '" + tags + "')");
			statement.addBatch("UPDATE `users` SET `count_posts` = `count_posts` + 1 WHERE `uid` = '" + uid + "'");
			statement.executeBatch();
        	statement.close();
		} catch (Exception e) {
            System.out.println(e);
        }
    }	
	
    public static void editPost(String title, String body, String tags, int pid) throws ClassNotFoundException, SQLException
	{
        try 
		{
        	connect();
            Statement statement = connection.createStatement();
            statement.executeUpdate("UPDATE `posts` SET  `title` = '" + title + "',`body`=  '" + body + "', `tags` = '" + tags + "' WHERE `pid` = '" + pid + "'");
			statement.close();
		} catch (Exception e) {
            System.out.println(e);
        }
    }
	
    public static void deletePost(int pid) throws ClassNotFoundException, SQLException
	{
        try 
		{
        	connect();
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM `posts` WHERE `pid` = '" + pid + "'");
        	statement.close();
		} catch (Exception e) {
            System.out.println(e);
        }
    }
	
    public static void addComment(int pid, String author, String email, String text) throws ClassNotFoundException, SQLException
	{
        try 
		{
        	connect();
            Statement statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO `comments` (`pid`, `author`, `email`, `text`)VALUES('" + pid + "', '" + author + "', '" + email + "', '" + text + "')");
        	statement.close();
		} catch (Exception e) {
            System.out.println(e);
        }
    }
	
    public static void deleteComment(int cid) throws ClassNotFoundException, SQLException
	{
        try 
		{
        	connect();
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM `comments` WHERE `cid` = '" + cid + "'");
        	statement.close();
		} catch (Exception e) {
            System.out.println(e);
        }
    }

    public static ArrayList<BPost> getPostsList() throws ClassNotFoundException, SQLException
    {
        ArrayList<BPost> posts = new ArrayList<BPost>();
		BPost post;
		
        try 
		{
        	connect();
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT (SELECT `login` from `users` `u` WHERE `u`.`uid` = `p`.`uid`) as `login`, `pid`, `title`, `tags` FROM `posts` `p`");
        
			while (resultSet.next()) 
			{
				post = new BPost();
				post.login = resultSet.getString("login");
				post.title = resultSet.getString("title");
				post.tags = resultSet.getString("tags");
				post.pid = resultSet.getInt("pid");
				posts.add(post);
			}
			
			statement.close();
			resultSet.close();
		} catch (Exception e) {
            System.out.println(e);
        }

        return posts;
    }

    public static BPost getPost(HttpServletRequest request, int pid) throws ClassNotFoundException, SQLException
    {
		BPost post = new BPost();
		
        try 
		{
        	connect();
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT (SELECT `login` from `users` `u` WHERE `u`.`uid` = `p`.`uid`) as `login`, `uid`, `title`, `body`, `tags` FROM `posts` `p` WHERE `pid` = '" + pid + "'");
        
			if(resultSet.next())
			{
				post.user.login = resultSet.getString("login");
				post.user.uid = resultSet.getInt("uid");
				post.title = resultSet.getString("title");
				post.body = resultSet.getString("body");
				post.tags = resultSet.getString("tags");
				post.pid = pid;
			}
			
        	Cookie[] cookies = request.getCookies();
			int uid = 0;
			for(int i = 0; i<cookies.length; ++i)
    			if(cookies[i].getName().equals("uid"))
    				uid = Integer.parseInt(cookies[i].getValue());
    		if(uid == MySQLClass.getUid(pid))
				post.owner = true;
			else
				post.owner = false;
			
			
			
			post.comments = getCommentsList(pid);
			
			statement.close();
			resultSet.close();
		} catch (Exception e) {
            System.out.println(e);
        }

        return post;
    }

    public static ArrayList<BComment> getCommentsList(int pid) throws ClassNotFoundException, SQLException
    {
        ArrayList<BComment> comments = new ArrayList<BComment>();
		BComment comment;
		
        try 
		{
        	connect();
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT `cid`, `author`, `email`, `text` FROM `comments` WHERE `pid` ='" + pid + "'");
        
			while (resultSet.next()) 
			{
				comment = new BComment();
				comment.author = resultSet.getString("author");
				comment.email = resultSet.getString("email");
				comment.text = resultSet.getString("text");
				comment.cid = resultSet.getInt("cid");
				comments.add(comment);
			}

			statement.close();
			resultSet.close();
		} catch (Exception e) {
            System.out.println(e);
        }

        return comments;
    }

    public static BUser getUser(int uid) throws ClassNotFoundException, SQLException
    {
		BUser user = new BUser();
		
        try 
		{
        	connect();
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT `login`, `email`, `count_posts` FROM `users` WHERE `uid` = '" + uid + "'");
        
			if(resultSet.next())
			{
				user.login = resultSet.getString("login");
				user.email = resultSet.getString("email");
				user.count_posts = resultSet.getString("count_posts");
			}
			
			statement.close();
			resultSet.close();
		} catch (Exception e) {
            System.out.println(e);
        }

        return user;
    }

    public static boolean checkUser(String login, String password) throws ClassNotFoundException, SQLException
    {
		int check = 0;
		
        try 
		{
        	connect();
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT count(`uid`) as `check` FROM `users` WHERE `login` = '" + login + "' AND `password` = PASSWORD('" + password + "')");
			
			if(resultSet.next())
				check = resultSet.getInt("check");
			
			statement.close();
			resultSet.close();
		} catch (Exception e) {
            System.out.println(e);
        }

        return (check == 1 ? true : false);
    }

    public static boolean checkUser(String login) throws ClassNotFoundException, SQLException
    {
		int check = 0;
		
        try 
		{
        	connect();
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT count(`uid`) as `check` FROM `users` WHERE `login` = '" + login + "'");
			
			if(resultSet.next())
				check = resultSet.getInt("check");
			
			statement.close();
			resultSet.close();
		} catch (Exception e) {
            System.out.println(e);
        }

        return (check == 1 ? true : false);
    }
    
    public static int getUid(String login, String password) throws ClassNotFoundException, SQLException
    {
    	int uid = 0;
        try 
		{
        	connect();
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT `uid` FROM `users` WHERE `login` = '" + login + "' AND `password` = PASSWORD('" + password + "')");
        
			if(resultSet.next())
				uid = resultSet.getInt("uid");
			
			statement.close();
			resultSet.close();
		} catch (Exception e) {
            System.out.println(e);
        }
        
        return uid;
    }
    public static int getUid(int pid) throws ClassNotFoundException, SQLException
    {
    	int uid = 0;
        try 
		{
        	connect();
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT `uid` FROM `users` WHERE `uid` = (SELECT `uid` FROM `posts` WHERE `pid` = '" + pid + "')");

			if(resultSet.next())
				uid = resultSet.getInt("uid");
			
			statement.close();
			resultSet.close();
		} catch (Exception e) {
            System.out.println(e);
        }
        
        return uid;
    }
}