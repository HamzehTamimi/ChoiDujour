package io.mesalabs.choidujour.ui.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import io.mesalabs.choidujour.R

class ListItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {
    companion object {
        private const val TAG: String = "HomeItemView"

        private const val VIEW_MIN_HEIGHT: Float = 56.0f
        private const val VIEW_PADDING_HORIZONTAL: Float = 24.0f
        private const val VIEW_PADDING_HORIZONTAL_ICON: Float = 20.0f
    }

    private val iconContainer: FrameLayout
    private val iconImageView: AppCompatImageView
    private val titleTextView: AppCompatTextView
    private val summaryTextView: AppCompatTextView
    private val badgeContainer: FrameLayout

    init {
        removeAllViews()

        val outValue = TypedValue()
        context.theme.resolveAttribute(
            androidx.appcompat.R.attr.listChoiceBackgroundIndicator,
            outValue,
            true
        )
        if (outValue.resourceId > 0) {
            setBackgroundResource(outValue.resourceId);
        } else {
            Log.w(TAG, "Couldn't retrieve listChoiceBackgroundIndicator!");
        }

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
        setPaddingRelative(
            horizontalPadding,
            0,
            horizontalPadding,
            0
        )

        gravity = Gravity.CENTER_VERTICAL

        orientation = HORIZONTAL
        inflate(context, R.layout.view_list_item_layout, this)

        iconContainer = findViewById(R.id.home_list_item_icon_container)
        iconImageView = findViewById(R.id.home_list_item_icon)
        titleTextView = findViewById(R.id.home_list_item_title)
        summaryTextView = findViewById(R.id.home_list_item_summary)
        badgeContainer = findViewById(R.id.home_list_item_badge_container)
    }

    var icon: Drawable? = null
        set(icon) {
            field = icon

            val hasIcon = icon != null
            iconContainer.visibility =
                if (hasIcon) VISIBLE else GONE
            iconImageView.setImageDrawable(icon)

            val startPadding = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                if (hasIcon) VIEW_PADDING_HORIZONTAL_ICON else VIEW_PADDING_HORIZONTAL,
                resources.displayMetrics
            ).toInt()
            val endPadding = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                VIEW_PADDING_HORIZONTAL,
                resources.displayMetrics
            ).toInt()
            setPaddingRelative(startPadding, 0, endPadding, 0)
        }

    var title: CharSequence? = null
        set(title) {
            field = title.also { titleTextView.text = it }
        }

    var summary: CharSequence? = null
        set(summary) {
            field = summary

            val isTextEmpty = summary.isNullOrBlank()
            summaryTextView.visibility = if (isTextEmpty) GONE else VISIBLE
            summaryTextView.text = summary
        }

    var showBadge: Boolean = false
        set(show) {
            field = show.also {
                badgeContainer.visibility = if (show) VISIBLE else GONE
            }
        }
}