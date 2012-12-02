

function Balloon(){this.initialize.apply(this, arguments)};
(function(Class){

	Class.popupBalloon = function($button, attr) {
		var alt = $button.attr(attr);
		var $balloon = $("#balloon");
		$balloon.offset({top:0,left:0}).css("display","inline-block").html(alt);

		var offset = $button.offset();
		offset.top += $button.width() + 8 ;
		offset.left += $button.height() + 8;
		if (offset.left+$balloon.width()>$(document.body).width()) {
			offset.left = $(document.body).width()-$balloon.width()-12;
		}
		$balloon.offset(offset);
	}
	Class.hideBalloon = function() {
		$("#balloon").hide();
	}

	$(function(){
		
		$("*[alt]").live("mouseover",function(ev){
			Class.popupBalloon($(this), "alt");
		}).live("mouseout",function(){
			Class.hideBalloon();
		});
		$("*[data-alt]").live("mouseover",function(ev){
			Class.popupBalloon($(this), "data-alt");
		}).live("mouseout",function(){
			Class.hideBalloon();
		});

	});
	

})(Balloon);
