package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.contracts.AiMode
import com.example.contracts.AppLanguage
import com.example.contracts.UiState
import com.example.contracts.UrgencyLevel
import com.example.l10n.AppStrings
import com.example.l10n.LocalizedStrings
import com.example.ui.theme.HaqooqMotion
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.EmergencyRedContainer
import com.example.ui.theme.LibrarySlate
import com.example.ui.theme.LibrarySlateContainer
import com.example.ui.theme.LibrarySlateDark
import com.example.ui.theme.LibrarySlateDarkContainer
import com.example.ui.theme.LocalHaqooqDarkMode
import com.example.ui.theme.OfflineAmber
import com.example.ui.theme.OfflineAmberContainer
import com.example.ui.theme.OfflineAmberDark
import com.example.ui.theme.OfflineAmberDarkContainer
import com.example.ui.theme.OnlineGreen
import com.example.ui.theme.OnlineGreenContainer
import com.example.ui.theme.OnlineGreenDark
import com.example.ui.theme.OnlineGreenDarkContainer

/**
 * Material 3 Expressive segmented group shape:
 * outer edges use large expressive radius (22.dp), adjacent inner edges use subtle 8.dp radius.
 */
fun expressiveGroupShape(
    index: Int,
    totalCount: Int,
    outerRadius: Dp = 22.dp,
    innerRadius: Dp = 8.dp
): Shape {
    if (totalCount <= 1) return RoundedCornerShape(outerRadius)
    return when (index) {
        0 -> RoundedCornerShape(
            topStart = outerRadius,
            topEnd = outerRadius,
            bottomStart = innerRadius,
            bottomEnd = innerRadius
        )
        totalCount - 1 -> RoundedCornerShape(
            topStart = innerRadius,
            topEnd = innerRadius,
            bottomStart = outerRadius,
            bottomEnd = outerRadius
        )
        else -> RoundedCornerShape(innerRadius)
    }
}

/**
 * Material 3 Expressive shape-morphing interactive surface with tactile spring scale & corner morph.
 */
@Composable
fun ExpressiveInteractiveSurface(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    restingCorner: Dp = 24.dp,
    pressedCorner: Dp = 14.dp,
    color: Color = MaterialTheme.colorScheme.surfaceContainer,
    border: BorderStroke? = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val cornerRadius by animateDpAsState(
        targetValue = if (isPressed) pressedCorner else restingCorner,
        animationSpec = HaqooqMotion.spatialSpring(),
        label = "expressiveCorner"
    )
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = HaqooqMotion.spatialSpring(),
        label = "expressiveScale"
    )

    Surface(
        shape = RoundedCornerShape(cornerRadius),
        color = color,
        border = border,
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.material3.ripple(),
                onClick = onClick
            ),
        content = content
    )
}

