package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FrontHand
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FriendUser
import com.example.data.model.GiftTransaction
import com.example.data.model.LiveRoomMessage
import com.example.data.model.UserProfile
import com.example.data.model.WorldwideRoom
import com.example.data.model.WorldwideSpeaker

@Composable
fun WorldwideRoomScreen(
    room: WorldwideRoom,
    userProfile: UserProfile,
    speakers: List<WorldwideSpeaker>,
    messages: List<LiveRoomMessage>,
    isPttActive: Boolean,
    isVoiceActive: Boolean,
    activeSpeakerName: String?,
    hasRaisedHand: Boolean,
    isAudioMuted: Boolean,
    activeGiftBanner: GiftTransaction?,
    onLeaveRoom: () -> Unit,
    onStartPtt: () -> Unit,
    onStopPtt: () -> Unit,
    onToggleRaiseHand: () -> Unit,
    onToggleMute: () -> Unit,
    onOpenSendGift: (WorldwideSpeaker?) -> Unit,
    onOpenBuyCoins: () -> Unit,
    onOpenFriends: () -> Unit,
    onAddFriendFromSpeaker: (WorldwideSpeaker) -> Unit,
    onInspectSpeaker: (WorldwideSpeaker) -> Unit,
    onPostTextMessage: (String) -> Unit
) {
    var textInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Auto-scroll on new messages
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val primaryCyan = Color(0xFF38BDF8)
    val accentGold = Color(0xFFF59E0B)
    val liveGreen = Color(0xFF3FB950)
    val darkBackground = Color(0xFF0B0E14)
    val darkCard = Color(0xFF161B22)
    val borderCol = Color(0xFF30363D)

    val quickReactions = listOf("👋 Hello everyone!", "📻 Loud and clear!", "🔥 Great energy here!", "👏 Awesome radio!", "✨ Big love from ${userProfile.city}!")

    // Pulse animation for active speaker / PTT
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBackground)
    ) {
        // Top Navigation Bar
        Surface(
            color = Color(0xFF0D1117),
            border = androidx.compose.foundation.BorderStroke(1.dp, borderCol)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    IconButton(
                        onClick = onLeaveRoom,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Leave Room",
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(text = room.countryFlag, fontSize = 22.sp)

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = room.name,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            maxLines = 1
                        )
                        Text(
                            text = "${room.city.uppercase()}, ${room.country.uppercase()} • ${room.listenersCount} LISTENING",
                            color = liveGreen,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Mute / Unmute Button
                    IconButton(
                        onClick = onToggleMute,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = if (isAudioMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                            contentDescription = "Toggle Audio",
                            tint = if (isAudioMuted) Color(0xFFEF4444) else primaryCyan
                        )
                    }

                    // Leave Button
                    Button(
                        onClick = onLeaveRoom,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF21262D),
                            contentColor = Color(0xFFF87171)
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text(
                            text = "LEAVE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // Live Floating Gift Banner Alert
        AnimatedVisibility(
            visible = activeGiftBanner != null,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            if (activeGiftBanner != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = accentGold.copy(alpha = 0.25f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, accentGold)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = activeGiftBanner.giftEmoji, fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "GIFT BLAST: ${activeGiftBanner.giftName.uppercase()}!",
                                color = accentGold,
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "${activeGiftBanner.senderUsername} sent to @${activeGiftBanner.recipientUsername}!",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                            if (activeGiftBanner.message.isNotBlank()) {
                                Text(
                                    text = "\"${activeGiftBanner.message}\"",
                                    color = Color(0xFFE6EDF3),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Active Speakers Stage (Horizontal Scroll or Row)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = darkCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF21262D))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = null,
                            tint = primaryCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "LIVE ON STAGE",
                            color = primaryCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Text(
                        text = if (isVoiceActive) "🎙️ ${activeSpeakerName ?: "Someone"} Speaking..." else "📻 Squelch Open",
                        color = if (isVoiceActive) liveGreen else Color(0xFF8B949E),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    speakers.forEach { speaker ->
                        val isThisSpeakerTalking = isVoiceActive && activeSpeakerName?.contains(speaker.username, ignoreCase = true) == true
                        WorldwideSpeakerAvatar(
                            speaker = speaker,
                            isSpeaking = isThisSpeakerTalking,
                            pulseScale = if (isThisSpeakerTalking) pulseScale else 1f,
                            onTap = { onInspectSpeaker(speaker) },
                            onSendGift = { onOpenSendGift(speaker) },
                            onAddFriend = { onAddFriendFromSpeaker(speaker) }
                        )
                    }
                }
            }
        }

        // Live Room Feed (Audience text messages & Voice snippet alerts)
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = darkCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF21262D))
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(messages) { msg ->
                    LiveMessageItem(msg = msg)
                }
            }
        }

        // Central Worldwide Push-to-Talk (PTT) Transceiver Panel
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF0D1117),
            border = androidx.compose.foundation.BorderStroke(1.dp, borderCol)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Quick Reactions Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quickReactions.forEach { rx ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF21262D))
                                .border(1.dp, Color(0xFF30363D), RoundedCornerShape(14.dp))
                                .clickable { onPostTextMessage(rx) }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = rx,
                                color = Color(0xFFC9D1D9),
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Action Bar: Raise Hand + PTT Button + Gift Store + Friends
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Raise Hand Button
                    IconButton(
                        onClick = onToggleRaiseHand,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(if (hasRaisedHand) accentGold.copy(alpha = 0.2f) else Color(0xFF21262D))
                            .border(1.5.dp, if (hasRaisedHand) accentGold else Color(0xFF30363D), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FrontHand,
                            contentDescription = "Raise Hand",
                            tint = if (hasRaisedHand) accentGold else Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Main Worldwide Walkie PTT Button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 12.dp)
                            .height(54.dp)
                            .clip(RoundedCornerShape(27.dp))
                            .background(
                                Brush.linearGradient(
                                    if (isPttActive) listOf(Color(0xFFDC2626), Color(0xFFEF4444))
                                    else listOf(Color(0xFF0284C7), Color(0xFF38BDF8))
                                )
                            )
                            .border(
                                2.dp,
                                if (isPttActive) Color.White else primaryCyan,
                                RoundedCornerShape(27.dp)
                            )
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        onStartPtt()
                                        tryAwaitRelease()
                                        onStopPtt()
                                    }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Radio,
                                contentDescription = null,
                                tint = if (isPttActive) Color.White else Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isPttActive) "LIVE BROADCASTING..." else "HOLD TO TALK WORLDWIDE",
                                color = if (isPttActive) Color.White else Color.Black,
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    // Send Gift Button
                    IconButton(
                        onClick = { onOpenSendGift(null) },
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(accentGold.copy(alpha = 0.2f))
                            .border(1.5.dp, accentGold, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CardGiftcard,
                            contentDescription = "Send Gift",
                            tint = accentGold,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Chat Input Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        placeholder = {
                            Text(
                                text = "Message ${room.name}...",
                                color = Color(0xFF8B949E),
                                fontSize = 11.sp
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(22.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryCyan,
                            unfocusedBorderColor = Color(0xFF30363D),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = darkCard,
                            unfocusedContainerColor = darkCard
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (textInput.isNotBlank()) {
                                onPostTextMessage(textInput)
                                textInput = ""
                            }
                        },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(primaryCyan)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WorldwideSpeakerAvatar(
    speaker: WorldwideSpeaker,
    isSpeaking: Boolean,
    pulseScale: Float,
    onTap: () -> Unit,
    onSendGift: () -> Unit,
    onAddFriend: () -> Unit
) {
    val primaryCyan = Color(0xFF38BDF8)
    val accentGold = Color(0xFFF59E0B)
    val liveGreen = Color(0xFF3FB950)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onTap() }
            .padding(2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .scale(pulseScale)
                .clip(CircleShape)
                .background(Color(0xFF21262D))
                .border(
                    width = if (isSpeaking) 2.5.dp else 1.dp,
                    color = if (isSpeaking) liveGreen else if (speaker.isHost) accentGold else Color(0xFF30363D),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = speaker.countryFlag, fontSize = 24.sp)
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (speaker.isHost) {
                Text(text = "👑", fontSize = 10.sp)
                Spacer(modifier = Modifier.width(2.dp))
            }
            Text(
                text = speaker.username,
                color = if (isSpeaking) liveGreen else Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }

        Text(
            text = speaker.city,
            color = Color(0xFF8B949E),
            fontSize = 9.sp
        )

        Spacer(modifier = Modifier.height(2.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            // Quick Gift
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(accentGold.copy(alpha = 0.15f))
                    .clickable { onSendGift() }
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(text = "🎁 Gift", color = accentGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }

            // Quick Friend
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(primaryCyan.copy(alpha = 0.15f))
                    .clickable { onAddFriend() }
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(text = "+ Friend", color = primaryCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun LiveMessageItem(msg: LiveRoomMessage) {
    val accentGold = Color(0xFFF59E0B)
    val primaryCyan = Color(0xFF38BDF8)
    val liveGreen = Color(0xFF3FB950)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (msg.isGiftNotification) accentGold.copy(alpha = 0.12f)
                else if (msg.isVoiceSnippet) liveGreen.copy(alpha = 0.08f)
                else Color(0xFF161B22)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (msg.isGiftNotification) {
            Text(text = msg.giftEmoji ?: "🎁", fontSize = 18.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = msg.text,
                color = accentGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        } else if (msg.isVoiceSnippet) {
            Text(text = msg.senderCountryFlag, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${msg.senderUsername} [${msg.senderCallsign}] (${msg.senderCity})",
                    color = liveGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
                Text(
                    text = msg.text,
                    color = Color.White,
                    fontSize = 11.sp
                )
            }
        } else {
            Text(text = msg.senderCountryFlag, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${msg.senderUsername} [${msg.senderCallsign}]",
                    color = primaryCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
                Text(
                    text = msg.text,
                    color = Color(0xFFE6EDF3),
                    fontSize = 11.sp
                )
            }
        }
    }
}
