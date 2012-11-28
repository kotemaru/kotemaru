

//@SingletonClass
function Folders(){this.initialize.apply(this, arguments)};
(function(Class){
	var _FOLDERS = "#folders";
	var INBOX = "inbox";

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
		folders.sort(function(a,b){return a.seq-b.seq;});
		for (var i=0; i<folders.length; i++) {
			$section.append(folders[i].build(false));
		}
		return this;
	}
	
	
})(Folders);


