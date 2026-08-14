package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Build
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Radio
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainViewModel
import com.example.ui.WalkieTarget
import com.example.ui.components.HudTopBar
import com.example.ui.dialogs.AddChannelDialog
import com.example.ui.dialogs.AddContactDialog
import com.example.ui.dialogs.BurnerStoreDialog
import com.example.ui.dialogs.NoiseCancellationDialog
import com.example.ui.dialogs.PhoneConfirmDialog
import com.example.ui.dialogs.PurchaseLayoutDialog
import com.example.ui.dialogs.SafetyKeyDialog
import com.example.ui.dialogs.SubscriptionPlansDialog
import com.example.ui.dialogs.TermsOfServiceDialog
import com.example.ui.dialogs.ThemeLayoutSelectorDialog
import com.example.ui.screens.BurnerManagementScreen
import com.example.ui.screens.ChannelsScreen
import com.example.ui.screens.ContactsScreen
import com.example.ui.screens.HumanVerificationScreen
import com.example.ui.screens.OnboardingFlowScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TransmissionsLogScreen
import com.example.ui.screens.WalkieTalkieScreen
import com.example.ui.theme.BurnerGold
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PttGreenDark
import com.example.ui.theme.PttGreenGlow
import com.example.ui.theme.PttNeonGreen
import com.example.ui.theme.TacticalCardBorder
import com.example.ui.theme.TacticalCyan
import com.example.ui.theme.TacticalDarkBg
import com.example.ui.theme.TacticalSurface
import com.example.ui.theme.TacticalSurfaceElevated
import com.example.ui.theme.TacticalTextMuted
import com.example.ui.theme.TacticalTextPrimary
import com.example.ui.theme.TacticalTextSecondary

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                InstaWireApp(viewModel = viewModel)
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            val identity = viewModel.userIdentity.value
            if (identity.hardwareVolumePttEnabled) {
                if (event?.repeatCount == 0) {
                    viewModel.onPttPressed()
                }
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            val identity = viewModel.userIdentity.value
            if (identity.hardwareVolumePttEnabled) {
                viewModel.onPttReleased()
                return true
            }
        }
        return super.onKeyUp(keyCode, event)
    }

    override fun onKeyLongPress(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            val identity = viewModel.userIdentity.value
            if (identity.hardwareVolumePttEnabled) {
                return true
            }
        }
        return super.onKeyLongPress(keyCode, event)
    }
}

data class NavTabItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