@Composable
fun ExpressiveThemeTogglePill(
    isDarkMode: Boolean,
    contentDescription: String,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier
            .minimumInteractiveComponentSize()
            .clickable(onClick = onToggle)
    ) {
        Box(
            modifier = Modifier.padding(9.dp),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = isDarkMode,
                transitionSpec = {
                    (fadeIn(HaqooqMotion.fastEffectsSpring()) + scaleIn(initialScale = 0.7f, animationSpec = HaqooqMotion.spatialSpring()))
                        .togetherWith(fadeOut(HaqooqMotion.fastEffectsSpring()) + scaleOut(targetScale = 0.7f))
                },
                label = "themeIconMorph"
            ) { dark ->
                Icon(
                    imageVector = if (dark) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                    contentDescription = contentDescription,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun ModeBadge(
    mode: AiMode,
    strings: LocalizedStrings,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isDark = LocalHaqooqDarkMode.current
    val (bg, fg, icon) = when (mode) {
        AiMode.ONLINE -> Triple(
            if (isDark) OnlineGreenDarkContainer else OnlineGreenContainer,
            if (isDark) OnlineGreenDark else OnlineGreen,
            Icons.Filled.CloudDone
        )
        AiMode.OFFLINE -> Triple(
            if (isDark) OfflineAmberDarkContainer else OfflineAmberContainer,
            if (isDark) OfflineAmberDark else OfflineAmber,
            Icons.Filled.CloudOff
        )
        AiMode.LIBRARY_ONLY -> Triple(
            if (isDark) LibrarySlateDarkContainer else LibrarySlateContainer,
            if (isDark) LibrarySlateDark else LibrarySlate,
            Icons.Filled.MenuBook
        )
    }

    Surface(
        color = bg,
        shape = CircleShape,
        border = BorderStroke(1.dp, fg.copy(alpha = 0.35f)),
        modifier = modifier
            .testTag("ai_mode_badge")
            .let { if (onClick != null) it.clickable(onClick = onClick) else it }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = AppStrings.modeLabel(mode, strings),
                tint = fg,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = AppStrings.modeLabel(mode, strings),
                style = MaterialTheme.typography.labelMedium,
                color = fg,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun UrgencyChip(
    urgency: UrgencyLevel,
    strings: LocalizedStrings,
    modifier: Modifier = Modifier
) {
    val isDark = LocalHaqooqDarkMode.current
    val (bg, fg) = when (urgency) {
        UrgencyLevel.HIGH -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.error
        UrgencyLevel.MEDIUM -> (if (isDark) OfflineAmberDarkContainer else OfflineAmberContainer) to (if (isDark) OfflineAmberDark else OfflineAmber)
        UrgencyLevel.LOW -> (if (isDark) OnlineGreenDarkContainer else OnlineGreenContainer) to (if (isDark) OnlineGreenDark else OnlineGreen)
    }
    Surface(
        color = bg,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Text(
            text = AppStrings.urgencyLabel(urgency, strings),
            style = MaterialTheme.typography.labelSmall,
            color = fg,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun DisclaimerBanner(
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun DemoLawyerBanner(
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, OfflineAmber.copy(alpha = 0.45f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("demo_lawyer_banner")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Gavel,
                contentDescription = null,
                tint = OfflineAmber,
                modifier = Modifier.size(17.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun LanguageSwitcherChip(
    currentLanguage: AppLanguage,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
        shape = CircleShape,
        modifier = modifier
            .testTag("language_switcher_chip")
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Language,
                contentDescription = currentLanguage.nativeLabel,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = currentLanguage.nativeLabel,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                maxLines = 1
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguagePickerSheet(
    currentLanguage: AppLanguage,
    strings: LocalizedStrings,
    onSelect: (AppLanguage) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = strings.chooseLanguageTitle,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = strings.chooseLanguageSubtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            AppLanguage.entries.forEach { lang ->
                val selected = lang == currentLanguage
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (selected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surface
                    },
                    border = BorderStroke(
                        width = if (selected) 1.5.dp else 1.dp,
                        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("lang_option_${lang.code}")
                        .clickable {
                            onSelect(lang)
                            onDismiss()
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = lang.nativeLabel,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = when (lang) {
                                    AppLanguage.EN -> "English (LTR)"
                                    AppLanguage.UR -> "اردو (دائیں سے بائیں RTL)"
                                    AppLanguage.ROMAN_UR -> "Roman Urdu (LTR)"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (selected) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HaqooqTopBar(
    title: String,
    currentLanguage: AppLanguage,
    activeMode: AiMode,
    isDarkMode: Boolean,
    strings: LocalizedStrings,
    showBack: Boolean = false,
    onBack: () -> Unit = {},
    onToggleTheme: () -> Unit,
    onLanguageClick: () -> Unit,
    onSettingsClick: (() -> Unit)? = null,
    onModeBadgeClick: (() -> Unit)? = null
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground
        ),
        navigationIcon = {
            if (showBack) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("top_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (!showBack) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.size(34.dp)
                    ) {
                        androidx.compose.foundation.Image(
                            painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.ic_haqooq_foreground_img),
                            contentDescription = strings.appName,
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        actions = {
            ExpressiveThemeTogglePill(
                isDarkMode = isDarkMode,
                contentDescription = strings.themeSwitchTooltip,
                onToggle = onToggleTheme,
                modifier = Modifier
                    .padding(end = 6.dp)
                    .testTag("theme_toggle_button")
            )
            LanguageSwitcherChip(
                currentLanguage = currentLanguage,
                onClick = onLanguageClick,
                modifier = Modifier.padding(end = 4.dp)
            )
            if (onSettingsClick != null) {
                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier.testTag("settings_nav_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = strings.settingsTitle
                    )
                }
            }
        }
    )
}

@Composable
fun <T> StateView(
    state: UiState<T>,
    strings: LocalizedStrings,
    emptyTitle: String,
    emptySubtitle: String,
    onRetry: (() -> Unit)? = null,
    content: @Composable (T) -> Unit
) {
    when (state) {
        is UiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Text(
                        text = strings.stateLoading,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        is UiState.Empty -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.SearchOff,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(44.dp)
                    )
                    Text(
                        text = emptyTitle,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = emptySubtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        is UiState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ErrorOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(44.dp)
                    )
                    Text(
                        text = state.message.ifBlank { strings.stateError },
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    if (onRetry != null) {
                        Button(onClick = onRetry) {
                            Text(strings.retryLabel)
                        }
                    }
                }
            }
        }
        is UiState.Offline -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.WifiOff,
                        contentDescription = null,
                        tint = OfflineAmber,
                        modifier = Modifier.size(44.dp)
                    )
                    Text(
                        text = state.fallbackMessage.ifBlank { strings.stateOffline },
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    if (onRetry != null) {
                        Button(onClick = onRetry) {
                            Text(strings.retryLabel)
                        }
                    }
                }
            }
        }
        is UiState.Success -> content(state.data)
    }
}
