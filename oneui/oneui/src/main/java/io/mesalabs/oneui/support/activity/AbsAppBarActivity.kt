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

import android.view.SemWindowManager.LayoutParams.SEM_EXTENSION_FLAG_RESIZE_FULLSCREEN_WINDOW_ON_SOFT_INPUT
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT

import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.SemView
import android.view.SemWindowManager
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout

import androidx.annotation.LayoutRes
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat

import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout

import dev.rikka.tools.refine.Refine

import io.mesalabs.oneui.R
import io.mesalabs.oneui.support.base.BaseActivity
import io.mesalabs.oneui.support.utils.BuildUtils

/**
 * Part of the code has been kanged from:
 * https://github.com/OneUIProject/oneui-design/blob/main/lib/src/main/java/dev/oneuiproject/oneui/layout/ToolbarLayout.java
 */
abstract class AbsAppBarActivity : BaseActivity() {
    protected val TAG: String = javaClass.simpleName

    // AppBar flags
    protected var appBarExpandable: Boolean = true
        set(expandable) {
            field = expandable

            if (expandable) {
                appBarLayout.isEnabled = true
                appBarLayout.seslSetCustomHeightProportion(false, 0.3f)
            } else {
                appBarLayout.isEnabled = false
                appBarLayout.seslSetCustomHeight(resources.getDimensionPixelSize(
                    androidx.appcompat.R.dimen.sesl_action_bar_height_with_padding))
            }
        }

    protected var appBarExpanded: Boolean = true
        get() = field and !appBarLayout.seslIsCollapsed()
        set(expanded) {
            field = expanded
            if (!appBarLayout.seslIsCollapsed() != field)
                appBarLayout.setExpanded(expanded, ViewCompat.isLaidOut(appBarLayout))
        }

    // Toolbar items
    protected var navigationButtonIcon: Drawable? = null
        set(icon) {
            if (field != null && this is DrawerActivity || this is SearchActivity) {
                Log.e(TAG, "setNavigationButtonIcon: this method is only supported by AppBarActivity.")
                return
            }

            val outValue = TypedValue()
            theme.resolveAttribute(
                androidx.appcompat.R.attr.actionMenuTextColor, outValue, true)
            if (outValue.resourceId > 0 && icon != null) {
                DrawableCompat.setTintList(icon, getColorStateList(outValue.resourceId))
            }
            toolbar.navigationIcon = icon.also { field = it }
        }

    protected var appBarTitleExpanded: CharSequence? = null
        set(title) {
            field = title
            collapsingToolbarLayout.title = title
        }

    protected var appBarTitleCollapsed: CharSequence? = null
        set(title) {
            field = title
            toolbar.title = title
        }

    protected var appBarSubtitleExpanded: CharSequence? = null
        set(subtitle) {
            field = subtitle
            collapsingToolbarLayout.seslSetSubtitle(title)
        }

    protected var appBarSubtitleCollapsed: CharSequence? = null
        set(subtitle) {
            field = subtitle
            toolbar.subtitle = subtitle
        }

    // Abstract methods
    protected abstract fun onLayoutCreate()

