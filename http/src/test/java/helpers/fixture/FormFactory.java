package helpers.fixture;

import com.ning.http.client.Param;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tommackenzie on 6/5/15.
 */
public class FormFactory {

    public static List<Param> makeLoginForm(String email, String csrfToken) {
        Param userName = new Param("email", email);
        Param password = new Param("password", "password");
        Param csrfParam = new Param("csrfToken", csrfToken);
        List<Param> postData = new ArrayList<>();
        postData.add(userName);
        postData.add(password);
        postData.add(csrfParam);

        return postData;
    }

    public static List<Param> makeRegisterForm(String email, String passwordValue, String repeatPasswordValue, String csrfToken) {
        Param userName = new Param("email", email);
        Param password = new Param("password", passwordValue);
        Param repeatPassword = new Param("repeatPassword", repeatPasswordValue);
        Param csrfParam = new Param("csrfToken", csrfToken);

        List<Param> postData = new ArrayList<>();

        postData.add(userName);
        postData.add(password);
        postData.add(repeatPassword);
        postData.add(csrfParam);

        return postData;
    }

    public static List<Param> makeForgotPasswordForm(String email, String csrfToken) {
        Param userName = new Param("email", email);
        Param csrfParam = new Param("csrfToken", csrfToken);

        List<Param> postData = new ArrayList<>();

        postData.add(userName);
        postData.add(csrfParam);

        return postData;
    }

    public static List<Param> makeUpdatePasswordForm(String password, String repeatPassword, String csrfToken) {
        Param pword = new Param("password", password);
        Param repeatPword = new Param("repeatPassword", repeatPassword);
        Param csrfParam = new Param("csrfToken", csrfToken);

        List<Param> postData = new ArrayList<>();

        postData.add(pword);
        postData.add(repeatPword);
        postData.add(csrfParam);

        return postData;
    }
}
