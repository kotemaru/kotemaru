package test.master;
import  sample.annotation.*;

@AutoBean()
public abstract class Test {
    protected String firstName;
    protected String lastName;
    protected int age;
    protected String email;
    protected String tel;

    @Attrs(setter=false)
    protected String hoge = "hoge";
}
