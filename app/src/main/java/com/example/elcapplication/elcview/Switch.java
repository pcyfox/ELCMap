package com.example.elcapplication.elcview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import com.example.elcapplication.R;
import com.example.elcapplication.widget.Anchor;
import com.example.elcapplication.widget.ElcViewGroup;

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


    {
        setName("Switch:" + getTag());
        LayoutInflater.from(getContext()).inflate(R.layout.elc_layout_sw, this);
        Anchor leftAnchor = findViewById(R.id.elc_anchor_l);
        leftAnchor.setName(leftAnchor.getTag()+"");
        Anchor rightAnchor = findViewById(R.id.elc_anchor_r);
        rightAnchor.setName(rightAnchor.getTag()+"");
        addAnchor(leftAnchor, rightAnchor);
    }


}
