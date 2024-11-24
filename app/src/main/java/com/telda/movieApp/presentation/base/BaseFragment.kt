package com.telda.android_task.presentation.base

import android.widget.Toast
import androidx.fragment.app.Fragment
import com.telda.movieApp.util.LoadingViewManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class BaseFragment : Fragment() {


    override fun onStop() {
        super.onStop()
        hideLoading()
    }


    protected fun showToast(text: String) {
        try {
            Toast.makeText(requireContext(), text, Toast.LENGTH_LONG).show()
        } catch (ex: Exception) {
        }
    }

    protected fun showLoading() {
        (requireActivity() as LoadingViewManager).showLoading()
    }

    protected fun hideLoading() {
        (requireActivity() as LoadingViewManager).hideLoading()
    }


}
