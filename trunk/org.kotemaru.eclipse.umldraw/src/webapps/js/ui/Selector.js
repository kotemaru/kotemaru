
function Selector(){this.initialize.apply(this, arguments)};
(function(_class){
	
	_class.setValue = function($selector, val){
		$selector.attr("data-value",val);
		$selector.find(".SelectorItem").removeClass("Selected");
		$selector.find(".SelectorItem[data-value='"+val+"']").addClass("Selected");
		$selector.find("input[type='radio']").val([val]);
	}

	$(function(){
		$(".Selector > .SelectorItem").live("click", function(){
			var $this = $(this);
			var $selector = $(this.parentNode);
			var value = $this.attr("data-value");
			_class.setValue($selector, value);
		});
		
		$(".Selector > input[type=radio]").live("change", function(){
			var $this = $(this);
			var $selector = $(this.parentNode);
			var value = $this.val();
			_class.setValue($selector, value);
		});
	});

})(Selector);
