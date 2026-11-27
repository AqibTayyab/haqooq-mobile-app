package com.example.ui.screens

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.contracts.AppLanguage
import com.example.contracts.EmergencyContactItem
import com.example.contracts.SafetyCheckInState
import com.example.contracts.SosIncident
import com.example.contracts.TrustedContact
import com.example.l10n.LocalizedStrings
import com.example.services.sos.SosDeviceHelper
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.LocalHaqooqDarkMode
import com.example.ui.theme.OfflineAmber
import com.example.ui.theme.OfflineAmberContainer
import com.example.ui.theme.OfflineAmberDark
import com.example.ui.theme.OfflineAmberDarkContainer
import com.example.ui.theme.OnlineGreen
import com.example.ui.theme.OnlineGreenContainer
import com.example.ui.theme.OnlineGreenDark
import com.example.ui.theme.OnlineGreenDarkContainer

@Composable
fun SosScreen(
    language: AppLanguage,
    strings: LocalizedStrings,
    contacts: List<TrustedContact>,
    incidents: List<SosIncident>,
    countdownSeconds: Int?,
    preparedMessage: String?,
    checkInState: SafetyCheckInState,
    emergencyNumbers: List<EmergencyContactItem>,
    onStartSosCountdown: () -> Unit,
    onCancelSosCountdown: () -> Unit,
    onDismissPreparedSos: () -> Unit,
    onAddContact: (String, String, String, Boolean, (Result<TrustedContact>) -> Unit) -> Unit,
    onUpdateContact: (TrustedContact, (Result<TrustedContact>) -> Unit) -> Unit,
    onRemoveContact: (String) -> Unit,
    onStartCheckIn: (Int) -> Unit,
    onConfirmCheckInSafe: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val haptic = LocalHapticFeedback.current
    val isDark = LocalHaqooqDarkMode.current

    var showAddDialog by remember { mutableStateOf(false) }
    var editingContact by remember { mutableStateOf<TrustedContact?>(null) }
    var copiedBanner by remember { mutableStateOf(false) }
    var selectedMinutes by remember { mutableIntStateOf(15) }

    var locationGranted by remember { mutableStateOf(SosDeviceHelper.hasLocationPermission(context)) }
    var notificationsGranted by remember { mutableStateOf(SosDeviceHelper.hasNotificationPermission(context)) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                locationGranted = SosDeviceHelper.hasLocationPermission(context)
                notificationsGranted = SosDeviceHelper.hasNotificationPermission(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        locationGranted = SosDeviceHelper.hasLocationPermission(context)
        notificationsGranted = SosDeviceHelper.hasNotificationPermission(context)
    }

    fun requestSosPermissions() {
        val perms = buildList {
            add(Manifest.permission.ACCESS_FINE_LOCATION)
            add(Manifest.permission.ACCESS_COARSE_LOCATION)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }.toTypedArray()
        permissionLauncher.launch(perms)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = 640.dp)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Big SOS Control Card
            item {
                val isCounting = countdownSeconds != null
                val buttonColor by animateColorAsState(
                    targetValue = if (isCounting) Color(0xFF7F1D1D) else EmergencyRed,
                    label = "sosColor"
                )

                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.errorContainer,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = strings.sosSubtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )

                        Box(
                            modifier = Modifier
                                .size(156.dp)
                                .clip(CircleShape)
                                .background(buttonColor)
                                .testTag("sos_main_button")
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    if (isCounting) onCancelSosCountdown() else onStartSosCountdown()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (isCounting) {
                                    Text(
                                        text = "${countdownSeconds}s",
                                        style = MaterialTheme.typography.displaySmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = strings.sosCancelCountdown,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = Color.White,
                                        textAlign = TextAlign.Center
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Filled.Warning,
                                        contentDescription = strings.sosButtonHoldOrTap,
                                        tint = Color.White,
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Text(
                                        text = strings.sosButtonHoldOrTap,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        if (isCounting) {
                            Text(
                                text = "${strings.sosCountdownPrefix} ${countdownSeconds}s",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        if (contacts.isEmpty()) {
                            Text(
                                text = strings.sosNoContactsWarning,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Prepared SOS Delivery Card (Honest Android Intent Flow)
            if (preparedMessage != null) {
                item {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(2.dp, EmergencyRed),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("sos_prepared_card")
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = strings.sosReadyTitle,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = EmergencyRed,
                                    modifier = Modifier.weight(1f)
                                )
                                TextButton(onClick = onDismissPreparedSos) {
                                    Text(strings.cancelLabel)
                                }
                            }
                            Text(
                                text = strings.sosHonestHint,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = preparedMessage,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }

                            Button(
                                onClick = {
                                    val numbers = contacts.joinToString(";") { it.phone }
                                    val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                                        data = Uri.parse("smsto:$numbers")
                                        putExtra("sms_body", preparedMessage)
                                    }
                                    runCatching { context.startActivity(smsIntent) }.onFailure {
                                        Toast.makeText(context, strings.noSmsAppToast, Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("sos_open_sms_button")
                            ) {
                                Icon(Icons.Filled.Sms, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.size(8.dp))
                                Text(strings.sosTapToSendSms)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_TEXT, preparedMessage)
                                        }
                                        context.startActivity(Intent.createChooser(shareIntent, strings.sosShareMessage))
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.size(4.dp))
                                    Text(strings.sosShareMessage, maxLines = 1)
                                }
                                OutlinedButton(
                                    onClick = {
                                        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        cm.setPrimaryClip(ClipData.newPlainText("HAQOOQ SOS", preparedMessage))
                                        copiedBanner = true
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.size(4.dp))
                                    Text(if (copiedBanner) strings.sosCopiedFeedback else strings.sosCopyMessage, maxLines = 1)
                                }
                            }
                        }
                    }
                }
            }

            // Permissions & GPS Readiness Card
            item {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = strings.permissionsTitle,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PermissionBadge(
                                icon = Icons.Filled.GpsFixed,
                                label = strings.locationPermLabel,
                                granted = locationGranted,
                                strings = strings,
                                isDark = isDark,
                                modifier = Modifier.weight(1f)
                            )
                            PermissionBadge(
                                icon = Icons.Filled.NotificationsActive,
                                label = strings.notificationsPermLabel,
                                granted = notificationsGranted,
                                strings = strings,
                                isDark = isDark,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (!locationGranted || !notificationsGranted) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { requestSosPermissions() },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("sos_grant_permissions_button")
                                ) {
                                    Text(strings.grantPermissionsBtn, maxLines = 1)
                                }
                                OutlinedButton(
                                    onClick = {
                                        val settingsIntent = Intent(
                                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                            Uri.fromParts("package", context.packageName, null)
                                        )
                                        runCatching { context.startActivity(settingsIntent) }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("sos_open_settings_button")
                                ) {
                                    Icon(Icons.Filled.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.size(4.dp))
                                    Text(strings.openAppSettingsBtn, maxLines = 1)
                                }
                            }
                        }
                    }
                }
            }

            // Safety Check-In Timer
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Timer,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = strings.safetyCheckInTitle,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Text(
                            text = strings.safetyCheckInDesc,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (checkInState.expiredTriggered) {
                            Surface(
                                color = MaterialTheme.colorScheme.errorContainer,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = strings.checkInExpiredWarning,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }

                        if (checkInState.isActive) {
                            val mins = checkInState.remainingSeconds / 60
                            val secs = checkInState.remainingSeconds % 60
                            Text(
                                text = "${strings.checkInRemainingPrefix}: %02d:%02d".format(mins, secs),
                                style = MaterialTheme.typography.headlineSmall,
                                color = OfflineAmber,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = onConfirmCheckInSafe,
                                    colors = ButtonDefaults.buttonColors(containerColor = OnlineGreen),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("checkin_safe_button")
                                ) {
                                    Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.size(6.dp))
                                    Text(strings.checkInConfirmSafe)
                                }
                                OutlinedButton(
                                    onClick = onConfirmCheckInSafe,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(strings.checkInCancel)
                                }
                            }
                        } else {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf(1, 15, 30, 60).forEach { m ->
                                    FilterChip(
                                        selected = selectedMinutes == m,
                                        onClick = { selectedMinutes = m },
                                        label = { Text("${m}m") }
                                    )
                                }
                            }
                            Button(
                                onClick = { onStartCheckIn(selectedMinutes) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("checkin_start_button")
                            ) {
                                Text("${strings.checkInStart} (${selectedMinutes}m)")
                            }
                        }
                        Text(
                            text = strings.checkInBackgroundNote,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Trusted Contacts CRUD (max 5)
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = strings.trustedContactsTitle,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "${contacts.size}/5 • ${strings.trustedContactsMaxHint}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            OutlinedButton(
                                onClick = { showAddDialog = true },
                                enabled = contacts.size < 5,
                                modifier = Modifier.testTag("add_trusted_contact_button")
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.size(4.dp))
                                Text(strings.addContactLabel)
                            }
                        }

                        contacts.forEach { contact ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Person,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Column {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Text(contact.name, style = MaterialTheme.typography.titleSmall)
                                                if (contact.isPrimary) {
                                                    Icon(
                                                        imageVector = Icons.Filled.Star,
                                                        contentDescription = null,
                                                        tint = OfflineAmber,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                }
                                            }
                                            Text(
                                                text = "${contact.phone} • ${contact.relationship}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    Row {
                                        IconButton(
                                            onClick = { editingContact = contact },
                                            modifier = Modifier.testTag("edit_contact_${contact.id}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Edit,
                                                contentDescription = strings.editContactLabel,
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        IconButton(
                                            onClick = { onRemoveContact(contact.id) },
                                            modifier = Modifier.testTag("delete_contact_${contact.id}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Delete,
                                                contentDescription = strings.deleteLabel,
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Emergency Numbers Directory
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = strings.emergencyNumbersTitle,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = strings.verifyNotice,
                            style = MaterialTheme.typography.bodySmall,
                            color = OfflineAmber
                        )
                        emergencyNumbers.forEach { item ->
                            val label = when (language) {
                                AppLanguage.EN -> item.labelEn
                                AppLanguage.UR -> item.labelUr
                                AppLanguage.ROMAN_UR -> item.labelRoman
                            }
                            val note = when (language) {
                                AppLanguage.EN -> item.noteEn
                                AppLanguage.UR -> item.noteUr
                                AppLanguage.ROMAN_UR -> item.noteRoman
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "$label (${item.number})",
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Text(
                                        text = note,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Button(
                                    onClick = {
                                        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${item.number}"))
                                        runCatching { context.startActivity(dialIntent) }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.errorContainer,
                                        contentColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Icon(Icons.Filled.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.size(4.dp))
                                    Text(strings.callNow)
                                }
                            }
                        }
                    }
                }
            }

            // Local SOS Incident Log
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = strings.incidentHistoryTitle,
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (incidents.isEmpty()) {
                        Text(
                            text = strings.noIncidentsYet,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        incidents.forEach { inc ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Text(
                                        text = "${inc.timestamp} • ${inc.status}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = EmergencyRed
                                    )
                                    Text(
                                        text = "Battery: ${inc.batteryPercent}% • Recipients: ${inc.recipientCount}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog || editingContact != null) {
        val currentEdit = editingContact
        AddOrEditTrustedContactDialog(
            strings = strings,
            existing = currentEdit,
            onDismiss = {
                showAddDialog = false
                editingContact = null
            },
            onSave = { name, phone, rel, isPrimary, onError ->
                if (currentEdit != null) {
                    onUpdateContact(
                        currentEdit.copy(
                            name = name,
                            phone = phone,
                            relationship = rel.ifBlank { "Family" },
                            isPrimary = isPrimary
                        )
                    ) { result ->
                        if (result.isSuccess) {
                            editingContact = null
                        } else {
                            onError(strings.invalidPhoneError)
                        }
                    }
                } else {
                    onAddContact(name, phone, rel, isPrimary) { result ->
                        if (result.isSuccess) {
                            showAddDialog = false
                        } else {
                            val msg = if (result.exceptionOrNull()?.message == "MAX_CONTACTS") {
                                strings.maxContactsError
                            } else {
                                strings.invalidPhoneError
                            }
                            onError(msg)
                        }
                    }
                }
            }
        )
    }
}

@Composable
private fun PermissionBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    granted: Boolean,
    strings: LocalizedStrings,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val bg = when {
        granted && isDark -> OnlineGreenDarkContainer
        granted -> OnlineGreenContainer
        isDark -> OfflineAmberDarkContainer
        else -> OfflineAmberContainer
    }
    val fg = when {
        granted && isDark -> OnlineGreenDark
        granted -> OnlineGreen
        isDark -> OfflineAmberDark
        else -> OfflineAmber
    }
    Surface(
        color = bg,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, fg.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icon, contentDescription = label, tint = fg, modifier = Modifier.size(18.dp))
            Column {
                Text(label, style = MaterialTheme.typography.labelMedium, color = fg, fontWeight = FontWeight.Bold)
                Text(
                    text = if (granted) strings.permGrantedLabel else strings.permNeededLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = fg
                )
            }
        }
    }
}

@Composable
private fun AddOrEditTrustedContactDialog(
    strings: LocalizedStrings,
    existing: TrustedContact? = null,
    onDismiss: () -> Unit,
    onSave: (String, String, String, Boolean, (String) -> Unit) -> Unit
) {
    var name by remember(existing) { mutableStateOf(existing?.name.orEmpty()) }
    var phone by remember(existing) { mutableStateOf(existing?.phone.orEmpty()) }
    var relation by remember(existing) { mutableStateOf(existing?.relationship.orEmpty()) }
    var isPrimary by remember(existing) { mutableStateOf(existing?.isPrimary ?: false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (existing != null) strings.editContactLabel else strings.addContactLabel)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(strings.contactNameHint) },
                    singleLine = true,
                    modifier = Modifier.testTag("contact_name_input")
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = {
                        phone = it
                        errorMsg = null
                    },
                    label = { Text(strings.contactPhoneHint) },
                    singleLine = true,
                    isError = errorMsg != null,
                    modifier = Modifier.testTag("contact_phone_input")
                )
                OutlinedTextField(
                    value = relation,
                    onValueChange = { relation = it },
                    label = { Text(strings.contactRelationHint) },
                    singleLine = true
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isPrimary = !isPrimary }
                ) {
                    Checkbox(checked = isPrimary, onCheckedChange = { isPrimary = it })
                    Text(strings.primaryContactToggle, style = MaterialTheme.typography.bodyMedium)
                }
                if (errorMsg != null) {
                    Text(
                        text = errorMsg!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank()) {
                        errorMsg = strings.contactNameHint
                    } else {
                        onSave(name, phone, relation, isPrimary) { err -> errorMsg = err }
                    }
                },
                modifier = Modifier.testTag("save_contact_button")
            ) {
                Text(strings.saveLabel)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.cancelLabel)
            }
        }
    )
}
