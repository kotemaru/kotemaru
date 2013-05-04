

function Strings(){this.initialize.apply(this, arguments)};
(function(_class){

	var common1 = "<br/>(ダブルクリックでロックします。)";
	var common2 = "<br/>このボタンを選択後に作成したい場所をクリックして下さい。" + common1;
	var common3 = "<br/>Please click a canvas after choice in this."
	
	var map = {
		"Action.undo": "UNDO",
		"Action.redo": "REDO",
		"Action.cursor": "要素の選択、移動、複製等を行います。",
		"Action.remove": "要素を削除します。"
			+"<br/>このボタンを選択後に削除したい要素をクリックしてください." + common1,
			
		"Action.Class": "クラス要素を作成します。"+common2,
		"Action.Object": "オブジェクト要素を作成します。"+common2,
		"Action.Cable": "ケーブル要素を作成します。<br/>"
			+"メニューからケーブルの種類を選べます。"+common2,
			
		"Action.Note": "ノート要素を作成します。"+common2,
		"Action.Disk": "データベース要素を作成します。"+common2,
		"Action.Marker": "マーカーを作成します。"+common2
			+"<br/>マーカーは印刷されません。"
			+"<br/>ケーブルの経路を制御する為に存在します。"
			,
	};
	
	_class.get = function(name) {
		if (map[name]) return map[name];
		return name;
	}
	

})(Strings);
