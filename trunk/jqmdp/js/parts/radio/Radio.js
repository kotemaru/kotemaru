/**
 * Radio/Selector Plugin.
 * Todo: document.
 * Ex.
<xmp>
			Radio:
			<span data-dp-scope="(new Radio($this))" >
				<a data-dp-val="a" data-dp-checked>a</a>
				<a data-dp-val="b">b</a>
				<a data-dp-val="c">c</a>
			</span>
			
			Selector:
			<span data-dp-scope="(new Radio($this,{multi:true}))" >
				<a data-dp-val="a">a</a>
				<a data-dp-val="b">b</a>
				<a data-dp-val="c" data-dp-checked>c</a>
				<a data-dp-val="d" data-dp-checked>d</a>
				<a data-dp-val="e">e</a>
			</span>
</xmp>
 
  */
(function(){
	var Package = window;
	var Class = function Radio(){this.initialize.apply(this, arguments)};
	var This = Class.prototype;
	Package[Class.name] = Class;

	var DP_VALUE = "data-dp-val";
	var XP_DP_VALUE = "a["+DP_VALUE+"]";
	var XP_DP_CHECKED = "a[data-dp-checked]";
	var OPTS = {
		multi: false,
		callback: null
	};
	
	This.initialize = function($this, opts) {
		this.$this = $this;
		this.values = [];
		this.opts = $.extend({}, OPTS, opts);

		$this.attr({
			"data-role":"controlgroup", 
			"data-type":"horizontal"
		});

		var _this = this;
		$this.find(XP_DP_VALUE).attr({
			href:"#", "data-role":"button",
			"data-dp-active":"(isActive($this))", "data-dp-active-class":"(['ui-btn-active'])", 
			onclick: "$(this).jqmdp().scope().onChange(this)"
		});
		// Note: bind/live in this timing becomes invalid. Move to onclick attribute.
		//this.$this.find(XP_DP_VALUE).live('click', function(ev){
		//	_this.setValue($(ev.currentTarget).attr(DP_VALUE));
		//});

		if (this.opts.multi) {
			$this.find(XP_DP_CHECKED).each(function(){
				_this.values.push($(this).attr(DP_VALUE));
			});
		} else {
			this.values = [$this.find(XP_DP_VALUE).attr(DP_VALUE)];
			$this.find(XP_DP_CHECKED).each(function(){
				_this.values[0] = ($(this).attr(DP_VALUE));
			});
		}

		// markup() is necessary to apply added JQM attribute.
		$.jqmdp.markup($this);
	}

	This.onChange = function(a) {
		var v = $(a).attr(DP_VALUE);
		this.opts.multi ? this.toggleValue(v) : this.setValue(v);
	}

	This.isActive = function($a){
		var v = $a.attr(DP_VALUE);
		return (this.values.indexOf(v)>=0);
	}

	This.setValue = function(v) {
		this.values[0] = v;
		if (this.opts.callback) this.opts.callback.call(this, v);
		$.jqmdp.refresh(this.$this);
	}
	
	This.toggleValue = function(v) {
		var idx = this.values.indexOf(v);
		(idx >= 0) ? this.values.splice(idx,1) : this.values.push(v);
		if (this.opts.callback) this.opts.callback.call(this, v);
		$.jqmdp.refresh(this.$this);
	}
	
	This.setValues = function(vs) {
		this.values = vs;
		if (this.opts.callback) this.opts.callback.call(this, vs);
		$.jqmdp.refresh(this.$this);
	}
	
	This.getValue = function() {
		return this.values[0];
	}
	This.getValues = function() {
		return this.values;
	}
})();
