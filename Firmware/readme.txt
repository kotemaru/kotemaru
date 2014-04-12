□ 赤外線リモコンキット　ファームウェア　Android対応版

RemoconServant-for-android.hex を HIDBootLoader.exe で更新すれば Android から認識されます。

ソースからビルドしたい場合は 2.1.0 の usb_descriptors.c に usb_descriptors-for-android.patch 
を当てて -D__FOR_ANDROID でコンパイルしてください。

以上。