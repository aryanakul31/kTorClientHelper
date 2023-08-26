package com.nakul.ktorexample

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nakul.ktorexample.api_handling.ApiInterface
import com.nakul.ktorexample.api_helper.ApiCalling
import kotlinx.coroutines.launch

class MainVM : ViewModel() {

    fun hitApi(context: Context) = viewModelScope.launch {
        ApiCalling.hitApi(
            context = context,
            layoutId = R.layout.progress_loader,
            requestHandler = { ApiInterface.getUserData() },
            onResponse = {
                Log.e("Data onResponse", it.articles?.get(0).toString())
            }
        )
    }

    fun hitCacheApi(context: Context) = viewModelScope.launch {
        ApiCalling.hitApi(
            context = context,
            layoutId = R.layout.progress_loader,
            requestHandler = { ApiInterface.checkCache() },
            onResponse = {
                Log.e("Data onResponse", it.articles?.get(0).toString())
            }
        )
    }
}
