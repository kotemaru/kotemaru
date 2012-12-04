
//@Singleton
function TicketTray(){this.initialize.apply(this, arguments)};
(function(Class){
	var _TICKET_TRAY = "#ticketTray";
	var _TICKET = "#ticketTray .ExTableRow";
	var TicketSelect = "TicketSelect";
	var _TicketSelect = "."+TicketSelect;
	var TicketUnChecked = "TicketUnChecked";
	var _Folder = ".Folder";

	
	var SETTERS = {
		id:			function($elem,issue) {
			$elem.text(issue.id);
			var $ticket = $elem.parent();
			$ticket[0].dataset.num = issue.id;
			$ticket.toggleClass(TicketUnChecked, !TicketPool.isChecked(issue.id));
		},
		project:	function($elem,issue) {$elem.html(name(issue.project));},
		tracker:	function($elem,issue) {$elem.html(name(issue.tracker));},
		priority:	function($elem,issue) {$elem.html(name(issue.priority));},
		assigned_to:function($elem,issue) {$elem.html(name(issue.assigned_to));},
		subject:	function($elem,issue) {$elem.text(issue.subject);},
	
		start_date:	function($elem,issue) {$elem.html(toYYMMDD(issue.start_date));},
		due_date:	function($elem,issue) {$elem.html(toYYMMDD(issue.due_date));},
		updated_on:	function($elem,issue) {$elem.html(toYYMMDD(issue.updated_on));},

		done_ratio: function($elem,issue) {
			$elem.html("<div class='RateBar'><span></span></div>");
			$elem.find(">div>span").css("width",issue.done_ratio+"%");
		}
	};
	var COMPS = {
		id:			function(a,b) {console.log(a,b);
			return a.id-b.id;},
		project:	function(a,b){return compId(a,b,"project");},
		tracker:	function(a,b){return compId(a,b,"tracker");},
		priority:	function(a,b){return compId(a,b,"priority");},
		assigned_to:function(a,b){return compName(a,b,"assigned_to");},
		subject:	function(a,b){var A=a.subject,B=b.subject;return(A==B?0:(A<B?-1:1));},
		
		start_date:	function(a,b){return compDate(a,b,"start_date");},
		due_date:	function(a,b){return compDate(a,b,"due_date");},
		updated_on:	function(a,b){return compDate(a,b,"updated_on");},

		done_ratio: function(a,b){return(a.done_ratio-b.done_ratio);}
	};
	function compId(a,b,key) {
		var A = a[key]?a[key].id:-1;
		var B = b[key]?b[key].id:-1;
		return A-B;
	}
	function compName(a,b,key) {
		var A = a[key]?a[key].name:"";
		var B = b[key]?b[key].name:"";
		return (A==B?0:(A<B?-1:1));
	}
	function compDate(a,b,key) {
		var A = Date.parse(a[key]);
		var B = Date.parse(b[key]);
		return A-B;
	}

	// カラムメタ情報
	var COLUMN_METAS =[
		{title:"番号",   	width:36, setter:SETTERS.id, comparator:COMPS.id,
						style:{textAlign:"right"}},
		{title:"プロジェクト", width:80, setter:SETTERS.project, 	 comparator:COMPS.project },
		{title:"トラッカー",	width:70, setter:SETTERS.tracker, 	 comparator:COMPS.tracker },
		{title:"優先度", 		width:48, setter:SETTERS.priority, 	 comparator:COMPS.priority },
		{title:"担当者", 		width:97, setter:SETTERS.assigned_to, comparator:COMPS.assigned_to },
		{title:"更新日",		width:54, setter:SETTERS.updated_on, comparator:COMPS.updated_on },
		{title:"開始日", 		width:54, setter:SETTERS.start_date, comparator:COMPS.start_date },
		{title:"期日", 		width:54, setter:SETTERS.due_date, 	 comparator:COMPS.due_date },
		{title:"進捗", 		width:28, setter:SETTERS.done_ratio, comparator:COMPS.done_ratio },
		{title:"題名",   	width:"100%",  setter:SETTERS.subject, comparator:COMPS.subject,
						style:{whiteSpace:"normal", height:"auto"}}
	];

	var SORT_NAME = [
	    "id", 
		"project", 	 
		"tracker", 	 
		"priority", 	 
		"assigned_to", 
		"updated_on", 
		"start_date", 
		"due_date", 	 
		"done_ratio", 
		"subject"         
	];
	
	
	//------------------------------------------------------
	var exTable = null;

	// 「進捗」表示用カスタムカラム関数
	function rateSetter($elem,data,index) {
		var val = data[index];
		$elem.html("<div class='RateBar'><span></span></div>");
		$elem.find(">div>span").css("width",val+"%");
	}


	function name(data){
		return data?data.name:"&nbsp;";
	}
	function to2ChStr(n) {
		if (n > 9) return ""+n;
		//return "&nbsp;"+n;
		return "0"+n;
	}
	function toYYMMDD(dateStr) {
		var time = Date.parse(dateStr);
		if (isNaN(time)) return "&nbsp;";
		var date = new Date(time);
		var text = (date.getFullYear()%100)
			+"/"+to2ChStr(date.getMonth()+1)
			+"/"+to2ChStr(date.getDate())
			+" "+to2ChStr(date.getHours())
			+":"+to2ChStr(date.getMinutes())
			+":"+to2ChStr(date.getSeconds())
		;
		return text
	}

	function toMMDD(dateStr) {
		var time = Date.parse(dateStr);
		if (isNaN(time)) return "&nbsp;";
		var date = new Date(time);
		return to2ChStr(date.getMonth()+1)+"/"+to2ChStr(date.getDate());
 	}
	
	Class.setTickets = function(tickets) {
		var data = [];
		for (var k in tickets) {
			var issue = tickets[k];
			data.push(issue);
		}
		exTable.data(data);
	}

	Class.getSortInfo = function() {
		var sortInfo = exTable.getSortInfo();
		if (sortInfo == null) return null;
		sortInfo.name = SORT_NAME[sortInfo.index];
		return sortInfo;
	}
	
	//---------------------------------------------------------------------
	// Event Handler
	
	var isDrag = false;

	Class.isDrag = function(b) {
		if (b !== undefined) isDrag = b;
		return isDrag;
	}

	Class.setDragCursor = function() {
		$(document.body).css("cursor", Class.getDragCursor(true));
		$(_Folder).css("cursor", Class.getDragCursor(false));
	}

	Class.getDragCursor = function(isNo) {
		if (isDrag) {
			var sels = Class.getSelection();
			var img = (sels.length>=2) ? "tickets":"ticket";
			if (isNo) img += "-no";
			return "url(img/"+img+".png) 16 8, pointer";
		} else {
			return "default";
		}
	}

	Class.getSelection = function() {
		var selection = [];
		var tickets = $(_TicketSelect);
		for (var i=0; i<tickets.length; i++) {
			selection.push(tickets[i].dataset.num);
		}
		return selection;
	}
	Class.clearSelection = function() {
		$(_TicketSelect).removeClass(TicketSelect);
	}
	Class.addSelection = function(elem) {
		$(elem).addClass(TicketSelect);
	}

	function bindMove() {
		// Ticket
		var draggable = null;
		var downTime = 0;
		$(_TICKET).live("mousedown",function(ev){
			Class.isDrag(true);
			draggable = this;
			downTime = new Date().getTime();
			return false;
		}).live("mousemove",function(ev){
			var isClick = (100 > (new Date().getTime() - downTime));
			if (!isClick && draggable == this) {
				Class.setDragCursor();
				if (Class.isDrag()) {
					Class.addSelection(this);
				}
			}
		}).live("mouseout",function(ev){
			if (draggable == this) {
				draggable = null;
			}
		}).live("mouseup",function(ev){
			var isClick = (200 > (new Date().getTime() - downTime));
			if (isClick) {
				if (!ev.ctrlKey) Class.clearSelection();
				$(this).toggleClass(TicketSelect);
				draggable = null;
			}
		}).live("dblclick",function(ev){
			var num = this.dataset.num;
			RedMine.openIsuue(num);
			TicketPool.checked(num);
			Class.refresh();
			Folder.refresh();
		});

		$(document.body).live("mouseup",function(ev){
			//Class.clearSelection();
			// ハンドラが先に実行されるので遅らせる。
			setTimeout(function(){
				draggable = null;
				Class.isDrag(false);
				Class.setDragCursor();
			}, 10);
		});
	}

	//----------------------------------------------------------------
	// 初期化
	
	function save(ev, columnMetas) {
		Storage.put("columnMetas", columnMetas);
	}
	function load(exTable) {
		var metas = Storage.get("columnMetas", COLUMN_METAS);
		// Note:関数は保存出来い...
		for (var i=0; i<metas.length; i++) {
			metas[i].setter = COLUMN_METAS[i].setter;
			metas[i].comparator = COLUMN_METAS[i].comparator;
		}
		exTable.header(metas);
	}
	
	
	$(function(){
		// テーブル生成
		exTable = new ExTable(_TICKET_TRAY);
		load(exTable);
		exTable.data([]);
		$(_TICKET_TRAY).live("columnmove",save).live("columnresize",save);
		bindMove();
	})

	

})(TicketTray);
