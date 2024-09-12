package com.mogukun.sentry.utils;

import java.util.ArrayList;
import java.util.List;

public class ListUtil {

    List<String> list = new ArrayList<>();

    public ListUtil() {}

    public ListUtil add(String s) {
        list.add(s);
        return this;
    }


    public List<String> getList() {
        return list;
    }

}
