package com.afollestad.silk.caching;

public interface OnReadyCallback<ItemType extends SilkComparable<ItemType>> {

    public void onReady(SilkCache<ItemType> cache);
}
