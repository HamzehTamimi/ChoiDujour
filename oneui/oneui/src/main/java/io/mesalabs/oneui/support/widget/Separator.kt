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
import android.util.AttributeSet
import android.view.ViewGroup.LayoutParams
import android.widget.TextView

import androidx.appcompat.R

class Separator@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.listSeparatorTextViewStyle,
    defStyleRes: Int = R.style.Widget_AppCompat_Light_TextView_ListSeparator
) : TextView(context, attrs, defStyleAttr, defStyleRes) {

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)

        val lp = layoutParams
        lp.height =
            if (text.isNotEmpty())
                LayoutParams.WRAP_CONTENT
            else
                resources.getDimensionPixelSize(
                    R.dimen.sesl_list_subheader_min_height)
        layoutParams = lp
    }

}
