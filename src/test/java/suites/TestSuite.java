package suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import tests.*;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        HelloWorldTestJunit4.class,
        UserAuthTestJunit4.class,
        UserDeleteTestJunit4.class,
        UserEditTestJunit4.class,
        UserGetTestJunit4.class,
        UserRegisterTestJunit4.class
})
public class TestSuite {

}
