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

package io.mesalabs.choidujour.ui.widget

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.annotation.ColorInt

import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SeslProgressBar

import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieListener
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.model.KeyPath

import io.mesalabs.oneui.support.utils.TypefaceUtils

import io.mesalabs.choidujour.R

class UpdateStatusView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {
    companion object {
        private const val TAG: String = "UpdateStatusView"

        const val STATE_NO_UPDATES = 1
        const val STATE_NEW_VERSION_AVAILABLE = 2
        const val STATE_ERROR = 3
        const val STATE_CHECKING = 4
        const val STATE_DOWNLOADED = 5

        private const val VIEW_MIN_HEIGHT: Float = 56.0f
        private const val VIEW_PADDING_HORIZONTAL: Float = 24.0f
        private const val VIEW_PADDING_VERTICAL: Float = 16.0f
    }

    private val progressBar: SeslProgressBar
    private val statusContainer: RelativeLayout
    private val lottieIconView: LottieAnimationView
    private val textView: AppCompatTextView

    @ColorInt private val iconTintColor: Int

    init {
        removeAllViews()

        setBackgroundColor(context.getColor(
            io.mesalabs.oneui.R.color.sep_theme_round_and_bg_color))

        minimumHeight = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            VIEW_MIN_HEIGHT,
            context.resources.displayMetrics
        ).toInt()

        val horizontalPadding: Int = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            VIEW_PADDING_HORIZONTAL,
            context.resources.displayMetrics
        ).toInt()
        val verticalPadding: Int = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            VIEW_PADDING_VERTICAL,
            context.resources.displayMetrics
        ).toInt()
        setPaddingRelative(
            horizontalPadding,
            verticalPadding,
            horizontalPadding,
            verticalPadding
        )

        inflate(context, R.layout.view_update_status_layout, this)

        progressBar = findViewById(R.id.progress_bar)
        statusContainer = findViewById(R.id.status_container)
        lottieIconView = findViewById(R.id.status_icon)
        textView = findViewById(R.id.status_text)

        textView.typeface = TypefaceUtils.getBoldSupportTypeface()

        iconTintColor = context.getColor(R.color.update_status_view_icon_color)
    }

    fun setUpdateStatus(status: Int) {
        if (status == STATE_CHECKING) {
            statusContainer.visibility = GONE
            progressBar.visibility = VISIBLE
            lottieIconView.setImageDrawable(null)
            textView.text = null
            return
        } else {
            progressBar.visibility = GONE
            statusContainer.visibility = VISIBLE
        }

        val icon: String
        val text: String
        val listener: LottieListener<LottieComposition>

        when (status) {
            STATE_NO_UPDATES -> {
                icon = "USV_no_updates.json"
                text = context.getString(R.string.usv_no_updates)
                listener = LottieListener<LottieComposition> {
                    lottieIconView.setComposition(it)
                    lottieIconView.addValueCallback(
                        KeyPath("**"), LottieProperty.COLOR_FILTER) {
                        PorterDuffColorFilter(iconTintColor, PorterDuff.Mode.SRC_IN)
                    }
                    lottieIconView.speed = 0.7f
                    lottieIconView.scaleX = 1.4f
                    lottieIconView.scaleY = 1.4f
                    lottieIconView.setMinAndMaxProgress(0.5f, 1f)
                    lottieIconView.playAnimation()
                }
            }

            STATE_NEW_VERSION_AVAILABLE -> {
                icon = "USV_new_version_available.json"
                text = context.getString(R.string.usv_new_version_available)
                listener = LottieListener<LottieComposition> {
                    lottieIconView.setComposition(it)
                    lottieIconView.addValueCallback(
                        KeyPath("**"), LottieProperty.COLOR_FILTER) {
                        PorterDuffColorFilter(iconTintColor, PorterDuff.Mode.SRC_IN)
                    }
                    lottieIconView.speed = 1.0f
                    lottieIconView.scaleX = 1.25f
                    lottieIconView.scaleY = 1.25f
                    lottieIconView.setMinAndMaxProgress(0.0f, 1.0f)
                    lottieIconView.playAnimation()
                }
            }

            STATE_ERROR -> {
                icon = "USV_error.json"
                text = context.getString(R.string.usv_error)
                listener = LottieListener<LottieComposition> {
                    lottieIconView.setComposition(it)
                    lottieIconView.addValueCallback(
                        KeyPath("**"), LottieProperty.COLOR_FILTER) {
                        PorterDuffColorFilter(iconTintColor, PorterDuff.Mode.SRC_IN)
                    }
                    lottieIconView.speed = 0.7f
                    lottieIconView.scaleX = 1.4f
                    lottieIconView.scaleY = 1.4f
                    lottieIconView.setMinAndMaxProgress(0.0f, 1.0f)
                    lottieIconView.playAnimation()
                }
            }

            STATE_DOWNLOADED -> {
                icon = "USV_downloaded.json"
                text = context.getString(R.string.usv_downloaded)
                listener = LottieListener<LottieComposition> {
                    lottieIconView.setComposition(it)
                    lottieIconView.addValueCallback(
                        KeyPath("**"), LottieProperty.COLOR_FILTER) {
                        PorterDuffColorFilter(iconTintColor, PorterDuff.Mode.SRC_IN)
                    }
                    lottieIconView.speed = 0.6f
                    lottieIconView.scaleX = 1.33f
                    lottieIconView.scaleY = 1.33f
                    lottieIconView.setMinAndMaxProgress(0.55f, 1.0f)
                    lottieIconView.playAnimation()
                }
            }

            else -> return
        }

        LottieCompositionFactory.fromAsset(context, icon).addListener(listener)
        textView.text = text
    }
}
