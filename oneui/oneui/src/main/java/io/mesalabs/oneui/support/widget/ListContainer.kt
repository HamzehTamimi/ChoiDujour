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

package io.mesalabs.oneui.support.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View

import io.mesalabs.oneui.R

class ListContainer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : RoundedLinearLayout(context, attrs, defStyleAttr, defStyleRes) {
    private val divider: Drawable
    private val showDivider: Boolean

    init {
        orientation = VERTICAL
        divider = context.getDrawable(R.drawable.sep_list_divider)!!

        val a = context.obtainStyledAttributes(attrs, R.styleable.ListContainer)
        showDivider = a.getBoolean(R.styleable.ListContainer_showDivider, true)
        a.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (showDivider) {
            for (i in 0 until childCount) {
                val child: View = getChildAt(i)

                val top: Int = (child.bottom
                        + (child.layoutParams as MarginLayoutParams).bottomMargin)
                val bottom: Int = divider.intrinsicHeight + top
                divider.setBounds(left, top, right, bottom)
                divider.draw(canvas)
            }
        }
    }
}
