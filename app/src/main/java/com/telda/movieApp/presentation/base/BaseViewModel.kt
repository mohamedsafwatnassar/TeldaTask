package com.telda.movieApp.presentation.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.telda.movieApp.util.LoadingErrorState

abstract class BaseViewModel : ViewModel() {

    protected val _viewState = MutableLiveData<LoadingErrorState>()
    val viewState: LiveData<LoadingErrorState> = _viewState


}