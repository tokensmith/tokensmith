package helper;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by tommackenzie on 2/21/16.
 *
 * http://stackoverflow.com/questions/70689/what-is-an-efficient-way-to-implement-a-singleton-pattern-in-java
 */
public enum SpringFactory {
    INSTANCE;
    private final ApplicationContext context = new ClassPathXmlApplicationContext("spring-auth-test.xml");

    public ApplicationContext getContext() {
        return this.context;
    }
}
