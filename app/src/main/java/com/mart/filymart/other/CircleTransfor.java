package com.mart.filymart.other;

import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

abstract class CircleTransfor extends BitmapTransformation {
    public CircleTransfor() {
        super();
    }

    public abstract String getId();
}
