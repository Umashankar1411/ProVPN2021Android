package com.ugt.premiumvpn;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

public class CustomTxtSemibold extends android.support.v7.widget.AppCompatTextView {

    public CustomTxtSemibold(Context context) {
        super(context);
        init();
    }

    public CustomTxtSemibold(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTxtSemibold(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init() {
        if (!isInEditMode()){
            Typeface normalTypeface = Typeface.createFromAsset(getContext().getAssets(), "Montserrat-SemiBold.ttf");
            setTypeface(normalTypeface);
        }
    }
}
