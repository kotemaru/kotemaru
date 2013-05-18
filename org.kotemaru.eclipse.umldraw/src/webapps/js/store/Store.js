

function Store(){this.initialize.apply(this, arguments)};
(function(_class){
	var coorRef = new Referer();	
	var itemRef = new Referer();	
	
	var isExportMode = false;
	
	_class.prototype.initialize = function() {}

	_class.save = function(itemsObj) {
		Canvas.clearSelect();
		Canvas.getSelectGroup().clear();
		Canvas.refresh();
		isExportMode = false;
		return save(itemsObj);
	}
	_class.copy = function(itemsObj, exportMode) {
		isExportMode = exportMode;
		return save(itemsObj);
	}
	function save(itemsObj) {
		coorRef.reset();
		itemRef.reset();
		var items = itemsObj.getItems(); //Note:速度優先。 
		for (var i in items) toJsonRef(items[i]);
		var data = {
			version: Version.CURRENT,
			canvas: Canvas.getProperties(),
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
			if (item.isSelectGroup) {
				selectGroup = item;
			} else {
				Canvas.addItem(item);
			}
		}
		if (selectGroup) {
			selectGroup.xy(ex,ey);
			selectGroup.clear();
			//Canvas.delItem(selectGroup);
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
		} else if (!isExportMode) {
			return {itemRef: toJsonWrap(itemRef,obj).id};
		} else { // exportMode
			if (getSelectGroup(obj)) {
				return {itemRef: toJsonWrap(itemRef,obj).id};
			} else {
				var xy = {x:obj.x(), y:obj.y()};
				var coor = new Coor(xy);
				return {coorRef: toJsonWrap(coorRef,coor).id};
			}
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
	function getSelectGroup(obj) {
		if (obj.group == null) return null;
		var g = obj.group;
		while (g.group != null) {
			g = g.group;
		}
		if (g.isSelectGroup) return g;
		return null;
	}
	
	
	function toJson(obj) {
		var attrs = getAttibutes(obj._class);
		var json = {};
		json._class = obj._class.name;
		if (json._class == null) {
			throw "Not find class name:"+obj.__class;
		}
		
		
		for (var k in attrs) {
			var type = attrs[k].type;
			if (type == "string" || type == "number" || type == "boolean") {
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
		if (__class.properties) return __class.properties;
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
			if (type == "string" || type == "number" || type == "boolean") {
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
