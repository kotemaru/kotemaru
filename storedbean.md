# storedbean v1.0 #
  * author 2011.06.18 kotemaru.org

## 概要 ##

storedbean は GAE 上で簡易に Bean インスタンスを DataStore
に永続化する事を目的とするライブラリです。

環境設定を保存するような Bean は通常インスタンスは一つしか存在せず複数の Bean が構造化されることも有ります。

このような Bean の為に JDO や Slim3 の Model をちゃんと定義する事は以外な負担となります。

Serialize を使う方法も有りますが後々、互換性の問題を引き起こしたりデータの可読性が悪くなる問題があります。

これらの問題を解決し気軽に作った Bean をそのまま永続化できるようにしたものが本ライブラリです。


## 使い方 ##

  1. storedbean-1.0.x.jar をビルトパスに追加します。
  1. 保存したい Bean に StoredBean インターフェースを実装します。
    * StoredBean にはメソッドはありません。
  1. StoredBeanService#put で保存します。
  1. StoredBeanService#get で取得します。

  * javadoc:http://kotemaru.googlecode.com/svn/trunk/storedbean/docs/javadoc/index.html

### サンプルコード ###
```
import org.kotemaru.gae.storedbean.StoredBean;
import org.kotemaru.gae.storedbean.StoredBeanService;

// Bean の定義
public class TestBean implements StoredBean {
	private int item01;
	private String item02;

	public int getItem01() {return item01;}
	public void setItem01(int item01) {this.item01 = item01;}
	public String getItem02() {return item02;}
	public void setItem02(String item02) {this.item02 = item02;}
}

// Bean に値を設定。
TestBean bean = new TestBean();
bean.setItem01(123);
bean.setItem02("abc");

StoredBeanService sbs = new StoredBeanService("StoredBean");
String key = "key-name";
// 保存
sbs.put(key, bean);
// 復元
TestBean restoreBean = (TestBean) sbs.get(key);
```

  * 全体としてはほぼ Map と同じように使えます。
  * StoredBeanServiceコンストラクタの引数は保存先DataStoreのkindです。
  * key-name は保存先の名前で任意の文字列です。
  * 復元時は Memcache を利用しますので性能を気にする必要はありません。
  * 保存したデータは GAE の管理画面から編集する事が可能です。
    * ※管理画面から編集後は Memcache のクリアをしてください。

### DataStore の状態 ###

サンプルを実行するとDataStoreは以下のような状態になります。

|![http://kotemaru.googlecode.com/svn/trunk/storedbean/docs/storedbean-ds-view.png](http://kotemaru.googlecode.com/svn/trunk/storedbean/docs/storedbean-ds-view.png)|
|:------------------------------------------------------------------------------------------------------------------------------------------------------------------|

そのまま編集も可能です。


|![http://kotemaru.googlecode.com/svn/trunk/storedbean/docs/storedbean-ds-edit.png](http://kotemaru.googlecode.com/svn/trunk/storedbean/docs/storedbean-ds-edit.png)|
|:------------------------------------------------------------------------------------------------------------------------------------------------------------------|





## 制限 ##

StoredBean として保存可能な Bean には幾つかの制限があります。

  * Bean の制限
    * 一般的な Bean の規約に従っていなければなりません。

  * 項目の型の制限
    * int,long,boolean,String,byte[.md](.md),List,Set,StoredBean のみが利用可能です。
    * List,Setの項目は Integer,Long,Boolean,String,StoredBean のみが利用可能です。

  * 復元時の制限
    * List,Set が復元されると実体として元の型とはならず ArrayList,HashSet となります。


## コンパイル ##
Eclipseでビルド後 build.xml の Ant実行 からターゲット jar を実行してください。

storedbean-1.0.x.jar が生成されます。