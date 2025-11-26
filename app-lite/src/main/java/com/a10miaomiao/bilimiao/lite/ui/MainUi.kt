package com.a10miaomiao.bilimiao.lite.ui

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.FragmentContainerView
import com.a10miaomiao.bilimiao.config.config
import splitties.dimensions.dip
import splitties.views.backgroundColor
import splitties.views.dsl.core.*

class MainUi(
    override val ctx: Context,
    private val startViewWrapper: StartViewWrapper
) : Ui {

    val mContainerView = inflate<View>(R.layout.container_fragment) {
        backgroundColor = android.graphics.Color.TRANSPARENT
    }

    override val root = view<FrameLayout>() {
        backgroundColor = config.windowBackgroundColor
        
        addView(mContainerView, lParams {
            width = matchParent
            height = matchParent
        })
        
        addView(startViewWrapper.getView(), lParams {
            width = matchParent
            height = matchParent
        })
    }
}