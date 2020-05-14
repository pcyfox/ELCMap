package com.pcyfox.elc.elcview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import com.pcyfox.elc.R;
import com.pcyfox.elc.widget.Anchor;
import com.pcyfox.elc.widget.ElcViewGroup;

/**
 * 滑动变阻器
 */
public class POTLIN extends ElcViewGroup {
    public POTLIN(Context context) {
        super(context);
        init();
    }

    public POTLIN(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public POTLIN(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public ElcViewGroup create() {
        return new POTLIN(getContext());
    }

    private void init() {
        String name = "P";
        Object tag = getTag();
        if (tag != null) {
            name = tag.toString();
        }
        setName(name);
        LayoutInflater.from(getContext()).inflate(R.layout.elc_layout_p, this);
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
