/*
 * ChoiDujour
 * Copyright (C) 2023 BlackMesa123
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.mesalabs.oneui.support.activity

import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Outline
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.SemView
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.WindowManager

import androidx.annotation.Dimension
import androidx.annotation.Px
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.drawerlayout.widget.DrawerLayout.SimpleDrawerListener

import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout

import dev.rikka.tools.refine.Refine

import io.mesalabs.oneui.R
import io.mesalabs.oneui.databinding.OuiLayoutDrawerUiBinding
import io.mesalabs.oneui.support.utils.BuildUtils

open class DrawerActivity : AbsAppBarActivity() {
    companion object {
        private const val DRAWER_CORNER_RADIUS = 15.0f
    }

    // Views
    private lateinit var mBinding: OuiLayoutDrawerUiBinding

    // Callbacks
    private val mDrawerListener: DrawerListener = DrawerListener()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDrawer()
    }

    override fun onLayoutCreate() {
        mBinding = OuiLayoutDrawerUiBinding.inflate(layoutInflater)
        setRootView(mBinding.root)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setDrawerWidth()
        if (mBinding.drawerLayout.isDrawerOpen(mBinding.drawerContentContainer)) {
            mBinding.drawerLayout.post {
                mDrawerListener.onDrawerSlide(mBinding.drawerContentContainer, 1.0f)
            }
        }
    }

    private fun initDrawer() {
        if (BuildUtils.isOneUI()) {
            Refine.unsafeCast<SemView>(window.decorView)
                .semSetRoundedCorners(SemView.SEM_ROUNDED_CORNER_NONE)
        }

        super.navigationButtonIcon = getDrawable(R.drawable.tw_ic_ab_drawer_mtrl)
        // TODO add localized string
        super.setNavigationButtonTooltip("Open drawer sar")
        super.setNavigationButtonOnClickListener {
            openDrawer(true, true)
        }

        mBinding.drawerLayout.setScrimColor(getColor(R.color.sep_drawerlayout_drawer_dim_color))
        mBinding.drawerLayout.drawerElevation = 0.0f
        mBinding.drawerLayout.addDrawerListener(mDrawerListener)

        setDrawerWidth()
        setDrawerCornerRadius(DRAWER_CORNER_RADIUS)
    }

    override val coordinatorLayout: CoordinatorLayout
        get() = mBinding.appBarContent.coordinatorLayout
    override val appBarLayout: AppBarLayout
        get() = mBinding.appBarContent.appBarLayout
    override val collapsingToolbarLayout: CollapsingToolbarLayout
        get() = mBinding.appBarContent.collapsingToolbarLayout
    override val toolbar: Toolbar
        get() = mBinding.appBarContent.toolbar
    override val contentContainer: ViewGroup
        get() = mBinding.appBarContent.contentContainer
    override val footerContainer: ViewGroup
        get() = mBinding.footerContainer

    /*
     * Toolbar navigation button methods.
     */
    override fun defaultHomeAsUp() {
        Log.e(TAG, "defaultHomeAsUp: not supported in DrawerLayout")
    }

    override fun setNavigationButtonTooltip(tooltipText: CharSequence?) {
        Log.e(TAG, "setNavigationButtonTooltip: not supported in DrawerLayout")
    }

    override fun setNavigationButtonOnClickListener(listener: View.OnClickListener?) {
        Log.e(TAG, "setNavigationButtonOnClickListener: not supported in DrawerLayout")
    }

    /*
     * Drawer methods.
     */
    private fun setDrawerWidth() {
        val wm: WindowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val size = wm.currentWindowMetrics

        val displayWidthPixel: Int = size.bounds.width()
        val density = resources.displayMetrics.density
        val displayWidthDp = displayWidthPixel.toFloat() / density

        val widthRate: Double = when {
            displayWidthDp >= 1920.0f -> 0.22
            displayWidthDp >= 960.0f -> 0.2734
            displayWidthDp >= 600.0f -> 0.46
            displayWidthDp >= 480.0f -> 0.5983
            else -> 0.844
        }

        val lp: ViewGroup.LayoutParams = mBinding.drawerContentContainer.layoutParams
        lp.width = (displayWidthPixel.toDouble() * widthRate).toInt()
    }

    fun setDrawerCornerRadius(@Dimension dp: Float) {
        val px = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).toInt()
        setDrawerCornerRadius(px)
    }

    fun setDrawerCornerRadius(@Px px: Int) {
        mBinding.drawerContentContainer.outlineProvider = DrawerOutlineProvider(px)
        mBinding.drawerContentContainer.clipToOutline = true
    }

    fun openDrawer(open: Boolean, animate: Boolean) {
        if (open) {
            mBinding.drawerLayout.openDrawer(mBinding.drawerContentContainer, animate)
        } else {
            mBinding.drawerLayout.closeDrawer(mBinding.drawerContentContainer, animate)
        }
    }

    private inner class DrawerOutlineProvider(
        @param:Px private val mCornerRadius: Int) : ViewOutlineProvider() {
        override fun getOutline(view: View, outline: Outline) {
            val isRtl: Boolean = view.layoutDirection == View.LAYOUT_DIRECTION_RTL

            outline.setRoundRect(
                if (isRtl) 0 else -mCornerRadius,
                0,
                if (isRtl) view.width + mCornerRadius else view.width, view.height,
                mCornerRadius.toFloat()
            )
        }
    }

    private inner class DrawerListener : SimpleDrawerListener() {
        override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            super.onDrawerSlide(drawerView, slideOffset)

            val isRtl: Boolean = drawerView.layoutDirection == View.LAYOUT_DIRECTION_RTL

            var slideX: Float = drawerView.width * slideOffset
            if (isRtl) slideX *= -1f
            mBinding.appBarContainer.translationX = slideX

            val hsv = FloatArray(3)
            Color.colorToHSV(getColor(R.color.sep_theme_round_and_bg_color), hsv)
            hsv[2] *= 1.0f - slideOffset * 0.2f

            val systemBarsColor = Color.HSVToColor(hsv)
            window.statusBarColor = systemBarsColor
            window.navigationBarColor = systemBarsColor
        }
    }
}