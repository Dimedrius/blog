<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
  <head>
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
      <title>Добро пожаловать</title>
	  <script src="js/script.js"></script>
  </head>
  <body>
  	<div>
  		<h2><a href="main">Главная</a></h2>
  		<h4><a href="javascript:void(0)" onclick="logout()">Выход</a></h4>
  	</div>
	<div id="content">
	</div>
	<script>getContent(${page}, ${id})</script>
  </body>
</html>