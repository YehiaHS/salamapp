package com.yehia.prayertimes.ui.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.io.OutputStreamWriter
import java.io.BufferedReader
import java.io.InputStreamReader
import org.json.JSONObject
import org.json.JSONArray
import com.yehia.prayertimes.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/*
private fun getDecryptedKey(): String {
    val xorArray = intArrayOf(
        89, 65, 7, 69, 88, 7, 92, 27, 7, 26, 76, 76, 78, 72, 19, 79, 26, 78, 31, 18, 30,
        28, 79, 73, 28, 28, 72, 27, 28, 25, 31, 78, 27, 78, 18, 28, 25, 29, 24, 19, 27,
        30, 79, 78, 25, 25, 26, 78, 75, 30, 24, 31, 76, 76, 19, 30, 27, 19, 29, 29, 73,
        78, 79, 78, 24, 19, 25, 24, 29, 78, 27, 72, 30
    )
    val sb = StringBuilder()
    for (i in xorArray) {
        sb.append((i xor 42).toChar())
    }
    return sb.toString()
}

private suspend fun fetchOpenRouterResponse(
    userMessage: String,
    history: List<ChatMessage>
): String = withContext(Dispatchers.IO) {
    try {
        val url = URL("https://openrouter.ai/api/v1/chat/completions")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type", "application/json")
        conn.setRequestProperty("Authorization", "Bearer ${getDecryptedKey()}")
        conn.setRequestProperty("HTTP-Referer", "https://prayertimes.yehia.com")
        conn.setRequestProperty("X-Title", "AiDeen Islamic Companion")
        
        conn.doOutput = true
        conn.connectTimeout = 10000
        conn.readTimeout = 10000

        val requestBody = JSONObject()
        requestBody.put("model", "openrouter/free")

        val messagesArray = JSONArray()
        
        // System prompt
        val systemMsg = JSONObject()
        systemMsg.put("role", "system")
        systemMsg.put("content", "You are AiDeen, an Islamic educational assistant. You provide authentic, educational, and respectful information about Islamic practices, Salah, Fasting, Zakat, Duas, and Sunnah. Be warm, accurate, and supportive. If the question is not related to Islam, politely redirect it to Islamic topics.")
        messagesArray.put(systemMsg)

        // Limit conversation context to last 6 messages
        val recentHistory = history.takeLast(6)
        for (msg in recentHistory) {
            val jsonMsg = JSONObject()
            jsonMsg.put("role", if (msg.isUser) "user" else "assistant")
            jsonMsg.put("content", msg.text)
            messagesArray.put(jsonMsg)
        }

        // Current user message
        val currentMsg = JSONObject()
        currentMsg.put("role", "user")
        currentMsg.put("content", userMessage)
        messagesArray.put(currentMsg)

        requestBody.put("messages", messagesArray)

        OutputStreamWriter(conn.outputStream).use { writer ->
            writer.write(requestBody.toString())
            writer.flush()
        }

        val responseCode = conn.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val reader = BufferedReader(InputStreamReader(conn.inputStream))
            val responseSb = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                responseSb.append(line)
            }
            reader.close()

            val jsonResponse = JSONObject(responseSb.toString())
            val choices = jsonResponse.getJSONArray("choices")
            if (choices.length() > 0) {
                val firstChoice = choices.getJSONObject(0)
                val messageObject = firstChoice.getJSONObject("message")
                return@withContext messageObject.getString("content").trim()
            }
        }
        throw Exception("Server returned code $responseCode")
    } catch (e: Exception) {
        e.printStackTrace()
        throw e
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiDeenScreen(
    onNavigateBack: () -> Unit
) {
    val palette by ThemeManager.currentPalette.collectAsState()
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var textInput by remember { mutableStateOf("") }
    val messages = remember {
        mutableStateListOf(
            ChatMessage("Assalamu Alaikum! I am AiDeen, your Islamic educational assistant. Ask me anything about Salah, Fasting, Zakat, Wudu, Duas, or Sunnah.", false)
        )
    }
    var isTyping by remember { mutableStateOf(false) }

    fun handleSend(queryText: String = textInput) {
        val query = queryText.trim()
        if (query.isEmpty()) return

        messages.add(ChatMessage(query, true))
        textInput = ""

        // Scroll to bottom
        scope.launch {
            delay(100)
            listState.animateScrollToItem(messages.size - 1)
        }

        val historyList = messages.toList()
        isTyping = true
        scope.launch {
            val aiResponse = try {
                fetchOpenRouterResponse(query, historyList)
            } catch (e: Exception) {
                // Graceful fallback to offline matching engine
                getIslamicQA(query)
            }
            isTyping = false

            // Streaming typewriter effect character by character
            val bubble = ChatMessage("", false)
            messages.add(bubble)
            val bubbleIndex = messages.size - 1

            var currentText = ""
            aiResponse.split(" ").forEach { word ->
                currentText += "$word "
                messages[bubbleIndex] = ChatMessage(currentText.trim(), false)
                listState.scrollToItem(messages.size - 1)
                delay(60) // speed of typing
            }
        }
    }

    SalamScreenScaffold {
        SalamTopBar(
            title = "AiDeen AI Assistant",
            onNavigateBack = onNavigateBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            Spacer(modifier = Modifier.height(SalamSpacing.elementGap))

            // Chat Messages Box
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(bottom = SalamSpacing.cardGap)
            ) {
                LazyColumn(
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(SalamSpacing.cardGap),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(messages) { msg ->
                        ChatBubble(message = msg)
                    }
                    if (isTyping) {
                        item {
                            Text(
                                text = "AiDeen is formulating answer...",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = palette.primary,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .staggeredEntrance(0)
                            )
                        }
                    }
                }
            }

            // Suggestion Chips (Horizontal Scroll)
            val suggestions = listOf("Salah", "Fasting", "Zakat", "Dua", "Sunnah")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(bottom = SalamSpacing.elementGap)
                    .staggeredEntrance(1),
                horizontalArrangement = Arrangement.spacedBy(SalamSpacing.elementGap)
            ) {
                suggestions.forEachIndexed { index, suggestion ->
                    val chipShape = if (index % 2 == 0) SalamShapes.expressiveCorner1 else SalamShapes.expressiveCorner2
                    Box(
                        modifier = Modifier
                            .clip(chipShape)
                            .background(palette.surfaceVariant.copy(alpha = 0.6f))
                            .border(1.dp, palette.outline.copy(alpha = 0.35f), chipShape)
                            .salamClickable {
                                handleSend(suggestion)
                            }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = suggestion,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = palette.primary
                            )
                        )
                    }
                }
            }

            // Input Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = SalamSpacing.screenPaddingV)
                    .staggeredEntrance(2),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(SalamSpacing.elementGap)
            ) {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    enabled = !isTyping,
                    placeholder = { Text("Ask about Salah, fasting, zakat...", style = MaterialTheme.typography.bodyMedium) },
                    modifier = Modifier.weight(1f),
                    shape = SalamShapes.cardMedium,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = palette.primary,
                        unfocusedBorderColor = palette.surfaceVariant,
                        focusedLabelColor = palette.primary,
                        cursorColor = palette.primary,
                        focusedTextColor = palette.textPrimary,
                        unfocusedTextColor = palette.textPrimary,
                        focusedContainerColor = palette.surface.copy(alpha = 0.5f),
                        unfocusedContainerColor = palette.surface.copy(alpha = 0.3f),
                        disabledBorderColor = palette.outline.copy(alpha = 0.15f),
                        disabledTextColor = palette.textMuted,
                        disabledPlaceholderColor = palette.textMuted
                    )
                )
                IconButton(
                    onClick = { if (!isTyping) handleSend() },
                    enabled = !isTyping,
                    modifier = Modifier
                        .size(SalamSpacing.touchTarget)
                        .background(
                            color = if (isTyping) palette.outline.copy(alpha = 0.15f) else palette.primary,
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = if (isTyping) palette.textMuted else palette.background,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val palette by ThemeManager.currentPalette.collectAsState()
    val isUser = message.isUser

    // Custom spring-based slide-in transitions (user slides from right, AI from left)
    var animatedState by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        animatedState = true
    }

    val translationX by animateFloatAsState(
        targetValue = if (animatedState) 0f else if (isUser) 150f else -150f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "bubbleTranslationX"
    )
    val alpha by animateFloatAsState(
        targetValue = if (animatedState) 1f else 0f,
        animationSpec = tween(durationMillis = 250),
        label = "bubbleAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        val bubbleShape = if (isUser) {
            RoundedCornerShape(
                topStart = 24.dp,
                topEnd = 6.dp,
                bottomStart = 24.dp,
                bottomEnd = 4.dp
            )
        } else {
            RoundedCornerShape(
                topStart = 6.dp,
                topEnd = 24.dp,
                bottomStart = 4.dp,
                bottomEnd = 24.dp
            )
        }

        Card(
            shape = bubbleShape,
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) palette.primary.copy(alpha = 0.15f) else palette.cardElevation2.copy(alpha = 0.88f)
            ),
            border = BorderStroke(
                width = 1.dp,
                color = if (isUser) palette.primary.copy(alpha = 0.5f) else palette.outline.copy(alpha = 0.35f)
            ),
            modifier = Modifier
                .widthIn(max = 280.dp)
                .graphicsLayer {
                    this.translationX = translationX
                    this.alpha = alpha
                }
        ) {
            Column(modifier = Modifier.padding(SalamSpacing.cardPaddingInner)) {
                Text(
                    text = if (isUser) "You" else "AiDeen",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isUser) palette.primary else palette.primary.copy(alpha = 0.8f)
                    ),
                    modifier = Modifier.padding(bottom = 2.dp)
                )
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = palette.textPrimary,
                        lineHeight = 20.sp
                    )
                )
            }
        }
    }
}

data class ChatMessage(val text: String, val isUser: Boolean)

// Keyword matching Islamic QA engine
fun getIslamicQA(query: String): String {
    val q = query.lowercase()
    return when {
        q.contains("salah") || q.contains("prayer") || q.contains("pray") -> {
            "Salah (prayer) is the second pillar of Islam. Allah states: 'Establish prayer and give zakah and bow with those who bow' (Quran 2:43). The Prophet (ﷺ) said: 'The key to Paradise is prayer, and the key to prayer is wudu.'"
        }
        q.contains("ramadan") || q.contains("fasting") || q.contains("fast") -> {
            "Fasting (Saum) is ordained to cultivate piety. Quran states: 'Decreed upon you is fasting as it was decreed upon those before you that you may become righteous' (Quran 2:183). The Prophet (ﷺ) said: 'Whoever fasts Ramadan out of faith will be forgiven his previous sins.'"
        }
        q.contains("zakat") || q.contains("charity") || q.contains("sadaqah") -> {
            "Zakat is an obligatory alms-giving representing 2.5% of net wealth. Quran states: 'Believe in Allah and His Messenger and spend out of that in which He has made you successors' (Quran 57:7). Prophet Muhammad (ﷺ) said: 'Charity does not decrease wealth.'"
        }
        q.contains("wudu") || q.contains("purify") || q.contains("clean") -> {
            "Ablution (Wudu) is required before Salah. Quran states: 'O you who have believed, when you rise to perform prayer, wash your faces...' (Quran 5:6). The Prophet (ﷺ) said: 'Cleanliness is half of faith (Iman).'"
        }
        q.contains("hadith") || q.contains("sunnah") -> {
            "Sunnah represents the actions and approvals of Prophet Muhammad (ﷺ). The Prophet said in his final sermon: 'I leave behind two things, if you hold fast to them you will never go astray: the Quran and my Sunnah.'"
        }
        q.contains("dua") || q.contains("supplicate") -> {
            "Dua is a personal call to Allah. Quran states: 'Call upon Me; I will respond to you' (Quran 40:60). The Prophet (ﷺ) said: 'Dua is the very essence of worship.'"
        }
        q.contains("hello") || q.contains("hi") || q.contains("salam") -> {
            "Walaikum Assalam! How can I assist you with your Islamic knowledge or practices today?"
        }
        else -> {
            "Verily, in the remembrance of Allah do hearts find rest (Quran 13:28). For specific rulings, please consult authentic collections such as Sahih Bukhari and Sahih Muslim or local scholars. May Allah grant you beneficial knowledge."
        }
    }
}
*/
