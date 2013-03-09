

$(function(){
	function toggleList(item) {
		var id = item.id;
		var $item = $(item);
		var $body = $("#body-"+id);
		if ($body.is(':visible')) {
			$body.hide();
			$item.css({"list-style-image":"url(/img/li-mark-0.png)"});
		} else {
			$body.show();
			$item.css({"list-style-image":"url(/img/li-mark-1.png)"});
		}
	}
	
	$(".YearItem").click(function(ev){toggleList(this);});
	$(".MonthItem").click(function(ev){toggleList(this);});

	$(".MonthBody").hide();
	$(".YearBody").hide();
});
