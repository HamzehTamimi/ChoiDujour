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

package io.mesalabs.oneui.support.widget;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.StringRes;
import androidx.appcompat.util.SeslMisc;
import androidx.appcompat.widget.SeslProgressBar;
import androidx.appcompat.widget.SeslToggleSwitch;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;

import java.util.ArrayList;
import java.util.List;

import io.mesalabs.oneui.R;

/**
 * Part of the code has been kanged from:
 * https://github.com/OneUIProject/oneui-core/blob/sesl4/appcompat/src/main/java/androidx/appcompat/widget/SeslSwitchBar.java
 */
public class SwitchBar extends LinearLayout implements CompoundButton.OnCheckedChangeListener {

    public interface OnSwitchChangeListener {
        /**
         * Called when the checked state of the Switch has changed.
         *
         * @param switchView The Switch view whose state has changed.
         * @param isChecked  The new checked state of switchView.
         */
        void onSwitchChanged(SwitchCompat switchView, boolean isChecked);
    }

    private static final int SWITCH_ON_STRING_RESOURCE_ID = androidx.appcompat.R.string.sesl_switchbar_on_text;
    private static final int SWITCH_OFF_STRING_RESOURCE_ID = androidx.appcompat.R.string.sesl_switchbar_off_text;

    private final List<OnSwitchChangeListener> mSwitchChangeListeners = new ArrayList<>();
    private SwitchBarDelegate mDelegate;
    private String mSessionDesc = null;

    private SeslToggleSwitch mSwitch;
    private SeslProgressBar mProgressBar;
    private TextView mTextView;
    private String mLabel;
    @StringRes
    private int mOnTextId;
    @ColorInt
    private int mOnTextColor;
    @StringRes
    private int mOffTextId;
    @ColorInt
    private int mOffTextColor;
    private LinearLayout mBackground;
    @ColorInt
    private int mBackgroundColor;
    @ColorInt
    private int mBackgroundActivatedColor;

    public SwitchBar(Context context) {
        this(context, null);
    }

