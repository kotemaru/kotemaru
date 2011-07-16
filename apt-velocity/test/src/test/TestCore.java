package test;
import  org.kotemaru.aptvelocity.sample.annotation.*;

@AutoBean(bean="test.TestBean")
public abstract class TestCore {
    protected String firstName;
    protected String lastName;
    protected int age;
    protected String email;
    protected String tel;
}
