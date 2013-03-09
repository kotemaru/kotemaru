
$.ajaxSetup({ cache: false });

$(function(){
	function commentView(data) {
		//var data = JSON.parse(json);
		var $view = $("#commentView");
		$view.html("");
		for (var i=0; i<data.length; i++ ) {
			var comment = data[i];
			var html = "<div class='CommentHead'><a href='mail:"
					+comment.email+"'>"+comment.name+"</a> さんが "
					+comment.date+" にコメント"
					+" [<a href='javascript:' class='CommentDel' key='"
							+comment.key+"'>削除</a>]</div>"
					+"<div class='CommentBody'>"+comment.body+"</div>";
			$view.append($(html));
		}
		$view.find("a.CommentDel").bind("click", function(){
			var passwd = prompt("削除キーを入力してください。","");
			if (passwd == null || passwd == "") {
				alert("削除キーが必要です。");
			}
			//alert("key="+$(this).attr("key")+"  passwd="+passwd);
			commentDelete({key:$(this).attr("key"), passwd:passwd});
		});
	}
	function commentFail(json) {
		var $view = $("#commentView");
		$view.html("<div>コメントの読み込みに失敗しました。</div>");
	}
	function commentDelete(params) {
		$.ajax({
			async: true,
			type: "POST",
			url: "/Comment?_action=DELETE",
			data: params,
			success: function(){
				commentLoad(1000); 
				//alert("削除に成功しました。\nコメントを再読込します。");
			},
			error: function(){alert("削除に失敗しました。");}
		});
	}

	function commentPost() {
		var form = document.forms.commentForm;
		var params = {};
		for (var i=0; i<form.length; i++) {
			params[form[i].name] = form[i].value;
		}
		if (params.body == "") {
			alert("本文は必須です。");
			return;
		}
		
		$.ajax({
			async: true,
			type: "POST",
			url: "/Comment",
			data: params,
			success: function(){
				commentLoad(1000);
				form.name.value = "名無し";
				form.email.value = "";
				form.passwd.value = "";
				form.body.value = "";
				//alert("書込に成功しました。\nコメントを再読込します。");
			},
			error: function(){alert("書込に失敗しました。");}
		});
		// for IE
		var $view = $("#commentView");
		$view.html("<img src='/img/waiting.gif' />コメント読込中...");
	}
	
	function commentLoad(delay) {
		if (delay == null) delay = 10;
		
		var $view = $("#commentView");
		$view.html("<img src='/img/waiting.gif' />コメント読込中...");

		setTimeout(function(){
			$.ajax({
				async: true,
				type: "GET",
				url: "/Comment",
				data: {limit:100, asc:true, page: Global.contentPath},
				success: commentView,
				error: commentFail
			});
		}, delay);
	}
	
	commentLoad();
	Global.commentPost = commentPost;
	Global.commentLoad = commentLoad;
});
