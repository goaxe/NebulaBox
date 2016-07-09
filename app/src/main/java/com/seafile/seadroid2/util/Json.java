package com.seafile.seadroid2.util;

import java.util.List;

/**
 * Created by Alfred on 2015/11/30.
 */
public abstract class Json {
    private static Json json;

    Json() {
    }

    public static Json get() {
        if (json == null) {
            json = new GsonImpl();
        }
        return json;
    }

    public abstract String toJson(Object src);

    public abstract <T> T toObject(String json, Class<T> claxxs);

    public abstract <T> T toObject(byte[] bytes, Class<T> claxx);

    public abstract <T> List<T> toList(String json, Class<T> claxx);
}