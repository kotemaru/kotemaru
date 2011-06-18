package org.kotemaru.gae.storedbean.tests;
import org.kotemaru.gae.storedbean.*;

import java.io.File;
import java.util.*;
import org.junit.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.tools.development.*;
import com.google.apphosting.api.ApiProxy;
import com.google.apphosting.api.ApiProxy.Environment;
import com.google.appengine.tools.development.testing.*;

import static org.junit.Assert.assertEquals;


public class StoredBeanTest {
	private final LocalServiceTestHelper helper =
		new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig())
		.setEnvIsAdmin(true)
		.setEnvIsLoggedIn(true)
		;

	@Before
	public void setUp() {
		helper.setUp();
	}

	@After
	public void tearDown() {
		helper.tearDown();
	}

	@Test 
	public void testNull() throws Exception  {
		SimpleBean bean = new SimpleBean();

		test("null", bean, false);
		test("null", bean, true);
		test("null", bean, true);
	}

	@Test 
	public void testSimple() throws Exception  {
		SimpleBean bean = newTestSimpleBean();

		test("simple", bean, false);
		test("simple", bean, true);
		test("simple", bean, true);
	}
	@Test 
	public void testNested() throws Exception  {
		NestedBean bean = new NestedBean();
		bean.setBean1(newTestSimpleBean());
		List<SimpleBean> list = new ArrayList<SimpleBean>(); 
		list.add(newTestSimpleBean());
		list.add(newTestSimpleBean());
		bean.setBeans(list);

		test("nested", bean, false);
		test("nested", bean, true);
		test("nested", bean, true);
	}

	private SimpleBean newTestSimpleBean() {
		SimpleBean bean = new SimpleBean();
		bean.setItem01(123);
		bean.setItem02("test2");
		bean.setItem03(true);
		bean.setItem04(new byte[]{'a','b','c'});

		List<String> list = new ArrayList<String>();
		list.add("aaa");
		list.add("bbb");
		bean.setItem05(list);

		Set<String> set = new HashSet<String>();
		set.add("ccc");
		set.add("ddd");
		bean.setItem06(set);

		return bean;
	}


	private void test(String name, StoredBean src, boolean withCache) throws Exception  {
		StoredBeanService sbs = new StoredBeanService("StoredBean");
		sbs.setMemcacheEnable(withCache);

		Key key = sbs.put(name, src);
		System.out.println(name+".key="+key);
		System.out.println("src="+src);
		StoredBean dst = (StoredBean) sbs.get(name);
		System.out.println("dst="+dst);
		assertEquals(src, dst);
	}

}
