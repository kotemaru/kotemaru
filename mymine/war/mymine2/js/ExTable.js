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
 * - データの改行をサポートする。(行毎に高さが可変)
 * - カスタムのカラム表示をサポートする。(コールバック関数)
 *
 * 使用例：
	var exTable = new ExTable("#testTable");
	exTable.header([
		//カラム名,  カラム幅(初期値), カラムstyle
		{title:"番号",  width:36,  style:{textAlign:"right"}},
		{title:"名前",  width:100 },
		{title:"住所",  width:"100%", style:{whiteSpace:"normal", height:"auto"}},
	]);
	exTable.data([
		[ 123,"山田 太郎","徳島県徳島市万代町1丁目1番地"],
		[ 124,"鈴木 花子","香川県高松市番町4-1-10"],
		[5567,"田中 一朗","愛媛県松山市一番町４丁目４－２"],
	]);

 * - 機能概要
 * -- exTable.header([{カラムメタ情報},…])   // カラムメタ情報設定
 * -- exTable.data(データ２次元配列);     // データ設定
 * -- exTable.updateHeader();          // ヘッダ情報更新通知(設定配列データの内容を変更した場合)
 * -- exTable.update(行番号);           // 行データ更新通知(設定配列データの内容を変更した場合)
 * -- exTable.update(行番号,行データ);   // 行データ変更
 * -- exTable.insert(行番号,行データ);   // 行挿入
 * -- exTable.append(行データ);         // 行追加
 * -- exTable.remove(行番号);           // 行削除
 * -- exTable.sort(カラムIdx, 順序フラグ);   // ソート
 * -- exTable.setHeaderHeight(高さ);   // ヘッダの高さ設定
 * -- exTable.setRowHeight(高さ);      // 行の高さ設定
 *
 * - カラムメタ情報
 * -- title(必須): カラムの名前
 * -- width(必須): カラムの幅。int=px, "nn%"=余白(レコード幅-固定カラム幅合計)に対する割合
 * -- seq(略可): カラム表示順。デフォルトは配列Index。
 * -- style(略可): カラム表示CSS。(ヘッダは影響しない)
 * -- fixed(略可): true=カラムのリサイズ、移動禁止。false=デフォルト。
 * -- setter(略可): カラム表示カスタム関数。
 * --- function($(カラム要素),行データ,カラムindex)
 */
