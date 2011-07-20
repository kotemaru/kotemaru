package sample.apt;

public class Version {
	public static final String ID = "$Id: Version.java 51 2011-07-19 09:02:57Z kotemaru@kotemaru.org $";
	public static final String DATE = "$Date: 2011-07-19 18:02:57 +0900 (火, 19 7 2011) $";
	public static final String REV = "$Rev: 51 $";
	public static final String Author = "$Author: kotemaru@kotemaru.org $";

	public static void main(String[] args) {
		System.out.println("apt-sample: "+ID);
	}
}
