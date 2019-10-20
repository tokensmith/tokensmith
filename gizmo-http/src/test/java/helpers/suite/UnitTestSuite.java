package helpers.suite;

import helpers.category.ServletContainerTest;
import helpers.category.UnitTests;
import org.junit.experimental.categories.Categories;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.rootservices.authorization.http.controller.regex.WelcomeResourceRegexTest;
import org.rootservices.authorization.http.controller.resource.html.authorization.helper.AuthorizationFactoryTest;
import org.rootservices.authorization.http.controller.resource.html.authorization.helper.AuthorizationHelperTest;
import org.rootservices.authorization.http.factory.MakeOAuthTokenTest;
import org.rootservices.authorization.http.factory.MakeOpenIdTokenTest;
import org.rootservices.authorization.http.factory.MakeTokenFactoryTest;


/**
 * Created by tommackenzie on 4/23/15.
 */
@RunWith(Categories.class)
@Categories.IncludeCategory(UnitTests.class)
@Categories.ExcludeCategory(ServletContainerTest.class)
@Suite.SuiteClasses({
        MakeTokenFactoryTest.class,
        MakeOpenIdTokenTest.class,
        MakeOAuthTokenTest.class,
        AuthorizationFactoryTest.class,
        AuthorizationHelperTest.class,
        WelcomeResourceRegexTest.class
})
public class UnitTestSuite {
}
