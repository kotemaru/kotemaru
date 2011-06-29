                                                                    2011.06.29
                                                         kotemaru@kotemaru.org
                            FreeBSD用CPUモニタ

1. 概要

本プログラムは FreeBSD用のCPUモニタです。
CPUの温度、使用率、動作周波数を一定間隔置きに表示します。

実装は手抜きです。自己責任で利用願います。

2. コンパイル

展開ディレクトリで make を実行してください。

	$ make

cpumon と xcpumon が生成されます。
前者がCUI、後者がGUI用の実行プログラムです。

3. 実行方法

3.1 CUI版

コマンドラインから cpumon を起動します。

	$ cpumon
	c0=[1.7GHz,  0.0%, 47.0C] c1=[1.7GHz,  0.0%, 47.0C] c2=[1.7GHz,  0.0%, 41.0C] c3=[1.7GHz,  0.0%, 41.0C] 

オプションは有りません。


3.2 GUI版

コマンドラインから xcpumon を起動します。XWindow が必要です。
オプションは以下の通り。

	usage: xcpumon [<options>...]
	    -g <width>x<height>+<x>+<y> : window size.
	    -mt <val>   : max value of temperature.
	    -mf <val>   : max value of freq.
	    -l <val>    : max logging count.
	    -i <val>    : interval sec.
	    -v          : verbose mode.
	    -wm         : enable window manager.
	    -font <font>: 8px X font name.

手抜きなので -display とか効きません。

例：周波数表示上限 2.0GHz、２秒間隔

	$ xcpumon -mf 2000 -i 2

グラフの色
黄色   : 動作周波数
ピンク : CPU温度、複数コアの中の最大値
緑色   : CPU使用率、全てのコアの平均値
水色   : CPU使用率、複数コアの中の最大値

--
以上


