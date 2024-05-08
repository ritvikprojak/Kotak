package com.abc.kotak;

public class Test {

	public static void main(String[] args) {
		
		String s1 = new String("nik");
		String s2 = new String("c");
		s1.concat("rit");
		String s3="chap";
		String s6="chap";
		String s4 = s3+s1;
		String s5 = s3+s1;
		
		System.out.println(s1);
		System.out.println(s2);
		System.out.println(s3.concat(s1));
		System.out.println(s4);
		
		System.out.println(s4 == s5);	//true
		System.out.println(s4.hashCode() +" >>>> " + s5.hashCode());
		
		
		System.out.println(s3 == s6);	//true
		System.out.println(s3.hashCode() +" >>>> " + s6.hashCode());
		
		StringBuffer sb = new StringBuffer("Abc");
		StringBuffer sb1 = new StringBuffer("Abc");
		System.out.println(sb==sb1); //false
		System.out.println(sb.equals(sb1));  //false
		
		
	}

}
