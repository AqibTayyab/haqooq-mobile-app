package com.example

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Gavel
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.contracts.AiMode
import com.example.contracts.AppLanguage
import com.example.contracts.Lawyer
import com.example.contracts.LegalCard
import com.example.l10n.AppStrings
import com.example.ui.HaqooqViewModel
import com.example.ui.components.HaqooqTopBar
import com.example.ui.components.LanguagePickerSheet
import com.example.ui.components.ModeBadge
import com.example.ui.screens.ArticleDetailScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LawyerDetailScreen
import com.example.ui.screens.LawyersScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.RightsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SosScreen
import com.example.services.ai.configureAppCheck
import com.example.ui.theme.HaqooqMotion
import com.example.ui.theme.HaqooqTheme

object Routes {
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val CHAT = "chat"
    const val RIGHTS = "rights"
    const val SOS = "sos"
    const val LAWYERS = "lawyers"
    const val SETTINGS = "settings"
    const val ARTICLE_DETAIL = "article/{cardId}"
    const val LAWYER_DETAIL = "lawyer/{lawyerId}"

    fun article(cardId: String) = "article/$cardId"
    fun lawyer(lawyerId: String) = "lawyer/$lawyerId"
}

private data class NavTabItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val isEmergency: Boolean = false
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureAppCheck(this, intent)
        enableEdgeToEdge()
        setContent {
            HaqooqApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HaqooqApp(viewModel: HaqooqViewModel = viewModel()) {
    val language by viewModel.language.collectAsStateWithLifecycle()
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val hasCompletedOnboarding by viewModel.hasCompletedOnboarding.collectAsStateWithLifecycle()
    val activeMode by viewModel.activeMode.collectAsStateWithLifecycle()
    val liteModeEnabled by viewModel.liteModeEnabled.collectAsStateWithLifecycle()
    val strings = remember(language) { AppStrings.forLanguage(language) }

    val layoutDirection = if (language.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !isDarkMode
            insetsController.isAppearanceLightNavigationBars = !isDarkMode
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        HaqooqTheme(darkTheme = isDarkMode, isUrdu = language == AppLanguage.UR) {
            val navController = rememberNavController()
            val navBackStack by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStack?.destination?.route

            var showLanguageSheet by remember { mutableStateOf(false) }
            var showModeSheet by remember { mutableStateOf(false) }

            val startDest = remember(hasCompletedOnboarding) {
                if (hasCompletedOnboarding) Routes.HOME else Routes.ONBOARDING
            }

            val tabs = listOf(
                NavTabItem(Routes.HOME, strings.navHome, Icons.Filled.Home, Icons.Outlined.Home),
                NavTabItem(Routes.CHAT, strings.navAiChat, Icons.AutoMirrored.Filled.Chat, Icons.AutoMirrored.Outlined.Chat),
                NavTabItem(Routes.RIGHTS, strings.navRights, Icons.Filled.MenuBook, Icons.Outlined.MenuBook),
                NavTabItem(Routes.SOS, strings.navSos, Icons.Filled.Warning, Icons.Outlined.Warning, isEmergency = true),
                NavTabItem(Routes.LAWYERS, strings.navLawyers, Icons.Filled.Gavel, Icons.Outlined.Gavel)
            )
            val topLevelRoutes = tabs.map { it.route }.toSet()
            val isTopLevel = currentRoute in topLevelRoutes
            val showChrome = currentRoute != null && currentRoute != Routes.ONBOARDING

            val topTitle = when {
                currentRoute == Routes.HOME -> strings.appName
                currentRoute == Routes.CHAT -> strings.navAiChat
                currentRoute == Routes.RIGHTS -> strings.rightsTitle
                currentRoute == Routes.SOS -> strings.sosTitle
                currentRoute == Routes.LAWYERS -> strings.lawyersTitle
                currentRoute == Routes.SETTINGS -> strings.settingsTitle
                currentRoute?.startsWith("article/") == true -> strings.rightsTitle
                currentRoute?.startsWith("lawyer/") == true -> strings.lawyersTitle
                else -> strings.appName
            }

            fun navigateToTab(route: String) {
                navController.navigate(route) {
                    popUpTo(Routes.HOME) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }

            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val useRail = maxWidth >= 600.dp

                Scaffold(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentWindowInsets = WindowInsets.safeDrawing,
                    topBar = {
                        if (showChrome) {
                            HaqooqTopBar(
                                title = topTitle,
                                currentLanguage = language,
                                activeMode = activeMode,
                                isDarkMode = isDarkMode,
                                strings = strings,
                                showBack = !isTopLevel,
                                onBack = { navController.popBackStack() },
                                onToggleTheme = viewModel::toggleDarkMode,
                                onLanguageClick = { showLanguageSheet = true },
                                onSettingsClick = if (currentRoute != Routes.SETTINGS) {
                                    { navController.navigate(Routes.SETTINGS) }
                                } else null,
                                onModeBadgeClick = { showModeSheet = true }
                            )
                        }
                    },
                    bottomBar = {
                        if (showChrome && isTopLevel && !useRail) {
                            ExpressiveFloatingBottomDock(
                                tabs = tabs,
                                currentRoute = currentRoute,
                                onSelectTab = ::navigateToTab
                            )
                        }
                    }
                ) { innerPadding ->
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        if (showChrome && isTopLevel && useRail) {
                            NavigationRail(
                                containerColor = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.fillMaxHeight()
                            ) {
                                tabs.forEach { tab ->
                                    val selected = currentRoute == tab.route
                                    NavigationRailItem(
                                        selected = selected,
                                        onClick = { navigateToTab(tab.route) },
                                        icon = {
                                            Icon(
                                                imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                                contentDescription = tab.label,
                                                tint = if (tab.isEmergency) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = tab.label,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        },
                                        modifier = Modifier.testTag("rail_nav_${tab.route}")
                                    )
                                }
                            }
                        }

                        Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                            NavHost(
                                navController = navController,
                                startDestination = startDest,
                                enterTransition = {
                                    fadeIn(HaqooqMotion.fastEffectsSpring()) + scaleIn(
                                        initialScale = 0.96f,
                                        animationSpec = HaqooqMotion.spatialSpring()
                                    )
                                },
                                exitTransition = {
                                    fadeOut(HaqooqMotion.fastEffectsSpring())
                                },
                                popEnterTransition = {
                                    fadeIn(HaqooqMotion.fastEffectsSpring()) + scaleIn(
                                        initialScale = 0.96f,
                                        animationSpec = HaqooqMotion.spatialSpring()
                                    )
                                },
                                popExitTransition = {
                                    fadeOut(HaqooqMotion.fastEffectsSpring()) + scaleOut(
                                        targetScale = 0.96f,
                                        animationSpec = HaqooqMotion.spatialSpring()
                                    )
                                }
                            ) {
                                composable(Routes.ONBOARDING) {
                                    OnboardingScreen(
                                        currentLanguage = language,
                                        isDarkMode = isDarkMode,
                                        strings = strings,
                                        onToggleTheme = viewModel::toggleDarkMode,
                                        onSelectLanguage = viewModel::setLanguage,
                                        onContinue = {
                                            viewModel.completeOnboarding()
                                            navController.navigate(Routes.HOME) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    inclusive = true
                                                }
                                            }
                                        }
                                    )
                                }

                                composable(Routes.HOME) {
                                    val caseNotes by viewModel.caseNotes.collectAsStateWithLifecycle()
                                    val rightsState by viewModel.rightsState.collectAsStateWithLifecycle()
                                    val lawyersState by viewModel.lawyersState.collectAsStateWithLifecycle()
                                    val featuredCards = (rightsState as? com.example.contracts.UiState.Success)?.data?.take(4).orEmpty()
                                    val featuredLawyers = (lawyersState as? com.example.contracts.UiState.Success)?.data.orEmpty()

                                    HomeScreen(
                                        language = language,
                                        activeMode = activeMode,
                                        strings = strings,
                                        featuredCards = featuredCards,
                                        featuredLawyers = featuredLawyers,
                                        caseNotes = caseNotes,
                                        emergencyNumbers = viewModel.emergencyNumbers,
                                        onNavigateToChat = { navigateToTab(Routes.CHAT) },
                                        onAskQuickQuestion = { q ->
                                            viewModel.sendChatMessage(q)
                                            navigateToTab(Routes.CHAT)
                                        },
                                        onNavigateToRights = { navigateToTab(Routes.RIGHTS) },
                                        onOpenCard = { card -> navController.navigate(Routes.article(card.id)) },
                                        onNavigateToSos = { navigateToTab(Routes.SOS) },
                                        onNavigateToLawyers = { navigateToTab(Routes.LAWYERS) },
                                        onOpenLawyer = { lawyer -> navController.navigate(Routes.lawyer(lawyer.id)) },
                                        onModeBadgeClick = { showModeSheet = true }
                                    )
                                }

                                composable(Routes.CHAT) {
                                    val messages by viewModel.chatMessages.collectAsStateWithLifecycle()
                                    val isLoading by viewModel.isChatLoading.collectAsStateWithLifecycle()
                                    val chatError by viewModel.chatError.collectAsStateWithLifecycle()
                                    ChatScreen(
                                        messages = messages,
                                        isLoading = isLoading,
                                        chatError = chatError,
                                        activeMode = activeMode,
                                        strings = strings,
                                        onSendMessage = viewModel::sendChatMessage,
                                        onRetryOnline = viewModel::retryLastChatMessage,
                                        onAnswerOffline = viewModel::answerLastQuestionOffline,
                                        onDismissError = viewModel::dismissChatError,
                                        onClearChat = viewModel::clearChat,
                                        onSaveNote = viewModel::saveNoteFromMessage,
                                        onOpenCard = { cardId -> navController.navigate(Routes.article(cardId)) },
                                        onOpenSos = { navigateToTab(Routes.SOS) },
                                        onOpenLawyers = { navigateToTab(Routes.LAWYERS) },
                                        onGenerateBriefing = viewModel::createBriefingPack,
                                        onModeBadgeClick = { showModeSheet = true }
                                    )
                                }

                                composable(Routes.RIGHTS) {
                                    val query by viewModel.rightsQuery.collectAsStateWithLifecycle()
                                    val selectedCategory by viewModel.selectedRightsCategory.collectAsStateWithLifecycle()
                                    val rightsState by viewModel.rightsState.collectAsStateWithLifecycle()
                                    RightsScreen(
                                        language = language,
                                        query = query,
                                        selectedCategory = selectedCategory,
                                        rightsState = rightsState,
                                        strings = strings,
                                        onQueryChange = viewModel::updateRightsSearch,
                                        onSelectCategory = viewModel::selectRightsCategory,
                                        onOpenCard = { card -> navController.navigate(Routes.article(card.id)) },
                                        onRetry = viewModel::refreshRights
                                    )
                                }

                                composable(
                                    route = Routes.ARTICLE_DETAIL,
                                    arguments = listOf(navArgument("cardId") { type = NavType.StringType })
                                ) { entry ->
                                    val cardId = entry.arguments?.getString("cardId").orEmpty()
                                    var card by remember(cardId, language) { mutableStateOf<LegalCard?>(null) }
                                    LaunchedEffect(cardId, language) {
                                        card = viewModel.findCardById(cardId)
                                    }
                                    card?.let { loadedCard ->
                                        ArticleDetailScreen(
                                            card = loadedCard,
                                            language = language,
                                            strings = strings,
                                            onAskAiAboutCard = { title ->
                                                viewModel.sendChatMessage(title)
                                                navigateToTab(Routes.CHAT)
                                            }
                                        )
                                    }
                                }

                                composable(Routes.SOS) {
                                    val contacts by viewModel.trustedContacts.collectAsStateWithLifecycle()
                                    val incidents by viewModel.sosIncidents.collectAsStateWithLifecycle()
                                    val countdown by viewModel.sosCountdown.collectAsStateWithLifecycle()
                                    val preparedMsg by viewModel.sosPreparedMessage.collectAsStateWithLifecycle()
                                    val checkInState by viewModel.checkInState.collectAsStateWithLifecycle()

                                    SosScreen(
                                        language = language,
                                        strings = strings,
                                        contacts = contacts,
                                        incidents = incidents,
                                        countdownSeconds = countdown,
                                        preparedMessage = preparedMsg,
                                        checkInState = checkInState,
                                        emergencyNumbers = viewModel.emergencyNumbers,
                                        onStartSosCountdown = viewModel::startSosCountdown,
                                        onCancelSosCountdown = viewModel::cancelSosCountdown,
                                        onDismissPreparedSos = viewModel::dismissPreparedSos,
                                        onAddContact = viewModel::addTrustedContact,
                                        onUpdateContact = viewModel::updateTrustedContact,
                                        onRemoveContact = viewModel::removeTrustedContact,
                                        onStartCheckIn = viewModel::startCheckIn,
                                        onConfirmCheckInSafe = viewModel::confirmCheckInSafe
                                    )
                                }

                                composable(Routes.LAWYERS) {
                                    val lawyersState by viewModel.lawyersState.collectAsStateWithLifecycle()
                                    val searchQuery by viewModel.lawyerSearchQuery.collectAsStateWithLifecycle()
                                    val selectedCity by viewModel.selectedCity.collectAsStateWithLifecycle()
                                    val selectedCat by viewModel.selectedLawyerCategory.collectAsStateWithLifecycle()
                                    val selectedLang by viewModel.selectedLawyerLanguage.collectAsStateWithLifecycle()
                                    val bookings by viewModel.bookings.collectAsStateWithLifecycle()

                                    LawyersScreen(
                                        language = language,
                                        strings = strings,
                                        lawyersState = lawyersState,
                                        searchQuery = searchQuery,
                                        selectedCity = selectedCity,
                                        selectedCategory = selectedCat,
                                        selectedLanguage = selectedLang,
                                        bookings = bookings,
                                        onSearchQueryChange = viewModel::setLawyerSearchQuery,
                                        onSelectCity = viewModel::setCityFilter,
                                        onSelectCategory = viewModel::setLawyerCategoryFilter,
                                        onSelectLanguage = viewModel::setLawyerLanguageFilter,
                                        onOpenLawyer = { lawyer -> navController.navigate(Routes.lawyer(lawyer.id)) },
                                        onRetry = viewModel::refreshLawyers
                                    )
                                }

                                composable(
                                    route = Routes.LAWYER_DETAIL,
                                    arguments = listOf(navArgument("lawyerId") { type = NavType.StringType })
                                ) { entry ->
                                    val lawyerId = entry.arguments?.getString("lawyerId").orEmpty()
                                    val lawyersState by viewModel.lawyersState.collectAsStateWithLifecycle()
                                    val bookings by viewModel.bookings.collectAsStateWithLifecycle()
                                    var lawyer by remember(lawyerId) { mutableStateOf<Lawyer?>(null) }
                                    LaunchedEffect(lawyerId, lawyersState, bookings) {
                                        lawyer = viewModel.getLawyerById(lawyerId)
                                    }
                                    val latestBriefing by viewModel.latestBriefing.collectAsStateWithLifecycle()
                                    val allChats by viewModel.lawyerChats.collectAsStateWithLifecycle()
                                    val isTyping by viewModel.isLawyerTyping.collectAsStateWithLifecycle()

                                    lawyer?.let { loadedLawyer ->
                                        val hasBooked = bookings.any { it.lawyerId == loadedLawyer.id }
                                        LawyerDetailScreen(
                                            lawyer = loadedLawyer,
                                            language = language,
                                            strings = strings,
                                            latestBriefing = latestBriefing,
                                            chatMessages = allChats[loadedLawyer.id].orEmpty(),
                                            isTyping = isTyping,
                                            hasBooked = hasBooked,
                                            onBookConsultation = { date, time, topic, attach, cb ->
                                                viewModel.bookDemoConsultation(loadedLawyer, date, time, topic, attach, cb)
                                            },
                                            onSendChatMessage = { text, briefing ->
                                                viewModel.sendDemoLawyerMessage(loadedLawyer, text, briefing)
                                            },
                                            onSubmitReview = { author, rating, comment, cb ->
                                                viewModel.submitDemoReview(loadedLawyer.id, author, rating, comment, cb)
                                            }
                                        )
                                    }
                                }

                                composable(Routes.SETTINGS) {
                                    val caseNotes by viewModel.caseNotes.collectAsStateWithLifecycle()
                                    SettingsScreen(
                                        language = language,
                                        isDarkMode = isDarkMode,
                                        activeMode = activeMode,
                                        liteModeEnabled = liteModeEnabled,
                                        caseNotes = caseNotes,
                                        strings = strings,
                                        onToggleDarkMode = viewModel::setDarkMode,
                                        onSelectLanguage = viewModel::setLanguage,
                                        onSelectModeOverride = viewModel::setModeOverride,
                                        onToggleLiteMode = viewModel::setLiteMode,
                                        onDeleteAllData = viewModel::deleteAllUserData
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (showLanguageSheet) {
                LanguagePickerSheet(
                    currentLanguage = language,
                    strings = strings,
                    onSelect = viewModel::setLanguage,
                    onDismiss = { showLanguageSheet = false }
                )
            }

            if (showModeSheet) {
                ModalBottomSheet(onDismissRequest = { showModeSheet = false }) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                            .padding(bottom = 28.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = strings.settingsAiModeSection,
                                style = MaterialTheme.typography.titleLarge
                            )
                            ModeBadge(mode = activeMode, strings = strings)
                        }
                        Text(
                            text = strings.switchModeTooltip,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        listOf(
                            Triple(AiMode.ONLINE, strings.modeOnlineLabel, strings.modeOnlineDesc),
                            Triple(AiMode.OFFLINE, strings.modeOfflineLabel, strings.modeOfflineDesc),
                            Triple(AiMode.LIBRARY_ONLY, strings.modeLibraryLabel, strings.modeLibraryDesc)
                        ).forEach { (mode, title, desc) ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (activeMode == mode) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.setModeOverride(mode)
                                        showModeSheet = false
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    RadioButton(
                                        selected = activeMode == mode,
                                        onClick = {
                                            viewModel.setModeOverride(mode)
                                            showModeSheet = false
                                        }
                                    )
                                    Column {
                                        Text(title, style = MaterialTheme.typography.titleSmall)
                                        Text(
                                            desc,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpressiveFloatingBottomDock(
    tabs: List<NavTabItem>,
    currentRoute: String?,
    onSelectTab: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            tonalElevation = 6.dp,
            shadowElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                tabs.forEach { tab ->
                    val selected = currentRoute == tab.route
                    val pillColor by animateColorAsState(
                        targetValue = when {
                            selected && tab.isEmergency -> MaterialTheme.colorScheme.errorContainer
                            selected -> MaterialTheme.colorScheme.primaryContainer
                            else -> Color.Transparent
                        },
                        animationSpec = HaqooqMotion.fastEffectsSpring(),
                        label = "dockPillBg"
                    )
                    val contentTint by animateColorAsState(
                        targetValue = when {
                            tab.isEmergency -> MaterialTheme.colorScheme.error
                            selected -> MaterialTheme.colorScheme.onPrimaryContainer
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        animationSpec = HaqooqMotion.fastEffectsSpring(),
                        label = "dockContentTint"
                    )

                    Surface(
                        shape = CircleShape,
                        color = pillColor,
                        modifier = Modifier
                            .weight(1f)
                            .minimumInteractiveComponentSize()
                            .clip(CircleShape)
                            .testTag("bottom_nav_${tab.route}")
                            .clickable { onSelectTab(tab.route) }
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 7.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.label,
                                tint = contentTint,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = tab.label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                color = contentTint,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}
