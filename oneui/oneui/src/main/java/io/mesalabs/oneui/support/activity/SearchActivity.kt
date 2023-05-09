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

import android.app.SearchManager
import android.os.Bundle
import android.view.View

import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.TooltipCompat

import io.mesalabs.oneui.R

open class SearchActivity : AppBarActivity() {
    private lateinit var mSearchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppBarExpanded(expanded = false, animate = false)
        initSearchView()
    }

    private fun initSearchView() {
        toolbar.setContentInsetsAbsolute(0, 0)
        mSearchView = SearchView(this)
        mSearchView.setIconifiedByDefault(false)
        val searchManager = getSystemService(SEARCH_SERVICE) as SearchManager
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        toolbar.addView(mSearchView)
    }

    protected val searchView : SearchView
        get() = mSearchView

    /*
     * SearchView navigation button methods.
     */
    override fun defaultHomeAsUp() {
        mSearchView.seslSetUpButtonVisibility(View.VISIBLE)
        setNavigationButtonOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun setNavigationButtonTooltip(tooltipText: CharSequence?) {
        TooltipCompat.setTooltipText(mSearchView.seslGetUpButton(), tooltipText)
    }

    override fun setNavigationButtonOnClickListener(listener: View.OnClickListener?) {
        mSearchView.seslSetOnUpButtonClickListener(listener)
    }

    /*
     * SearchView style methods.
     */
    fun showSearchViewRoundBg(showBg : Boolean) {
        mSearchView.background =
            if (showBg)
                getDrawable(R.drawable.sep_search_view_round_background)
            else
                null
    }
}