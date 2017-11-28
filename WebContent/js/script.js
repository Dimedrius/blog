function CreateRequest()
{
    var Request = false;

    if(window.XMLHttpRequest)
        Request = new XMLHttpRequest();
    else if (window.ActiveXObject)
        try
		{
             Request = new ActiveXObject('Microsoft.XMLHTTP');
        }catch (CatchException){
             Request = new ActiveXObject('Msxml2.XMLHTTP');
		}
    if (!Request)
        alert('Невозможно создать XMLHttpRequest');
    
    return Request;
} 

function SendRequest(path, args, handler)
{
    var Request = CreateRequest();
    
	if(handler != null)
		Request.onreadystatechange = function()
		{
			if(Request.readyState == 4)
				handler(Request);
		};
    
    Request.open('post', path, true);
        
    Request.setRequestHeader('Content-Type','application/x-www-form-urlencoded; charset=utf-8');
    Request.send(args);
}

function addUser()
{
	var ans = '';
	var user = new Object();
	user.login = document.getElementById('login').value;
	user.password = document.getElementById('password').value;
	user.email = document.getElementById('email').value;
	
	var handler = function(Request)
				  {
					ans = Request.response;	
					if(ans === 'error')
						alert('Такой логин существует');
					else
						window.location.href = 'login';
				  };
	SendRequest('sign', JSON.stringify(user), handler);

}

function loginUser()
{
	var ans = '';
	var user = new Object();
	user.login = document.getElementById('login').value;
	user.password = document.getElementById('password').value;
	var handler = function(Request)
				  {
						ans = Request.response;
						if(ans == 'success')
							window.location.href = 'main';
						else
							alert('Неверный логин');
				  };
	SendRequest('login', JSON.stringify(user), handler);
}

function getContent(type, id)
{
	var ans = '';
	var sender = new Object();
	sender.type = type;//1 - all posts, 2 - selected post, 3 - user
	sender.id = id;
	var handler = function(Request)
				  {
						ans = Request.response;	
						ans = JSON.parse(ans);

						switch(type)
						{
						case 1:
							var posts = '';
							posts += '<div class="modal-body" style="text-align: center; margin: 10px">';
							posts += '<div class="top-title" style="text-align: center;"><h1>Список постов</h1></div>';
							posts += '<div class="medium-title" style="text-align: center;"><b>Добавить пост</b></div>';
							posts += '<div>Заголовок<br /><input type="text" class="form-control" id="title" /></div>';
							posts += '<div>Теги<br /><input type="text" class="form-control" id="tags" /></div>';
							posts += '<div>Текст<br /><textarea class="form-control" id="body"></textarea></div>';
							posts += '<div><button type="submit" onclick="addContent(4)" >Отправить</button></div>';
							posts += '</div>';
							posts += '<center>';
							for(var i = 0; i<ans.length; i++)
								posts += '<div class="title"><a href="javascript:void(0)" onclick="getContent(2,' + ans[i].pid + ')"><h2><b>' + ans[i].title + '</b></h2></a></div>';

							posts += '</center>';
							document.getElementById('content').innerHTML = posts;
							break;
						case 2:
							var post = '';
							post += '<div class="modal-body" style="text-align: center; margin: 10px">';
							post += '<input type="hidden" class="form-control" id="pid" value="' + ans.pid + '" />';
							if(!ans.owner)
							{
								post += '<div class="title">' + ans.title + '</div>';
								post += '<div class="author"><a href="javascript:void(0)" onclick="getContent(3,' + ans.user.uid + ')">' + ans.user.login + '</a></div>';
								post += '<div class="tags">' + ans.tags + '</div>';
								post += '<div class="body">' + ans.body + '</body>';
							}else{
								post += '<div class="title"><input type="text" class="form-control" id="title" value="' + ans.title + '"/></div>';
								post += '<div class="author"><a href="javascript:void(0)" onclick="getContent(3,' + ans.user.uid + ')">' + ans.user.login + '</a></div>';
								post += '<div class="tags"><input type="text" class="form-control" id="tags" value="' + ans.tags + '" /></div>';
								post += '<div class="body"><textarea class="form-control" id="body">' + ans.body + '</textarea></div>';
								post += '<div>';
								post += '<button type="submit" onclick="addContent(5)" >Изменить</button>';
								post += '<button type="submit" onclick="rmContent(7, null)" >Удалить</button>';
								post += '</div>';
							}
							post += '</div>';
							
							var comments = '';
							comments += '<div class="modal-body" style="text-align: center; margin: 10px">';
							comments += '<div class="top-title" style="text-align: center;"><h2>Коментарии</h2></div>';
							comments += '<div>Автор<br /><input type="text" class="form-control" id="author" /></div>';
							comments += '<div>Email<br /><input type="text" class="form-control" id="email" /></div>';
							comments += '<div>Комментарий<br /><input type="text" class="form-control" id="text" /></div>';
							comments += '<div><button type="submit" onclick="addContent(6)" >Добавить комментарий</button></div>';
							comments += '</div>';
							
							comments += '<center>';
							for(var i = 0; i<ans.comments.length; i++)
							{
								comments += '<div class="comment" style=" border: 1px solid black; margin: 10px">';
								comments += '<div class="author">' + ans.comments[i].author + '</div>';
								comments += '<div class="email">' + ans.comments[i].email + '</div>';
								comments += '<div class="text">' + ans.comments[i].text + '</div>';
								if(ans.owner)
									comments += '<button type="submit" onclick="rmContent(8, ' + ans.comments[i].cid + ')" >Удалить</button>';;
								comments += '</div>';
							}
							comments += '</center>';
							
							post += comments;
							document.getElementById('content').innerHTML = post;
							break;
						case 3:
							document.getElementById('content').innerHTML = '<div class="login">Логин: ' + ans.login + '</div><div class="email">email: ' + ans.email + '</div></div><div class="count_posts">Количество публикаций: ' + ans.count_posts + '</div>';
							break;
						}
				  };
	SendRequest('main', JSON.stringify(sender), handler);
}
	
