package com.mogukun.sentry.check;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

// CREDIT @DerRedstoner's CheatGuard, It helped me a lot.

@Retention(RetentionPolicy.RUNTIME)
public @interface CheckInfo {

    String name();
    String path();
    String description();
    Category category();
    boolean experimental() default false;
    Plan plan() default Plan.Free;

}
