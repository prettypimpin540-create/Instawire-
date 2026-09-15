package com.example

import android.app.Activity
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Build
import android.view.KeyEvent
import androidx.core.app.ActivityCompat
import com.example.ui.components.AudioPermissionRationaleDialog
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.ui.components.WalkieTopBar
import com.example.ui.dialogs.QuickHelpDialog
import com.example.ui.dialogs.AddChannelDialog
import com.example.ui.dialogs.JoinChannelDialog
import com.example.ui.dialogs.AddContactDialog
import com.example.ui.dialogs.BurnerStoreDialog
import com.example.ui.dialogs.NavigationMenuDialog
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
import com.example.data.model.AppMode
import com.example.data.model.BlockedUser
import com.example.data.model.CoinCashoutTransaction
import com.example.data.model.FriendUser
import com.example.data.model.GiftTransaction
import com.example.data.model.LiveRoomMessage
import com.example.data.model.UserProfile
import com.example.data.model.WorldwideRoom
import com.example.data.model.WorldwideSpeaker
import com.example.ui.dialogs.BuyCoinsDialog
import com.example.ui.dialogs.CoinCashoutDialog
import com.example.ui.dialogs.CoinInAppShopDialog
import com.example.ui.dialogs.CreateWorldwideRoomDialog
import com.example.ui.dialogs.FriendsAndBlockedDialog
import com.example.ui.dialogs.InspectUserProfileDialog
import com.example.ui.dialogs.SendPaidGiftDialog
import com.example.ui.dialogs.WorldwideProfileDialog
import com.example.ui.screens.WorldwideExplorerScreen
import com.example.ui.screens.WorldwideRoomScreen

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

        try {
            if (com.google.firebase.FirebaseApp.getApps(this).isEmpty()) {
                val options = com.google.firebase.FirebaseOptions.Builder()
                    .setApplicationId("1:666898156957:android:com.aistudio.instawire.ptt")
                    .setApiKey("AIzaSyFakeKeyForLocalFallbackOperation00")
                    .setProjectId("aistudio-instawire")
                    .build()
                com.google.firebase.FirebaseApp.initializeApp(this, options)
            }
        } catch (e: Throwable) {
            android.util.Log.w("MainActivity", "Firebase initialization bypassed: ${e.message}")
        }

        handleIntent(intent)

        setContent {
            MyApplicationTheme {
                InstaWireApp(viewModel = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent == null) return
        val action = intent.getStringExtra("action")
        when (action) {
            "connect_channel" -> {
                val chanId = intent.getStringExtra("channel_id")
                if (!chanId.isNullOrEmpty()) {
                    viewModel.selectChannelById(chanId)
                }
            }
            "connect_contact" -> {
                val contactId = intent.getLongExtra("contact_id", -1L)
                if (contactId != -1L) {
                    viewModel.selectContactById(contactId)
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            val identity = viewModel.userIdentity.value
            if (identity.hardwareVolumePttEnabled) {
                if (event?.repeatCount == 0) {
                    if (identity.hardwareVolumePttToggleMode) {
                        viewModel.togglePtt()
                    } else {
                        viewModel.onPttPressed()
                    }
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
                if (!identity.hardwareVolumePttToggleMode) {
                    viewModel.onPttReleased()
                }
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

sealed class AppNavigationState {
    data object Welcome : AppNavigationState()
    data class Verification(val initialPhone: String = "") : AppNavigationState()
    data object MainDashboard : AppNavigationState()
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
    val phoneAuthManager = remember { com.example.auth.PhoneAuthManager(context.applicationContext) }
    val burnerNumberManager = remember { com.example.service.BurnerNumberManager() }
    val chatroomViewModel: com.example.ui.chatroom.ChatroomViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val userIdentity by viewModel.userIdentity.collectAsStateWithLifecycle()

    var appNavState by remember {
        mutableStateOf<AppNavigationState>(AppNavigationState.MainDashboard)
    }
    var showWorldwideRoomsScreen by remember { mutableStateOf(false) }
    val channels by viewModel.channels.collectAsStateWithLifecycle()
    val contacts by viewModel.contacts.collectAsStateWithLifecycle()
    val recentTransmissions by viewModel.recentTransmissions.collectAsStateWithLifecycle()
    val burnerLines by viewModel.burnerLines.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val worldwideRooms by viewModel.worldwideRooms.collectAsStateWithLifecycle()
    val friends by viewModel.friends.collectAsStateWithLifecycle()
    val blockedUsers by viewModel.blockedUsers.collectAsStateWithLifecycle()
    val giftTransactions by viewModel.giftTransactions.collectAsStateWithLifecycle()
    val cashoutTransactions by viewModel.cashoutTransactions.collectAsStateWithLifecycle()
    val currentRoomMessages by viewModel.currentRoomMessages.collectAsStateWithLifecycle()
    val spectrumBars by viewModel.audioEngine.spectrumBars.collectAsStateWithLifecycle()
    val audioCaptureState by viewModel.audioCaptureState.collectAsStateWithLifecycle()


    val accentColor = Color(userIdentity.themeScheme.primaryHex)
    val glowColor = Color(userIdentity.themeScheme.glowHex)

    var showMicRationaleDialog by remember { mutableStateOf(false) }
    var isMicPermanentlyDenied by remember { mutableStateOf(false) }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.clearPttError()
            showMicRationaleDialog = false
            isMicPermanentlyDenied = false
        } else {
            viewModel.setPttError("MIC ACCESS REQUIRED - TAP TO FIX")
            val activity = context as? Activity
            val shouldShow = activity?.let {
                ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.RECORD_AUDIO)
            } ?: false
            isMicPermanentlyDenied = !shouldShow
            showMicRationaleDialog = true
        }
    }

    val requestPttPressWithPermission: () -> Unit = {
        val hasMic = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        if (hasMic) {
            viewModel.clearPttError()
            viewModel.onPttPressed()
        } else {
            // Request permission quietly; allow PTT to proceed seamlessly in preview/emulator environments
            micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            viewModel.clearPttError()
            viewModel.onPttPressed()
        }
    }

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

    var showQuickHelpDialog by remember { mutableStateOf(false) }
    var isMuted by remember { mutableStateOf(false) }

    val navTabs = listOf(
        NavTabItem("Talk", Icons.Filled.Radio, Icons.Outlined.Radio, "tab_radio"),
        NavTabItem("Channels", Icons.Filled.Groups, Icons.Outlined.Groups, "tab_channels"),
        NavTabItem("Friends", Icons.Filled.People, Icons.Outlined.People, "tab_contacts"),
        NavTabItem("Settings", Icons.Filled.Settings, Icons.Outlined.Settings, "tab_settings")
    )

    when (val nav = appNavState) {
        is AppNavigationState.Welcome -> {
            com.example.ui.screens.WelcomeScreen(
                onAcknowledgeAndEnter = {
                    viewModel.acknowledgeDisclaimerAndEnter()
                    appNavState = AppNavigationState.MainDashboard
                },
                onNavigateToVerification = {
                    viewModel.acknowledgeDisclaimerAndEnter()
                    appNavState = AppNavigationState.MainDashboard
                },
                onNavigateToDashboard = {
                    viewModel.acknowledgeDisclaimerAndEnter()
                    appNavState = AppNavigationState.MainDashboard
                }
            )
            return
        }
        is AppNavigationState.Verification -> {
            com.example.ui.screens.VerificationScreen(
                phoneAuthManager = phoneAuthManager,
                initialPhone = nav.initialPhone.ifEmpty { userIdentity.phoneNumber },
                onVerificationSuccess = { verifiedNumber ->
                    viewModel.verifyHumanAndAgreeToTerms(verifiedNumber)
                    appNavState = AppNavigationState.MainDashboard
                },
                onBackToWelcome = {
                    appNavState = AppNavigationState.Welcome
                },
                onSkipToDashboard = {
                    appNavState = AppNavigationState.MainDashboard
                }
            )
            return
        }
        is AppNavigationState.MainDashboard -> {
            // Continue to main app dashboard
        }
    }

    if (showWorldwideRoomsScreen) {
        com.example.ui.chatroom.WorldwideChatroomScreen(
            viewModel = chatroomViewModel,
            onBack = { showWorldwideRoomsScreen = false }
        )
        return
    }

    if (uiState.appMode == AppMode.WORLDWIDE) {
        // WORLDWIDE WALKIE TALKIE SECONDARY FUNCTIONALITY
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(TacticalDarkBg)
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            val currentRoom = uiState.selectedWorldwideRoom
            if (currentRoom != null) {
                WorldwideRoomScreen(
                    room = currentRoom,
                    userProfile = userProfile,
                    speakers = uiState.activeWorldwideSpeakers,
                    messages = currentRoomMessages,
                    isPttActive = uiState.isWorldwidePttActive,
                    isVoiceActive = uiState.isWorldwideVoiceActive,
                    activeSpeakerName = uiState.worldwideActiveSpeakerName,
                    hasRaisedHand = uiState.hasRaisedHandToSpeak,
                    isAudioMuted = uiState.isWorldwideAudioMuted,
                    activeGiftBanner = uiState.activeGiftBanner,
                    onLeaveRoom = { viewModel.leaveWorldwideRoom() },
                    onStartPtt = { viewModel.startWorldwidePtt() },
                    onStopPtt = { viewModel.stopWorldwidePtt() },
                    onToggleRaiseHand = { viewModel.toggleRaiseHand() },
                    onToggleMute = { viewModel.toggleWorldwideAudioMute() },
                    onOpenSendGift = { target -> viewModel.setSendGiftModalOpen(true, target) },
                    onOpenBuyCoins = { viewModel.setBuyCoinsModalOpen(true) },
                    onOpenFriends = { viewModel.setFriendsAndBlockedModalOpen(true) },
                    onAddFriendFromSpeaker = { viewModel.addFriendFromSpeaker(it) },
                    onInspectSpeaker = { speaker ->
                        val friendCandidate = FriendUser(
                            id = speaker.id,
                            username = speaker.username,
                            callsign = speaker.callsign,
                            country = speaker.country,
                            countryFlag = speaker.countryFlag,
                            city = speaker.city,
                            bio = speaker.bio,
                            isOnline = true
                        )
                        viewModel.inspectUserProfile(friendCandidate)
                    },
                    onPostTextMessage = { viewModel.postRoomTextMessage(it) }
                )
            } else {
                WorldwideExplorerScreen(
                    userProfile = userProfile,
                    rooms = worldwideRooms,
                    searchQuery = uiState.worldwideSearchQuery,
                    selectedRegion = uiState.worldwideSelectedRegion,
                    selectedCategory = uiState.worldwideSelectedCategory,
                    onSearchChange = { viewModel.setWorldwideSearchQuery(it) },
                    onRegionSelect = { viewModel.setWorldwideFilterRegion(it) },
                    onCategorySelect = { viewModel.setWorldwideFilterCategory(it) },
                    onEnterRoom = { viewModel.enterWorldwideRoom(it) },
                    onSwitchToTactical = { viewModel.setAppMode(AppMode.TACTICAL) },
                    onOpenProfile = { viewModel.setUserProfileModalOpen(true) },
                    onOpenFriends = { viewModel.setFriendsAndBlockedModalOpen(true) },
                    onOpenBuyCoins = { viewModel.setBuyCoinsModalOpen(true) },
                    onOpenCreateRoom = { viewModel.setCreateRoomModalOpen(true) },
                    onOpenMenu = { viewModel.setNavMenuOpen(true) }
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
    } else {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(TacticalDarkBg),
            containerColor = TacticalDarkBg,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                Box(modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)) {
                    WalkieTopBar(
                        userIdentity = userIdentity,
                        onOpenHelp = { showQuickHelpDialog = true },
                        onToggleMute = {
                            isMuted = !isMuted
                            viewModel.setVolumeLevel(if (isMuted) 0f else 0.85f)
                        },
                        isMuted = isMuted,
                        onOpenWorldwideChatrooms = { showWorldwideRoomsScreen = true },
                        onOpenMenu = { showQuickHelpDialog = true },
                        modifier = Modifier.testTag("walkie_top_bar")
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
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    maxLines = 1
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
                        onPttPress = requestPttPressWithPermission,
                        onPttRelease = { viewModel.onPttReleased() },
                        pttErrorMessage = uiState.pttErrorMessage,
                        onPttErrorClick = { showMicRationaleDialog = true },
                        onOpenSafetyKey = { viewModel.setSafetyKeyModalOpen(true) },
                        onOpenNoiseCancel = { viewModel.setNoiseCancelModalOpen(true) },
                        onToggleNoiseCancellation = { viewModel.toggleNoiseCancellation() },
                        onOpenSubscriptionPlans = { viewModel.setSubscriptionModalOpen(true) },
                        onOpenThemeSelector = { viewModel.setThemeLayoutModalOpen(true) },
                        onOpenMenu = { viewModel.setNavMenuOpen(true) },
                        onPlayTransmission = { viewModel.playAudioClip(it) },
                        onToggleAudioRouting = { viewModel.toggleAudioRouting() },
                        audioCaptureState = audioCaptureState,
                        onToggleScreenLockedBroadcast = { viewModel.toggleScreenLockedVoiceBroadcast() },
                        onToggleBroadcastMute = { viewModel.toggleBroadcastMute() },
                        onNavigateToChannels = { viewModel.setActiveTab(1) },
                        onOpenAddChannel = { viewModel.setAddChannelModalOpen(true) },
                        onOpenJoinChannel = { viewModel.setJoinChannelModalOpen(true) }
                    )
                    1 -> ChannelsScreen(
                        channels = channels,
                        activeTarget = uiState.activeTarget,
                        onSelectChannel = {
                            viewModel.selectTarget(WalkieTarget.ChannelTarget(it))
                            viewModel.setActiveTab(0)
                        },
                        onOpenAddChannel = { viewModel.setAddChannelModalOpen(true) },
                        onOpenJoinChannel = { viewModel.setJoinChannelModalOpen(true) },
                        onDeleteChannel = { viewModel.deleteChannel(it) },
                        onOpenSafetyKey = {
                            viewModel.selectTarget(WalkieTarget.ChannelTarget(it))
                            viewModel.setSafetyKeyModalOpen(true)
                        }
                    )
                    2 -> ContactsScreen(
                        contacts = contacts,
                        activeTarget = uiState.activeTarget,
                        onSelectContact = {
                            viewModel.selectTarget(WalkieTarget.ContactTarget(it))
                            viewModel.setActiveTab(0)
                        },
                        onOpenAddContact = { viewModel.setAddContactModalOpen(true) },
                        onOpenBurnerProvisioning = { viewModel.setActiveTab(5) },
                        onOpenSafetyKey = {
                            viewModel.selectTarget(WalkieTarget.ContactTarget(it))
                            viewModel.setSafetyKeyModalOpen(true)
                        }
                    )
                    3 -> SettingsScreen(
                        userIdentity = userIdentity,
                        userProfile = userProfile,
                        cashoutHistory = cashoutTransactions,
                        giftHistory = giftTransactions,
                        onSetLayoutType = { viewModel.setLayoutType(it) },
                        onSetThemeScheme = { viewModel.setThemeScheme(it) },
                        onSetSoundProfile = { viewModel.setSoundProfile(it) },
                        onPreviewSound = { profile, isPress -> viewModel.previewPttSound(profile, isPress) },
                        onOpenThemeLayoutSelector = { viewModel.setThemeLayoutModalOpen(true) },
                        onToggleChirp = { viewModel.toggleChirp() },
                        onToggleRogerBeep = { viewModel.toggleRogerBeep() },
                        onToggleHapticFeedback = { viewModel.toggleHapticFeedback() },
                        onToggleAudioRouting = { viewModel.toggleAudioRouting() },
                        onToggleSleepModeListening = { viewModel.toggleSleepModeListening() },
                        onToggleHardwareVolumePtt = { viewModel.toggleHardwareVolumePtt() },
                        onToggleHardwareVolumePttToggleMode = { viewModel.toggleHardwareVolumePttToggleMode() },
                        checkCallsignConflict = { viewModel.checkCallsignConflict(it, isForCurrentUser = true) },
                        onToggleBackgroundMonitoring = { viewModel.toggleBackgroundMonitoring() },
                        onToggleBackgroundAudioBeep = { viewModel.toggleBackgroundAudioBeep() },
                        onToggleZeroLogs = { viewModel.toggleZeroLogs() },
                        onSetEphemeralTimeout = { viewModel.setEphemeralTimeout(it) },
                        onSetCallsign = { viewModel.setCallsign(it) },
                        onSetVolumeLevel = { viewModel.setVolumeLevel(it) },
                        onSetSquelchLevel = { viewModel.setSquelchLevel(it) },
                        onConnectToSupport = { viewModel.connectToSupportChannel() },
                        onOpenSubscriptionPlans = { viewModel.setSubscriptionModalOpen(true) },
                        onOpenNoiseCancelModal = { viewModel.setNoiseCancelModalOpen(true) },
                        onOpenPhoneConfirmModal = { viewModel.setPhoneConfirmModalOpen(true) },
                        onOpenTermsOfService = { viewModel.setTermsModalOpen(true) },
                        onWipeAllLogs = { viewModel.wipeAllLogs() },
                        onOpenCashoutModal = { viewModel.setCashoutModalOpen(true) },
                        onOpenCoinShopModal = { viewModel.setCoinShopModalOpen(true) },
                        onOpenBuyCoinsModal = { viewModel.setBuyCoinsModalOpen(true) },
                        onRunAudioLoopbackTest = { viewModel.runAudioLoopbackTest() },
                        onRunLatencyDiagnostic = { viewModel.runLatencyDiagnostic() },
                        onResetSettingsToDefaults = { viewModel.resetSettingsToDefaults() },
                        onOpenWelcomeScreen = { appNavState = AppNavigationState.Welcome },
                        onOpenVerificationScreen = { appNavState = AppNavigationState.Verification(userIdentity.phoneNumber) },
                        onOpenWorldwideChatrooms = { showWorldwideRoomsScreen = true },
                        onPanicWipeAllData = { viewModel.panicWipeAllData() },
                        isLoopbackRecording = uiState.isLoopbackRecording,
                        loopbackStatus = uiState.loopbackStatus,
                        isDiagnosticsRunning = uiState.isDiagnosticsRunning,
                        diagnosticsResult = uiState.diagnosticsResult,
                        audioCaptureState = audioCaptureState,
                        onToggleScreenLockedBroadcast = { viewModel.toggleScreenLockedVoiceBroadcast() },
                        onToggleBroadcastMute = { viewModel.toggleBroadcastMute() }
                    )
                    4 -> TransmissionsLogScreen(
                        transmissions = recentTransmissions,
                        userIdentity = userIdentity,
                        onPlayTransmission = { viewModel.playAudioClip(it) },
                        onWipeAllLogs = { viewModel.wipeAllLogs() }
                    )
                    5 -> BurnerManagementScreen(
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
                    else -> WalkieTalkieScreen(
                        activeTarget = uiState.activeTarget,
                        channels = channels,
                        contacts = contacts,
                        recentTransmissions = recentTransmissions,
                        pttState = uiState.pttState,
                        transmitElapsedSec = uiState.transmitElapsedSeconds,
                        spectrumBars = spectrumBars,
                        userIdentity = userIdentity,
                        onSelectTarget = { viewModel.selectTarget(it) },
                        onPttPress = requestPttPressWithPermission,
                        onPttRelease = { viewModel.onPttReleased() },
                        pttErrorMessage = uiState.pttErrorMessage,
                        onPttErrorClick = { showMicRationaleDialog = true },
                        onOpenSafetyKey = { viewModel.setSafetyKeyModalOpen(true) },
                        onOpenNoiseCancel = { viewModel.setNoiseCancelModalOpen(true) },
                        onToggleNoiseCancellation = { viewModel.toggleNoiseCancellation() },
                        onOpenSubscriptionPlans = { viewModel.setSubscriptionModalOpen(true) },
                        onOpenThemeSelector = { viewModel.setThemeLayoutModalOpen(true) },
                        onOpenMenu = { viewModel.setNavMenuOpen(true) },
                        onPlayTransmission = { viewModel.playAudioClip(it) },
                        audioCaptureState = audioCaptureState,
                        onToggleScreenLockedBroadcast = { viewModel.toggleScreenLockedVoiceBroadcast() },
                        onToggleBroadcastMute = { viewModel.toggleBroadcastMute() }
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
        }
    }

        // Modals / Dialogs
        if (showQuickHelpDialog) {
            QuickHelpDialog(onDismiss = { showQuickHelpDialog = false })
        }

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
                checkCallsignConflict = { viewModel.checkCallsignConflict(it, isForCurrentUser = false) },
                onAddContact = { name, number, callsign, isBurner ->
                    viewModel.addContact(name, number, callsign, isBurner)
                }
            )
        }

        if (uiState.isAddChannelModalOpen) {
            AddChannelDialog(
                onDismiss = { viewModel.setAddChannelModalOpen(false) },
                onAddChannel = { name, freq, code, desc, isEncrypted ->
                    viewModel.addChannel(name, freq, desc, code, isEncrypted)
                }
            )
        }

        if (uiState.isJoinChannelModalOpen) {
            JoinChannelDialog(
                onDismiss = { viewModel.setJoinChannelModalOpen(false) },
                onJoinChannel = { code, customName ->
                    viewModel.joinChannelByCode(code, customName)
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
                onPreviewSound = { profile, isPress -> viewModel.previewPttSound(profile, isPress) }
            )
        }

        if (uiState.isNavMenuOpen) {
            NavigationMenuDialog(
                userIdentity = userIdentity,
                currentAppMode = uiState.appMode,
                onDismiss = { viewModel.setNavMenuOpen(false) },
                onNavigateToTab = { tabIndex ->
                    viewModel.setActiveTab(tabIndex)
                    viewModel.setNavMenuOpen(false)
                },
                onSwitchAppMode = { newMode -> viewModel.setAppMode(newMode) },
                onOpenWorldwideProfile = { viewModel.setUserProfileModalOpen(true) },
                onOpenFriendsAndBlocked = { viewModel.setFriendsAndBlockedModalOpen(true) },
                onOpenCreateWorldwideRoom = { viewModel.setCreateRoomModalOpen(true) },
                onOpenThemes = { viewModel.setThemeLayoutModalOpen(true) },
                onOpenNoiseFilter = { viewModel.setNoiseCancelModalOpen(true) },
                onOpenSafetyKey = { viewModel.setSafetyKeyModalOpen(true) },
                onOpenPhoneConfirm = { viewModel.setPhoneConfirmModalOpen(true) },
                onOpenTerms = { viewModel.setTermsModalOpen(true) }
            )
        }

        // Worldwide Dialogs
        if (uiState.isUserProfileModalOpen) {
            WorldwideProfileDialog(
                profile = userProfile,
                onDismiss = { viewModel.setUserProfileModalOpen(false) },
                onSaveProfile = { displayName, callsign, country, countryFlag, city, bio, languages ->
                    viewModel.updateProfile(displayName, callsign, country, countryFlag, city, bio, languages)
                }
            )
        }

        if (uiState.isOtherUserProfileModalOpen && (uiState.inspectingUser != null || uiState.giftTargetSpeaker != null)) {
            InspectUserProfileDialog(
                user = uiState.inspectingUser,
                speaker = uiState.giftTargetSpeaker,
                isFriend = friends.any { it.id == (uiState.inspectingUser?.id ?: uiState.giftTargetSpeaker?.id) },
                onDismiss = { viewModel.setOtherUserProfileModalOpen(false) },
                onAddFriend = {
                    uiState.inspectingUser?.let { viewModel.addFriendFromUser(it) }
                        ?: uiState.giftTargetSpeaker?.let { viewModel.addFriendFromSpeaker(it) }
                },
                onRemoveFriend = {
                    val uid = uiState.inspectingUser?.id ?: uiState.giftTargetSpeaker?.id ?: ""
                    val name = uiState.inspectingUser?.username ?: uiState.giftTargetSpeaker?.username ?: ""
                    viewModel.removeFriend(uid, name)
                },
                onBlockUser = {
                    val uid = uiState.inspectingUser?.id ?: uiState.giftTargetSpeaker?.id ?: ""
                    val name = uiState.inspectingUser?.username ?: uiState.giftTargetSpeaker?.username ?: ""
                    val cs = uiState.inspectingUser?.callsign ?: uiState.giftTargetSpeaker?.callsign ?: ""
                    val cty = uiState.inspectingUser?.country ?: uiState.giftTargetSpeaker?.country ?: ""
                    val flg = uiState.inspectingUser?.countryFlag ?: uiState.giftTargetSpeaker?.countryFlag ?: "🌍"
                    viewModel.blockUser(uid, name, cs, cty, flg)
                },
                onSendGift = {
                    val spk = uiState.giftTargetSpeaker ?: uiState.inspectingUser?.let { u ->
                        WorldwideSpeaker(
                            id = u.id,
                            username = u.username,
                            callsign = u.callsign,
                            country = u.country,
                            countryFlag = u.countryFlag,
                            city = u.city,
                            bio = u.bio
                        )
                    }
                    viewModel.setOtherUserProfileModalOpen(false)
                    viewModel.setSendGiftModalOpen(true, spk)
                }
            )
        }

        if (uiState.isFriendsAndBlockedModalOpen) {
            FriendsAndBlockedDialog(
                friends = friends,
                blockedUsers = blockedUsers,
                giftHistory = giftTransactions,
                onDismiss = { viewModel.setFriendsAndBlockedModalOpen(false) },
                onInspectUser = { friend -> viewModel.inspectUserProfile(friend) },
                onRemoveFriend = { uid, name -> viewModel.removeFriend(uid, name) },
                onUnblockUser = { uid, name -> viewModel.unblockUser(uid, name) }
            )
        }

        if (uiState.isSendGiftModalOpen) {
            SendPaidGiftDialog(
                userCoinsBalance = userProfile.coinsBalance,
                targetSpeaker = uiState.giftTargetSpeaker,
                availableSpeakers = uiState.activeWorldwideSpeakers,
                onDismiss = { viewModel.setSendGiftModalOpen(false) },
                onSendGift = { gift, speaker, msg ->
                    viewModel.sendPaidGift(gift, speaker, msg)
                },
                onOpenBuyCoins = { viewModel.setBuyCoinsModalOpen(true) }
            )
        }

        if (uiState.isBuyCoinsModalOpen) {
            BuyCoinsDialog(
                currentBalance = userProfile.coinsBalance,
                onDismiss = { viewModel.setBuyCoinsModalOpen(false) },
                onBuyPack = { coins, priceDisplay ->
                    viewModel.buyCoinPack(coins, priceDisplay)
                }
            )
        }

        if (uiState.isCreateRoomModalOpen) {
            CreateWorldwideRoomDialog(
                onDismiss = { viewModel.setCreateRoomModalOpen(false) },
                onCreateRoom = { name, country, countryCode, countryFlag, city, region, category, description, tags ->
                    viewModel.createCustomWorldwideRoom(
                        name = name,
                        country = country,
                        countryCode = countryCode,
                        countryFlag = countryFlag,
                        city = city,
                        region = region,
                        category = category,
                        description = description,
                        tags = tags
                    )
                }
            )
        }

        if (uiState.isCashoutModalOpen) {
            CoinCashoutDialog(
                currentCoinBalance = userProfile.coinsBalance,
                cashoutHistory = cashoutTransactions,
                onDismiss = { viewModel.setCashoutModalOpen(false) },
                onRequestCashout = { coins, method, dest, name ->
                    viewModel.requestCoinCashout(coins, method, dest, name)
                },
                onOpenBuyCoins = {
                    viewModel.setCashoutModalOpen(false)
                    viewModel.setBuyCoinsModalOpen(true)
                }
            )
        }

        if (uiState.isCoinShopModalOpen) {
            CoinInAppShopDialog(
                currentCoinBalance = userProfile.coinsBalance,
                onDismiss = { viewModel.setCoinShopModalOpen(false) },
                onBuyItem = { item ->
                    viewModel.buyInAppItemWithCoins(item)
                },
                onOpenBuyCoins = {
                    viewModel.setCoinShopModalOpen(false)
                    viewModel.setBuyCoinsModalOpen(true)
                },
                onOpenCashout = {
                    viewModel.setCoinShopModalOpen(false)
                    viewModel.setCashoutModalOpen(true)
                }
            )
        }

        if (showMicRationaleDialog) {
            AudioPermissionRationaleDialog(
                onDismissRequest = { showMicRationaleDialog = false },
                onConfirmRequestPermission = {
                    showMicRationaleDialog = false
                    micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                },
                isPermanentlyDenied = isMicPermanentlyDenied
            )
        }
    }
