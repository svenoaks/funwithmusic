package com.afollestad.silk.http;

import ch.boye.httpclientandroidlib.Header;

/**
 * Represents a header for an HTTP request.
 *
 * @author Aidan Follestad (afollestad)
 */
public class SilkHttpHeader {

    private final String mName;
    private final String mValue;

    public SilkHttpHeader(String name, String value) {
        mName = name;
        mValue = value;
    }

    public SilkHttpHeader(Header header) {
        this(header.getName(), header.getValue());
    }

    public String getName() {
        return mName;
    }

    public String getValue() {
        return mValue;
    }

    @Override
    public String toString() {
        return mName + " = " + mValue;
    }
}
