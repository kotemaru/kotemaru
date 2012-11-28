

//@SingletonClass
function Folders(){this.initialize.apply(this, arguments)};
(function(Class){
	var _FOLDERS = "#folders";
	var INBOX = "inbox";
	var _Folder = ".Folder";
	var FolderSelect = "FolderSelect";


	Class.prototype.initialize = function() {
	}
	
	var DEFAULT_FOLDERS = [
   	    {name:INBOX,     title:"受信箱",     icon:"img/inbox.png", nosave:true},
   	    {name:"trash",   title:"ゴミ箱",     icon:"img/bin_closed.png", nosave:true},
   	    //{name:"sepa1",   title:"---",        icon:"---"},
   	    {name:"now",     title:"至急",       icon:"img/alarm.png"},
   	    {name:"play",    title:"現行作業",   icon:"img/hand.png"},
   	    {name:"reserve", title:"予定作業",   icon:"img/bookmark_folder.png"},
   	    {name:"todo",    title:"後回し",     icon:"img/folder.png"},
   	    {name:"ovserve", title:"相談のみ",   icon:"img/comment.png"},
   	    {name:"wait",    title:"進捗待ち",   icon:"img/comment.png"},
   	    {name:"other",   title:"その他",     icon:"img/folder.png"}
   	];

	var folders = [];
	var current = null;
	
	Class.init = function(){
		for (var i=0; i<DEFAULT_FOLDERS.length; i++) {
			var folder = new Folder().setParams(DEFAULT_FOLDERS[i]);
			folder.seq = i;
			folders.push(folder);
		}
		return this;
	}
	
	Class.refresh = function(){
		var $section = $(_FOLDERS);

		$(_Folder).removeClass(FolderSelect);
		if (current != null) {
			current.get$Elem().addClass(FolderSelect);
		}
		
		folders.sort(function(a,b){return a.seq-b.seq;});
		for (var i=0; i<folders.length; i++) {
			$section.append(folders[i].build(false));
		}
		return this;
	}

	//------------------------------------------------------------------
	// Event Handler.
	/**
	 * カラムの移動ハンドラ設定。
	 */
	function bindMove() {
		// Move
		var handle = null;
		var mouseDownTime = 0;
		$(_Folder).live("mousedown", function(){
			handle = this;
			current = $(this).data("folder");
			$(handle).css({cursor: "url(img/cursor-move-box-UD.png) 8 8, row-resize"});
			mouseDownTime = new Date().getTime();
			return false;
		}).live("mousemove",function(ev){
			if (handle == null) return;
			if (handle == this) return;

			var $handle = $(handle);
			var $target = $(this);
			
			var hFolder = $handle.data("folder");
			var tFolder = $target.data("folder");

			var tmp = hFolder.seq;
			hFolder.seq = tFolder.seq;
			tFolder.seq = tmp;
			Class.refresh();

		}).live("mouseup", function(){
			Class.refresh();
			var time = (new Date().getTime())- mouseDownTime;
			mouseDownTime = 0;
			if (time>200) return;
		});

		$(document.body).live("mouseup",function(ev){
			handle = null;
		});
	}

	
	$(function(){
		bindMove();
	})
	
	
	
	
	
})(Folders);


