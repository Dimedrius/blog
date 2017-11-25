package org.database;

import java.util.ArrayList;

public class BPost
{
		public String login;
		public String title;
		public String body;
		public String tags;
		public BUser user;
		public ArrayList<BComment> comments;
		public boolean owner;
		public int pid;
		public BPost()
		{
			user = new BUser();
		}
}