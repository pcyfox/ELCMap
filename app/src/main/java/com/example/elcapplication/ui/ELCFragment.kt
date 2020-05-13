package com.example.elcapplication.ui

import android.graphics.Rect
import android.graphics.RectF
import android.os.Bundle
import android.util.Log

import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.example.elcapplication.R
import com.example.elcapplication.widget.ElcViewGroup
import kotlinx.android.synthetic.main.elc_fragment.*

class ELCFragment(contentLayoutId: Int = R.layout.elc_fragment) : Fragment(contentLayoutId) {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onStart() {
        super.onStart()
        initView()
    }

    var newElcView: ElcViewGroup? = null

    private fun initView() {
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
                        newElcView = (v as ElcViewGroup).create()
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
                false
            }
        }
    }


}