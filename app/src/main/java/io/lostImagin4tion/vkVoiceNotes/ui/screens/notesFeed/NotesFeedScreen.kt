package io.lostImagin4tion.vkVoiceNotes.ui.screens.notesFeed

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.lostImagin4tion.vkVoiceNotes.domain.entities.VoiceNote
import io.lostImagin4tion.vkVoiceNotes.ui.theme.Dimensions
import io.lostImagin4tion.vkVoiceNotes.ui.uiKit.cards.VoiceNoteCard
import io.lostImagin4tion.vkVoiceNotes.ui.uiKit.text.SubtitleText
import io.lost_imagin4tion.vk_voicenotes.R

@Composable
fun NotesFeedScreen(

) {
    val viewModel: NotesFeedViewModel = viewModel()

    val voiceNotes = viewModel.voiceNotes

    NotesFeedScreenContent(
        voiceNotes = voiceNotes,
        startRecording = viewModel::startRecording,
        stopRecording = viewModel::stopRecording,
        addNewVoiceNote = viewModel::addNewVoiceNote
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotesFeedScreenContent(
    voiceNotes: List<VoiceNote> = emptyList(),
    startRecording: () -> Unit = {},
    stopRecording: () -> Unit = {},
    addNewVoiceNote: (String?) -> Unit = {},
    startPlaying: () -> Unit = {},
    stopPlaying: () -> Unit = {}
) {
    var isRecordingAudio by rememberSaveable {
        mutableStateOf(false)
    }

    val interactionSource = remember { MutableInteractionSource() }
    val interactions = remember { mutableStateListOf<Interaction>() }

    val transition = updateTransition(isRecordingAudio, label = "recordingTransition")

    val buttonBackgroundScale by transition.animateFloat(
        label = "fabScale"
    ) {
        if (it) {
            1.2f
        } else {
            1f
        }
    }

    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.5f * buttonBackgroundScale,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> {
                    interactions.add(interaction)
                    isRecordingAudio = true
                    startRecording()
                }
                is PressInteraction.Release -> {
                    interactions.remove(interaction.press)
                    isRecordingAudio = false
                    stopRecording()
                    addNewVoiceNote("хуй")
                }
                is PressInteraction.Cancel -> {
                    interactions.remove(interaction.press)
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .padding(bottom = Dimensions.mainVerticalPadding * 2)
                    .size(80.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer.copy(
                        alpha = 0.25f
                    ),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(64.dp)
                        .scale(
                            if (isRecordingAudio) scale else 1f
                        )
                ) {}

                FloatingActionButton(
                    onClick = {},
                    interactionSource = interactionSource,
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = Color.White,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(64.dp * buttonBackgroundScale)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_mic),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(36.dp * buttonBackgroundScale)
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Dimensions.mainHorizontalPadding / 2)
            ) {
                item {
                    Spacer(modifier = Modifier.height(Dimensions.mainVerticalPadding * 2))

                    SubtitleText(
                        textRes = R.string.notes_feed_header
                    )

                    Spacer(modifier = Modifier.height(Dimensions.mainVerticalPadding * 2))
                }

                item {
                    var isPlaying by rememberSaveable {
                        mutableStateOf(true)
                    }

                    VoiceNoteCard(
                        onIconButtonCLick = { isPlaying = !isPlaying },
                        cardName = "Поход к адвокату",
                        timestamp = "Сегодня в 12:51",
                        audioDuration = "5:32",
                        currentAudioTime = "2:18",
                        listeningProgress = 45f,
                        isPlaying = isPlaying
                    )

                    Spacer(modifier = Modifier.height(Dimensions.commonPadding))
                }

                item {
                    var isPlaying by rememberSaveable {
                        mutableStateOf(false)
                    }

                    VoiceNoteCard(
                        onIconButtonCLick = { isPlaying = !isPlaying },
                        cardName = "Разговор с Иваном",
                        timestamp = "14.02.2022 в 15:32",
                        audioDuration = "2:31",
                        isPlaying = isPlaying
                    )

                    Spacer(modifier = Modifier.height(Dimensions.commonPadding))
                }

                item {
                    var isPlaying by rememberSaveable {
                        mutableStateOf(false)
                    }

                    VoiceNoteCard(
                        onIconButtonCLick = { isPlaying = !isPlaying },
                        cardName = "Крутой трек - надо найти!",
                        timestamp = "12.02.2022 в 13:11",
                        audioDuration = "0:31",
                        isPlaying = isPlaying
                    )

                    Spacer(modifier = Modifier.height(Dimensions.commonPadding))
                }
            }
        }
    }
}