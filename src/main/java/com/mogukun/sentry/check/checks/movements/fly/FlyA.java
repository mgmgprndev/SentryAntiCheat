package com.mogukun.sentry.check.checks.movements.fly;

import com.mogukun.sentry.check.Category;
import com.mogukun.sentry.check.Check;
import com.mogukun.sentry.check.CheckInfo;
import com.mogukun.sentry.check.CheckResult;

@CheckInfo(
        name = "Fly (A)",
        description = "Simple Flight Check",
        category = Category.MOVEMENT
)
public class FlyA extends Check {

    @Override
    public CheckResult handle(){
        return null;
    }

}
