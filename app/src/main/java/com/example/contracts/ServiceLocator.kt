package com.example.contracts

import android.content.Context
import com.example.data.MockAiService
import com.example.data.MockConnectivityService
import com.example.data.MockLawyerService
import com.example.data.MockRagService
import com.example.data.MockSosService
import com.example.services.ai.AndroidConnectivityService
import com.example.services.ai.HybridAiService
import com.example.services.lawyers.JsonLawyerService
import com.example.services.sos.PersistentSosService

/**
 * Dependency wiring for HAQOOQ modules.
 * Initializes real Android Connectivity, Hybrid Gemini AI (Online + Offline RAG fallback),
 * persistent SOS, and JSON-backed Lawyer services with app context.
 */
object ServiceLocator {
    var useMockAi: Boolean = false
    var useMockRag: Boolean = true
    var useMockSos: Boolean = false
    var useMockLawyers: Boolean = false

    private var _connectivityService: ConnectivityService? = null
    val connectivityService: ConnectivityService
        get() = _connectivityService ?: MockConnectivityService().also { _connectivityService = it }

    val ragService: RagService by lazy { MockRagService() }

    private var _aiService: AiService? = null
    val aiService: AiService
        get() = _aiService ?: MockAiService(ragService).also { _aiService = it }

    private var _sosService: SosService? = null
    val sosService: SosService
        get() = _sosService ?: MockSosService().also { _sosService = it }

    private var _lawyerService: LawyerService? = null
    val lawyerService: LawyerService
        get() = _lawyerService ?: MockLawyerService().also { _lawyerService = it }

    fun init(context: Context) {
        val appContext = context.applicationContext
        if (_connectivityService !is AndroidConnectivityService) {
            _connectivityService = AndroidConnectivityService(appContext)
        }
        if (!useMockAi && _aiService !is HybridAiService) {
            val androidConnectivity = _connectivityService as AndroidConnectivityService
            _aiService = HybridAiService(
                ragService = ragService,
                offlineFallback = MockAiService(ragService),
                isNetworkAvailable = { androidConnectivity.hasActiveNetwork() },
                currentModeProvider = { androidConnectivity.activeMode.value }
            )
        }
        if (!useMockSos && _sosService !is PersistentSosService) {
            _sosService = PersistentSosService(appContext)
        }
        if (!useMockLawyers && _lawyerService !is JsonLawyerService) {
            _lawyerService = JsonLawyerService(appContext)
        }
    }
}
