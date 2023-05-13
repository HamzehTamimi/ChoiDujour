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

package android.os;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SystemProperties {

    @NonNull
    public static String get(@NonNull String key) {
        throw new RuntimeException("Stub!");
    }

    @NonNull
    public static String get(@NonNull String key, @Nullable String def) {
        throw new RuntimeException("Stub!");
    }

    public static int getInt(@NonNull String key, int def) {
        throw new RuntimeException("Stub!");
    }

    public static long getLong(@NonNull String key, long def) {
        throw new RuntimeException("Stub!");
    }

    public static boolean getBoolean(@NonNull String key, boolean def) {
        throw new RuntimeException("Stub!");
    }

    public static void set(@NonNull String key, @Nullable String val) {
        throw new RuntimeException("Stub!");
    }

}
