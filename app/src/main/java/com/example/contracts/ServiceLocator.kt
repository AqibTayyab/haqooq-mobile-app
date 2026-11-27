package com.example.contracts

import android.content.Context
import com.example.data.MockAiService
import com.example.data.MockConnectivityService
import com.example.data.MockLawyerService
import com.example.data.MockRagService
import com.example.data.MockSosService
import com.example.services.lawyers.JsonLawyerService
import com.example.services.sos.PersistentSosService

/**
 * Dependency wiring for HAQOOQ modules.
 * Member 1 ships UI against these contracts using mock services;
 * Member 4 initializes persistent SOS & JSON-backed Lawyer services with app context.
 */
object ServiceLocator {
    var useMockAi: Boolean = true
    var useMockRag: Boolean = true
    var useMockSos: Boolean = false
    var useMockLawyers: Boolean = false

    val connectivityService: ConnectivityService by lazy { MockConnectivityService() }
    val ragService: RagService by lazy { MockRagService() }
    val aiService: AiService by lazy { MockAiService(ragService) }

    private var _sosService: SosService? = null
    val sosService: SosService
        get() = _sosService ?: MockSosService().also { _sosService = it }

    private var _lawyerService: LawyerService? = null
    val lawyerService: LawyerService
        get() = _lawyerService ?: MockLawyerService().also { _lawyerService = it }

    fun init(context: Context) {
        val appContext = context.applicationContext
        if (!useMockSos && _sosService !is PersistentSosService) {
            _sosService = PersistentSosService(appContext)
        }
        if (!useMockLawyers && _lawyerService !is JsonLawyerService) {
            _lawyerService = JsonLawyerService(appContext)
        }
    }
}
