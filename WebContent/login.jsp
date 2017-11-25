<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
  <head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Авторизация</title>
		<script src="js/script.js"></script>
  </head>
  <body>
		<div class="input">
			<div class="modal-body" style="text-align: center;">
				<div>
                Логин<br />
				<input type="text" class="form-control" id="login" />
				</div>
				<div>
                Пароль<br /> 
				<input type="password" class="form-control" id="password" />
				</div>
				<div>
				<button type="submit" onclick="loginUser()" >Авторизация</button>
				</div>
            </div>
		</div>
  </body>
</html>