    protected abstract val coordinatorLayout: CoordinatorLayout
    protected abstract val appBarLayout: AppBarLayout
    protected abstract val collapsingToolbarLayout: CollapsingToolbarLayout
    protected abstract val toolbar: Toolbar
    protected abstract val contentContainer: ViewGroup
    protected abstract val footerContainer: ViewGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onLayoutCreate()
        initToolbar()
        refreshLayout(resources.configuration)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        refreshLayout(newConfig)
    }

    override fun setContentView(@LayoutRes layoutResID: Int) {
        val inflater = LayoutInflater.from(mContext)
        setContentView(inflater.inflate(layoutResID, contentContainer, false))
    }

    override fun setContentView(view: View) {
        contentContainer.addView(view, FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT))
    }

    override fun setContentView(view: View, params: ViewGroup.LayoutParams?) {
        contentContainer.addView(view, params)
    }

    override fun setTitle(titleId: Int) {
        setAppBarTitle(getString(titleId))
    }

    override fun setTitle(title: CharSequence?) {
        setAppBarTitle(title)
    }

    private fun initToolbar() {
        if (!BuildUtils.isSemDevice()) {
            toolbar.setTitleTextAppearance(
                this, R.style.TextAppearance_OneUI_SeslToolbar_Title)
        }

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
    }

    private fun refreshLayout(newConfig: Configuration) {
        val isLandscape = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE
        applyLandscapeFullScreen(newConfig)
        appBarLayout.setExpanded(!isLandscape && appBarExpanded, false)
        setContentSideMargin(newConfig, contentContainer)
    }

    /*
     * Calls Super setContentView.
     */
    protected fun setRootView(view: View) {
        super.setContentView(view)
    }

    protected fun setRootView(view: View, params: ViewGroup.LayoutParams) {
        super.setContentView(view, params)
    }

    /*
     * AppBarLayout expanded methods.
     */
    fun setAppBarExpanded(expanded: Boolean, animate: Boolean) {
        if (appBarExpandable) {
            appBarLayout.setExpanded(expanded, animate)
            appBarExpanded = expanded
        } else {
            Log.d(TAG, "setExpanded: appBar is not expandable")
        }
    }

    /*
     * Toolbar navigation button methods.
     */
    open fun defaultHomeAsUp() {
        if (navigationButtonIcon == null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            navigationButtonIcon = getDrawable(R.drawable.tw_ic_ab_back_mtrl)
            setNavigationButtonOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    open fun setNavigationButtonTooltip(tooltipText: CharSequence?) {
        toolbar.navigationContentDescription = tooltipText
    }

    open fun setNavigationButtonOnClickListener(listener: View.OnClickListener?) {
        toolbar.setNavigationOnClickListener(listener)
    }

    /*
     * AppBar Title methods.
     */
    fun setAppBarTitle(title: CharSequence?) {
        appBarTitleExpanded = title
        appBarTitleCollapsed = title
    }

    fun setAppBarSubtitle(subtitle: CharSequence?) {
        appBarSubtitleExpanded = subtitle
        appBarSubtitleCollapsed = subtitle
    }

    /*
     * SeslCollapsingToolbar custom views.
     */
    fun setAppBarCustomTitleView(view: View) {
        setAppBarCustomTitleView(view, CollapsingToolbarLayout.LayoutParams(view.layoutParams))
    }

    fun setAppBarCustomTitleView(view: View, params: CollapsingToolbarLayout.LayoutParams?) {
        var lp: CollapsingToolbarLayout.LayoutParams? = params
        if (params == null) {
            lp = CollapsingToolbarLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        }

        lp!!.seslSetIsTitleCustom(true)
        collapsingToolbarLayout.seslSetCustomTitleView(view, lp)
    }

    fun setAppBarCustomSubtitleView(view: View?) {
        collapsingToolbarLayout.seslSetCustomSubtitle(view)
    }

    /*
     * CoordinatorLayout methods.
     */
    fun addViewInRoot(view: View) {
        addViewInRoot(view, null)
    }

    fun addViewInRoot(view: View, params: CoordinatorLayout.LayoutParams?) {
        var lp: CoordinatorLayout.LayoutParams? = params
        if (params == null) {
            lp = CoordinatorLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        }

        coordinatorLayout.addView(view, lp)
    }

    /*
     * Footer methods.
     */
    fun addFooterView(view: View) {
        footerContainer.addView(
            view, LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
        if (BuildUtils.isOneUI()) {
            Refine.unsafeCast<SemView>(window.decorView)
                .semSetRoundedCorners(SemView.SEM_ROUNDED_CORNER_NONE)
        }
    }

    fun addFooterView(view: View, params: ViewGroup.LayoutParams) {
        footerContainer.addView(view, params)
        if (BuildUtils.isOneUI()) {
            Refine.unsafeCast<SemView>(window.decorView)
                .semSetRoundedCorners(SemView.SEM_ROUNDED_CORNER_NONE)
        }
    }

    /*
     * Misc
     */
    private fun applyLandscapeFullScreen(config: Configuration) {
        if (!isInMultiWindowMode
            && config.smallestScreenWidthDp < 420
            && config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            window.insetsController!!.hide(WindowInsets.Type.statusBars())
        } else {
            window.insetsController!!.show(WindowInsets.Type.statusBars())
        }

        if (BuildUtils.isSemDevice()) {
            val attributes: WindowManager.LayoutParams = window.attributes
            Refine.unsafeCast<SemWindowManager.LayoutParams>(attributes)
                .semAddExtensionFlags(SEM_EXTENSION_FLAG_RESIZE_FULLSCREEN_WINDOW_ON_SOFT_INPUT)
            window.attributes = attributes
        }
    }

    private fun setContentSideMargin(config: Configuration, layout: ViewGroup) {
        if (!isDestroyed && !isFinishing) {
            findViewById<View>(android.R.id.content).post {
                var m: Int = getSideMargin(config)
                if (m < 0)
                    m = 0

                val lp: MarginLayoutParams = layout.layoutParams as MarginLayoutParams
                lp.setMargins(m, 0, m, 0)
                layout.layoutParams = lp
            }
        }
    }

    private fun getSideMargin(config: Configuration): Int {
        val ratio = {
            val screenWidthDp: Int = config.screenWidthDp
            val screenHeightDp: Int = config.screenHeightDp
            if (screenWidthDp in 589..959) {
                (screenWidthDp * if (screenHeightDp < 411) 1.0f else 0.86f).toInt()
            } else if (screenWidthDp >= 960) {
                840
            } else {
                screenWidthDp
            }
        }

        val density = resources.displayMetrics.density
        return ((config.screenWidthDp - ratio()) / 2 * density).toInt()
    }
}