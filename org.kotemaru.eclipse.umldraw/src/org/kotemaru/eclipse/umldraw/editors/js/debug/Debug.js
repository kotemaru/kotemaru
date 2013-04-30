

function Debug(){this.initialize.apply(this, arguments)};
(function(_class){

	_class.prototype.initialize = function(targetClass) {
	}
	
	function save() {
		Dialog.open("#debugDialog", data);
		var data = Store.save(Canvas.getItems());
		$("#saveText").val(JSON.stringify(data,null, "\t"));
		Store.load(data);
		Actions.resetAction(true);
	}
	function print() {
		Dialog.open("#svgDialog", data);
		var data = {svg: Canvas.toSVG()};
		var ifr = $("#iframeSvg")[0];
		ifr.contentDocument.body.innerHTML = data.svg;
		Actions.resetAction(true);
	}
	function undo() {
		EditBuffer.undo();
		Actions.resetAction(true);
	}
	function redo() {
		EditBuffer.redo();
		Actions.resetAction(true);
	}
	
	$(function(){
		Actions.registerAction("save", {selectMe:save});
		Actions.registerAction("print", {selectMe:print});
		Actions.registerAction("undo", {selectMe:undo});
		Actions.registerAction("redo", {selectMe:redo});
	});
	
	
})(Debug);


//EOF
