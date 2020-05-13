package com.example.elcapplication.elcview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;

import com.example.elcapplication.R;
import com.example.elcapplication.widget.Anchor;
import com.example.elcapplication.widget.ElcViewGroup;

/**
 * 电源
 */
public class Electric extends ElcViewGroup {
    public Electric(Context context) {
        super(context);
    }

    public Electric(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

       // TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.El);
    }

    public Electric(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    public ElcViewGroup create() {
        return new Electric(getContext());
    }

    {
        String name = "E";
        Object tag = getTag();
        if (tag != null) {
            name = tag.toString();
        }
        setName(name);
        LayoutInflater.from(getContext()).inflate(R.layout.elc_layout_e, this);
        Anchor leftAnchor = findViewById(R.id.elc_anchor_l);
        leftAnchor.setName(name + "-" + leftAnchor.getTag());
        Anchor rightAnchor = findViewById(R.id.elc_anchor_r);
        rightAnchor.setName(name + "-" + rightAnchor.getTag());
        addAnchor(leftAnchor, rightAnchor);
    }

}
