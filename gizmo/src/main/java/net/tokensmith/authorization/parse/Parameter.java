package net.tokensmith.authorization.parse;

import java.lang.annotation.*;


@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Parameter {
    String name();
    boolean required() default true;
    String[] expected() default {};
}
