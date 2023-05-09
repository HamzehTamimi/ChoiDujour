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

package io.mesalabs.oneui.support.utils

import android.graphics.Typeface

import io.mesalabs.oneui.support.utils.BuildUtils.isSemDevice

object TypefaceUtils {
    private const val SEC_ROBOTO = "sec-roboto-light"
    private const val ROBOTO_REGULAR = "sans-serif"
    private const val ROBOTO_MEDIUM = "sans-serif-medium"

    @JvmStatic
    fun getNormalSupportTypeface(): Typeface {
        return if (isSemDevice()) {
            Typeface.create(SEC_ROBOTO, Typeface.NORMAL)
        } else {
            Typeface.create(ROBOTO_REGULAR, Typeface.NORMAL)
        }
    }

    @JvmStatic
    fun getBoldSupportTypeface(): Typeface {
        return if (isSemDevice()) {
            Typeface.create(SEC_ROBOTO, Typeface.BOLD)
        } else {
            Typeface.create(ROBOTO_MEDIUM, Typeface.NORMAL)
        }
    }
}
