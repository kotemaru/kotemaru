
//@Singleton
function TicketPool(){this.initialize.apply(this, arguments)};
(function(Class){
	var pool = {};
	Class.put = function(issue) {
		pool[issue.id] = issue;
	}
	Class.get = function(num) {
		return pool[num];
	}
	Class.getWithSave = function(num) {
		Storage.put("issue/"+num, pool[num]);
		return pool[num];
	}
	Class.checked = function(num) {
		pool[num].checked = new Date().getTime();
		Storage.put("issue/"+num, pool[num]);
	}
	Class.isChecked = function(num) {
		return pool[num].checked > Date.parse(pool[num].updated_on);
	}
	Class.removeFromStorage = function(num) {
		Storage.remove("issue/"+num);
	}

	function load() {
		Storage.each("issue/",function(name, data){
			var num = parseInt(name.substr(6));
			pool[num] = data;
		});
	}
	
	$(function(){
		load();
	});
	

})(TicketPool);
