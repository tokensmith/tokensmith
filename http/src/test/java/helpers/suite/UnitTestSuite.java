package helpers.suite;

import helpers.category.ServletContainerTest;
import helpers.category.UnitTests;
import net.tokensmith.authorization.http.service.ProfileServiceTest;
import net.tokensmith.authorization.http.service.translator.AddressTranslatorTest;
import net.tokensmith.authorization.http.service.translator.ProfileTranslatorTest;
import org.junit.experimental.categories.Categories;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import net.tokensmith.authorization.http.controller.regex.WelcomeResourceRegexTest;
import net.tokensmith.authorization.http.controller.resource.html.authorization.helper.AuthorizationFactoryTest;
import net.tokensmith.authorization.http.controller.resource.html.authorization.helper.AuthorizationHelperTest;
import net.tokensmith.authorization.http.factory.MakeOAuthTokenTest;
import net.tokensmith.authorization.http.factory.MakeOpenIdTokenTest;
import net.tokensmith.authorization.http.factory.MakeTokenFactoryTest;


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
        WelcomeResourceRegexTest.class,
        AddressTranslatorTest.class,
        ProfileTranslatorTest.class,
        ProfileServiceTest.class
})
public class UnitTestSuite {
}
