
var Dialog = {
	close : function() {
		$(".Dialog").hide();
	},
	open : function(name, onClick) {
		Dialog.close();
		var $dialog = $(name).show();
		var $dialogBody = $dialog.find(".DialogBody");
		$dialogBody.width($dialog.width()*0.7);
		$dialogBody.offset({top:$dialog.height()/2 -$dialogBody.height()/2});
		
		$dialog.find(".DialogButton").bind("click",function(){
			if (onClick) onClick(this);
			Dialog.close();
		})
		return $dialog;
	},
	openRegisterButtonDialog : function($button, onClick) {
		var $dialog = Dialog.open("#registerButtonDialog", onClick);
		$dialog.find("#dialogButtonImg").empty().append($button.contents().clone());
	},
	openErrorDialog : function(errorMessage) {
		var $dialog = Dialog.open("#errorDialog");
		$dialog.find("#errorMessage").empty().append(errorMessage);
	},
	load: function(url) {
		$(document.body).append($("<div>").load(url));
	}
}

$(function(){
	Dialog.load("../dialog/errorDialog.html");
	Dialog.load("../dialog/registerButtonDialog.html");
	Dialog.close();
});
