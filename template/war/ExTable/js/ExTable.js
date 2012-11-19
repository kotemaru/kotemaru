/* Copyright 2012 kotemaru.org. (http://www.apache.org/licenses/LICENSE-2.0) */

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
	exTable.header([
		//カラム名,  カラム幅(初期値),カラム順, カラムstyle
		{title:"番号",  width:36,  seq:1, style:{"text-align":"right"}},
		{title:"名前",  width:100, seq:2},
		{title:"住所",  width:500, seq:3},
	]);
	exTable.data([
		[ 123,"山田 太郎","徳島県徳島市万代町1丁目1番地"],
		[ 124,"鈴木 花子","香川県高松市番町4-1-10"],
		[5567,"田中 一朗","愛媛県松山市一番町４丁目４－２"],
	]);

 *
 * - $(e).exTable().header([...]).data([...]);
 * - $(e).exTable().update(no);
 * - $(e).exTable().sort(no,desc);
 * - $(e).exTable().updateHeader();
 *
 *
 */
function ExTable(){this.initialize.apply(this, arguments)};
(function(Class){
	var ExTable        = "ExTable";
	var ExTableColumn  = "ExTableColumn";
	var ExTableColumn_ = "ExTableColumn_";
	var ExTableBody    = "ExTableBody";
	var ExTableRow     = "ExTableRow";
	var ExTableHeader  = "ExTableHeader";
	var ExTableHeaderLabel  = "ExTableHeaderLabel";
	var ExTableHandle  = "ExTableHandle";
	var ExTableHeaderSortDesc = "ExTableHeaderSortDesc";
	var ExTableHeaderSortAsc = "ExTableHeaderSortAsc";

	var _ExTable        = "."+ExTable;
	var _ExTableColumn  = "."+ExTableColumn;
	var _ExTableColumn_ = "."+ExTableColumn_;
	var _ExTableBody    = "."+ExTableBody;
	var _ExTableRow     = "."+ExTableRow;
	var _ExTableHeader  = "."+ExTableHeader;
	var _ExTableHandle  = "."+ExTableHandle;
	var _ExTableHeaderLabel  = "."+ExTableHeaderLabel;
	//var _ExTableHeaderSortDesc = "."+ExTableHeaderSortDesc;
	//var _ExTableHeaderSortAsc  = "."+ExTableHeaderSortAsc;

	var COLUMN_IDX  = "columnIdx";

	var TEMPL_ROOT = "<div class='"+ExTableHeader+"'></div><div class='"+ExTableBody+"'></div>";
	var $TEMPL_ROW = $("<div class='"+ExTableRow+"'></div>");
	var $TEMPL_COL = $("<span class='"+ExTableColumn+"'></span>");
	//var $TEMPL_COL = $("<pre class='"+ExTableColumn+"'></pre>");
	var $TEMPL_HEADER_COL = $(
		"<span class='"+ExTableColumn+"'>"
			+"<span class='"+ExTableHeaderLabel+"'><span></span></span>"
			+"<span class='"+ExTableHandle+"'>&nbsp;</span>"
		+"</span>"
	);



	/**
	 * コンストラクタ。
	 * $param selector トップノードのセレクタ。
	 */
	Class.prototype.initialize = function(selector) {
		this.rootSelector = selector;
		this.sortInfo = null;
		this.masterData = null;
		this.viewRows = null;
		this.masterRows = null;
		this.useVariableHeight = false;
		this.setRowHeight(20);
		$(selector).data(ExTable, this);
	}

	/**
	 * カラム情報設定。
	 * - 以下の書式の配列
	 * - {title:"カラム名", width:カラム幅, seq:カラム表示順, style:{カラムのstyle}}
	 * - seqとstyleはオプション。
	 * - カラム表示順が変わってもデータの順版は変わらない。
	 * $param meta カラム情報データ。
	 */
	Class.prototype.header = function(meta) {
		this.columnMeta = meta;
		for (var i=0; i<meta.length; i++) {
			if (meta[i].seq == null) meta[i].seq = i;
			if (meta[i].style == null) meta[i].style = {};
			if (meta[i].style.height == "auto") this.useVariableHeight = true;
		}
		return this;
	}

	/**
	 * カラム情報取得。
	 */
	Class.prototype.getColumnMeta = function() {
		return this.columnMeta;
	}

	/**
	 * データ設定。
	 * $param data ２次元配列データ。
	 */
	Class.prototype.data = function(data) {
		this.masterData = data;
		this.build();
		var self = this;
		if (this.useVariableHeight) {
			setTimeout(function(){self.refreshRowHight();}, 1);
		}
		return this;
	}


	/**
	 * ソート条件設定。
	 * - ソートするとデータの順序も変わる。
	 * @param idx ソートするカラムの位置(0はじまり)
	 * @param desc true=逆順
	 */
	Class.prototype.sort = function(idx, desc) {
		this.sortInfo = {index:idx, desc:desc};
		this.refreshHeader();
		this.refreshBody();
		return this;
	}


	/**
	 * ソート条件設定。
	 * - 既に選択されている場合は逆順／正順入れ換え。
	 * @param idx ソートするカラムの位置(0はじまり)
	 */
	Class.prototype.toggleSort = function(idx) {
		if (this.sortInfo && this.sortInfo.index==idx) {
			this.sort(idx, !this.sortInfo.desc);
		} else {
			this.sort(idx);
		}
		return this;
	}

	/**
	 * 行の更新
	 */
	Class.prototype.update = function(rowNo, data) {
		var pair = this.masterRows[rowNo];
		if (data != null) {
			this.masterData[rowNo] = data;
			pair.data = data;
		}
		refreshRow($(pair.elem), pair.data, this.columnMeta);
	}

	/**
	 * 行の挿入
	 */
	Class.prototype.insert = function(rowNo, data) {
		var $row = buildRow(data, this.columnMeta);
		var pair = {elem:$row[0], data:data}
		this.masterData.splice(rowNo, 0, data);
		this.masterRows.splice(rowNo, 0, pair);
		this.viewRows.splice(rowNo, 0, pair);
		this.refreshBody();
	}
	/**
	 * 行の追加
	 */
	Class.prototype.append = function(data) {
		var $row = buildRow(data, this.columnMeta);
		var pair = {elem:$row[0], data:data}
		this.masterData.push(data);
		this.masterRows.push(pair);
		this.viewRows.push(pair);
		this.refreshBody();
	}

	/**
	 * 行の更新
	 */
	Class.prototype.remove = function(rowNo) {
		var pair = this.masterRows[rowNo];
		$(this.rootSelector).find(_ExTableBody)[0].removeChild(pair.elem);

		this.masterData.splice(rowNo, 1);
		this.masterRows.splice(rowNo, 1);
		this.viewRows = this.masterRows.concat();
		this.refreshBody();
	}


	/**
	 * ヘッダー更新
	 */
	Class.prototype.updateHeader = function() {
		this.refreshHeader();
	}

	/**
	 * ヘッダの高さ設定。
	 * @param h ヘッダの高さ
	 */
	Class.prototype.setHeaderHeight = function(h) {
		var base = this.rootSelector+" ";
		var hpx = h+"px";
		setCssRule(this.rootSelector, {paddingTop:hpx});
		setCssRule(base+_ExTableHeader, {height:hpx, marginTop:-h+"px"});
		this.refreshHeader();
		return this;
	}

	/**
	 * 行の高さ設定。
	 * - 個別設定は出来ない。
	 * @param h 行の高さ
	 */
	Class.prototype.setRowHeight = function(h) {
		this.rowHeight = h;
		var base = this.rootSelector+" ";
		setCssRule(base+_ExTableRow, {"min-height": h+"px", "height": h+"px"});
		return this;
	}



	//---------------------------------------------------------
	/**
	 * ヘッダの再描画。
	 * @param $header ヘッダ行要素
	 * @param columnMeta カラム情報
	 */
	Class.prototype.refreshHeader = function() {
		var $root = $(this.rootSelector);
		var $header = $root.find(_ExTableHeader);
		var columnMeta = this.columnMeta;

		var hasMax = 0;
		var lefts = [];
		for (var i=0; i<columnMeta.length; i++) {
			var cmeta = columnMeta[i];
			lefts.push({idx:i, seq:cmeta.seq, width:cmeta.width, left:0});
			if (typeof cmeta.width == "string") hasMax++;
		}

		if (hasMax > 0) {
			var w = $root.find(_ExTableBody)[0].clientWidth;
			for (var i=0; i<lefts.length; i++) {
				if (typeof lefts[i].width == "number") w = w -lefts[i].width;
			}
			for (var i=0; i<lefts.length; i++) {
				if (typeof lefts[i].width == "string") {
					lefts[i].width = (w*parseInt(lefts[i].width)/100);
				}
			}
		}


		lefts.sort(function(a,b){return a.seq-b.seq;});
		var left = 0;
		for (var i=0; i<columnMeta.length; i++) {
			lefts[i].left = left;
			left += lefts[i].width;
		}
		lefts.sort(function(a,b){return a.idx-b.idx;});

		var sortInfo = this.sortInfo;
		var rootSelector = this.rootSelector;
		$header.find(_ExTableColumn).each(function(){
			var $col = $(this);
			var idx = $col.data(COLUMN_IDX);
			var cmeta = columnMeta[idx];
			var $label = $col.find(_ExTableHeaderLabel+">span");

			$label.text(cmeta.title);
			$col.removeClass(ExTableHeaderSortDesc);
			$col.removeClass(ExTableHeaderSortAsc);
			if (sortInfo && sortInfo.index==idx) {
				$col.addClass(sortInfo.desc?ExTableHeaderSortDesc:ExTableHeaderSortAsc);
			}

			// Bodyのみに適用
			setCssRule(_ExTableRow+" "+_ExTableColumn_+idx, cmeta.style);

			// HeaderとBodyに適用
			var selector = rootSelector+" "+_ExTableColumn_+idx;
			var style = {
				width: lefts[idx].width+"px",
				left: lefts[idx].left+"px",
				paddingLeft: null,
				paddingRight: null
			};
			// 文字半分残るのでその対策。
			if (cmeta.width<=4) {
				style.width = "0px";
				style.paddingLeft = "4px";
				style.paddingRight = "0px";
			}
			setCssRule(selector, style);
		});

		return $header;
	}




	/**
	 *
	 * -
	 */
	Class.prototype.refreshBody = function() {

		var $body = $(this.rootSelector+" "+_ExTableBody);
		var rows = sortRow(this.viewRows, this.sortInfo);

		var body = $body[0];
		for (var i=0; i<rows.length; i++) {
			body.appendChild(rows[i].elem);
		}
	}

	/**
	 * 行のソート。
	 * @param rows 行データ
	 * @param cond ソート条件
	 */
	function sortRow(rows, cond) {
		if (cond == null) return rows;

		var idx = cond.index;
		var type = typeof rows[0].data[idx];

		function defaultComp(a,b) {
			var A=a.data[idx], B=b.data[idx]
			return A==B?0:(A>B?1:-1);
		};
		var comp = defaultComp;
		if (type == "number") {
			comp = function(a,b){
				return a.data[idx]-b.data[idx];
			}
		}
		if (cond.desc) {
			var orgComp = comp;
			comp = function(a,b){return orgComp(b,a);}
		}

		return rows.sort(comp);
	}

	/**
	 * 行の再描画。
	 * @param $row 行要素
	 * @param rowData 行データ
	 * @param columnMeta カラム情報
	 */
	function refreshRow($row, rowData, columnMeta) {
		$row.find(_ExTableColumn).each(function(){
			var $col = $(this);
			var idx = $col.data(COLUMN_IDX);
			var setter = columnMeta[idx].setter;
			if (setter) {
				setter($col,rowData,idx);
			} else {
				$col.text(rowData[idx]);
			}
		});
		return $row;
	}

	/**
	 * 行の高さ再設定。
	 */
	Class.prototype.refreshRowHight = function() {
		var self = this;
		var $root = $(this.rootSelector);
		$root.find(_ExTableRow).each(function(){
			var $row = $(this);
			var orgh = $row.height();
			var rowh = self.rowHeight;
			//if (orgh > self.rowHeight) console.log("a",orgh,rowh);
			if (orgh > self.rowHeight) $row.height(rowh);
			$row.find(_ExTableColumn).each(function(){
				//var h = $(this).attr("offsetHeight");
				var h = this.offsetHeight;
				rowh = h>rowh?h:rowh;
			});
			//console.log("b",orgh,rowh);
			if (orgh != rowh || orgh > self.rowHeight) $row.height(rowh);
		})
		return this;
	}


	//-----------------
	/**
	 * テーブル要素の生成。
	 */
	Class.prototype.build = function() {
		var $root = $(this.rootSelector);
		$root.html(TEMPL_ROOT);

		buildHeader($root.find(_ExTableHeader), this.columnMeta);
		this.refreshHeader();

		this.viewRows = [];
		this.masterRows = [];
		var $body = $root.find(_ExTableBody);
		for (var i=0; i<this.masterData.length; i++) {
			var $row = buildRow(this.masterData[i], this.columnMeta);
			$body.append($row);
			var rowPair = {elem:$row[0], data:this.masterData[i]}
			this.viewRows.push(rowPair);
			this.masterRows.push(rowPair);
		}

		return this;
	}


	/**
	 * 行要素の生成。
	 */
	function buildRow(rowData, columnMeta) {
		var $row = $TEMPL_ROW.clone();
		for (var i=0; i<rowData.length; i++) {
			var $col = $TEMPL_COL.clone();
			$col.addClass(ExTableColumn_+i);
			$col.data(COLUMN_IDX, i);
			$row.append($col);
		}
		refreshRow($row, rowData, columnMeta);
		return $row;
	}

	/**
	 * ヘッダ行要素の生成。
	 */
	function buildHeader($header, columnMeta) {
		for (var i=0; i<columnMeta.length; i++) {
			var $col = $TEMPL_HEADER_COL.clone();
			//$col.find("span:first-child");
			$col.addClass(ExTableColumn_+i);
			$col.data(COLUMN_IDX, i);
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
			if (rules == null) rules = sheets[i].rules; // ForIE
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
			sheet.addRule(selector,"dummy:dummy");//forIE
		}
		return getCssRule(selector);
	}
	function setCssRule(selector, style) {
		var rule = getCssRuleWithDefine(selector);
		if (rule == null) return;
		for (var k in style) rule.style[k] = style[k];
	}
	function setCssRuleImportant(selector, style) {
		var rule = getCssRuleWithDefine(selector);
		if (rule == null) return;
		for (var k in style) rule.style.setProperty(k, style[k], 'important');
	}

	//----------------------------------------------------------------------------


	/**
	 * カラムのリサイズハンドラ設定。
	 */
	function bindResize() {
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

			var idx = $col.data(COLUMN_IDX);
			var exTable = $col.parents(_ExTable).data(ExTable);
			var metas = exTable.getColumnMeta();
			if (metas[idx].fixed) return; // 固定

			metas[idx].width = w;
			exTable.updateHeader();
			//exTable.refreshRowHight();
		}).live("mouseup", function(){
			if (handle) {
				var exTable = $(handle).parents(_ExTable).data(ExTable);
				waiting(function(){
					exTable.refreshRowHight();
				});
			}
			handle = null;
		});
	}

	function waiting(callback) {
		setTimeout(function(){
			callback();
		}, 1);
	}



	/**
	 * カラムの移動ハンドラ設定。
	 */
	function bindMove() {
		// Move
		var handle = null;
		var lastChanged = null;
		var onClick = false;
		$(_ExTableHeader+">"+_ExTableColumn).live("mousedown", function(){
			handle = this;
			$(handle).css({cursor: "url(img/cursor-move-box-LR.png) 8 8, col-resize"});
			onClick = true;
			return false;
		}).live("mousemove",function(ev){
			if (handle == null) return;
			if (handle == this) {
				lastChanged = null;
				return;
			}
			if (lastChanged == this) return;
			lastChanged = this;

			var $handle = $(handle);
			var $target = $(this);
			var hIdx = $handle.data(COLUMN_IDX);
			var tIdx = $target.data(COLUMN_IDX);

			var exTable = $handle.parents(_ExTable).data(ExTable);
			var metas = exTable.getColumnMeta();
			if (metas[tIdx].fixed) return; // 固定

			var tmp = metas[hIdx].seq;
			metas[hIdx].seq = metas[tIdx].seq;
			metas[tIdx].seq = tmp;
			exTable.refreshHeader();

			onClick = false;
		}).live("mouseup", function(){
			if (!onClick) return;
			/**
			 * カラムクリックでソート設定。
			 */
			var col = this;
			var $col = $(this);
			var exTable = $col.parents(_ExTable).data(ExTable);
			var idx = $col.data(COLUMN_IDX);

			var backup = col.style.cursor;
			col.style.cursor = "wait";
			setTimeout(function(){
				exTable.toggleSort(idx);
				col.style.cursor = backup;
			},1);
		});

		$(document.body).live("mouseup",function(ev){
			$(handle).css({cursor: "pointer"});
			handle = null;
			lastChanged = null;
		});
	}

	/**
	 * クラス初期化処理。
	 * - 各種イベントハンドラ設定。
	 */
	$(function(){
		bindResize();
		bindMove();

		$(window).resize(function(){
			$(_ExTable).each(function(){
				var exTable = $(this).data(ExTable);
				exTable.refreshHeader();
				exTable.refreshRowHight();
			})
		});
	});


})(ExTable);


