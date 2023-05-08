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

import android.os.Bundle
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout

import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout

import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout

import io.mesalabs.oneui.R
import io.mesalabs.oneui.databinding.OuiLayoutSwitchAppBarUiBinding
import io.mesalabs.oneui.support.widget.SwitchBar

open class SwitchBarActivity : AbsAppBarActivity() {
    private lateinit var mBinding: OuiLayoutSwitchAppBarUiBinding
    private lateinit var mFooterContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppBarExpanded(false, false)
    }

    override fun onLayoutCreate() {
        val rootView = LinearLayout(this)
        rootView.orientation = LinearLayout.VERTICAL

        mBinding = OuiLayoutSwitchAppBarUiBinding.inflate(
            LayoutInflater.from(this), rootView, false)

        val appBarLp = LinearLayout.LayoutParams(MATCH_PARENT, 0)
        appBarLp.weight = 1.0f

        rootView.addView(mBinding.root, appBarLp)

        mFooterContainer = LinearLayout(this)
        mFooterContainer.orientation = LinearLayout.VERTICAL
        mFooterContainer.setBackgroundColor(getColor(R.color.sep_theme_round_and_bg_color))
        val footerLp = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        rootView.addView(mFooterContainer, footerLp)

        setRootView(rootView)
    }

    override val coordinatorLayout: CoordinatorLayout
        get() = mBinding.coordinatorLayout
    override val appBarLayout: AppBarLayout
        get() = mBinding.appBarLayout
    override val collapsingToolbarLayout: CollapsingToolbarLayout
        get() = mBinding.collapsingToolbarLayout
    override val toolbar: Toolbar
        get() = mBinding.toolbar
    override val contentContainer: ViewGroup
        get() = mBinding.contentContainer
    override val footerContainer: ViewGroup
        get() = mFooterContainer

    protected val switchBar: SwitchBar
        get() = mBinding.switchBar
}