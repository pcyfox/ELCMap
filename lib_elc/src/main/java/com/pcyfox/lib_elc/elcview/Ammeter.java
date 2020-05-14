package com.pcyfox.lib_elc.elcview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;

import com.pcyfox.lib_elc.R;
import com.pcyfox.lib_elc.widget.Anchor;
import com.pcyfox.lib_elc.widget.ElcViewGroup;


/**
 * 电流表
 */
public class Ammeter extends ElcViewGroup {
    public Ammeter(Context context) {
        super(context);
    }

    public Ammeter(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public Ammeter(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public ElcViewGroup create() {
        return new Ammeter(getContext());
    }

    {
        String name = "A";
        Object tag = getTag();
        if (tag != null) {
            name = tag.toString();
        }
        setName(name);
        LayoutInflater.from(getContext()).inflate(R.layout.elc_layout_a, this);
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
