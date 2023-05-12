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

package io.mesalabs.oneui.support.view.recyclerview

import android.content.Context
import android.view.View
import android.view.animation.PathInterpolator

import kotlin.math.sqrt

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView

class SupportLinearLayoutManager(context: Context) : LinearLayoutManager(context) {
    private val pathInterpolator = PathInterpolator(0.22f, 0.5f, 0.0f, 1.0f)

    fun smoothScrollToTop(recyclerView: RecyclerView) {
        val showGoToTop = RecyclerView::class.java.getDeclaredMethod("showGoToTop")
        showGoToTop.isAccessible = true
        showGoToTop.invoke(recyclerView)

        val scroller = GotoTopSmoothScroller(recyclerView.context)
        scroller.targetPosition = 0
        startSmoothScroll(scroller)
    }

    private inner class GotoTopSmoothScroller(context: Context) : LinearSmoothScroller(context) {
        override fun getVerticalSnapPreference(): Int {
            return SNAP_TO_START
        }

        override fun onTargetFound(targetView: View, state: RecyclerView.State, action: Action) {
            val dx: Int = calculateDxToMakeVisible(targetView, horizontalSnapPreference)
            val dy: Int = calculateDyToMakeVisible(targetView, verticalSnapPreference)
            val distance: Int = sqrt((dx * dx + dy * dy).toDouble()).toInt()

            if (calculateTimeForDeceleration(distance) > 0) {
                // No idea how Samsung calculates those
                var duration = ((distance * 2.0E-4 + 0.44999998807907104) * 1000.0).toInt()
                if (duration > 800) {
                    duration = 800
                }
                action.update(
                    -dx,
                    -dy,
                    duration,
                    pathInterpolator
                )
            }
        }
    }
}