function ExTable(){this.initialize.apply(this, arguments)};
(function(Class){
	var ExTable        = "ExTable";
	var ExTableColumn  = "ExTableColumn";
	var ExTableColumn_ = "ExTableColumn_";
	var ExTableBody    = "ExTableBody";
	var ExTableRow     = "ExTableRow";
	var ExTableHeader  = "ExTableHeader";
	var ExTableHeaderRow  = "ExTableHeaderRow";
	var ExTableHeaderLabel  = "ExTableHeaderLabel";
	var ExTableHeaderLabelInner  = "ExTableHeaderLabelInner";
	var ExTableHandle  = "ExTableHandle";
	var ExTableHeaderSortDesc = "ExTableHeaderSortDesc";
	var ExTableHeaderSortAsc = "ExTableHeaderSortAsc";

	var _ExTable        = "."+ExTable;
	var _ExTableColumn  = "."+ExTableColumn;
	var _ExTableColumn_ = "."+ExTableColumn_;
	var _ExTableBody    = "."+ExTableBody;
	var _ExTableRow     = "."+ExTableRow;
	var _ExTableHeader  = "."+ExTableHeader;
	var _ExTableHeaderRow  = "."+ExTableHeaderRow;
	var _ExTableHandle  = "."+ExTableHandle;
	var _ExTableHeaderLabel  = "."+ExTableHeaderLabel;
	//var _ExTableHeaderSortDesc = "."+ExTableHeaderSortDesc;
	//var _ExTableHeaderSortAsc  = "."+ExTableHeaderSortAsc;

	var COLUMN_IDX  = "columnIdx";

	var TEMPL_ROOT = 
		"<div class='"+ExTableHeader+"'><div class="+ExTableHeaderRow+"></div></div>"
		+"<div class='"+ExTableBody+"'></div>";
	var $TEMPL_ROW = $("<div class='"+ExTableRow+"'></div>");
	var $TEMPL_COL = $("<span class='"+ExTableColumn+"'></span>");
	var $TEMPL_HEADER_COL = $(
		"<span class='"+ExTableColumn+"'>"
			+"<span class='"+ExTableHeaderLabel+"'>"
				+"<span class='"+ExTableHeaderLabelInner+"'></span>"
			+"</span>"
			+"<span class='"+ExTableHandle+"'>&nbsp;</span>"
		+"</span>"
	);



	/**
	 * コンストラクタ。
	 * $param selector トップノードのセレクタ。
	 */
	Class.prototype.initialize = function(selector) {
		this.rootSelector = selector;
		this.sortInfo   = null;
		this.masterData = null;
		this.viewRows   = null;
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
	Class.prototype.header = function(metas) {
		this.columnMetas = metas;
		for (var i=0; i<metas.length; i++) {
			if (metas[i].seq == null) metas[i].seq = i;
			if (metas[i].style == null) metas[i].style = {};
			if (metas[i].style.height == "auto") this.useVariableHeight = true;
		}
		return this;
	}

	/**
	 * カラム情報取得。
	 */
	Class.prototype.getColumnMetas = function() {
		return this.columnMetas;
	}

	/**
	 * データ設定。
	 * $param data ２次元配列データ。
	 */
	Class.prototype.data = function(data) {
		this.masterData = data;
		this.build();
		this.refreshBody();
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
	Class.prototype.getSortInfo = function() {
		return this.sortInfo;
	}


	/**
	 * ソート条件設定。
	 * - 既に選択されている場合は昇順／降順入れ換え。
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
		refreshRow($(pair.elem), pair.data, this.columnMetas);
	}

	/**
	 * 行の挿入
	 */
	Class.prototype.insert = function(rowNo, data) {
		var $row = buildRow(data, this.columnMetas);
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
		var $row = buildRow(data, this.columnMetas);
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
	 * @param columnMetas カラム情報
	 */
	Class.prototype.refreshHeader = function() {
		var $root = $(this.rootSelector);
		var $header = $root.find(_ExTableHeader);
		var columnMetas = this.columnMetas;

		var $body = $root.find(_ExTableBody);
		var recw = $body.width()-16;
		
		// カラムの幅決定。
		var columns = calcColumnSize(columnMetas, recw);
		// カラムの位置決定。
		columns = calcColumnPosition(columns);

		var sortInfo = this.sortInfo;
		var rootSelector = this.rootSelector;
		$header.find(_ExTableColumn).each(function(){
			var $col = $(this);
			var idx = $col.data(COLUMN_IDX);
			var cmeta = columnMetas[idx];
			var $label = $col.find(_ExTableHeaderLabel+">span");

			// ヘッダタイトル設定
			$label.text(cmeta.title);
			// ソートのマーク設定
			$col.removeClass(ExTableHeaderSortDesc);
			$col.removeClass(ExTableHeaderSortAsc);
			if (sortInfo && sortInfo.index==idx) {
				$col.addClass(sortInfo.desc?ExTableHeaderSortDesc:ExTableHeaderSortAsc);
			}

			// オプションのスタイルはBodyのみに適用
			setCssRule(_ExTableRow+" "+_ExTableColumn_+idx, cmeta.style);

			// サイズ変更はHeaderとBodyに適用
			var selector = rootSelector+" "+_ExTableColumn_+idx;
			var style = {
				width: columns[idx].width+"px",
				left: columns[idx].left+"px",
				paddingLeft: null,
				paddingRight: null
			};
			// 最小化したとき文字半分残るのでその対策。
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
	 * カラムサイズの計算。
	 * - widthが"nn%"の場合はレコード幅から固定幅カラム分を差し引いて残り幅の nn% の幅になる。
	 * @param columnMetas カラム情報
	 * @param recodeWidth レコードの幅
	 * @return カラム幅（＋他の情報）の配列
	 */
	function calcColumnSize(columnMetas, recodeWidth) {
		var columns = [];
		for (var i=0; i<columnMetas.length; i++) {
			var m = columnMetas[i];
			var column = {idx:i, seq:m.seq, width:m.width, left:0};
			if (typeof m.width == "string") column.varticalWidth = m.width;
			columns.push(column);
		}

		var remainWidth = getRemineWidth(columnMetas, recodeWidth);
		for (var i=0; i<columns.length; i++) {
			if (typeof columns[i].width == "string") {
				columns[i].width = remainWidth*parseInt(columns[i].width) / 100;
			}
		}
		return columns;
	}
	/**
	 * レコード幅から固定幅カラム分を差し引いて残り幅を返す。
	 * @param columnMetas カラム情報
	 * @param recodeWidth レコードの幅
	 * @return 残り幅
	 */
	function getRemineWidth(columnMetas, recodeWidth) {
		var remainWidth = recodeWidth;
		for (var i=0; i<columnMetas.length; i++) {
			var m = columnMetas[i];
			if (typeof m.width == "number") remainWidth -= m.width;
		}
		return remainWidth;
	}

	/**
	 * レコード幅から固定幅カラム分を差し引いて残り幅を返す。
	 * - Eventハンドラ用
	 * @return 残り幅
	 */
	Class.prototype.getRemineWidth = function() {
		return getRemineWidth(this.columnMetas,
				$(this.rootSelector+" "+_ExTableBody)[0].clientWidth);
	}

	/**
	 * カラム位置の計算。
	 * - カラム順にソートしてカラム幅を足していくだけ。
	 * - ソートは元の順番(columnMetasと同じ)に戻す。
	 * @param columns カラム幅（＋他の情報）の配列
	 * @return カラム幅（＋他の情報）の配列
	 */
	function calcColumnPosition(columns) {
		columns.sort(function(a,b){return a.seq-b.seq;});
		var left = 0;
		for (var i=0; i<columns.length; i++) {
			columns[i].left = left;
			left += columns[i].width;
		}
		columns.sort(function(a,b){return a.idx-b.idx;});
		return columns;
	}

	/**
	 * 行の再配置。ソート後等に呼び出す。
	 */
	Class.prototype.refreshBody = function() {

		var $body = $(this.rootSelector+" "+_ExTableBody);
		var rows = sortRow(this.viewRows, this.sortInfo, this.columnMetas);

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
	function sortRow(rows, cond, columnMetas) {
		if (cond == null) return rows;

		var idx = cond.index;
		var comp = null;
		var customComp = columnMetas[idx].comparator;
		if (customComp) {
			comp = function(a,b){return customComp(a.data, b.data);};
		} else {
			var type = typeof rows[0].data[idx];
			if (type == "number") {
				comp = function(a,b){
					return a.data[idx]-b.data[idx];
				};
			} else {
				comp = function(a,b) {
					var A=a.data[idx], B=b.data[idx]
					return A==B?0:(A>B?1:-1);
				};
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
	 * @param columnMetas カラム情報
	 */
	function refreshRow($row, rowData, columnMetas) {
		$row.find(_ExTableColumn).each(function(){
			var $col = $(this);
			var idx = $col.data(COLUMN_IDX);
			var setter = columnMetas[idx].setter;
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

		buildHeader($root.find(_ExTableHeader), this.columnMetas);
		this.refreshHeader();

		this.viewRows = [];
		this.masterRows = [];
		var $body = $root.find(_ExTableBody);
		for (var i=0; i<this.masterData.length; i++) {
			var $row = buildRow(this.masterData[i], this.columnMetas);
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
	function buildRow(rowData, columnMetas) {
		var $row = $TEMPL_ROW.clone();
		for (var i=0; i<columnMetas.length; i++) {
			var $col = $TEMPL_COL.clone();
			$col.addClass(ExTableColumn_+i);
			$col.data(COLUMN_IDX, i);
			$row.append($col);
		}
		refreshRow($row, rowData, columnMetas);
		return $row;
	}

	/**
	 * ヘッダ行要素の生成。
	 */
	function buildHeader($header, columnMetas) {
		var $headerInner = $header.find(_ExTableHeaderRow);
		for (var i=0; i<columnMetas.length; i++) {
			var $col = $TEMPL_HEADER_COL.clone();
			//$col.find("span:first-child");
			$col.addClass(ExTableColumn_+i);
			$col.data(COLUMN_IDX, i);
			$headerInner.append($col);
		}
		return $header;
	}


	function setCssRule(selector, style) {
		Common.setCssRule(selector, style);
	}

	Class.prototype.trigger = function(type){
		$(this.rootSelector).trigger(type, [this.columnMetas]);
	}
	
	
	//----------------------------------------------------------------------------
	// イベントハンドラ。

	/**
	 * カラムのリサイズハンドラ設定。
	 */
	function bindResize() {
		// Resize
		var handle = null;
		var isResized = false;
		$(_ExTableHandle).live("mousedown", function(){
			handle = this;
			isResized = false;
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
			var metas = exTable.getColumnMetas();
			if (metas[idx].fixed) return; // 固定

			if (typeof metas[idx].width == "string") { // =="n%"
				var remaineWidth = exTable.getRemineWidth();
				metas[idx].width = Math.floor(w*100/remaineWidth)+"%";
			} else {
				metas[idx].width = w;
			}

			exTable.updateHeader();
			//exTable.refreshRowHight();
			isResized = true;
		}).live("mouseup", function(){
			if (handle) {
				var exTable = $(handle).parents(_ExTable).data(ExTable);
				Common.waiting(function(){
					exTable.refreshRowHight();
				});
				if (isResized) {
					exTable.trigger("columnresize");
				}
			}
			handle = null;
		});
	}

	/**
	 * カラムの移動ハンドラ設定。
	 */
	function bindMove() {
		// Move
		var handle = null;
		var lastChanged = null;
		var mouseDownTime = 0;
		var isMoved = false;
		$(_ExTableHeader+" "+_ExTableColumn).live("mousedown", function(){
			handle = this;
			$(handle).css({cursor: "url(img/cursor-move-box-LR.png) 8 8, col-resize"});
			mouseDownTime = new Date().getTime();
			isMoved = false;
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
			var metas = exTable.getColumnMetas();
			if (metas[tIdx].fixed) return; // 固定

			var tmp = metas[hIdx].seq;
			metas[hIdx].seq = metas[tIdx].seq;
			metas[tIdx].seq = tmp;
			exTable.updateHeader();
			isMoved = true;
		}).live("mouseup", function(){
			var time = (new Date().getTime())- mouseDownTime;
			mouseDownTime = 0;
			if (time>200) return;

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
			if (isMoved && handle) {
				var exTable = $(handle).parents(_ExTable).data(ExTable);
				exTable.trigger("columnmove");
			}
			
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


