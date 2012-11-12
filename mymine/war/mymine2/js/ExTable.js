/**
 * ExTable v1.0
 * 拡張テーブル。
 *
 * - ２次元配列のデータをテーブル表示する。
 * - 固定ヘッダをサポートする。
 * - カラムのサイズ変更をサポートする。
 * - カラムの順番入れ換えをサポートする。
 * - カラム毎のソートをサポートする。
 *
 * 使用例：
	var exTable = new ExTable("#testTable");
	exTable.setColumnInfo([
		//カラム名,  カラム幅(初期値),カラム順, カラムstyle
		{title:"番号",  width:36,  seq:1, style:{"text-align":"right"}},
		{title:"名前",  width:100, seq:2},
		{title:"住所",  width:500, seq:3},
	]);
	exTable.setData([
		[ 123,"山田 太郎","徳島県徳島市万代町1丁目1番地"],
		[ 124,"鈴木 花子","香川県高松市番町4-1-10"],
		[5567,"田中 一朗","愛媛県松山市一番町４丁目４－２"],
	]);
	exTable.refresh(true);
 */
function ExTable(){this.initialize.apply(this, arguments)};
(function(Class){
	var ExTable        = "ExTable";
	var ExTableColumn  = "ExTableColumn";
	var ExTableColumn_ = "ExTableColumn_";
	var ExTableBody    = "ExTableBody";
	var ExTableRow     = "ExTableRow";
	var ExTableHeader  = "ExTableHeader";
	var ExTableHandle  = "ExTableHandle";

	var _ExTable        = "."+ExTable;
	var _ExTableColumn  = "."+ExTableColumn;
	var _ExTableColumn_ = "."+ExTableColumn_;
	var _ExTableBody    = "."+ExTableBody;
	var _ExTableRow     = "."+ExTableRow;
	var _ExTableHeader  = "."+ExTableHeader;
	var _ExTableHandle  = "."+ExTableHandle;

	var TEMPL_ROOT = "<div class='"+ExTableHeader+"'></div><div class='"+ExTableBody+"'></div>";
	var $TEMPL_ROW = $("<div class='"+ExTableRow+"'></div>");
	var $TEMPL_COL = $("<span class='"+ExTableColumn+"'></span>");
	var $TEMPL_HEADER_COL = $("<span class='"+ExTableColumn+"'><span></span>"
			+"<span class='"+ExTableHandle+"'>&nbsp;</span></span>");

	/**
	 * クラス初期化処理。
	 * - 各種イベントハンドラ設定。
	 */
	$(function(){
		initResize();
		initMove();

		/**
		 * カラムクリックでソート設定。
		 */
		$(_ExTableHeader+">"+_ExTableColumn).live("click", function(){
			var $col = $(this);
			var self = $col.parents(_ExTable).data(ExTable);
			var idx = $col.data("columnIdx");
			self.toggleSort(idx);
			self.refresh();
		});
	});

	/**
	 * カラムのリサイズハンドラ設定。
	 */
	function initResize() {
		// Resize
		var handle = null;
		$(_ExTableHandle).live("mousedown", function(){
			handle = this;
			return false;
		}).live("click",function(){
			return false;
		})

		$(document.body).live("mousemove",function(ev){
			if (handle == null) return;

			var $col = $(handle.parentNode);
			var offset = $col.offset();
			var w = (ev.clientX - offset.left);
			if (w<4) w=4;

			var idx = $col.data("columnIdx");
			var self = $col.parents(_ExTable).data(ExTable);
			var infos = self.getColumnInfo();
			infos[idx].width = w;
			self.refresh();
		}).live("mouseup", function(){
			handle = null;
		});
	}
	/**
	 * カラムの移動ハンドラ設定。
	 */
	function initMove() {
		// Move
		var handle = null;
		$(_ExTableHeader+">"+_ExTableColumn).live("mousedown", function(){
			handle = this;
			$(handle).css({cursor: "col-resize"});
			return false;
		}).live("mousemove",function(ev){
			if (handle == null) return;
			if (handle == this) return;

			var $handle = $(handle);
			var $target = $(this);
			var hIdx = $handle.data("columnIdx");
			var tIdx = $target.data("columnIdx");

			var self = $handle.parents(_ExTable).data(ExTable);
			var infos = self.getColumnInfo();

			var tmp = infos[hIdx].seq;
			infos[hIdx].seq = infos[tIdx].seq;
			infos[tIdx].seq = tmp;
			self.refresh();
		});
		$(document.body).live("mouseup",function(ev){
			$(handle).css({cursor: "pointer"});
			handle = null;
		});

	}


	/**
	 * コンストラクタ。
	 * $param selector トップノードのセレクタ。
	 */
	Class.prototype.initialize = function(selector) {
		this.rootSelector = selector;
		this.sortInfo = null;
		$(selector).data(ExTable, this);
	}

	/**
	 * データ設定。
	 * $param data ２次元配列データ。
	 */
	Class.prototype.setData = function(data) {
		this.data = data;
		return this;
	}

	/**
	 * カラム情報設定。
	 * - 以下の書式の配列
	 * - {title:"カラム名", width:カラム幅, seq:カラム表示順, style:{カラムのstyle}}
	 * - seqとstyleはオプション。
	 * - カラム表示順が変わってもデータの順版は変わらない。
	 * $param info カラム情報データ。
	 */
	Class.prototype.setColumnInfo = function(info) {
		this.columnInfo = info;
		for (var i=0; i<info.length; i++) {
			if (info[i].seq == null) info[i].seq = i;
			if (info[i].style == null) info[i].style = {};
		}
		return this;
	}

	/**
	 * カラム情報取得。
	 */
	Class.prototype.getColumnInfo = function(info) {
		return this.columnInfo;
	}
	/**
	 * ソート条件設定。
	 * - ソートするとデータの順序も変わる。
	 * @param idx ソートするカラムの位置(0はじまり)
	 * @param desc true=逆順
	 */
	Class.prototype.setSortInfo = function(idx, desc) {
		this.sortInfo = {index:idx, desc:desc};
	}


	/**
	 * ソート条件設定。
	 * - 既に選択されている場合は逆順／正順入れ換え。
	 * @param idx ソートするカラムの位置(0はじまり)
	 */
	Class.prototype.toggleSort = function(idx) {
		if (this.sortInfo && this.sortInfo.index==idx) {
			this.sortInfo.desc = !this.sortInfo.desc;
		} else {
			this.setSortInfo(idx);
		}
	}

	/**
	 * ヘッダの高さ設定。
	 * @param h ヘッダの高さ
	 */
	Class.prototype.setHeaderHeight = function(h) {
		var base = this.rootSelector+" ";
		var hpx = h+"px"
		setCssRule(this.rootSelector, {paddingTop:hpx});
		setCssRule(base+_ExTableHeader, {height:hpx, marginTop:-h+"px"});
	}

	/**
	 * 行の高さ設定。
	 * - 個別設定は出来ない。
	 * @param h 行の高さ
	 */
	Class.prototype.setRowHeight = function(h) {
		var base = this.rootSelector+" ";
		setCssRule(base+_ExTableRow, {height: h+"px"});
	}



	//---------------------------------------------------------
	/**
	 * 再描画。
	 * - 現在の設定でテーブルを再表示する。
	 * - カラム、行に増減が有る場合は deep を true にする必要が有る。
	 * @param deep true=要素を再構築する。
	 */
	Class.prototype.refresh = function(deep) {
		if (deep) this.build();

		var $root = $(this.rootSelector);
		this.refreshHeader($root.find(_ExTableHeader), this.columnInfo);

		sort(this.data, this.sortInfo);
		var self = this;
		var $body = $root.find(_ExTableBody);
		$body.find(_ExTableRow).each(function(){
			var $row = $(this);
			var idx = $row.data("rowIdx");
			refreshRow($row, self.data[idx]);
		});
		return this;
	}

	function sort(data, info) {
		if (info == null) return;

		var idx = info.index;
		var type = typeof data[0][idx];

		function defoltComp(a,b) {
			var A=a[idx], B=b[idx]
			return A==B?0:(A>B?1:-1);
		};
		var comp = defoltComp;
		if (type == "number") {
			comp = function(a,b){return a[idx]-b[idx];}
		}
		if (info.desc) {
			var orgComp = comp;
			comp = function(a,b){return orgComp(b,a);}
		}

		return data.sort(comp);
	}




	function refreshRow($row, rowData) {
		$row.find(_ExTableColumn).each(function(){
			var $col = $(this);
			var idx = $col.data("columnIdx");
			var col = rowData[idx];
			$col.text(col);
		});
		return $row;
	}
	Class.prototype.refreshHeader = function($header, columnInfo) {
		var lefts = [];
		for (var i=0; i<columnInfo.length; i++) {
			var cinfo = columnInfo[i];
			lefts.push({idx:i, seq:cinfo.seq, width:cinfo.width, left:0});
		}
		lefts.sort(function(a,b){return a.seq-b.seq;});
		var left = 0;
		for (var i=0; i<columnInfo.length; i++) {
			lefts[i].left = left;
			left += lefts[i].width;
		}
		lefts.sort(function(a,b){return a.idx-b.idx;});

		var self = this;
		$header.find(_ExTableColumn).each(function(){
			var $col = $(this);
			var idx = $col.data("columnIdx");
			var cinfo = columnInfo[idx];
			var $label = $col.find("span:first-child");

			$label.text(cinfo.title);
			$col.removeClass("ExTableHeaderSortDesc");
			$col.removeClass("ExTableHeaderSortAsc");
			if (self.sortInfo && self.sortInfo.index==idx) {
				$col.addClass(self.sortInfo.desc?"ExTableHeaderSortDesc":"ExTableHeaderSortAsc");
			}

			var selector = self.rootSelector+" "+_ExTableColumn_+idx;
			var style = $.extend({},cinfo.style, {
				width: cinfo.width+"px",
				left: lefts[idx].left+"px",
				visibility: (cinfo.width>4)?"visible":"hidden"
			});
			setCssRule(selector, style);

			var selector = self.rootSelector+" "+_ExTableHeader+" "+_ExTableColumn_+idx;
			var style = {visibility: "visible"};
			setCssRule(selector, style);
		});

		return $header;
	}

	//-----------------
	Class.prototype.build = function() {
		var $root = $(this.rootSelector);
		$root.html(TEMPL_ROOT);

		this.buildHeader($root.find(_ExTableHeader), this.columnInfo);

		var $body = $root.find(_ExTableBody);
		for (var i=0; i<this.data.length; i++) {
			var $row = buildRow(this.data[i]);
			$row.data("rowIdx", i);
			$body.append($row);
		}

		return this;
	}


	function buildRow(rowData) {
		var $row = $TEMPL_ROW.clone();
		for (var i=0; i<rowData.length; i++) {
			var $col = $TEMPL_COL.clone();
			$col.addClass(ExTableColumn_+i);
			$col.data("columnIdx", i);
			$row.append($col);
		}
		return $row;
	}
	Class.prototype.buildHeader = function($header, columnInfo) {
		for (var i=0; i<columnInfo.length; i++) {
			var $col = $TEMPL_HEADER_COL.clone();
			$col.find("span:first-child");
			$col.addClass(ExTableColumn_+i);
			$col.data("columnIdx", i);
			$header.append($col);

		}
		return $header;
	}


	//---------------------------------------------------

	var classCssRuleCache = {};
	function getCssRule(selector) {
		if (classCssRuleCache[selector]) return classCssRuleCache[selector];
		var sheets = document.styleSheets;
		for (var i=0; i<sheets.length; i++) {
			var rules = sheets[i].cssRules;
			for (var j=0; j<rules.length; j++) {
				if (selector == rules[j].selectorText) {
					classCssRuleCache[selector] = rules[j];
					return rules[j];
				}
			}
		}
		return null;
	}
	function getCssRuleWithDefine(selector) {
		var rule = getCssRule(selector);
		if (rule) return rule;

		var sheet = document.styleSheets[0];
		if (sheet.insertRule) {
			sheet.insertRule(selector+"{}", sheet.cssRules.length);
		} else {
			sheet.addRule(selector,"");//forIE
		}
		return getCssRule(selector);
	}
	function setCssRule(selector, style) {
		var rule = getCssRuleWithDefine(selector);
		if (rule == null) return;
		for (var k in style) rule.style[k] = style[k];
	}
})(ExTable);


