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

		System.out.println(
				bean.getAge()
				+","+bean.getEmail()
				+","+bean.getFirstName()
				+","+bean.getLastName()
				+","+bean.getTel()

		);

	}

}
