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

package android.view;

import dev.rikka.tools.refine.RefineAs;

@RefineAs(View.class)
public class SemView {
    public static final int SEM_ROUNDED_CORNER_NONE = 0;
    public static final int SEM_ROUNDED_CORNER_ALL = 15;
    public static final int SEM_ROUNDED_CORNER_TOP_LEFT = 1;
    public static final int SEM_ROUNDED_CORNER_TOP_RIGHT = 2;
    public static final int SEM_ROUNDED_CORNER_BOTTOM_LEFT = 4;
    public static final int SEM_ROUNDED_CORNER_BOTTOM_RIGHT = 8;

    public void semSetRoundedCorners(int corners) {
        throw new RuntimeException("Stub!");
    }

    public int semGetRoundedCorners() {
        throw new RuntimeException("Stub!");
    }

    public void semSetRoundedCornerColor(int corners, int color) {
        throw new RuntimeException("Stub!");
    }

    @Deprecated
    public int semGetRoundedCornerColor(int corner) {
        throw new RuntimeException("Stub!");
    }
}