function addContent(type)
{
	var sender = new Object();
	
	switch(type)//4 - add post, 5 - edit post, 6 - add comment
	{
	case 4:
		sender.title = document.getElementById('title').value;
		sender.tags = document.getElementById('tags').value;
		sender.body = document.getElementById('body').value;
		sender.type = type;
		
		var handler = function(Request)
		  			  {
						getContent(1, null);
		  			  };
		SendRequest('main', JSON.stringify(sender), handler);
		break;
	case 5:
		sender.title = document.getElementById('title').value;
		sender.tags = document.getElementById('tags').value;
		sender.body = document.getElementById('body').value;
		sender.type = type;
		sender.pid = document.getElementById('pid').value;
		
		var handler = function(Request)
		  			  {
						getContent(2, sender.pid);
		  			  };
		SendRequest('main', JSON.stringify(sender), handler);
		break;
	case 6:
		sender.author = document.getElementById('author').value;
		sender.email = document.getElementById('email').value;
		sender.text = document.getElementById('text').value;
		sender.pid = document.getElementById('pid').value;
		sender.type = type;
		
		var handler = function(Request)
		  			  {
						getContent(2, sender.pid);
		  			  };
		SendRequest('main', JSON.stringify(sender), handler);
		break;
	}
}
function rmContent(type, id)
{
	var sender = new Object();
	
	//7 - delete post, 8 - delete comment
	switch(type)
	{
	case 7:
		sender.type = type;
		sender.pid = document.getElementById('pid').value;
		
		var handler = function(Request)
		  			  {
						getContent(1, null);
		  			  };
		SendRequest('main', JSON.stringify(sender), handler);
		break;
	case 8:
		sender.type = type;
		sender.pid = document.getElementById('pid').value;
		sender.cid = id;
		
		var handler = function(Request)
		  			  {
						getContent(2, sender.pid);
		  			  };
		SendRequest('main', JSON.stringify(sender), handler);
		break;
	}
}
function logout()
{
	var sender = new Object();
	sender.type = 9;
	var handler = function(Request)
	  			  {
					location.href = '.';
	  			  };
	SendRequest('main', JSON.stringify(sender), handler);
}