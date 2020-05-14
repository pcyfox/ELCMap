package com.pcyfox.elc.elcview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;

import com.pcyfox.elc.R;
import com.pcyfox.elc.widget.Anchor;
import com.pcyfox.elc.widget.ElcViewGroup;


/**
 * 开关
 */
public class Switch extends ElcViewGroup {
    public Switch(Context context) {
        super(context);
    }

    public Switch(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public Switch(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    public ElcViewGroup create() {
        return new Switch(getContext());
    }
    {
        String name = "S";
        Object tag = getTag();
        if (tag != null) {
            name = tag.toString();
        }
        setName(name);
        LayoutInflater.from(getContext()).inflate(R.layout.elc_layout_s, this);
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
