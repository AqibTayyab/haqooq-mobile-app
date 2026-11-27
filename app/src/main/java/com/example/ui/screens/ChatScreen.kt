package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.contracts.AiMode
import com.example.contracts.AskResponse
import com.example.contracts.ChatMessage
import com.example.contracts.LawyerBriefingPack
import com.example.l10n.LocalizedStrings
import com.example.ui.components.DisclaimerBanner
import com.example.ui.components.ModeBadge
import com.example.ui.components.UrgencyChip
import com.example.ui.theme.EmergencyRed

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    messages: List<ChatMessage>,
    isLoading: Boolean,
    chatError: String? = null,
    activeMode: AiMode,
    strings: LocalizedStrings,
    onSendMessage: (String) -> Unit,
    onRetryOnline: () -> Unit = {},
    onAnswerOffline: () -> Unit = {},
    onDismissError: () -> Unit = {},
    onClearChat: () -> Unit,
    onSaveNote: (ChatMessage) -> Unit,
    onOpenCard: (String) -> Unit,
    onOpenSos: () -> Unit,
    onOpenLawyers: () -> Unit,
    onGenerateBriefing: ((LawyerBriefingPack) -> Unit) -> Unit,
    onModeBadgeClick: () -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    var activeBriefing by remember { mutableStateOf<LawyerBriefingPack?>(null) }
    var savedBannerVisible by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size, isLoading) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 640.dp)
                .fillMaxSize()
        ) {
            // Sub-header with mode badge & New Chat
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ModeBadge(
                    mode = activeMode,
                    strings = strings,
                    onClick = onModeBadgeClick
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (messages.isNotEmpty()) {
                        TextButton(
                            onClick = {
                                onGenerateBriefing { pack -> activeBriefing = pack }
                            },
                            modifier = Modifier.testTag("chat_briefing_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Assignment,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.size(4.dp))
                            Text(strings.btnCreateBriefing)
                        }
                        TextButton(
                            onClick = onClearChat,
                            modifier = Modifier.testTag("chat_clear_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.size(4.dp))
                            Text(strings.clearChat)
                        }
                    }
                }
            }

            if (savedBannerVisible) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.noteSavedToast,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "OK",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable { savedBannerVisible = false }
                        )
                    }
                }
            }

            // Message List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (messages.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.AutoAwesome,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = strings.chatWelcomeTitle,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                    }
                                    Text(
                                        text = strings.chatWelcomeSubtitle,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    DisclaimerBanner(text = strings.legalDisclaimer)
                                }
                            }

                            Text(
                                text = strings.starterQuestionsLabel,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            listOf(
                                strings.starterQ1,
                                strings.starterQ2,
                                strings.starterQ3,
                                strings.starterQ4
                            ).forEachIndexed { idx, prompt ->
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("starter_question_$idx")
                                        .clickable { onSendMessage(prompt) }
                                ) {
                                    Text(
                                        text = prompt,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
                                    )
                                }
                            }
                        }
                    }
                } else {
                    items(messages, key = { it.id }) { msg ->
                        if (msg.isUser) {
                            UserBubble(msg)
                        } else {
                            AssistantResponseCard(
                                message = msg,
                                strings = strings,
                                onOpenCard = onOpenCard,
                                onFollowUpClick = onSendMessage,
                                onOpenSos = onOpenSos,
                                onOpenLawyers = onOpenLawyers,
                                onSaveNote = {
                                    onSaveNote(msg)
                                    savedBannerVisible = true
                                },
                                onCreateBriefing = {
                                    onGenerateBriefing { pack -> activeBriefing = pack }
                                }
                            )
                        }
                    }
                }

                if (isLoading) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.5.dp
                            )
                            Text(
                                text = strings.stateLoading,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                if (!chatError.isNullOrBlank()) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.errorContainer,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("chat_error_card")
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = chatError,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedButton(
                                        onClick = onRetryOnline,
                                        modifier = Modifier.testTag("chat_retry_online_button")
                                    ) {
                                        Text(strings.retryLabel)
                                    }
                                    Button(
                                        onClick = onAnswerOffline,
                                        modifier = Modifier.testTag("chat_fallback_offline_button")
                                    ) {
                                        Text(strings.modeOfflineLabel)
                                    }
                                    TextButton(onClick = onDismissError) {
                                        Text("OK")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Input Bar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text(strings.chatInputHint) },
                        singleLine = false,
                        maxLines = 3,
                        shape = RoundedCornerShape(22.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_field")
                    )
                    FilledIconButton(
                        onClick = {
                            val toSend = inputText.trim()
                            if (toSend.isNotEmpty()) {
                                inputText = ""
                                onSendMessage(toSend)
                            }
                        },
                        enabled = inputText.isNotBlank() && !isLoading,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("chat_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = strings.sendLabel
                        )
                    }
                }
            }
        }
    }

    activeBriefing?.let { briefing ->
        LawyerBriefingSheet(
            briefing = briefing,
            strings = strings,
            onDismiss = { activeBriefing = null },
            onSendToDemoLawyer = {
                activeBriefing = null
                onOpenLawyers()
            }
        )
    }
}

