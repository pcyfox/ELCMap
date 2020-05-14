package com.pcyfox.lib_elc.elcview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;

import com.pcyfox.lib_elc.R;
import com.pcyfox.lib_elc.widget.Anchor;
import com.pcyfox.lib_elc.widget.ElcViewGroup;


/**
 * 电压表
 */
public class Voltmeter extends ElcViewGroup {
    public Voltmeter(Context context) {
        super(context);
    }

    public Voltmeter(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public Voltmeter(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    public ElcViewGroup create() {
        return new Voltmeter(getContext());
    }
    {
        String name = "V";
        Object tag = getTag();
        if (tag != null) {
            name = tag.toString();
        }
        setName(name);
        LayoutInflater.from(getContext()).inflate(R.layout.elc_layout_v, this);
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
