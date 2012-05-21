


function Actors(){this.initialize.apply(this, arguments)};
(function(Class) {

	Class.prototype.initialize = function(view, opts, layer) {
		this.name = opts.name;
		this.$elem = view.$elem;
		this.list = {};
		this.idCount = 0;
		this.layer = layer;
	}
	
	Class.prototype.add = function(actor, x,y) {
		with (this) {
			var id = idCount++;
			actor.id = id;
			list[id] = actor;
			actor.appendTo($elem);
			actor.setPos(x,y);
		}
	}
	Class.prototype.remove = function(actor) {
		with (this) {
			actor.parge();
			delete list[actor.id];
		}
	}
	Class.prototype.get = function(x,y) {
		return null;
	}
	Class.prototype.save = function() {
		var data = [];
		for (var id in this.list) {
			var a = this.list[id];
			data.push({name:a.name, x:a.x, y:a.y});
		}
		return data;
	}
	Class.prototype.load = function(data) {
		for (var i=0; data.length; i++) {
			var a = data[i];
			Buttons.instance.getByName(a.name);
			this.add(button.copy(), a.x, a.y);
		}
	}
})(Actors);