@Composable
private fun UserBubble(msg: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Surface(
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(18.dp, 18.dp, 4.dp, 18.dp),
            modifier = Modifier.widthIn(max = 310.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                Text(
                    text = msg.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                if (msg.timestampLabel.isNotBlank()) {
                    Text(
                        text = msg.timestampLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f),
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AssistantResponseCard(
    message: ChatMessage,
    strings: LocalizedStrings,
    onOpenCard: (String) -> Unit,
    onFollowUpClick: (String) -> Unit,
    onOpenSos: () -> Unit,
    onOpenLawyers: () -> Unit,
    onSaveNote: () -> Unit,
    onCreateBriefing: () -> Unit
) {
    val resp: AskResponse? = message.response

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (resp != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ModeBadge(mode = resp.modeUsed, strings = strings)
                    UrgencyChip(urgency = resp.urgency, strings = strings)
                }

                // Emergency banner if urgent keywords detected
                if (resp.isUrgentEmergency) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Warning,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = strings.urgentBannerTitle,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                            Text(
                                text = strings.urgentBannerBody,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = onOpenSos,
                                    colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed)
                                ) {
                                    Text(strings.btnOpenSos)
                                }
                                OutlinedButton(onClick = onOpenLawyers) {
                                    Text(strings.btnCallLawyer)
                                }
                            }
                        }
                    }
                }
            }

            // Answer Body
            Text(
                text = resp?.answer ?: message.text,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Next steps checklist
            if (resp != null && resp.nextSteps.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = strings.nextStepsLabel,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    resp.nextSteps.forEachIndexed { index, step ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircleOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .padding(top = 2.dp)
                                    .size(16.dp)
                            )
                            Text(
                                text = "${index + 1}. $step",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            // Source chips
            if (resp != null && resp.sources.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = strings.sourcesLabel,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        resp.sources.forEach { source ->
                            AssistChip(
                                onClick = { source.cardId?.let(onOpenCard) },
                                label = {
                                    Text(
                                        text = source.title,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            )
                        }
                    }
                }
            }

            // Follow-up chips
            if (resp != null && resp.followUpQuestions.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = strings.followUpLabel,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        resp.followUpQuestions.forEach { q ->
                            AssistChip(
                                onClick = { onFollowUpClick(q) },
                                label = { Text(q, style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }
                }
            }

            // Mandatory Disclaimer
            DisclaimerBanner(text = resp?.disclaimer ?: strings.legalDisclaimer)

            // Action Buttons Row
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onOpenLawyers,
                    modifier = Modifier.testTag("chat_action_lawyer")
                ) {
                    Icon(Icons.Filled.Gavel, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.size(4.dp))
                    Text(strings.btnCallLawyer)
                }
                OutlinedButton(
                    onClick = onOpenSos,
                    modifier = Modifier.testTag("chat_action_sos")
                ) {
                    Icon(Icons.Filled.Warning, contentDescription = null, tint = EmergencyRed, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.size(4.dp))
                    Text(strings.btnOpenSos, color = EmergencyRed)
                }
                OutlinedButton(
                    onClick = onSaveNote,
                    modifier = Modifier.testTag("chat_action_save_note")
                ) {
                    Icon(Icons.Filled.BookmarkAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.size(4.dp))
                    Text(strings.btnSaveNote)
                }
                OutlinedButton(
                    onClick = onCreateBriefing,
                    modifier = Modifier.testTag("chat_action_briefing")
                ) {
                    Icon(Icons.Filled.Assignment, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.size(4.dp))
                    Text(strings.btnCreateBriefing)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LawyerBriefingSheet(
    briefing: LawyerBriefingPack,
    strings: LocalizedStrings,
    onDismiss: () -> Unit,
    onSendToDemoLawyer: () -> Unit
) {
    val context = LocalContext.current

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.briefingSheetTitle,
                    style = MaterialTheme.typography.titleLarge
                )
                UrgencyChip(urgency = briefing.urgency, strings = strings)
            }

            Text(
                text = strings.briefingSummaryLabel,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Text(text = briefing.summary, style = MaterialTheme.typography.bodyMedium)

            if (briefing.timeline.isNotEmpty()) {
                Text(
                    text = strings.briefingTimelineLabel,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                briefing.timeline.forEach { ev ->
                    Text(
                        text = "• [${ev.time}] ${ev.event}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Text(
                text = strings.briefingEvidenceLabel,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            briefing.evidenceChecklist.forEach { item ->
                Text(text = "☐ $item", style = MaterialTheme.typography.bodyMedium)
            }

            Text(
                text = strings.briefingQuestionsLabel,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            briefing.questionsForLawyer.forEachIndexed { idx, q ->
                Text(text = "${idx + 1}. $q", style = MaterialTheme.typography.bodyMedium)
            }

            DisclaimerBanner(text = strings.legalDisclaimer)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val text = buildString {
                            appendLine("HAQOOQ ${strings.briefingSheetTitle}")
                            appendLine("${strings.briefingSummaryLabel}: ${briefing.summary}")
                            appendLine("${strings.briefingEvidenceLabel}: ${briefing.evidenceChecklist.joinToString(", ")}")
                        }
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, text)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, strings.briefingShareText))
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.size(4.dp))
                    Text(strings.briefingShareText)
                }
                Button(
                    onClick = onSendToDemoLawyer,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("briefing_send_lawyer")
                ) {
                    Text(strings.briefingAttachToLawyer)
                }
            }
        }
    }
}
