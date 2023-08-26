package com.nakul.ktorexample.api_helper

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.annotation.LayoutRes
import com.nakul.ktorexample.R
import io.ktor.client.features.ResponseException
import io.ktor.client.features.cache.InvalidCacheStateException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch


object ApiCalling {
    private fun showError(context: Context, message: String) {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }


    private val onApiErrorDefaultHandler =
        { context: Context, message: String, exception: Throwable? ->
            Log.e("Api Util", "onApiErrorDefaultHandler $message")
            when(exception){
                is InvalidCacheStateException ->{
                    //TODO Handle 304 Response with absent cache
                }
            }
            exception?.printStackTrace()
            showError(context, message)
        }

    private val onResponseErrorDefaultHandler =
        { context: Context, responseException: ResponseException ->
            responseException.printStackTrace()
            Log.e("Api Util", "onResponseErrorDefaultHandler ${responseException.response.status}")
            when (responseException.response.status.value) {
                in 400 until 500 -> {
                    //TODO Handle UN_AUTHORISED access in APIs
                }

                else -> {
                    showError(context, responseException.response.status.description)
                }
            }
        }

    suspend fun <T> hitApi(
        context: Context,
        requestHandler: RequestHandler<T>,
        showLoader: Boolean = false,
        @LayoutRes layoutId: Int,
        onResponse: (T) -> Unit,
        onApiError: (context: Context, message: String, exception: Throwable?) -> Unit = onApiErrorDefaultHandler,
        onResponseError: (context: Context, responseException: ResponseException) -> Unit = onResponseErrorDefaultHandler,
    ) {
        if (!isNetworkAvailable(context)) {
            onApiError.invoke(context, context.getString(R.string.no_internet), null)
            return
        }

        if (showLoader)
            showProgress(context, layoutId)


        val dataResponse = flow {
            emit(requestHandler.sendRequest())
        }.flowOn(Dispatchers.IO)


        dataResponse.catch { exception ->
            hideProgress()
            when (exception) {
                is ResponseException -> onResponseError.invoke(context, exception)
                else -> onApiError.invoke(
                    context,
                    exception.message ?: exception.localizedMessage,
                    exception
                )
            }
        }.collectLatest { response ->
            onResponse(response)
            hideProgress()
        }
    }

    //Loader
    private var customDialog: AlertDialog? = null
    private fun showProgress(context: Context, @LayoutRes layoutId: Int) =
        CoroutineScope(Dispatchers.Main).launch {
            hideProgress()
            val customAlertBuilder = AlertDialog.Builder(context)
            val view = LayoutInflater.from(context).inflate(layoutId, null, false)
            customAlertBuilder.setView(view)
            customAlertBuilder.setCancelable(false)
            customDialog = customAlertBuilder.create()

            customDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            customDialog?.show()
        }

    private fun hideProgress() = CoroutineScope(Dispatchers.Main).launch {
        if (customDialog != null && customDialog?.isShowing!!) {
            customDialog?.dismiss()
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}