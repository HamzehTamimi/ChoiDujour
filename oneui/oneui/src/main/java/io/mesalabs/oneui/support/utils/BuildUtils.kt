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

import android.os.SemBuild

object BuildUtils {
    private const val ONEUI_1_0: Int = 100000

    @JvmStatic
    fun isSemDevice(): Boolean {
        try {
            val semVersion: Int = SemBuild.VERSION.SEM_INT
            return true
        } catch (e: NoSuchFieldError) {
            return false
        }
    }

    @JvmStatic
    fun isOneUI(): Boolean {
        return getSEPVersion() >= ONEUI_1_0
    }

    @JvmStatic
    fun getSEPVersion(): Int {
        try {
            val sepVersion: Int = SemBuild.VERSION.SEM_PLATFORM_INT
            return sepVersion
        } catch (e: NoSuchFieldError) {
            return -1
        }
    }
}