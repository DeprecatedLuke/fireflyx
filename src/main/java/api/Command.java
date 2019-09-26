package api;

import org.atteo.classindex.IndexAnnotated;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IndexAnnotated
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    String name();
    String desc() default "";
    String usage() default "";
    String[] alias() default {};

    boolean opOnly() default true;
    String permission() default "";
}
