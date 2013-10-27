package com.afollestad.silk.caching;

import java.io.Serializable;

public interface SilkComparable<Type> extends Serializable {

    public Object getSilkId();

    public boolean equalTo(Type other);
}
