package test.master;
import  test.annotation.*;

@AutoBean()
public abstract class TestCore {
    protected String firstName;
    protected String lastName;
    protected int age;
    protected String email;
    protected String tel;

    @Attrs(setter=false)
    protected String hoge;
}
