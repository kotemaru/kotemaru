

function Ticket(){this.initialize.apply(this, arguments)};
(function(Class){
	var C_TICKET = ".Ticket";
	Class.prototype.initialize = function() {
	}
	Class.init = function() {
	}

	var ISSUE_TEMPL = {id:9999, subject:"", assigned_to:{},
			due_date:"", updated_on:"",done_ratio:100, checked_on:"",
			folder:"", tracker:"", priority:""
	};
	var issues = {};

	Class.register = function(data){
		var issue = issues[data.id];
		if (issue == null) issue = {};

		//for (var k in ISSUE_TEMPL) {
		//	if (data[k] != undefined) issue[k] = data[k];
		//}
		issue = data;
		
		issues[issue.id] = issue;
		if (issue.folder) {
			Storage.saveTicket(issue);
		} else {
			Storage.removeTicket(issue);
		}
		return issue;
	}

	Class.issue = function(num) {
		return issues[num];
	}

	Class.checked = function(num) {
		var issue = Ticket.issue(num);
		if (issue != null) {
			issue.checked_on = new Date().toString();
			Storage.saveTicket(issue);
		}
	}



	Class.isChecked = function(num) {
		var issue = issues[num];
		if (issue == null) return false;
		return isChecked(issue);
	}
	function isChecked(issue) {
		return Date.parse(issue.checked_on) > Date.parse(issue.updated_on);
	}

	Class.makeArticle = function(issue, $template) {
		var $article = $template.clone();
		$article.attr("id","T"+issue.id);
		$article.data("ticketNum",issue.id);
		$article.attr("data-ticket-num",issue.id);
		$article.find("nobr[data-field='num']").text(issue.id);

		$article.find("nobr[data-field='tracker']").html(
				issue.tracker?issue.tracker.name:"&nbsp;"
		);

		$article.find("nobr[data-field='charge']").html(
			issue.assigned_to?issue.assigned_to.name:"&nbsp;"
		);
		$article.find("nobr[data-field='state']>div>div")
			.width((22*issue.done_ratio/100));
		$article.find("nobr[data-field='limitDate']").html(toYYMMDD(issue.due_date));
		$article.find("nobr[data-field='updateDate']").html(toYYMMDD(issue.updated_on));
		$article.find("nobr[data-field='subject']").text(issue.subject);

		$article.css("font-weight", isChecked(issue)?"normal":"bold");

		return $article;
	}


	Class.cleaning = function() {
		for (var num in issues) {
			var issue = issues[num];
			if (issue.done_ratio == 100 && issue.folder) {
				Folder.remove(issue.folder, issue);
				Folder.register("trash", issue);
			}
		}

		Folder.refresh();
		//Folder.select("trash");
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


})(Ticket);
