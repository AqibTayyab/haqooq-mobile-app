package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.contracts.AppLanguage
import com.example.contracts.Lawyer
import com.example.contracts.LawyerBooking
import com.example.contracts.LawyerBriefingPack
import com.example.contracts.LawyerChatMessage
import com.example.contracts.LawyerReview
import com.example.contracts.LegalCategory
import com.example.contracts.UiState
import com.example.l10n.AppStrings
import com.example.l10n.LocalizedStrings
import com.example.ui.components.DemoLawyerBanner
import com.example.ui.components.DisclaimerBanner
import com.example.ui.components.StateView

@Composable
fun LawyersScreen(
    language: AppLanguage,
    strings: LocalizedStrings,
    lawyersState: UiState<List<Lawyer>>,
    searchQuery: String,
    selectedCity: String?,
    selectedCategory: LegalCategory,
    selectedLanguage: AppLanguage?,
    bookings: List<LawyerBooking>,
    onSearchQueryChange: (String) -> Unit,
    onSelectCity: (String?) -> Unit,
    onSelectCategory: (LegalCategory) -> Unit,
    onSelectLanguage: (AppLanguage?) -> Unit,
    onOpenLawyer: (Lawyer) -> Unit,
    onRetry: () -> Unit
) {
    val cities = listOf(null, "Multan", "Lahore", "Karachi", "Islamabad")
    val filterCats = listOf(
        LegalCategory.ALL,
        LegalCategory.POLICE_ARREST,
        LegalCategory.FIR,
        LegalCategory.WOMEN_SAFETY,
        LegalCategory.CYBERCRIME,
        LegalCategory.EMPLOYMENT,
        LegalCategory.PROPERTY
    )
    val filterLanguages: List<AppLanguage?> = listOf(null) + AppLanguage.entries

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 640.dp)
                .fillMaxSize()
        ) {
            DemoLawyerBanner(
                text = strings.demoLawyerBanner,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text(strings.lawyerSearchHint) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = strings.cancelLabel
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .testTag("lawyer_search_field")
            )

            // City filters
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(cities) { city ->
                    FilterChip(
                        selected = selectedCity == city,
                        onClick = { onSelectCity(city) },
                        label = { Text(city ?: strings.filterAllCities) }
                    )
                }
            }

            // Specialization filters
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filterCats) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { onSelectCategory(cat) },
                        label = { Text(AppStrings.categoryLabel(cat, language)) }
                    )
                }
            }

            // Spoken language filters
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filterLanguages) { langOption ->
                    FilterChip(
                        selected = selectedLanguage == langOption,
                        onClick = { onSelectLanguage(langOption) },
                        label = { Text(langOption?.nativeLabel ?: strings.filterAllLanguages) },
                        modifier = Modifier.testTag("lawyer_lang_filter_${langOption?.code ?: "all"}")
                    )
                }
            }

            StateView(
                state = lawyersState,
                strings = strings,
                emptyTitle = strings.lawyersTitle,
                emptySubtitle = strings.filterAllTopics,
                onRetry = onRetry
            ) { lawyers ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (bookings.isNotEmpty()) {
                        item {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "${strings.bookingConfirmedTitle}: ${bookings.first().lawyerName}",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        text = "${bookings.first().dateLabel} • ${bookings.first().timeSlot} (${bookings.first().topic})",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }
                    }

                    items(lawyers, key = { it.id }) { lawyer ->
                        LawyerCard(
                            lawyer = lawyer,
                            language = language,
                            strings = strings,
                            onClick = { onOpenLawyer(lawyer) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LawyerCard(
    lawyer: Lawyer,
    language: AppLanguage,
    strings: LocalizedStrings,
    onClick: () -> Unit
) {
    val bio = when (language) {
        AppLanguage.EN -> lawyer.bioEn
        AppLanguage.UR -> lawyer.bioUr
        AppLanguage.ROMAN_UR -> lawyer.bioRoman
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("lawyer_card_${lawyer.id}")
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Column {
                        Text(
                            text = lawyer.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${lawyer.city} • ${lawyer.experienceYears} ${strings.experienceSuffix}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${lawyer.rating}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            Text(
                text = bio,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                lawyer.specializations.forEach { cat ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = AppStrings.categoryLabel(cat, language),
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = lawyer.feePkr,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Button(onClick = onClick) {
                    Text(strings.viewProfile)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LawyerDetailScreen(
    lawyer: Lawyer,
    language: AppLanguage,
    strings: LocalizedStrings,
    latestBriefing: LawyerBriefingPack?,
    chatMessages: List<LawyerChatMessage>,
    isTyping: Boolean,
    hasBooked: Boolean,
    onBookConsultation: (String, String, String, Boolean, (LawyerBooking) -> Unit) -> Unit,
    onSendChatMessage: (String, LawyerBriefingPack?) -> Unit,
    onSubmitReview: (String, Int, String, (Result<LawyerReview>) -> Unit) -> Unit
) {
    var activeTab by remember { mutableIntStateOf(0) }
    var showBookingSheet by remember { mutableStateOf(false) }
    var showReviewDialog by remember { mutableStateOf(false) }
    var confirmedBooking by remember { mutableStateOf<LawyerBooking?>(null) }
    var chatInput by remember { mutableStateOf("") }

    val bio = when (language) {
        AppLanguage.EN -> lawyer.bioEn
        AppLanguage.UR -> lawyer.bioUr
        AppLanguage.ROMAN_UR -> lawyer.bioRoman
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
            DemoLawyerBanner(
                text = strings.demoLawyerBanner,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            TabRow(selectedTabIndex = activeTab) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    text = { Text(strings.viewProfile) }
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    text = { Text(strings.chatWithDemoLawyer) },
                    modifier = Modifier.testTag("lawyer_chat_tab")
                )
            }

            if (activeTab == 0) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = lawyer.name,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = "${lawyer.city} • ${lawyer.experienceYears} ${strings.experienceSuffix} • ${lawyer.feePkr}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = bio,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        lawyer.languages.forEach { spoken ->
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = spoken.nativeLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    if (confirmedBooking != null) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Column {
                                    Text(
                                        text = strings.bookingConfirmedTitle,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        text = "${confirmedBooking!!.dateLabel} • ${confirmedBooking!!.timeSlot}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { showBookingSheet = true },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("lawyer_book_button")
                        ) {
                            Icon(Icons.Filled.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.size(6.dp))
                            Text(strings.bookDemoConsult)
                        }
                        OutlinedButton(
                            onClick = { activeTab = 1 },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.size(6.dp))
                            Text(strings.chatWithDemoLawyer)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${strings.reviewsTitle} (${lawyer.rating} ★ • ${lawyer.reviewCount})",
                                style = MaterialTheme.typography.titleMedium
                            )
                            if (!hasBooked && confirmedBooking == null) {
                                Text(
                                    text = strings.reviewRequiresBookingNote,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        if (hasBooked || confirmedBooking != null) {
                            OutlinedButton(
                                onClick = { showReviewDialog = true },
                                modifier = Modifier.testTag("lawyer_write_review_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.RateReview,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.size(6.dp))
                                Text(strings.addReviewButton)
                            }
                        }
                    }

                    if (lawyer.reviews.isEmpty()) {
                        Text(
                            text = "No demo reviews yet.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        lawyer.reviews.forEach { review ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(review.authorName, style = MaterialTheme.typography.titleSmall)
                                        Text("${review.rating}/5 ★", style = MaterialTheme.typography.labelMedium)
                                    }
                                    Text(review.comment, style = MaterialTheme.typography.bodyMedium)
                                    Text(
                                        text = review.date,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    DisclaimerBanner(text = strings.legalDisclaimer)
                }
            } else {
                // Demo Lawyer Chat
                Column(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(chatMessages, key = { it.id }) { msg ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start
                            ) {
                                Surface(
                                    color = if (msg.isUser) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant
                                    },
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.widthIn(max = 300.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        if (msg.attachedBriefing != null) {
                                            Text(
                                                text = "📎 ${strings.briefingAttachedTag}: ${msg.attachedBriefing.summary.take(70)}...",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (msg.isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                                            )
                                            Spacer(Modifier.height(4.dp))
                                        }
                                        Text(
                                            text = msg.text,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = if (msg.isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }

                        if (isTyping) {
                            item {
                                Text(
                                    text = "${lawyer.name}...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    if (latestBriefing != null) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = strings.briefingConsentNote,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            OutlinedButton(
                                onClick = {
                                    onSendChatMessage("", latestBriefing)
                                },
                                modifier = Modifier.testTag("attach_briefing_to_chat")
                            ) {
                                Icon(Icons.Filled.Assignment, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.size(6.dp))
                                Text(strings.attachBriefingButton)
                            }
                        }
                    }

                    Surface(tonalElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = chatInput,
                                onValueChange = { chatInput = it },
                                placeholder = { Text(strings.lawyerChatInputHint) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(20.dp)
                            )
                            FilledIconButton(
                                onClick = {
                                    val msg = chatInput.trim()
                                    if (msg.isNotEmpty()) {
                                        chatInput = ""
                                        onSendChatMessage(msg, null)
                                    }
                                },
                                enabled = chatInput.isNotBlank() && !isTyping,
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = strings.sendLabel)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showBookingSheet) {
        var selectedDate by remember { mutableStateOf("Tomorrow") }
        var selectedTime by remember { mutableStateOf("11:00 AM") }
        var topic by remember { mutableStateOf("") }
        var attachBriefing by remember { mutableStateOf(false) }

        ModalBottomSheet(onDismissRequest = { showBookingSheet = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .padding(bottom = 28.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DemoLawyerBanner(text = strings.demoLawyerBanner)
                Text(strings.bookDemoConsult, style = MaterialTheme.typography.titleLarge)

                Text(strings.selectDateSlot, style = MaterialTheme.typography.titleSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Today", "Tomorrow", "Mon, 30 Nov").forEach { d ->
                        FilterChip(
                            selected = selectedDate == d,
                            onClick = { selectedDate = d },
                            label = { Text(d) }
                        )
                    }
                }

                Text(strings.selectTimeSlot, style = MaterialTheme.typography.titleSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("11:00 AM", "03:00 PM", "06:30 PM").forEach { t ->
                        FilterChip(
                            selected = selectedTime == t,
                            onClick = { selectedTime = t },
                            label = { Text(t) }
                        )
                    }
                }

                OutlinedTextField(
                    value = topic,
                    onValueChange = { topic = it },
                    label = { Text(strings.bookingTopicHint) },
                    modifier = Modifier.fillMaxWidth()
                )

                if (latestBriefing != null) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { attachBriefing = !attachBriefing }
                        ) {
                            Checkbox(checked = attachBriefing, onCheckedChange = { attachBriefing = it })
                            Text(strings.attachBriefingToggle, style = MaterialTheme.typography.bodyMedium)
                        }
                        Text(
                            text = strings.briefingConsentNote,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }

                Button(
                    onClick = {
                        onBookConsultation(selectedDate, selectedTime, topic, attachBriefing) { booking ->
                            confirmedBooking = booking
                            showBookingSheet = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("confirm_demo_booking_button")
                ) {
                    Text(strings.confirmBooking)
                }
            }
        }
    }

    if (showReviewDialog) {
        var authorName by remember { mutableStateOf("") }
        var rating by remember { mutableIntStateOf(5) }
        var comment by remember { mutableStateOf("") }
        var reviewError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showReviewDialog = false },
            title = { Text(strings.reviewDialogTitle) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        (1..5).forEach { star ->
                            IconButton(
                                onClick = { rating = star },
                                modifier = Modifier.testTag("review_star_$star")
                            ) {
                                Icon(
                                    imageVector = if (star <= rating) Icons.Filled.Star else Icons.Outlined.StarOutline,
                                    contentDescription = "$star",
                                    tint = if (star <= rating) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    OutlinedTextField(
                        value = authorName,
                        onValueChange = { authorName = it },
                        label = { Text(strings.reviewNameHint) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = comment,
                        onValueChange = {
                            comment = it
                            reviewError = null
                        },
                        label = { Text(strings.reviewCommentHint) },
                        minLines = 2,
                        maxLines = 4,
                        isError = reviewError != null,
                        supportingText = reviewError?.let { err -> { Text(err) } },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("review_comment_field")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSubmitReview(authorName, rating, comment) { res ->
                            res.onSuccess {
                                showReviewDialog = false
                            }.onFailure { err ->
                                reviewError = when (err.message) {
                                    "BOOKING_REQUIRED" -> strings.reviewRequiresBookingNote
                                    else -> strings.reviewCommentHint
                                }
                            }
                        }
                    },
                    modifier = Modifier.testTag("submit_demo_review_button")
                ) {
                    Text(strings.reviewSubmitButton)
                }
            },
            dismissButton = {
                TextButton(onClick = { showReviewDialog = false }) {
                    Text(strings.cancelLabel)
                }
            }
        )
    }
}
