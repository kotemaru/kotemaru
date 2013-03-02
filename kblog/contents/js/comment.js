

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
			alert("key="+$(this).attr("key")+"  passwd="+passwd);
		});
	}
	function commentFail(json) {
		var $view = $("#commentView");
		$view.html("<div>コメントの読み込みに失敗しました。</div>");
	}

	$.ajax({
		type: "GET",
		url: "/Comment",
		data: {limit:100, asc:true, page: Global.contentPath},
		success: commentView,
		fail: commentFail
	});
});
