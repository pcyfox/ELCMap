package com.pcyfox.elc.ui

import android.graphics.Rect
import android.util.Log

import android.view.MotionEvent
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.pcyfox.lib_elc.widget.ElcViewGroup
import com.pcyfox.elc.R
import com.pcyfox.lib_elc.widget.Anchor


import kotlinx.android.synthetic.main.elc_fragment.*

class ELCFragment(contentLayoutId: Int = R.layout.elc_fragment) : Fragment(contentLayoutId) {
    override fun onStart() {
        super.onStart()
        initView()
    }

    private fun initView() {
        var newElcView: ElcViewGroup? = null
        elc_ll_elements.children.forEach {
            it.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        newElcView?.dispatchGenericMotionEvent(event)
                    }
                    MotionEvent.ACTION_MOVE -> {
                        newElcView?.dispatchGenericMotionEvent(event)
                    }
                    MotionEvent.ACTION_UP -> {
                        Log.d("setOnTouchListener:", v.toString())
                        if (v is ElcViewGroup) {
                            newElcView = v.create()
                            newElcView?.state = ElcViewGroup.STATE_LAYOUT
                            val lp = ViewGroup.MarginLayoutParams(v.layoutParams)
                            val rect = Rect()
                            v.getGlobalVisibleRect(rect)
                            lp.marginStart = 0
                            lp.topMargin = 0
                            newElcView?.layoutParams = lp
                            elc_link_view?.addView(newElcView, 0)
                            newElcView?.dispatchGenericMotionEvent(event)
                        }
                    }
                }
                false
            }
        }


        btn_print.setOnClickListener {
            val anchorList = ArrayList<Anchor>()
            elc_link_view.elcViewGroups.forEach {
                anchorList.addAll(it.anchors)
            }
            elc_link_view.clearAllElcViewGroup()
            Log.d("ELCFragment first:", anchorList.toString())
        }
    }

    private fun check(input: String = "E-R->S->A->L->E-L"): Boolean {
        return false
    }


    private fun isLeftLinkTo(src: String, dest: String, anchors: List<Anchor>): Boolean {
        var srcAnchor: Anchor
        var destAnchor: Anchor

        return false
    }


}