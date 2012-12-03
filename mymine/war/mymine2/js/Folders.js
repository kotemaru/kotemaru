

//@SingletonClass
function Folders(){this.initialize.apply(this, arguments)};
(function(Class){
	var _FOLDERS = "#folders";
	var INBOX = "inbox";
	var TRASH = "trash";
	var _Folder = ".Folder";
	var FolderSelect = "FolderSelect";


	Class.prototype.initialize = function() {
	}
	
	var DEFAULT_FOLDERS = [
   	    {name:INBOX,     title:"受信箱",     icon:"img/inbox.png", nosave:true},
   	    {name:TRASH,     title:"ゴミ箱",     icon:"img/bin_closed.png", nosave:true},
   	    {name:"sepa1",   title:"---",      icon:"---", isSeparator:true, nosave:true},
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
	var inbox = null;
	
	Class.init = function(){};
	
	Class.addFolder = function(params){
		for (var i=0; i<folders.length; i++) {
			if (folders[i].name == params.name) return null;
		}
		
		var folder = new Folder().setParams(params);
		folder.seq = folders.length;
		folders.push(folder);
		save();
		return folder;
	}
	Class.delFolder = function(){
		for (var i=0; i<folders.length; i++) {
			if (folders[i] == current) {
				folders.splice(i, 1);
				current.get$Elem().remove();
				Storage.remove("folder/"+current.name);
				break;
			}
		}
		current = null;
		save();
		Class.refresh();
	}
	
	Class.select = function(folder){
		current = folder;
		TicketTray.setTickets(folder.getTickets());
		Class.refresh();
		$("#editFolderButton").toggle(!current.nosave)
		$("#delFolderButton").toggle(!current.nosave)
	}
	Class.getInbox = function(){
		return inbox;
	}
	Class.getCurrent = function(){
		return current;
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
			mouseDownTime = new Date().getTime();
			current = $(this).data("folder");
			if (current.nosave) return false;
			handle = this;
			$(handle).css({cursor: "url(img/cursor-move-box-UD.png) 8 8, row-resize"});
			return false;
		}).live("mousemove",function(ev){
			if (handle == null) return;
			if (handle == this) return;

			var $handle = $(handle);
			var $target = $(this);

			var hFolder = $handle.data("folder");
			var tFolder = $target.data("folder");
			if (tFolder.nosave) return;
			
			var tmp = hFolder.seq;
			hFolder.seq = tFolder.seq;
			tFolder.seq = tmp;
			Class.refresh();

		}).live("mouseup", function(){
			var folder = $(this).data("folder");
			if (TicketTray.isDrag()) {
				folder.dropTicket(current);
				save();
			}
			Class.select(folder);
			handle = null;
		
			var time = (new Date().getTime())- mouseDownTime;
			mouseDownTime = 0;
			if (time>200) return;
		});
	
	}
	
	function save() {
		for (var i=0; i<folders.length; i++) {
			var params = folders[i].getParams();
			Storage.put("folder/"+params.name, params);
		}
	}
	
	function load() {
		folders = [];
		Storage.each("folder/", function(name, params){
			folders.push(new Folder().setParams(params));
		});
		if (folders.length == 0) {
			for (var i=0; i<DEFAULT_FOLDERS.length; i++) {
				var folder = new Folder().setParams(DEFAULT_FOLDERS[i]);
				folder.seq = i;
				folders.push(folder);
			}
		}
		for (var i=0; i<folders.length; i++) {
			if (folders[i].name == INBOX) inbox = folders[i];
		}
	}
	
	$(function(){
		load();
		Class.refresh();
		bindMove();
	})
	
	
})(Folders);


