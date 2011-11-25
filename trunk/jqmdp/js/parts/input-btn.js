
(function($) {
	
	var WRAP = '<div class=""><a style="float:left;">xxx</a></div>';
	
	$.widget( "mobile.inputbtn", $.mobile.widget, {
		options: { },
		_create: function() {
			var input = this.element;
			var $warpDiv = input.wrap(WRAP);
			//input.removeClass('ui-corner-all ui-shadow-inset')
		}
	});
		
	$(document).bind( "pageinit", function(e){
		$(e.target).find("input[data-role='inputbtn']").each(function() {
			if ( typeof($(this).data('input-btn')) === "undefined" ) {
				$(this).inputbtn();
			}
		});
	});
})(jQuery);
	
	