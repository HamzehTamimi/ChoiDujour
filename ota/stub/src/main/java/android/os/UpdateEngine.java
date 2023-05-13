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

import android.content.res.AssetFileDescriptor;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

public class UpdateEngine {
    public static final class ErrorCodeConstants {
        public static final int SUCCESS = 0;
        public static final int ERROR = 1;
        public static final int FILESYSTEM_COPIER_ERROR = 4;
        public static final int POST_INSTALL_RUNNER_ERROR = 5;
        public static final int PAYLOAD_MISMATCHED_TYPE_ERROR = 6;
        public static final int INSTALL_DEVICE_OPEN_ERROR = 7;
        public static final int KERNEL_DEVICE_OPEN_ERROR = 8;
        public static final int DOWNLOAD_TRANSFER_ERROR = 9;
        public static final int PAYLOAD_HASH_MISMATCH_ERROR = 10;
        public static final int PAYLOAD_SIZE_MISMATCH_ERROR = 11;
        public static final int DOWNLOAD_PAYLOAD_VERIFICATION_ERROR = 12;
        public static final int PAYLOAD_TIMESTAMP_ERROR = 51;
        public static final int UPDATED_BUT_NOT_ACTIVE = 52;
        public static final int NOT_ENOUGH_SPACE = 60;
        public static final int DEVICE_CORRUPTED = 61;
    }

    @IntDef(value = {
            ErrorCodeConstants.SUCCESS,
            ErrorCodeConstants.ERROR,
            ErrorCodeConstants.FILESYSTEM_COPIER_ERROR,
            ErrorCodeConstants.POST_INSTALL_RUNNER_ERROR,
            ErrorCodeConstants.PAYLOAD_MISMATCHED_TYPE_ERROR,
            ErrorCodeConstants.INSTALL_DEVICE_OPEN_ERROR,
            ErrorCodeConstants.KERNEL_DEVICE_OPEN_ERROR,
            ErrorCodeConstants.DOWNLOAD_TRANSFER_ERROR,
            ErrorCodeConstants.PAYLOAD_HASH_MISMATCH_ERROR,
            ErrorCodeConstants.PAYLOAD_SIZE_MISMATCH_ERROR,
            ErrorCodeConstants.DOWNLOAD_PAYLOAD_VERIFICATION_ERROR,
            ErrorCodeConstants.PAYLOAD_TIMESTAMP_ERROR,
            ErrorCodeConstants.UPDATED_BUT_NOT_ACTIVE,
            ErrorCodeConstants.NOT_ENOUGH_SPACE,
            ErrorCodeConstants.DEVICE_CORRUPTED,
    })
    public @interface ErrorCode {}

    public static final class UpdateStatusConstants {
        public static final int IDLE = 0;
        public static final int CHECKING_FOR_UPDATE = 1;
        public static final int UPDATE_AVAILABLE = 2;
        public static final int DOWNLOADING = 3;
        public static final int VERIFYING = 4;
        public static final int FINALIZING = 5;
        public static final int UPDATED_NEED_REBOOT = 6;
        public static final int REPORTING_ERROR_EVENT = 7;
        public static final int ATTEMPTING_ROLLBACK = 8;
        public static final int DISABLED = 9;
    }


    public UpdateEngine() {
        throw new RuntimeException("Stub!");
    }

    public boolean bind(final UpdateEngineCallback callback, final Handler handler) {
        throw new RuntimeException("Stub!");
    }

    public boolean bind(final UpdateEngineCallback callback) {
        throw new RuntimeException("Stub!");
    }

    public void applyPayload(String url, long offset, long size, String[] headerKeyValuePairs) {
        throw new RuntimeException("Stub!");
    }

    public void applyPayload(@NonNull AssetFileDescriptor assetFd,
                             @NonNull String[] headerKeyValuePairs) {
        throw new RuntimeException("Stub!");
    }

    public void cancel() {
        throw new RuntimeException("Stub!");
    }

    public void suspend() {
        throw new RuntimeException("Stub!");
    }

    public void resume() {
        throw new RuntimeException("Stub!");
    }

    public void resetStatus() {
        throw new RuntimeException("Stub!");
    }

    public void setShouldSwitchSlotOnReboot(@NonNull String payloadMetadataFilename) {
        throw new RuntimeException("Stub!");
    }

    public void resetShouldSwitchSlotOnReboot() {
        throw new RuntimeException("Stub!");
    }

    public boolean unbind() {
        throw new RuntimeException("Stub!");
    }

    public boolean verifyPayloadMetadata(String payloadMetadataFilename) {
        throw new RuntimeException("Stub!");
    }
}
