package com.telda.movieApp.util.extention

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.telda.movieApp.R

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}


inline fun View.onDebouncedListener(
    delayInClick: Long = 500L,
    crossinline listener: (View) -> Unit,
) {
    val enableAgain = Runnable { isEnabled = true }
    setOnClickListener {
        if (isEnabled) {
            isEnabled = false
            postDelayed(enableAgain, delayInClick)
            listener(it)
        }
    }
}

fun String?.isEmpty(): Boolean {
    return (TextUtils.isEmpty(this ?: "".trim()) || this!!.trim()
        .equals("", ignoreCase = true) || this.trim().equals("{}", ignoreCase = true) || this.trim()
        .equals("null", ignoreCase = true) || this.trim().equals("undefined", ignoreCase = true))
}

fun NavController.customNavigate(
    @IdRes destinationId: Int,
    data: Bundle? = null,
) {
    val navOption = NavOptions.Builder().apply {
        setEnterAnim(R.anim.slide_from_out_right_to_center)
        setExitAnim(R.anim.slide_from_center_to_out_left)
        setPopEnterAnim(R.anim.slide_from_out_left_to_center)
        setPopExitAnim(R.anim.slide_from_center_to_out_right)
    }.build()
    navigate(destinationId, data, navOption)
}

