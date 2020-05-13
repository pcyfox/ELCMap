package com.example.elcapplication.elcview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;

import com.example.elcapplication.R;
import com.example.elcapplication.widget.Anchor;
import com.example.elcapplication.widget.ElcViewGroup;

/**
 * 电阻
 */
public class Resistance extends ElcViewGroup {
    public Resistance(Context context) {
        super(context);
    }

    public Resistance(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public Resistance(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public ElcViewGroup create() {
        return new Resistance(getContext());
    }

    {
        String name = "R";
        Object tag = getTag();
        if (tag != null) {
            name = tag.toString();
        }
        setName(name);
        LayoutInflater.from(getContext()).inflate(R.layout.elc_layout_r, this);
        Anchor leftAnchor = findViewById(R.id.elc_anchor_l);
        leftAnchor.setName(name + "-" + leftAnchor.getTag());
        Anchor rightAnchor = findViewById(R.id.elc_anchor_r);
        rightAnchor.setName(name + "-" + rightAnchor.getTag());
        addAnchor(leftAnchor, rightAnchor);
    }

}
