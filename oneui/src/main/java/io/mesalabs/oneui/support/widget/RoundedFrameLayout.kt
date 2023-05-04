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
import android.content.res.TypedArray
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.FrameLayout

import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import androidx.appcompat.util.SeslRoundedCorner

import io.mesalabs.oneui.R

class RoundedFrameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {
    private val mRoundedCorner: SeslRoundedCorner

    init {
        val a: TypedArray = context.obtainStyledAttributes(
            attrs, R.styleable.RoundedFrameLayout)
        val roundedCorners = a.getInt(R.styleable.RoundedFrameLayout_roundedCorners,
            SeslRoundedCorner.ROUNDED_CORNER_ALL)
        a.recycle()

        mRoundedCorner = SeslRoundedCorner(context)
        mRoundedCorner.roundedCorners = roundedCorners
    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        mRoundedCorner.drawRoundedCorner(canvas)
    }

    @setparam:IntRange(from = 0, to = 15)
    var roundedCorners: Int
        get() = mRoundedCorner.roundedCorners
        set(corners) {
            mRoundedCorner.roundedCorners = corners
            invalidate()
        }

    fun setRoundedCornersColor(@IntRange(from = 0, to = 15) corners: Int,
                               @ColorInt color: Int) {
        mRoundedCorner.setRoundedCornerColor(corners, color)
        invalidate()
    }
}