@Composable
fun InstaWireApp(viewModel: MainViewModel) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val userIdentity by viewModel.userIdentity.collectAsStateWithLifecycle()
    val channels by viewModel.channels.collectAsStateWithLifecycle()
    val contacts by viewModel.contacts.collectAsStateWithLifecycle()
    val recentTransmissions by viewModel.recentTransmissions.collectAsStateWithLifecycle()
    val burnerLines by viewModel.burnerLines.collectAsStateWithLifecycle()
    val spectrumBars by viewModel.audioEngine.spectrumBars.collectAsStateWithLifecycle()

    val accentColor = Color(userIdentity.themeScheme.primaryHex)
    val glowColor = Color(userIdentity.themeScheme.glowHex)

    // Request audio record and notification permissions smoothly on startup
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { _ -> }

    LaunchedEffect(Unit) {
        val permissions = mutableListOf(Manifest.permission.RECORD_AUDIO)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        val missing = permissions.filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }
        if (missing.isNotEmpty()) {
            permissionLauncher.launch(missing.toTypedArray())
        }
    }

    // Manage Background Walkie-Talkie Transceiver Service
    LaunchedEffect(userIdentity.backgroundMonitoringEnabled, userIdentity.isHumanVerified) {
        if (userIdentity.isHumanVerified && userIdentity.backgroundMonitoringEnabled) {
            com.example.service.WalkieBackgroundService.start(context)
        } else {
            com.example.service.WalkieBackgroundService.stop(context)
        }
    }

    val navTabs = listOf(
        NavTabItem("PTT", Icons.Filled.Radio, Icons.Outlined.Radio, "tab_radio"),
        NavTabItem("CHANNELS", Icons.Filled.Groups, Icons.Outlined.Groups, "tab_channels"),
        NavTabItem("BURNER", Icons.Filled.Whatshot, Icons.Outlined.Whatshot, "tab_burner"),
        NavTabItem("SETTINGS", Icons.Filled.Settings, Icons.Outlined.Settings, "tab_settings"),
        NavTabItem("LOGS", Icons.Filled.History, Icons.Outlined.History, "tab_logs")
    )

    // Gated Check: If not human verified or not agreed to terms of service, show mandatory multi-screen onboarding & agreement flow
    if (!userIdentity.isHumanVerified || !userIdentity.hasAgreedToTerms) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(TacticalDarkBg)
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            OnboardingFlowScreen(
                userIdentity = userIdentity,
                onCompleteVerification = { confirmedNum ->
                    viewModel.verifyHumanAndAgreeToTerms(confirmedNum)
                },
                onPreviewSoundChirp = {
                    viewModel.previewPttSound(userIdentity.soundProfile, true)
                }
            )
        }
        return
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(TacticalDarkBg),
        containerColor = TacticalDarkBg,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            Box(modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)) {
                HudTopBar(
                    userIdentity = userIdentity,
                    onOpenSafetyKey = { viewModel.setSafetyKeyModalOpen(true) },
                    onOpenNoiseCancel = { viewModel.setNoiseCancelModalOpen(true) },
                    onOpenBurnerStore = { viewModel.setActiveTab(2) },
                    onOpenSubscriptionPlans = { viewModel.setSubscriptionModalOpen(true) },
                    onOpenThemeSelector = { viewModel.setThemeLayoutModalOpen(true) },
                    onOpenPhoneConfirm = { viewModel.setPhoneConfirmModalOpen(true) },
                    modifier = Modifier.testTag("hud_top_bar")
                )
            }
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_nav_bar"),
                containerColor = TacticalSurface,
                tonalElevation = 8.dp
            ) {
                navTabs.forEachIndexed { index, tab ->
                    val isSelected = uiState.activeTab == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.setActiveTab(index) },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.label,
                                tint = if (isSelected) accentColor else TacticalTextMuted
                            )
                        },
                        label = {
                            Text(
                                text = tab.label,
                                color = if (isSelected) accentColor else TacticalTextMuted,
                                fontSize = 9.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontFamily = FontFamily.Monospace
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = accentColor,
                            unselectedIconColor = TacticalTextMuted,
                            selectedTextColor = accentColor,
                            unselectedTextColor = TacticalTextMuted,
                            indicatorColor = TacticalSurfaceElevated
                        ),
                        modifier = Modifier.testTag(tab.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(TacticalDarkBg)
        ) {
            when (uiState.activeTab) {
                0 -> WalkieTalkieScreen(
                    activeTarget = uiState.activeTarget,
                    channels = channels,
                    contacts = contacts,
                    recentTransmissions = recentTransmissions,
                    pttState = uiState.pttState,
                    transmitElapsedSec = uiState.transmitElapsedSeconds,
                    spectrumBars = spectrumBars,
                    userIdentity = userIdentity,
                    onSelectTarget = { viewModel.selectTarget(it) },
                    onPttPress = { viewModel.onPttPressed() },
                    onPttRelease = { viewModel.onPttReleased() },
                    onOpenSafetyKey = { viewModel.setSafetyKeyModalOpen(true) },
                    onOpenNoiseCancel = { viewModel.setNoiseCancelModalOpen(true) },
                    onToggleNoiseCancellation = { viewModel.toggleNoiseCancellation() },
                    onOpenSubscriptionPlans = { viewModel.setSubscriptionModalOpen(true) },
                    onOpenThemeSelector = { viewModel.setThemeLayoutModalOpen(true) },
                    onPlayTransmission = { viewModel.playAudioClip(it) }
                )
                1 -> ChannelsScreen(
                    channels = channels,
                    activeTarget = uiState.activeTarget,
                    onSelectChannel = {
                        viewModel.selectTarget(WalkieTarget.ChannelTarget(it))
                        viewModel.setActiveTab(0)
                    },
                    onOpenAddChannel = { viewModel.setAddChannelModalOpen(true) },
                    onOpenSafetyKey = {
                        viewModel.selectTarget(WalkieTarget.ChannelTarget(it))
                        viewModel.setSafetyKeyModalOpen(true)
                    }
                )
                2 -> BurnerManagementScreen(
                    userIdentity = userIdentity,
                    savedBurnerLines = burnerLines,
                    onToggleActiveNumber = { viewModel.toggleActiveNumber() },
                    onActivateBurnerLine = { viewModel.activateBurnerLine(it) },
                    onAddAndActivateBurnerLine = { number, label, areaCode, cityRegion ->
                        viewModel.addAndActivateBurnerLine(number, label, areaCode, cityRegion)
                    },
                    onDeleteBurnerLine = { viewModel.deleteBurnerLine(it) },
                    onOpenSubscriptionPlans = { viewModel.setSubscriptionModalOpen(true) }
                )
                3 -> SettingsScreen(
                    userIdentity = userIdentity,
                    onSetLayoutType = { viewModel.setLayoutType(it) },
                    onSetThemeScheme = { viewModel.setThemeScheme(it) },
                    onSetSoundProfile = { viewModel.setSoundProfile(it) },
                    onPreviewSound = { profile, isPress -> viewModel.previewPttSound(profile, isPress) },
                    onOpenThemeLayoutSelector = { viewModel.setThemeLayoutModalOpen(true) },
                    onToggleChirp = { viewModel.toggleChirp() },
                    onToggleRogerBeep = { viewModel.toggleRogerBeep() },
                    onToggleHardwareVolumePtt = { viewModel.toggleHardwareVolumePtt() },
                    onToggleBackgroundMonitoring = { viewModel.toggleBackgroundMonitoring() },
                    onToggleBackgroundAudioBeep = { viewModel.toggleBackgroundAudioBeep() },
                    onToggleZeroLogs = { viewModel.toggleZeroLogs() },
                    onSetEphemeralTimeout = { viewModel.setEphemeralTimeout(it) },
                    onSetCallsign = { viewModel.setCallsign(it) },
                    onConnectToSupport = { viewModel.connectToSupportChannel() },
                    onOpenSubscriptionPlans = { viewModel.setSubscriptionModalOpen(true) },
                    onOpenNoiseCancelModal = { viewModel.setNoiseCancelModalOpen(true) },
                    onOpenPhoneConfirmModal = { viewModel.setPhoneConfirmModalOpen(true) },
                    onOpenTermsOfService = { viewModel.setTermsModalOpen(true) },
                    onWipeAllLogs = { viewModel.wipeAllLogs() }
                )
                4 -> TransmissionsLogScreen(
                    transmissions = recentTransmissions,
                    userIdentity = userIdentity,
                    onPlayTransmission = { viewModel.playAudioClip(it) },
                    onWipeAllLogs = { viewModel.wipeAllLogs() }
                )
            }

            // Notification Banner
            AnimatedVisibility(
                visible = uiState.lastVerifiedNotification != null,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
            ) {
                uiState.lastVerifiedNotification?.let { msg ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(PttGreenDark)
                            .border(1.dp, PttNeonGreen, RoundedCornerShape(10.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = "Notification",
                                    tint = PttNeonGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = msg,
                                    color = TacticalTextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = TacticalTextSecondary,
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable { viewModel.clearNotification() }
                            )
                        }
                    }
                }
            }
        }

        // Modals / Dialogs
        if (uiState.isSafetyKeyModalOpen) {
            SafetyKeyDialog(
                target = uiState.activeTarget,
                userIdentity = userIdentity,
                onDismiss = { viewModel.setSafetyKeyModalOpen(false) },
                onVerifyToggled = { isVerified ->
                    val target = uiState.activeTarget
                    if (target is WalkieTarget.ContactTarget) {
                        viewModel.verifyContactKey(target.contact.id, isVerified)
                    }
                }
            )
        }

        if (uiState.isNoiseCancelModalOpen) {
            NoiseCancellationDialog(
                userIdentity = userIdentity,
                onDismiss = { viewModel.setNoiseCancelModalOpen(false) },
                onToggleNoiseCancellation = { viewModel.toggleNoiseCancellation() },
                onSelectFilterMode = { viewModel.setNoiseFilterMode(it) },
                onToggleChirp = { viewModel.toggleChirp() },
                onToggleRogerBeep = { viewModel.toggleRogerBeep() },
                onToggleZeroLogs = { viewModel.toggleZeroLogs() }
            )
        }

        if (uiState.isBurnerStoreModalOpen) {
            BurnerStoreDialog(
                userIdentity = userIdentity,
                isProvisioning = uiState.isFirebaseProvisioning,
                lastProvisioningResponse = uiState.lastFirebaseProvisioningResult,
                onDismiss = { viewModel.setBurnerStoreModalOpen(false) },
                onGenerateNewBurner = { viewModel.generateNewBurnerNumber() },
                onToggleActiveNumber = { viewModel.toggleActiveNumber() },
                onOpenSubscriptionPlans = {
                    viewModel.setBurnerStoreModalOpen(false)
                    viewModel.setSubscriptionModalOpen(true)
                }
            )
        }

        if (uiState.isSubscriptionModalOpen) {
            SubscriptionPlansDialog(
                userIdentity = userIdentity,
                onDismiss = { viewModel.setSubscriptionModalOpen(false) },
                onSelectTier = { tier -> viewModel.setSubscriptionTier(tier) }
            )
        }

        if (uiState.isPhoneConfirmModalOpen) {
            PhoneConfirmDialog(
                initialNumber = userIdentity.phoneNumber,
                onDismiss = { viewModel.setPhoneConfirmModalOpen(false) },
                onConfirm = { confirmedNum -> viewModel.confirmPhoneNumber(confirmedNum) }
            )
        }

        if (uiState.isTermsModalOpen) {
            TermsOfServiceDialog(
                onDismiss = { viewModel.setTermsModalOpen(false) }
            )
        }

        if (uiState.isAddContactModalOpen) {
            AddContactDialog(
                onDismiss = { viewModel.setAddContactModalOpen(false) },
                onAddContact = { name, number, callsign, isBurner ->
                    viewModel.addContact(name, number, callsign, isBurner)
                }
            )
        }

        if (uiState.isAddChannelModalOpen) {
            AddChannelDialog(
                onDismiss = { viewModel.setAddChannelModalOpen(false) },
                onAddChannel = { name, freq, desc ->
                    viewModel.addChannel(name, freq, desc)
                }
            )
        }

        if (uiState.isThemeLayoutModalOpen) {
            ThemeLayoutSelectorDialog(
                userIdentity = userIdentity,
                onDismiss = { viewModel.setThemeLayoutModalOpen(false) },
                onSelectLayout = { viewModel.setLayoutType(it) },
                onSelectTheme = { viewModel.setThemeScheme(it) },
                onSelectSound = { viewModel.setSoundProfile(it) },
                onPreviewSound = { profile, isPress -> viewModel.previewPttSound(profile, isPress) },
                onOpenPurchaseModal = { itemKey, title, price, desc ->
                    viewModel.openPurchaseModal(itemKey, title, price, desc)
                }
            )
        }

        if (uiState.isPurchaseModalOpen && uiState.pendingPurchaseItemKey != null) {
            PurchaseLayoutDialog(
                itemKey = uiState.pendingPurchaseItemKey ?: "",
                title = uiState.pendingPurchaseTitle,
                price = uiState.pendingPurchasePrice,
                description = uiState.pendingPurchaseDescription,
                userIdentity = userIdentity,
                onDismiss = { viewModel.closePurchaseModal() },
                onConfirmPurchase = { viewModel.completePurchase(it) }
            )
        }
    }
}
