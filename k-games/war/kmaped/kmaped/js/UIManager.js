function UIManager() {}
(function(Class) {
	const DEFAULT_CHIPSET = "map0.gif";
	var editor = null;
	
	//------------------------------------------------
	// base
	Class.init = function() {
		editor =  new Editor();
		
		initBalloon();
		initRadio();
		windowResize();
		$(window).bind("resize", windowResize)
			.bind("keydown", keyAction);
		
		$("#map").append(editor.workSpace.elem);
		$("#chipSet").append(editor.chipSpace.elem);
		$("#clipBord").append(editor.clipBuffer.elem);
	  	$("#leftPanel").tabPanel().select("Chip");
	  	
	  	Class.loadChipSet(DEFAULT_CHIPSET);
		editor.sizeFlush();
		Class.flush();


	}
	Class.flush = function() {
		$("#fileName").val(editor.workSpace.name);
		$("#chipSetUrl").val(editor.workSpace.chipSet.src);
		$("#mapWidth").val(editor.workSpace.width);
		$("#mapHeight").val(editor.workSpace.height);
		
		onChangeRadio.mode(editor.editMode);
		onChangeRadio.penMode(editor.editPenMode);
	
		var templ = $("#layerLabelTemplate").html();
		var $elem = editor.workSpace.makeLabelElem(templ);
		$("#layers").html("").append($elem);
	}
	function initBalloon() {
		var $alts = $("*[data-alt]");
		$alts.live("mouseover",function(ev){
			var $this = $(this);
			$("#balloon").html($this.attr("data-alt"))
				.show().offset({left:ev.pageX+5, top:ev.pageY+10});
		}).bind("mouseout",function(ev){
			$("#balloon").hide();
		});
	}
	function initRadio() {
		var $radios = $("button[data-radio]");
		$radios.live("click",function(ev){
			var $this = $(this);
			var name = $this.attr("data-radio");
			$("button[data-radio='"+name+"']").removeClass("select");
			$this.addClass("select");
			onChangeRadio[name]($this.attr("data-value"));
		});
	}
	var onChangeRadio = {};

	function windowResize() {
		$("#rightPanel").width($(window).width()-$("#leftPanel").width()-12);
	}
	function keyAction(ev) {
		if (!ev.ctrlKey) return;

		switch (ev.keyCode) {
		case 90: Class.undo();break; //z
		case 88: Class.cut();break; //x
		case 67: Class.copy();break; //c
		case 86: Class.paste();break; //v
		}
	}
	
	//------------------------------------------------
	// FileIO (Header left)
	
	Class.fileList = function() {
		var list = editor.server.list("");
		var $fileList = $("#fileList");
		$fileList.html("");
		for (var i=0; i<list.length; i++) {
			$fileList.append($("<option>"+list[i]+"</option>"));
		}
		
		var $fileName = $("#fileName");
		Class.openDialog("#fileListDialog");
	}
	Class.load = function() {
		var $fileList = $("#fileList");
		var name = $fileList.val();
		
		editor.load(name);
		Class.flush();
	}
	Class.save = function() {
		var name = $("#fileName").val();
		if (name == "") {
			alert("ファイル名がありません。");
			return;
		}
		editor.save(name);
        Class.closeDialog();
		Class.flush();
	}
	Class.upload = function() {
		var $file = $("#uploadFile");
		$file.show().bind("change", function(){
			var reader = new FileReader();
			reader.onload = function(e) {
                editor.apply(reader.result);
            };
            reader.readAsText($file[0].files[0]);
            Class.closeDialog();
		});
		Class.openDialog("#uploadDialog");
	}
	Class.download = function() {
		var json = editor.workSpace.saveJSON();
		var $atag = $("#downloadLink");
		$atag.attr("href", "data:text/json,"+encodeURIComponent(json));
		Class.openDialog("#downloadDialog");
	}

	Class.fileRemove = function() {
		var name = $("#fileName").val();
		if (name == "") {
			alert("ファイル名がありません。");
			return;
		}
		if (confirm(name+" を削除します。\nよろしいですか？")) {
			editor.server.remove(name);
		}
	}
	Class.testView = function() {
		var name = $("#fileName").val();
		if (name == "") {
			alert("ファイル名がありません。");
			return;
		}
		window.open("test.html?"+name,"testView");
	}
	
	//------------------------------------------------
	// Edit (Header right)
	var CURSOR = {
		"pen":       "url('kmaped/img/led/pencil.png') 1 15, pointer",
		"erase":     "url('kmaped/img/oxygen/eraser.png') 5 13, pointer",
		"selection": "pointer",
		"magic":    "url('kmaped/img/led/wand.png') 13 2, pointer",
	};
	
	onChangeRadio.mode = function(val) {
		editor.setMode(val);
		$("#penMode").toggle(val=="pen"||val=="erase");
		$("#selectionCmds").toggle(val=="selection");
		$("#magicMode").toggle(val=="magic");
		
		var cursor = CURSOR[val];
		$("#map *").css("cursor", cursor);
		$("#map *:active").css("cursor", cursor);
	}
	onChangeRadio.penMode = function(val) {
		editor.setPenMode(val);
	}
	onChangeRadio.magicMode = function(val) {
		editor.setMagicMode(val);
	}
	
	Class.clickPen = function() {
		$("button[data-value='pen']").click();
	}

	Class.copy   = function() {EditAction.copy(editor);}
	Class.cut    = function() {EditAction.cut(editor);}
	Class.remove = function() {EditAction.remove(editor);}
	Class.paste   = function() {EditAction.paste(editor);}
	
	Class.undo = function() {editor.undo();Class.flush();}
	Class.redo = function() {editor.redo();Class.flush();}

	
	//------------------------------------------------
	// Layer tab
	
	Class.addLayer = function() {
		editor.addLayer();
		Class.flush();
	}
	onChangeRadio.opacity = function(val) {
		editor.workSpace.opacity(val);
	}
	Class.grid = function() {
		editor.workSpace.grid();
	}

	Class.layerOnOff = function(self) {
		var idx = getLayerIdx(self);
		editor.workSpace.showLayer(idx, $(self).is(":checked"));
		editor.workSpace.opacity();
	}
	Class.layerUp = function(self){
		var idx = getLayerIdx(self);
		editor.workSpace.moveLayer(idx,1);
		editor.edited();
		Class.flush();
	}
	Class.layerDown = function(self){
		var idx = getLayerIdx(self);
		editor.workSpace.moveLayer(idx,-1);
		editor.edited();
		Class.flush();
	}
	
	Class.layerConfig = function(self){
		var idx = getLayerIdx(self);
		var val = window.prompt("Layer name:", self.name);
		if (val) {
			editor.workSpace.layers[idx].name = val;
			editor.edited();
		}
		Class.flush();
	}
	
	Class.layerRemove = function(self){
		var idx = getLayerIdx(self);
		if (idx == 0) {
			alert("最下層のレイヤは削除できません。");
			return;
		}
		editor.workSpace.removeLayer(idx);
		editor.edited();
		Class.flush();
	}

	function getLayerIdx(self) {
		var idx = $(self.parentNode.parentNode).attr("data-idx");
		return parseInt(idx);
	}

	//------------------------------------------------
	// Config tab

	Class.resize = function() {
		var w = parseInt($("#mapWidth").val());
		var h = parseInt($("#mapHeight").val());
		editor.resize(w,h);
		editor.workSpace.flush();
	}

	Class.loadChipSet = function(src) {
		src = src?src:$("#chipSetUrl").val();
		$("#chipSetUrl").val(src);
		editor.chipSet.load(src);
		Class.flush();
	}
	Class.source = function(val) {
		$("#source").val(editor.workSpace.saveJSON(" "));
	}

	Class.apply = function(val) {
		editor.apply($("#source").val());
	}
	
	//------------------------------------------------
	// Dialog
	Class.openDialog = function(id) {
		var $win = $(window);
		var $dialog = $(id);
		$dialog.css({
			zIndex: ZIndex.DIALOG,
			left: ($win.width() / 2 - $dialog.width() / 2)+"px",
			top: ($win.height() / 2 - $dialog.height() / 2)+"px",
		});
		$dialog.show();
	}
	Class.closeDialog = function(id) {
		$(".Dialog").hide();
	}

})(UIManager);

var UI = UIManager;