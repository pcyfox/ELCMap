package com.pcyfox.lib_elc.elcview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;

import com.pcyfox.lib_elc.R;
import com.pcyfox.lib_elc.widget.Anchor;
import com.pcyfox.lib_elc.widget.ElcViewGroup;

/**
 * 开关
 */
public class Light extends ElcViewGroup {
    public Light(Context context) {
        super(context);
    }

    public Light(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public Light(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    public ElcViewGroup create() {
        return new Light(getContext());
    }

    {
        String name = "L";
        Object tag = getTag();
        if (tag != null) {
            name = tag.toString();
        }
        setName(name);
        LayoutInflater.from(getContext()).inflate(R.layout.elc_layout_l, this);
        invalidate();
        Anchor leftAnchor = findViewById(R.id.elc_anchor_l);
        leftAnchor.setName(name + "-" + leftAnchor.getTag());
        Anchor rightAnchor = findViewById(R.id.elc_anchor_r);
        rightAnchor.setName(name + "-" + rightAnchor.getTag());
        addAnchor(leftAnchor, rightAnchor);
        leftAnchor.invalidate();
        rightAnchor.invalidate();
    }

}
