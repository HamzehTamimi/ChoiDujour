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

import android.view.SoundEffectConstants

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class SupportGoToTopClickListener : RecyclerView.SeslOnGoToTopClickListener {
    override fun onGoToTopClick(view: RecyclerView): Boolean {
        if (view.computeVerticalScrollOffset() != 0) {
            view.stopScroll()

            val lm = view.layoutManager!!
            if (lm is StaggeredGridLayoutManager) {
                lm.scrollToPositionWithOffset(0, 0);
            } else {
                var startPosition: Int = view.childCount * 2

                if (startPosition > 0 && startPosition < findFirstVisibleItemPosition(lm)) {
                    if (lm is LinearLayoutManager) {
                        if (lm is GridLayoutManager) {
                            val spanCount = lm.spanCount
                            if (startPosition < spanCount) {
                                startPosition = spanCount
                            }
                        }

                        lm.scrollToPositionWithOffset(startPosition, 0)
                    } else {
                        view.scrollToPosition(startPosition);
                    }
                }

                view.post {
                    if (lm is SupportLinearLayoutManager) {
                        lm.smoothScrollToTop(view)
                    } else {
                        view.smoothScrollToPosition(0)
                    }
                }
            }
        }

        val autoHide = RecyclerView::class.java.getDeclaredMethod("autoHide", Integer.TYPE)
        autoHide.isAccessible = true
        autoHide.invoke(view, 0)

        view.playSoundEffect(SoundEffectConstants.CLICK)

        return true
    }

    private fun findFirstVisibleItemPosition(lm: RecyclerView.LayoutManager): Int {
        return when {
            (lm is LinearLayoutManager) -> lm.findFirstVisibleItemPosition()
            (lm is StaggeredGridLayoutManager) -> lm.findFirstVisibleItemPositions(null)[0]
            else -> -1
        }
    }
}
