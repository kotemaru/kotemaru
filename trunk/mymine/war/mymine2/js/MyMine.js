

function MyMine(){this.initialize.apply(this, arguments)};
(function(Class){
	var isDrag = false;

	Class.prototype.initialize = function() {
	}


	Class.reconfig = function() {
		Control.reconfig();
	}



	Class.waiting = function(b) {
		Class.progress(b?0:100);
		setTimeout(function(){$("#waiting").toggle(b);},b?1:300);
	}
	Class.progress = function(per) {
		$("#progressBar>div").css("width",per+"%");
	}


})(MyMine);
