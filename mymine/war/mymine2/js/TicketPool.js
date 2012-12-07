
//@Singleton
function TicketPool(){this.initialize.apply(this, arguments)};
(function(Class){
	var pool = {};
	Class.put = function(issue) {
		if (pool[issue.id]) issue.checked = pool[issue.id].checked;
		pool[issue.id] = issue;
	}
	Class.putWithSave = function(issue) {
		var num = issue.id;
		Class.put(issue);
		Storage.put("issue/"+num, pool[num]);
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
	Class.update = function(nums) {
		var count = nums.length;
		var total = nums.length;
		for (var i=0; i<nums.length; i++) {
			RedMine.getIssue(nums[i], function(issue){
				Class.putWithSave(issue);
				if (--count <= 0) {
					MyMine.waiting(false);
					Folders.refresh();
				}
				MyMine.progress(100*(total-count)/total);
			});
		}
	}
	Class.updateAll = function() {
		var count = 0;
		var total = 0;
		for (var num in pool) {
			count++;
			total++;
			RedMine.getIssue(num, function(issue){
				Class.putWithSave(issue);
				if (--count <= 0) {
					MyMine.waiting(false);
					Folders.refresh();
				}
				MyMine.progress(100*(total-count)/total);
			});
		}
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
