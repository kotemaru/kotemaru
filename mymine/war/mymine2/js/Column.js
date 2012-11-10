
function ColumnInfo(){this.initialize.apply(this, arguments)};
(function(Class){

	var saveData = {
		num     : {title:"番号", width:36,  seq:1},
		project : {title:"プロジェクト", width:80,  seq:2},
		tracker : {title:"トラッカー", width:70,  seq:3},
		priority: {title:"優先度", width:48,  seq:4},
		assigned: {title:"担当者", width:97,  seq:5},
		creator : {title:"作成者", width:97,  seq:6},
		status  : {title:"状態", width:28,  seq:7},
		upDate  : {title:"更新日", width:54,  seq:8},
		startDate:{title:"開始日", width:54,  seq:9},
		dueDate : {title:"終了日", width:54,  seq:10},
		subject : {title:"概要", width:1000,  seq:11},
	}
	var staticData = {
		num     : {selector:".TNum", 
			setter:function($e, issue){
				$e.attr("id","T"+issue.id);
				$e.data("ticketNum",issue.id);
				$e.find(this.selector).text(issue.id);
			},
			comparator:function(a,b){
				return(b.id-a.id);
			},
		},

		project : {selector:".TProject"},
		tracker : {selector:".TTracker"},
		priority: {selector:".TPriority"},
		assigned: {selector:".TAssigned"},
		creator : {selector:".TCreator"},
		status  : {selector:".TState"},
		upDate  : {selector:".TUpDate"},
		startDate:{selector:".TStartDate"},
		dueDate : {selector:".TDueDate"},
		subject : {selector:".TSubject"},
	}

	
})(ColumnInfo);


