package com.example.services.ai

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.example.contracts.AiMode
import com.example.contracts.ConnectivityService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Monitors real Android network availability via ConnectivityManager and automatically switches
 * between ONLINE (Gemini via Firebase AI Logic) and OFFLINE (local RAG + verified legal cards).
 */
class AndroidConnectivityService(context: Context) : ConnectivityService {
    private val connectivityManager =
        context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

    private val _isNetworkAvailable = MutableStateFlow(checkCurrentNetwork())
    val isNetworkAvailable: StateFlow<Boolean> = _isNetworkAvailable.asStateFlow()

    private var overrideMode: AiMode? = null
    private var simulatedOffline: Boolean = false

    private val _activeMode = MutableStateFlow(computeMode())
    override val activeMode: StateFlow<AiMode> = _activeMode.asStateFlow()

    init {
        runCatching {
            connectivityManager?.registerDefaultNetworkCallback(
                object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        _isNetworkAvailable.value = true
                        recompute()
                    }

                    override fun onLost(network: Network) {
                        _isNetworkAvailable.value = checkCurrentNetwork()
                        recompute()
                    }

                    override fun onCapabilitiesChanged(
                        network: Network,
                        networkCapabilities: NetworkCapabilities
                    ) {
                        _isNetworkAvailable.value =
                            networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                        recompute()
                    }
                }
            )
        }
    }

    override fun setModeOverride(mode: AiMode?) {
        overrideMode = mode
        recompute()
    }

    override fun setSimulatedOffline(offline: Boolean) {
        simulatedOffline = offline
        recompute()
    }

    fun hasActiveNetwork(): Boolean = checkCurrentNetwork()

    private fun checkCurrentNetwork(): Boolean {
        val cm = connectivityManager ?: return false
        return runCatching {
            val network = cm.activeNetwork ?: return false
            val caps = cm.getNetworkCapabilities(network) ?: return false
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }.getOrDefault(false)
    }

    private fun recompute() {
        _activeMode.value = computeMode()
    }

    private fun computeMode(): AiMode = when {
        overrideMode != null -> overrideMode!!
        simulatedOffline -> AiMode.OFFLINE
        !_isNetworkAvailable.value -> AiMode.OFFLINE
        else -> AiMode.ONLINE
    }
}