    public SwitchBar(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.switchBarStyle);
    }

    public SwitchBar(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SwitchBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        LayoutInflater.from(context).inflate(R.layout.oui_view_switch_bar, this);

        final Resources res = getResources();

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SwitchBar, defStyleAttr, defStyleRes);
        final int switchBarMarginStart = (int) res.getDimension(R.dimen.sep_switch_bar_margin_start);
        final int switchBarMarginEnd = (int) res.getDimension(R.dimen.sep_switch_bar_margin_end);
        mBackgroundColor = a.getColor(R.styleable.SwitchBar_switchBarBackgroundColor,
                context.getColor(R.color.sep_theme_switch_bar_off_background_color));
        mBackgroundActivatedColor = a.getColor(R.styleable.SwitchBar_switchBarBackgroundActivatedColor,
                context.getColor(R.color.sep_theme_switch_bar_on_background_color));
        mOnTextColor = a.getColor(R.styleable.SwitchBar_switchBarTextActivatedColor,
                context.getColor(R.color.sep_theme_switch_bar_on_text_color));
        mOffTextColor = a.getColor(R.styleable.SwitchBar_switchBarTextColor,
                context.getColor(R.color.sep_theme_switch_bar_off_text_color));
        a.recycle();

        mProgressBar = findViewById(R.id.switchbar_progress);

        mBackground = findViewById(R.id.switchbar_container);
        mBackground.setOnClickListener(v -> {
            if (mSwitch != null && mSwitch.isEnabled()) {
                mSwitch.setChecked(!mSwitch.isChecked());
            }
        });

        mOnTextId = SWITCH_ON_STRING_RESOURCE_ID;
        mOffTextId = SWITCH_OFF_STRING_RESOURCE_ID;

        mTextView = findViewById(R.id.switchbar_text);
        MarginLayoutParams lp = (MarginLayoutParams) mTextView.getLayoutParams();
        lp.setMarginStart(switchBarMarginStart);

        mSwitch = findViewById(R.id.switchbar_switch);
        // Prevent onSaveInstanceState() to be called as we are managing the state of the Switch
        // on our own
        mSwitch.setSaveEnabled(false);
        // Set the ToggleSwitch non-focusable and non-clickable to avoid multiple focus.
        mSwitch.setFocusable(false);
        mSwitch.setClickable(false);
        mSwitch.setOnCheckedChangeListener(this);

        setSwitchBarText(mOnTextId, mOffTextId);

        addOnSwitchChangeListener((switchView, isChecked) -> setTextViewLabelAndBackground(isChecked));

        lp = (MarginLayoutParams) mSwitch.getLayoutParams();
        lp.setMarginEnd(switchBarMarginEnd);

        mDelegate = new SwitchBarDelegate(this);
        ViewCompat.setAccessibilityDelegate(mBackground, mDelegate);

        setSessionDescription(getActivityTitle());
    }

    // Override the performClick method to eliminate redundant click.
    @Override
    public boolean performClick() {
        return mSwitch.performClick();
    }

    public void setProgressBarVisible(boolean visible) {
        try {
            mProgressBar.setVisibility(visible ?
                    View.VISIBLE : View.GONE);
        } catch (IndexOutOfBoundsException e) {
            Log.i("SetProgressBarVisible", "Invalid argument" + e);
        }
    }

    public void setTextViewLabelAndBackground(boolean isChecked) {
        mLabel = getResources().getString(isChecked ? mOnTextId : mOffTextId);
        DrawableCompat.setTintList(DrawableCompat.wrap(mBackground.getBackground()).mutate(),
                ColorStateList.valueOf(isChecked ? mBackgroundActivatedColor : mBackgroundColor));
        mTextView.setTextColor(isChecked ? mOnTextColor : mOffTextColor);

        if (isEnabled()) {
            mTextView.setAlpha(1.0f);
        } else if (SeslMisc.isLightTheme(getContext()) && isChecked) {
            mTextView.setAlpha(0.55f);
        } else {
            mTextView.setAlpha(0.4f);
        }

        if (mLabel == null || !mLabel.contentEquals(mTextView.getText())) {
            mTextView.setText(mLabel);
        }
    }

    public void setTextViewLabel(boolean isChecked) {
        mLabel = getResources().getString(isChecked ? mOnTextId : mOffTextId);
        mTextView.setText(mLabel);
    }

    public void setSessionDescription(String sessionDescription) {
        mSessionDesc = sessionDescription;
        mDelegate.setSessionName(sessionDescription);
    }

    public void setSwitchBarText(int onTextId, int offTextId) {
        mOnTextId = onTextId;
        mOffTextId = offTextId;
        setTextViewLabelAndBackground(isChecked());
    }

    public void setChecked(boolean checked) {
        setTextViewLabelAndBackground(checked);
        mSwitch.setChecked(checked);
    }

    public void setCheckedInternal(boolean checked) {
        setTextViewLabelAndBackground(checked);
        mSwitch.setCheckedInternal(checked);
    }

    public boolean isChecked() {
        return mSwitch.isChecked();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        mTextView.setEnabled(enabled);
        mSwitch.setEnabled(enabled);
        mBackground.setEnabled(enabled);
        setTextViewLabelAndBackground(isChecked());
    }

    public final SeslToggleSwitch getSwitch() {
        return mSwitch;
    }

    public void show() {
        if (!isShowing()) {
            setVisibility(View.VISIBLE);
            mSwitch.setOnCheckedChangeListener(this);
        }
        if (TextUtils.isEmpty(mSessionDesc)) {
            mDelegate.setSessionName(getActivityTitle());
        } else {
            mDelegate.setSessionName(mSessionDesc);
        }
    }

    public void hide() {
        if (isShowing()) {
            setVisibility(View.GONE);
            mSwitch.setOnCheckedChangeListener(null);
        }
        mDelegate.setSessionName(" ");
        mSessionDesc = null;
    }

    public boolean isShowing() {
        return (getVisibility() == View.VISIBLE);
    }

    private void propagateChecked(boolean isChecked) {
        final int count = mSwitchChangeListeners.size();
        for (int n = 0; n < count; n++) {
            mSwitchChangeListeners.get(n).onSwitchChanged(mSwitch, isChecked);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        propagateChecked(isChecked);
    }

    public void addOnSwitchChangeListener(OnSwitchChangeListener listener) {
        if (mSwitchChangeListeners.contains(listener)) {
            throw new IllegalStateException("Cannot add twice the same OnSwitchChangeListener");
        }
        mSwitchChangeListeners.add(listener);
    }

    public void removeOnSwitchChangeListener(OnSwitchChangeListener listener) {
        if (!mSwitchChangeListeners.contains(listener)) {
            throw new IllegalStateException("Cannot remove OnSwitchChangeListener");
        }
        mSwitchChangeListeners.remove(listener);
    }

    static class SavedState extends BaseSavedState {
        boolean checked;
        boolean visible;

        SavedState(Parcelable superState) {
            super(superState);
        }

        /**
         * Constructor called from {@link #CREATOR}
         */
        private SavedState(Parcel in) {
            super(in);
            checked = (Boolean) in.readValue(null);
            visible = (Boolean) in.readValue(null);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeValue(checked);
            out.writeValue(visible);
        }

        @Override
        public String toString() {
            return "SwitchBar.SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " checked=" + checked
                    + " visible=" + visible + "}";
        }

        public static final Creator<SavedState> CREATOR
                = new Creator<SavedState>() {

            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }


    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        SavedState ss = new SavedState(superState);
        ss.checked = mSwitch.isChecked();
        ss.visible = isShowing();
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;

        super.onRestoreInstanceState(ss.getSuperState());

        mSwitch.setCheckedInternal(ss.checked);
        setTextViewLabelAndBackground(ss.checked);
        setVisibility(ss.visible ? View.VISIBLE : View.GONE);
        mSwitch.setOnCheckedChangeListener(ss.visible ? this : null);

        requestLayout();
    }

    private String getActivityTitle() {
        Context context = getContext();

        while (context instanceof ContextWrapper) {
            context = ((ContextWrapper) context).getBaseContext();
            if (context instanceof Activity) {
                CharSequence title = ((Activity) context).getTitle();
                return title != null ? title.toString() : "";
            }
        }

        return "";
    }

    private static class SwitchBarDelegate extends AccessibilityDelegateCompat {
        private String mSessionName = "";
        private SeslToggleSwitch mSwitch;
        private TextView mText;

        public SwitchBarDelegate(View switchBar) {
            mText = switchBar.findViewById(R.id.switchbar_text);
            mSwitch = switchBar.findViewById(R.id.switchbar_switch);
        }

        public void setSessionName(String sessionName) {
            mSessionName = sessionName;
        }

        @Override
        public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfoCompat info) {
            super.onInitializeAccessibilityNodeInfo(host, info);
            mSwitch.setContentDescription(mText.getText());
            if (!TextUtils.isEmpty(mSessionName)) {
                info.setText(mSessionName);
            }
        }
    }

    public void updateHorizontalMargins() {
        final Resources res = getResources();
        if (mTextView != null) {
            MarginLayoutParams lp = (MarginLayoutParams) mTextView.getLayoutParams();
            lp.setMarginStart((int) res.getDimension(R.dimen.sep_switch_bar_margin_start));
            mTextView.setLayoutParams(lp);
        }
        if (mSwitch != null) {
            MarginLayoutParams lp = (MarginLayoutParams) mSwitch.getLayoutParams();
            lp.setMarginEnd((int) res.getDimension(R.dimen.sep_switch_bar_margin_end));
            mSwitch.setLayoutParams(lp);
        }
    }

    @Override
    public CharSequence getAccessibilityClassName() {
        return SwitchBar.class.getName();
    }
}
