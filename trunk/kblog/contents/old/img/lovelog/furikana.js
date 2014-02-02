/**
ふりかな入力支援 jQuery Plugin.
使い方：
-----------------------------------------------------------------------
<script>
$(function(){
	var form1 = document.form1;
	$(form1.kanji).furikana("init",{target: form1.kana});
});
</script>
	:
<form action="" name="form1">
	Kanji:<input name="kanji" value="" /><br/>
	Kana:<input name="kana" value="" />
</form>
-----------------------------------------------------------------------

@auther kotemaru@kotemaru.org
*/
(function() {
	var NAME = "furikana";

	var Methods = {

		/**
		 * 初期化処理。
		 */
		init: function(jqobj, args) {
			// セルフデータを生成。
			var self = jQuery.extend({
				interval: 100
			}, args);

			self.lastText = "";    // 直前の入力テキスト
			self.addedText = "";   // 追加部分のテキスト
			/**
			 * 入力テキストのログ。
			 * - [0]が最新の状態となる。
			 * - 漢字に変換された時点で更新する。
			 */
			self.kanjiText = [""];
			/**
			 * ふりかなテキストのログ。
			 * - kanjiTextと対になり、同期する。
			 */
			self.kanaText = [""];

			jqobj.data(NAME, self);

			// focus-inでTicker起動、focus-outで終了。
			jqobj.focus(function(){Methods.start(jqobj)});
			jqobj.blur(function(){Methods.end(jqobj)});
		},

		/** Ticker 起動。*/
		start: function(jqobj, args) {
			var self = jqobj.data(NAME);
			self.isStart = true;
			setTimeout(function(){Methods.update(jqobj);}, self.interval);
		},
		/** Ticker 終了。*/
		end: function(jqobj, args) {
			var self = jqobj.data(NAME);
			self.isStart = false;
		},

		/**
		 * 更新処理。
		 * - Ticker から繰り返し呼ばれる。
		 * - IMEの入力中はイベントが発生しないので Ticker を使う。
		 */
		update: function(jqobj, args) {
			var self = jqobj.data(NAME);
			if (! self.isStart) return;

			setTimeout(function(){Methods.update(jqobj);}, self.interval);

			// 入力テキストに変更がなければ何もしない。
			var text = jqobj.val();
			if (self.lastText == text) return;

			// 入力テキストのログとの差分を追加入力とする。
			var addedText = text.substr(self.kanjiText[0].length);

			if (text.length < self.kanjiText[0].length) {
				// 削除されていたら入力確定処理へ
				Methods.fix(jqobj);
			} else if (addedText.match(/[ 　]+$/)) {
				// 空白は入力確定処理へ
				self.addedText = addedText;
				$(self.target).val(self.kanaText[0] + self.addedText);
				Methods.fix(jqobj);
			} else if (addedText.match(/[^ぁあ-んァーa-zA-Z0-9\-./]/)) {
				// 平仮名以外が入力されたら確定へ
				Methods.fix(jqobj);
			} else {
				// 平仮名のみの場合はふりかなを更新する。
				self.addedText = addedText;
				$(self.target).val(self.kanaText[0] + self.addedText);
			}
			self.lastText = text;
		},
	
		/**
		 * 確定処理。
		 * - IMEの入力中はイベントが発生しないので Ticker を使う。
		 */
		fix: function(jqobj, args) {
			var self = jqobj.data(NAME);
			if (! self.isStart) return;

			// 入力テキストに変更がなければ何もしない。
			var text = jqobj.val();
			if (self.lastText == text) return;

			if (text.length >= self.kanjiText[0].length) {
				// 入力テキスト追加ならログに追加。
				self.kanjiText.unshift(text);
				self.kanaText.unshift(self.kanaText[0] + self.addedText);
			} else {
				// 入力テキスト削除ならログを更新。
				self.kanjiText[0] = text;
				self.kanaText[0] = self.kanaText[1]+"※";

				// 古いログに現在値と同じ物があればそこまでログを戻す。
				for (var i=1; i<self.kanjiText.length; i++) {
					if (text == self.kanjiText[i]) {
						self.kanjiText.splice(0,i);
						self.kanaText.splice(0,i);
						break;
					}
				}
			}
			// ふりかなの状態をログに合わせる。
			$(self.target).val(self.kanaText[0]);
			self.lastText = text;
		},

	};
		
	/** Pluginメソッド */
	jQuery.fn[NAME] = function(cmd, args){
		Methods[cmd](this, args);
		return this;
	};
})(jQuery);
