

function MyMine(){this.initialize.apply(this, arguments)};
(function(Class){
	var C_TICKET = ".Ticket";

	var isDrag = false;

	Class.prototype.initialize = function() {
	}
	Class.init = function() {
		Config.init();
		Ticket.init();
		Folder.init();
		Control.init();
		Storage.init();
		Folder.refresh();
		
		$(document.body).bind("mouseup",function(){
			setTimeout(function() {
				Class.isDrag(false);
				Class.setDragCursor();
			}, 50);
		});
		
	}
	
	Class.isDrag = function(b) {
		if (b !== undefined) {
			isDrag = b;
		}
		return isDrag;
	}

	Class.setDragCursor = function() {
		if (isDrag) {
			var sels = Tickets.getSelection();
			var img = (sels.length>=2) ? "tickets-no":"ticket-no";
			$(document.body).css("cursor","url(img/"+img+".png) 16 8, pointer");
		} else {
			$(document.body).css("cursor","default");
		}
	}
	
	
	Class.waiting = function(b) {
		Class.progress(b?0:100);
		setTimeout(function(){$("#waiting").toggle(b);},b?1:300);
	}
	Class.progress = function(per) {
		$("#progressBar>div").css("width",per+"%");
	} 
	
})(MyMine);
