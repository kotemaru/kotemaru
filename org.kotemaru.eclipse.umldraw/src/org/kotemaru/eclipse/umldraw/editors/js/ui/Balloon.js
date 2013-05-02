

function Balloon(){this.initialize.apply(this, arguments)};
(function(_class){

	_class.popupBalloon = function($button, alt) {
		//var alt = $button.attr(attr);
		var $balloon = $("#balloon");
		$balloon.offset({top:0,left:0}).css("display","inline-block").html(alt);

		var offset = $button.offset();
		offset.top += $button.height() + 8 ;
		offset.left += $button.width() + 8;
		if (offset.left+$balloon.width()>$(document.body).width()) {
			offset.left = $(document.body).width()-$balloon.width()-12;
		}
		$balloon.offset(offset);
	}
	_class.showBalloon = function(offset, alt) {
		var $balloon = $("#balloon");
		$balloon.offset({top:0,left:0}).css("display","inline-block").html(alt);
		$balloon.offset(offset);
	}
	_class.hideBalloon = function() {
		$("#balloon").hide();
	}

	$(function(){
		
		$("*[alt]").live("mouseover",function(ev){
			_class.popupBalloon($(this), $(this).attr("alt"));
		}).live("mouseout",function(){
			_class.hideBalloon();
		});
		$("*[data-alt]").live("mouseover",function(ev){
			_class.popupBalloon($(this), $(this).attr("data-alt"));
		}).live("mouseout",function(){
			_class.hideBalloon();
		});
		
		$(".Action").live("mouseover",function(ev){
			var msg = Strings.get("Action."+$(this).attr("data-value"));
			_class.popupBalloon($(this), msg);
		}).live("mouseout",function(){
			_class.hideBalloon();
		});
		/* TODO:
		var validTime =0;
		$("#canvas").live("mousemove",function(ev){
			_class.hideBalloon();
			validTime = new Date().getTime()+500;
			setTimeout(function(){
				
				_class.showBalloon({top:ev.clientY+32,left:ev.clientX+32}, "canvas");
			}, 600);
		}).live("mouseout",function(){
			_class.hideBalloon();
		});
		*/
		
	});
	

})(Balloon);
