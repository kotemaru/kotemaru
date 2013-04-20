

function Store(){this.initialize.apply(this, arguments)};
(function(_class){
	var coorRef = new Referer();	
	var itemRef = new Referer();	
	
	_class.prototype.initialize = function() {}

	_class.save = function(items) {
		coorRef.reset();
		itemRef.reset();
		for (var i in items) toJsonRef(items[i]);
		var data = {
			coors: coorRef.getJsons(),
			items: itemRef.getJsons(),
		};
		return data;
	}
	

	_class.load = function(data) {
		coorRef.reset();
		itemRef.reset();
		coorRef.preLoad(data.coors);
		itemRef.preLoad(data.items);
		coorRef.load(fromJson, data.coors);
		itemRef.load(fromJson, data.items);
		
		Canvas.reset();
		for (var i in itemRef.objs) {
			Canvas.addItem(itemRef.objs[i]);
		}
		Canvas.refresh();
	}

	
	
	function toJsonRef(obj) {
		if (obj == null) {
			return null;
		} else if (obj.isCoor) {
			return {coorRef: toJsonWrap(coorRef,obj).id};
		} else {
			return {itemRef: toJsonWrap(itemRef,obj).id};
		}
	}
	function toJsonWrap(refer, obj) {
		var json = refer.getJson(obj.internalId);
		if (json == null) {
			json = toJson(obj);
			refer.putJson(obj.internalId, json);
		}
		return json;
	}
	
	function toJson(obj) {
		var attrs = getAttibutes(obj._class);
		var json = {};
		json._class = obj._class.name;
		
		for (var k in attrs) {
			var type = typeof attrs[k];
			if (type == "string" || type == "number") {
				json[k] = obj[k];
			} else if (attrs[k] instanceof Point) {
				json[k] = toJsonRef(obj[k]);
			} else if (attrs[k] instanceof Array) {
				var ary = [];
				for (var i=0; i<obj[k].length; i++) {
					ary.push(toJsonRef(obj[k][i]));
				}
				json[k] = ary;
			}
		}
		return json;
	}
	function getAttibutes(__class) {
		if (__class == undefined) return {};
		if (__class.attributes) return __class.attributes;
		return getAttibutes(__class._super);
	}
	
	function fromJsonRef(json) {
		if (json == null) {
			return null;
		} else if (json.coorRef) {
			return coorRef.getObj(json.coorRef);
		} else {
			return itemRef.getObj(json.itemRef);
		}
	}
	
	function fromJson(obj, json) {
		var attrs = getAttibutes(obj._class);

		for (var k in attrs) {
			var type = typeof attrs[k];
			if (type == "string" || type == "number") {
				obj[k] = json[k];
			} else if (attrs[k] instanceof Point) {
				obj[k] = fromJsonRef(json[k]);
			} else if (attrs[k] instanceof Array) {
				var ary = [];
				for (var i=0; i<json[k].length; i++) {
					ary.push(fromJsonRef(json[k][i]));
				}
				obj[k] = ary;
			}
		}
		return obj;
	}
	
	
	
})(Store);
