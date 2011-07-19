package org.kotemaru.apthelper;

public class Version {
	public static final String ID = "$Id$";
	public static final String DATE = "$Date$";
	public static final String REV = "$Rev$";
	public static final String Author = "$Author$";

	public static void main(String[] args) {
		System.out.println("apt-helper: "+ID);
	}
}
