

function Store(){this.initialize.apply(this, arguments)};
(function(_class){
	var coorRef = new Referer();	
	var itemRef = new Referer();	
	
	_class.prototype.initialize = function() {}

	_class.save = function(itemsObj) {
		coorRef.reset();
		itemRef.reset();
		var items = itemsObj.getItems(); //Note:速度優先。 
		for (var i in items) toJsonRef(items[i]);
		var data = {
			version: Version.CURRENT,
			canvas: Canvas.getAttributes(),
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

	_class.copy = function(itemsObj) {
		return _class.save(itemsObj);
	}
	
	_class.paste = function(data, ex,ey) {
		coorRef.reset();
		itemRef.reset();
		coorRef.preLoad(data.coors);
		itemRef.preLoad(data.items);
		coorRef.load(fromJson, data.coors);
		itemRef.load(fromJson, data.items);

		var selectGroup;
		for (var i in itemRef.objs) {
			var item = itemRef.objs[i];
			if (item instanceof SelectGroup) selectGroup = item;
			Canvas.addItem(item);
		}
		if (selectGroup) {
			selectGroup.xy(ex,ey);
			selectGroup.clear();
			Canvas.delItem(selectGroup);
		}
	}
	

	
	function toJsonRef(obj) {
		if (obj == null) {
			return null;
		} else if (obj.isCoor) {
			return {coorRef: toJsonWrap(coorRef,obj).id};
		} else if (obj.isRemove) {
			var xy = {x:obj.x(), y:obj.y()};
			var coor = new Coor(xy);
			return {coorRef: toJsonWrap(coorRef,coor).id};
		} else if (obj.isGroup) {
			return {groupRef: toJsonWrap(itemRef,obj).id};
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
			var type = attrs[k].type;
			if (type == "string" || type == "number") {
				json[k] = obj[k];
			} else if (type == "Point") {
				json[k] = toJsonRef(obj[k]);
			} else if (type == "Group") {
				json[k] = toJsonRef(obj[k]);
			} else if (type == "Point[]") {
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
		} else if (json.groupRef) {
			return itemRef.getObj(json.groupRef);
		} else {
			return itemRef.getObj(json.itemRef);
		}
	}
	
	function fromJson(obj, json) {
		var attrs = getAttibutes(obj._class);

		for (var k in attrs) {
			var type = attrs[k].type;
			if (type == "string" || type == "number") {
				obj[k] = json[k];
			} else if (type == "Point") {
				obj[k] = fromJsonRef(json[k]);
			} else if (type == "Group") {
				var group = fromJsonRef(json[k]);
				obj[k] = group;
				if (group) group.getItems().addItem(obj);
			} else if (type == "Point[]") {
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
