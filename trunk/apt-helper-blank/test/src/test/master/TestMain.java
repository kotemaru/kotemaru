package test.master;
import test.autobean.*;

public class TestMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TestBean bean = new TestBean();
		bean.setAge(10);
		bean.setEmail("abg@efg.com");
		bean.setFirstName("jone");
		bean.setLastName("smith");
		bean.setTel("090-1234-5678");

		System.out.println("["
				+"\nAge="+bean.getAge()
				+"\nEmail="+bean.getEmail()
				+"\nName="+bean.getFirstName()
				+" "+bean.getLastName()
				+"\nTel="+bean.getTel()
				+"\nHoge="+bean.getHoge()
				+"\n]"
		);

	}

}
