package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.ReelAIApplication
import com.example.data.firebase.FirebaseManager
import android.util.Log
import com.example.data.api.Content
import com.example.data.api.GenerateContentRequest
import com.example.data.api.Part
import com.example.data.api.RetrofitClient
import com.example.data.model.Draft
import com.example.data.repository.DraftRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONObject

sealed interface AppScreen {
    object Splash : AppScreen
    object Auth : AppScreen
    sealed interface MainTab : AppScreen {
        object Home : MainTab
        object Create : MainTab
        object Drafts : MainTab
    }
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: DraftRepository = (application as ReelAIApplication).repository
    private val sharedPrefs = application.getSharedPreferences("reelai_prefs", Context.MODE_PRIVATE)

    // API connectivity status
    private val _isRealApiKey = MutableStateFlow(false)
    val isRealApiKey: StateFlow<Boolean> = _isRealApiKey.asStateFlow()

    // Auth state persistence & configuration
    private val _isAuthLoading = MutableStateFlow(false)
    val isAuthLoading: StateFlow<Boolean> = _isAuthLoading.asStateFlow()

    private val _isFirebaseActive = MutableStateFlow(FirebaseManager.isInitialized)
    val isFirebaseActive: StateFlow<Boolean> = _isFirebaseActive.asStateFlow()

    private val _userEmail = MutableStateFlow(sharedPrefs.getString("user_email", "") ?: "")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _currentScreen = MutableStateFlow<AppScreen>(AppScreen.Splash)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Subscription & Generation Limit States
    private val _isPremium = MutableStateFlow(sharedPrefs.getBoolean("is_premium", false))
    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    private val _generatedCount = MutableStateFlow(sharedPrefs.getInt("generated_count", 0))
    val generatedCount: StateFlow<Int> = _generatedCount.asStateFlow()

    private val _showSubscriptionDialog = MutableStateFlow(false)
    val showSubscriptionDialog: StateFlow<Boolean> = _showSubscriptionDialog.asStateFlow()

    // Creator States
    private val _selectedNiche = MutableStateFlow("Tech & AI")
    val selectedNiche: StateFlow<String> = _selectedNiche.asStateFlow()

    private val _selectedTone = MutableStateFlow("Energetic")
    val selectedTone: StateFlow<String> = _selectedTone.asStateFlow()

    private val _selectedDuration = MutableStateFlow("30s")
    val selectedDuration: StateFlow<String> = _selectedDuration.asStateFlow()

    private val _topicInput = MutableStateFlow("")
    val topicInput: StateFlow<String> = _topicInput.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _generatedDraft = MutableStateFlow<Draft?>(null)
    val generatedDraft: StateFlow<Draft?> = _generatedDraft.asStateFlow()

    // Standalone AI Toolkits states
    private val _hookInputTopic = MutableStateFlow("")
    val hookInputTopic: StateFlow<String> = _hookInputTopic.asStateFlow()
    
    private val _hookStyle = MutableStateFlow("Curiosity Gap")
    val hookStyle: StateFlow<String> = _hookStyle.asStateFlow()

    private val _generatedHooks = MutableStateFlow<List<String>>(emptyList())
    val generatedHooks: StateFlow<List<String>> = _generatedHooks.asStateFlow()

    private val _isGeneratingHook = MutableStateFlow(false)
    val isGeneratingHook: StateFlow<Boolean> = _isGeneratingHook.asStateFlow()


    private val _captionInputSummary = MutableStateFlow("")
    val captionInputSummary: StateFlow<String> = _captionInputSummary.asStateFlow()

    private val _captionFormatStyle = MutableStateFlow("Storytelling")
    val captionFormatStyle: StateFlow<String> = _captionFormatStyle.asStateFlow()

    private val _generatedCaption = MutableStateFlow("")
    val generatedCaption: StateFlow<String> = _generatedCaption.asStateFlow()

    private val _isGeneratingCaption = MutableStateFlow(false)
    val isGeneratingCaption: StateFlow<Boolean> = _isGeneratingCaption.asStateFlow()


    private val _hashtagInputTopic = MutableStateFlow("")
    val hashtagInputTopic: StateFlow<String> = _hashtagInputTopic.asStateFlow()

    private val _hashtagPlatform = MutableStateFlow("Instagram")
    val hashtagPlatform: StateFlow<String> = _hashtagPlatform.asStateFlow()

    private val _generatedHashtags = MutableStateFlow("")
    val generatedHashtags: StateFlow<String> = _generatedHashtags.asStateFlow()

    private val _isGeneratingHashtags = MutableStateFlow(false)
    val isGeneratingHashtags: StateFlow<Boolean> = _isGeneratingHashtags.asStateFlow()


    private val _thumbnailInputConcept = MutableStateFlow("")
    val thumbnailInputConcept: StateFlow<String> = _thumbnailInputConcept.asStateFlow()

    private val _thumbnailTactic = MutableStateFlow("Solutions Click Bait")
    val thumbnailTactic: StateFlow<String> = _thumbnailTactic.asStateFlow()

    private val _generatedThumbnails = MutableStateFlow<List<String>>(emptyList())
    val generatedThumbnails: StateFlow<List<String>> = _generatedThumbnails.asStateFlow()

    private val _isGeneratingThumbnails = MutableStateFlow(false)
    val isGeneratingThumbnails: StateFlow<Boolean> = _isGeneratingThumbnails.asStateFlow()

    // Persistent Drafts from Room
    val savedDrafts: StateFlow<List<Draft>> = repository.allDrafts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Single-shot events (e.g. show toast)
    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

    init {
        // Run simulated Splash delay of 2 seconds
        viewModelScope.launch {
            kotlinx.coroutines.delay(2000)
            
            _isFirebaseActive.value = FirebaseManager.isInitialized
            val firebaseUser = FirebaseManager.auth?.currentUser
            
            if (FirebaseManager.isInitialized && firebaseUser != null) {
                _userEmail.value = firebaseUser.email ?: ""
                _currentScreen.value = AppScreen.MainTab.Home
                syncDraftsFromFirestore()
            } else if (FirebaseManager.isInitialized) {
                // Connected with Firebase but no user session exists
                _userEmail.value = ""
                _currentScreen.value = AppScreen.Auth
            } else {
                // Local DB Sandbox Session support
                if (_userEmail.value.isNotEmpty()) {
                    _currentScreen.value = AppScreen.MainTab.Home
                } else {
                    _currentScreen.value = AppScreen.Auth
                }
            }
        }

        // Evaluate actual API Key status at launch
        val apiKey = BuildConfig.GEMINI_API_KEY
        _isRealApiKey.value = apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY"
    }

    // Auth actions (Supports both live Firebase Auth or Local Mock fallbacks)
    fun login(email: String, passwordEntered: String, isSignUpMode: Boolean) {
        val emailClean = email.trim()
        val passwordClean = passwordEntered.trim()

        if (emailClean.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailClean).matches()) {
            emitToast("Please enter a valid email address.")
            return
        }
        if (passwordClean.length < 6) {
            emitToast("Password must be at least 6 characters.")
            return
        }

        _isFirebaseActive.value = FirebaseManager.isInitialized
        val authInstance = FirebaseManager.auth

        if (FirebaseManager.isInitialized && authInstance != null) {
            _isAuthLoading.value = true
            if (isSignUpMode) {
                // Register standard account in Firestore auth
                authInstance.createUserWithEmailAndPassword(emailClean, passwordClean)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = task.result?.user
                            val resolvedEmail = user?.email ?: emailClean
                            sharedPrefs.edit().putString("user_email", resolvedEmail).apply()
                            _userEmail.value = resolvedEmail
                            _currentScreen.value = AppScreen.MainTab.Home
                            _isAuthLoading.value = false
                            emitToast("Firebase Account created successfully!")
                        } else {
                            _isAuthLoading.value = false
                            emitToast("Signup Failed: ${task.exception?.localizedMessage}")
                        }
                    }
            } else {
                // SignIn standard account in Firestore auth
                authInstance.signInWithEmailAndPassword(emailClean, passwordClean)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = task.result?.user
                            val resolvedEmail = user?.email ?: emailClean
                            sharedPrefs.edit().putString("user_email", resolvedEmail).apply()
                            _userEmail.value = resolvedEmail
                            _currentScreen.value = AppScreen.MainTab.Home
                            _isAuthLoading.value = false
                            emitToast("Logged in via Firebase!")
                            syncDraftsFromFirestore()
                        } else {
                            _isAuthLoading.value = false
                            emitToast("Login Failed: ${task.exception?.localizedMessage}")
                        }
                    }
            }
        } else {
            // Local Sandbox Fallback Mode
            sharedPrefs.edit().putString("user_email", emailClean).apply()
            _userEmail.value = emailClean
            _currentScreen.value = AppScreen.MainTab.Home
            emitToast("Logged in via Local Sandbox Mode (Firebase offline/unconfigured)!")
        }
    }

    fun logout() {
        if (FirebaseManager.isInitialized) {
            FirebaseManager.auth?.signOut()
        }
        sharedPrefs.edit().remove("user_email").apply()
        _userEmail.value = ""
        _currentScreen.value = AppScreen.Auth
        emitToast("Logged out successfully.")
    }

    // Navigation actions
    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    // Creator Form updates
    fun updateNiche(niche: String) {
        if ((niche == "Business" || niche == "Comedy") && !_isPremium.value) {
            emitToast("'$niche' is a PRO premium niche!")
            _showSubscriptionDialog.value = true
            return
        }
        _selectedNiche.value = niche
    }

    fun updateTone(tone: String) {
        if ((tone == "Dramatic" || tone == "Humorous") && !_isPremium.value) {
            emitToast("'$tone' is a PRO script tone!")
            _showSubscriptionDialog.value = true
            return
        }
        _selectedTone.value = tone
    }

    fun updateDuration(duration: String) {
        if (duration == "60s" && !_isPremium.value) {
            emitToast("60-second scripts require ReelAI Premium!")
            _showSubscriptionDialog.value = true
            return
        }
        _selectedDuration.value = duration
    }

    fun updateTopic(topic: String) {
        _topicInput.value = topic
    }

    fun clearCreatorForm() {
        _topicInput.value = ""
        _generatedDraft.value = null
    }

    // Standalone AI Toolkits API actions & setters
    fun updateHookInputTopic(value: String) { _hookInputTopic.value = value }
    fun updateHookStyle(value: String) { _hookStyle.value = value }

    fun updateCaptionInputSummary(value: String) { _captionInputSummary.value = value }
    fun updateCaptionFormatStyle(value: String) { _captionFormatStyle.value = value }

    fun updateHashtagInputTopic(value: String) { _hashtagInputTopic.value = value }
    fun updateHashtagPlatform(value: String) { _hashtagPlatform.value = value }

    fun updateThumbnailInputConcept(value: String) { _thumbnailInputConcept.value = value }
    fun updateThumbnailTactic(value: String) { _thumbnailTactic.value = value }

    // Transplant Actions
    fun applyHookToDraft(hook: String) {
        val current = _generatedDraft.value
        if (current != null) {
            _generatedDraft.value = current.copy(hook = hook)
            emitToast("Hook transplanted to draft preview!")
        } else {
            emitToast("No active script draft. First generate a script above!")
        }
    }

    fun applyCaptionToDraft(caption: String) {
        val current = _generatedDraft.value
        if (current != null) {
            _generatedDraft.value = current.copy(caption = caption)
            emitToast("Caption transplanted to draft preview!")
        } else {
            emitToast("No active script draft. First generate a script above!")
        }
    }

    fun applyHashtagsToDraft(hashtags: String) {
        val current = _generatedDraft.value
        if (current != null) {
            _generatedDraft.value = current.copy(hashtags = hashtags)
            emitToast("Hashtags transplanted to draft preview!")
        } else {
            emitToast("No active script draft. First generate a script above!")
        }
    }

    fun applyThumbnailTextToDraft(text: String) {
        val current = _generatedDraft.value
        if (current != null) {
            _generatedDraft.value = current.copy(thumbnailText = text)
            emitToast("Thumbnail text transplanted to draft preview!")
        } else {
            emitToast("No active script draft. First generate a script above!")
        }
    }

    // AI Viral Hook Generator Action
    fun generateAIHook() {
        val topic = _hookInputTopic.value.trim()
        if (topic.isEmpty()) {
            emitToast("Please enter a concept or topic for the hook!")
            return
        }
        if (!_isPremium.value && _generatedCount.value >= 3) {
            emitToast("Free credit limit reached (3/3). Upgrade to Premium to generate!")
            _showSubscriptionDialog.value = true
            return
        }
        val style = _hookStyle.value
        _isGeneratingHook.value = true

        viewModelScope.launch {
            try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                if (apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY") {
                    val prompt = """
                        You are an expert social media copywriter. Generate 3 highly viral, scroll-stopping video hooks of style '$style' for the topic: '$topic'.
                        Return them strictly as a valid JSON array of 3 strings. e.g. ["Hook 1", "Hook 2", "Hook 3"].
                        Do not wrap in markdown or backticks. Return raw JSON.
                    """.trimIndent()
                    val request = GenerateContentRequest(contents = listOf(Content(parts = listOf(Part(text = prompt)))))
                    val response = RetrofitClient.service.generateContent(apiKey = apiKey, request = request)
                    val generatedText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    if (!generatedText.isNullOrBlank()) {
                        var cleanJson = generatedText.trim()
                        if (cleanJson.startsWith("```")) {
                            cleanJson = cleanJson.removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
                        }
                        val jsonArray = org.json.JSONArray(cleanJson)
                        val hooksList = mutableListOf<String>()
                        for (i in 0 until jsonArray.length()) {
                            hooksList.add(jsonArray.getString(i))
                        }
                        _generatedHooks.value = hooksList
                        emitToast("3 viral hooks generated successfully!")
                        incrementGenerationCountIfNeeded()
                    } else {
                        fallbackHookMock(topic, style)
                    }
                } else {
                    fallbackHookMock(topic, style)
                }
            } catch (e: Exception) {
                fallbackHookMock(topic, style)
            } finally {
                _isGeneratingHook.value = false
            }
        }
    }

    private fun fallbackHookMock(topic: String, style: String) {
        val list = when (style) {
            "Curiosity Gap" -> listOf(
                "Most people think $topic is hard, but the 1% use this simple 3-step cheat code...",
                "I was struggling with $topic until I found this stupidly simple trick. Here is how it works.",
                "Here is the one thing about $topic they don't want you to know..."
            )
            "Fear of Missing Out" -> listOf(
                "If you're not doing this with $topic in 2026, you're literally falling behind your competition.",
                "Stop wasting hours on $topic. Do this instead before this loophole gets patched.",
                "The clock is ticking on this $topic strategy. Here is what you need to change immediately."
            )
            "Contrarian Take" -> listOf(
                "Unpopular opinion: Everything you've been told about $topic is a complete waste of time.",
                "Why 99% of creators fail at $topic (and the simple tweak that changes it all).",
                "Stop centering your focus on $topic. It is actually hurting your growth, and here's why."
            )
            else -> listOf(
                "This 15-second checklist will completely change how you approach $topic forever.",
                "My absolute biggest secret to double your results with $topic in 24 hours.",
                "How I mastered $topic with zero experience—and how you can replicate today."
            )
        }
        _generatedHooks.value = list
        emitToast("Viral hooks created (Sandbox Mode)")
        incrementGenerationCountIfNeeded()
    }

    // Instagram Caption Generator Action
    fun generateAICaption() {
        val query = _captionInputSummary.value.trim()
        if (query.isEmpty()) {
            emitToast("Please enter key points or details for the Instagram caption!")
            return
        }
        if (!_isPremium.value && _generatedCount.value >= 3) {
            emitToast("Free credit limit reached (3/3). Upgrade to Premium to generate!")
            _showSubscriptionDialog.value = true
            return
        }
        val format = _captionFormatStyle.value
        _isGeneratingCaption.value = true

        viewModelScope.launch {
            try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                if (apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY") {
                    val prompt = """
                        You are an expert Instagram growth manager. Write a high-converting, styled post caption based on these points: '$query'.
                        Use style: '$format'. Include line breaks, dynamic emojis, brief value bullets, and a clear call-to-action to leave a comment or save.
                        Return just the raw caption text.
                    """.trimIndent()
                    val request = GenerateContentRequest(contents = listOf(Content(parts = listOf(Part(text = prompt)))))
                    val response = RetrofitClient.service.generateContent(apiKey = apiKey, request = request)
                    val generatedText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    if (!generatedText.isNullOrBlank()) {
                        _generatedCaption.value = generatedText.trim()
                        emitToast("Viral post caption created successfully!")
                        incrementGenerationCountIfNeeded()
                    } else {
                        fallbackCaptionMock(query, format)
                    }
                } else {
                    fallbackCaptionMock(query, format)
                }
            } catch (e: Exception) {
                fallbackCaptionMock(query, format)
            } finally {
                _isGeneratingCaption.value = false
            }
        }
    }

    private fun fallbackCaptionMock(query: String, format: String) {
        val intro = "Let's be real for a minute: $query. 🚀\n\nMost creators completely overlook the simplest hacks when starting out."
        val caption = when (format) {
            "Short & Punchy" -> """
                $intro
                
                Here is the absolute breakdown:
                🎯 Focus on core value delivery.
                ⚡ Strip away secondary noise.
                📈 Automate feedback systems.
                
                Drop a 👇 below if you are ready to implement this next setup!
            """.trimIndent()
            "Storytelling" -> """
                I used to spend hours struggling with these exact points: '$query'.
                
                It felt like pushing water uphill. The breakthrough only came when I stopped trying to do everything manually and focused on a structured gameplan.
                
                Here are the 3 major items that changed the game:
                1️⃣ Automated workflow priority
                2️⃣ Real-time metric review
                3️⃣ Quality feedback loops
                
                📌 Save this post so you don't lose the roadmap!
            """.trimIndent()
            "Step-by-Step tutorial" -> """
                The step-by-step tutorial to master: $query ⬇️
                
                Follow this exact sequence to get consistent results:
                🔹 STEP 1: Define your target metrics before filming.
                🔹 STEP 2: Use attention-grabbing pattern-interrupts at second zero.
                🔹 STEP 3: Pair your visual cues with dynamic sound design.
                
                💬 Share this with a creator who is trying to grow right now!
            """.trimIndent()
            else -> """
                $intro
                
                Why does this matter so much? Because 90% of your progress depends on mastering this exact structure.
                
                How are you currently handling these points on your account? Let me know in the comments! 👇
            """.trimIndent()
        }
        _generatedCaption.value = caption
        emitToast("Instagram caption created (Sandbox Mode)")
        incrementGenerationCountIfNeeded()
    }

    // Auto Trending Hashtags Action
    fun generateAIHashtags() {
        val topic = _hashtagInputTopic.value.trim()
        if (topic.isEmpty()) {
            emitToast("Please enter a topic or focus for the hashtags!")
            return
        }
        if (!_isPremium.value && _generatedCount.value >= 3) {
            emitToast("Free credit limit reached (3/3). Upgrade to Premium to generate!")
            _showSubscriptionDialog.value = true
            return
        }
        val platform = _hashtagPlatform.value
        _isGeneratingHashtags.value = true

        viewModelScope.launch {
            try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                if (apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY") {
                    val prompt = """
                        Generate 12 highly viral trending hashtags for platform '$platform' centered around the topic '$topic'. 
                        Separate them by spaces and prepend with '#'. Ensure high, medium, and low competition mix. Return only the hashtags text, nothing else.
                    """.trimIndent()
                    val request = GenerateContentRequest(contents = listOf(Content(parts = listOf(Part(text = prompt)))))
                    val response = RetrofitClient.service.generateContent(apiKey = apiKey, request = request)
                    val generatedText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    if (!generatedText.isNullOrBlank()) {
                        _generatedHashtags.value = generatedText.trim()
                        emitToast("Trending hashtags auto-selected!")
                        incrementGenerationCountIfNeeded()
                    } else {
                        fallbackHashtagsMock(topic, platform)
                    }
                } else {
                    fallbackHashtagsMock(topic, platform)
                }
            } catch (e: Exception) {
                fallbackHashtagsMock(topic, platform)
            } finally {
                _isGeneratingHashtags.value = false
            }
        }
    }

    private fun fallbackHashtagsMock(topic: String, platform: String) {
        val cleanTopic = topic.replace(" ", "").lowercase()
        val suffix = when (platform) {
            "Instagram" -> "#reelsinstagram #instagramgaming #creatoreconomy #viralstrategies"
            "TikTok" -> "#fyp #foryoupage #trendingreels #tiktokgrowth #trends"
            else -> "#shorts #youtubeshorts #shortsfeeds #diyreels #shortstips"
        }
        _generatedHashtags.value = "#$cleanTopic #$cleanTopic" + "tips #contentstrategy #socialmediahacks #viralreel #creatorsuccess $suffix"
        emitToast("Trending hashtags auto-selected (Sandbox Mode)")
        incrementGenerationCountIfNeeded()
    }

    // Thumbnail Text Ideas Action
    fun generateAIThumbnails() {
        val concept = _thumbnailInputConcept.value.trim()
        if (concept.isEmpty()) {
            emitToast("Please enter a thumbnail concept or focus!")
            return
        }
        if (!_isPremium.value && _generatedCount.value >= 3) {
            emitToast("Free credit limit reached (3/3). Upgrade to Premium to generate!")
            _showSubscriptionDialog.value = true
            return
        }
        val tactic = _thumbnailTactic.value
        _isGeneratingThumbnails.value = true

        viewModelScope.launch {
            try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                if (apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY") {
                    val prompt = """
                        Generate 3 ultra high-impact, big-bold clickable thumbnail text overlay ideas (max 4 words, UPPERCASE) for video topic '$concept'.
                        Use click-intent tactic '$tactic' (like curiosity gaps, extreme disbelief, or secret setup).
                        Return strictly as a valid JSON array of 3 strings. e.g. ["DO THIS NEXT", "THEY LIED", "10X SECRET"].
                        Do not wrap in markdown or backticks. Return raw JSON.
                    """.trimIndent()
                    val request = GenerateContentRequest(contents = listOf(Content(parts = listOf(Part(text = prompt)))))
                    val response = RetrofitClient.service.generateContent(apiKey = apiKey, request = request)
                    val generatedText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    if (!generatedText.isNullOrBlank()) {
                        var cleanJson = generatedText.trim()
                        if (cleanJson.startsWith("```")) {
                            cleanJson = cleanJson.removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
                        }
                        val jsonArray = org.json.JSONArray(cleanJson)
                        val thList = mutableListOf<String>()
                        for (i in 0 until jsonArray.length()) {
                            thList.add(jsonArray.getString(i))
                        }
                        _generatedThumbnails.value = thList
                        emitToast("Thumbnail overlay text designs compiled!")
                        incrementGenerationCountIfNeeded()
                    } else {
                        fallbackThumbnailsMock(concept, tactic)
                    }
                } else {
                    fallbackThumbnailsMock(concept, tactic)
                }
            } catch (e: Exception) {
                fallbackThumbnailsMock(concept, tactic)
            } finally {
                _isGeneratingThumbnails.value = false
            }
        }
    }

    private fun fallbackThumbnailsMock(concept: String, tactic: String) {
        val cleanConcept = if (concept.length > 15) concept.take(12).uppercase() else concept.uppercase()
        val list = when (tactic) {
            "Extreme Disbelief" -> listOf(
                "THEY LIED TO YOU",
                "STOP DOING THIS",
                "WORST MISTAKE EVER"
            )
            "Curiosity Gap" -> listOf(
                "THE 1% SECRET",
                "WATCH UNTIL END",
                "DON'T MISS THIS"
            )
            else -> listOf(
                "DO THIS $cleanConcept",
                "10X YOUR RESULTS",
                "TRY COLD OUT"
            )
        }
        _generatedThumbnails.value = list
        emitToast("Thumbnail text overlays compiled (Sandbox Mode)")
        incrementGenerationCountIfNeeded()
    }

    // Subscription & Billing Operations
    fun toggleSubscriptionDialog(show: Boolean) {
        _showSubscriptionDialog.value = show
    }

    fun purchasePremium() {
        sharedPrefs.edit().putBoolean("is_premium", true).apply()
        _isPremium.value = true
        _showSubscriptionDialog.value = false
        emitToast("PRO unlocked! Welcome to ReelAI Elite. 🚀")
    }

    fun cancelPremium() {
        sharedPrefs.edit().putBoolean("is_premium", false).apply()
        _isPremium.value = false
        sharedPrefs.edit().putInt("generated_count", 0).apply()
        _generatedCount.value = 0
        emitToast("Account reset to Free tier.")
    }

    private fun incrementGenerationCountIfNeeded() {
        if (!_isPremium.value) {
            val newCount = _generatedCount.value + 1
            sharedPrefs.edit().putInt("generated_count", newCount).apply()
            _generatedCount.value = newCount
            emitToast("Free credits used: $newCount/3 generations")
        }
    }

    // Dynamic Script Generator
    fun generateScript() {
        val topic = _topicInput.value.trim()
        if (topic.isEmpty()) {
            emitToast("Please enter a video concept or topic!")
            return
        }

        if (!_isPremium.value && _generatedCount.value >= 3) {
            emitToast("Free generation limit reached (3/3). Upgrade to Premium!")
            _showSubscriptionDialog.value = true
            return
        }

        val niche = _selectedNiche.value
        val tone = _selectedTone.value
        val duration = _selectedDuration.value

        _isGenerating.value = true
        _generatedDraft.value = null

        viewModelScope.launch {
            try {
                // Read API key from BuildConfig safely.
                // It is injected at runtime from the Secrets panel.
                val apiKey = BuildConfig.GEMINI_API_KEY
                
                if (apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY") {
                    // Assemble a strict instruction instructing a structured JSON response to guarantee bulletproof parsing
                    val prompt = """
                        You are a world-class social media strategist and professional viral Instagram Reels script writer. 
                        Your task is to write a high-converting, attention-grabbing, retention-optimized Instagram Reel/TikTok script based on these inputs:
                        - Niche: $niche
                        - Topic: $topic
                        - Tone: $tone
                        - Target Duration: $duration
                        
                        You MUST return your response as a valid, well-formed JSON object with EXACTLY the following seven fields.
                        Do not wrap the response in markdown code blocks (like ```json), just return raw, play-ready JSON text.
                        
                        JSON keys required:
                        {
                          "title": "A short, viral, punchy title under 6 words",
                          "hook": "An attention-grabbing, pattern-interrupt viral hook for the first 0-5 seconds that stops readers from scrolling. Use punchy conversational text.",
                          "body": "The scene-by-scene script. Format this strictly as 2-4 consecutive visual scenes/descriptions paired with spoken audio, like:\nScene 1: [Visual: Close-up showing neon lighting, face centered] 'Have you ever wondered why some posts get 10k views while others get zero?'\nScene 2: [Visual: Screen recording displaying key statistics] 'The secret is pattern interruption. Let me show you how...'",
                          "cta": "A sharp, high-converting call to action for the last 5 seconds (e.g. 'Read caption for my secret setup', 'Share with a friend who is struggling')",
                          "caption": "A highly engaging Instagram post caption (70-150 words) written with dynamic formatting, line breaks, and emojis that hooks the reader, elaborates on the topic, and guides them step-by-step.",
                          "hashtags": "A set of 5-8 highly relevant viral hashtags separated by spaces (e.g. '#contentcreator #instagramreels #viralmarketing')",
                          "thumbnail_text": "3-5 high-impact, big-bold words suitable for a thumbnail text overlay (designed to capture click intent, e.g., 'DO THIS NEXT')"
                        }
                    """.trimIndent()

                    val request = GenerateContentRequest(
                        contents = listOf(Content(parts = listOf(Part(text = prompt))))
                    )

                    val response = RetrofitClient.service.generateContent(apiKey = apiKey, request = request)
                    val generatedText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    
                    if (!generatedText.isNullOrBlank()) {
                        // Attempt to parse out structured data safely
                        try {
                            var cleanJson = generatedText.trim()
                            if (cleanJson.startsWith("```")) {
                                cleanJson = cleanJson.removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
                            }
                            
                            val jsonObject = JSONObject(cleanJson)
                            val title = jsonObject.optString("title") ?: "Viral $niche: ${topic.take(20)}..."
                            val hook = jsonObject.optString("hook") ?: "Stop scrolling! Here's the deal."
                            val body = jsonObject.optString("body") ?: cleanJson
                            val cta = jsonObject.optString("cta") ?: "Follow for more!"
                            val caption = jsonObject.optString("caption") ?: "Want to learn the secret of $topic? Check out this breakdown of our absolute best strategies to optimize your results today! 🚀\n\nDrop a comment if you are ready to implement this."
                            val hashtags = jsonObject.optString("hashtags") ?: "#${niche.replace(" & ", "").replace(" ", "")} #${topic.replace(" ", "").take(15)} #viral #contenttips"
                            val thumbnailText = jsonObject.optString("thumbnail_text") ?: "STOP DOING THIS"

                            val scriptDraft = Draft(
                                title = title,
                                niche = niche,
                                topic = topic,
                                tone = tone,
                                duration = duration,
                                hook = hook,
                                body = body,
                                callToAction = cta,
                                caption = caption,
                                hashtags = hashtags,
                                thumbnailText = thumbnailText
                            )
                            _generatedDraft.value = scriptDraft
                            emitToast("Viral AI Script created successfully!")
                            incrementGenerationCountIfNeeded()
                        } catch (jsonEx: Exception) {
                            // Fallback to legacy tag parsing if JSON fails
                            val title = parseTag(generatedText, "[TITLE]") ?: "Viral $niche: ${topic.take(20)}..."
                            val hook = parseTag(generatedText, "[HOOK]") ?: parseTag(generatedText, "HOOK:") ?: "Stop scrolling! Here's the deal."
                            val body = parseTag(generatedText, "[BODY]") ?: parseTag(generatedText, "BODY:") ?: generatedText
                            val cta = parseTag(generatedText, "[CTA]") ?: parseTag(generatedText, "CTA:") ?: "Follow for more!"
                            val caption = parseTag(generatedText, "[CAPTION]") ?: "Check out this detailed guide on $topic!"
                            val hashtags = parseTag(generatedText, "[HASHTAGS]") ?: "#${niche.replace(" ", "")} #viral"
                            val thumbnailText = parseTag(generatedText, "[THUMBNAIL]") ?: "DO NOT SKIP"

                            val scriptDraft = Draft(
                                title = title,
                                niche = niche,
                                topic = topic,
                                tone = tone,
                                duration = duration,
                                hook = hook,
                                body = body,
                                callToAction = cta,
                                caption = caption,
                                hashtags = hashtags,
                                thumbnailText = thumbnailText
                            )
                            _generatedDraft.value = scriptDraft
                            emitToast("AI Script created (tag parsed)!")
                            incrementGenerationCountIfNeeded()
                        }
                    } else {
                        // Fallback to high-quality procedural generation if API responds with empty text
                        fallbackToMock(niche, topic, tone, duration)
                    }
                } else {
                    // API key is missing or is the default placeholder, run rich procedural generator
                    fallbackToMock(niche, topic, tone, duration)
                }
            } catch (e: Exception) {
                // If query fails (like timeout or offline/sandbox environment limits), fall back instantly
                fallbackToMock(niche, topic, tone, duration)
            } finally {
                _isGenerating.value = false
            }
        }
    }

    private fun parseTag(text: String, tag: String): String? {
        if (!text.contains(tag)) return null
        val startIndex = text.indexOf(tag) + tag.length
        val remaining = text.substring(startIndex)
        val nextTagIndex = remaining.indexOf("[")
        return if (nextTagIndex != -1) {
            remaining.substring(0, nextTagIndex).trim().trim(':').trim()
        } else {
            remaining.trim().trim(':').trim()
        }
    }

    private fun fallbackToMock(niche: String, topic: String, tone: String, duration: String) {
        val title = "Viral $niche: ${if (topic.length > 25) topic.take(22) + "..." else topic}"
        
        val hook = when (tone.lowercase()) {
            "energetic" -> "Stop scrolling! If you are interested in $topic, this is the exact formula you need to hear right now."
            "educational" -> "The actual science behind $topic is surprisingly simple. Here's how it works."
            "dramatic" -> "They lied to you about $topic. And the reality? It is much deeper than they're letting on."
            else -> "Let's be real for a minute about $topic. It's actually way easier than everyone makes it sound."
        }

        val point1 = when (niche) {
            "Tech & AI" -> "Step 1: Leverage automated workflow pipelines. Standard manual systems are slowing your progression by 10x."
            "Finance" -> "Step 1: Understand compound micro-investing. Most people wait until they have thousands, which is a major error."
            "Fitness" -> "Step 1: Track progressive dynamic load. Doing the same sets week after week leads to hypertrophy stagnation."
            "Lifestyle" -> "Step 1: Adopt atomic habit tracking. Visual markers trigger consistent repetition in daily schedules."
            "Comedy" -> "Step 1: Break expectations immediately. Create relatable situational setup that turns standard logic upside down."
            else -> "Step 1: Establish low-friction distribution options. Focus purely on conversion funnels before expanding footprint."
        }

        val point2 = when (niche) {
            "Tech & AI" -> "Step 2: Connect real-time feedback loops. This allows model pipelines to self-correct during generation."
            "Finance" -> "Step 2: Automate regular distributions. Take the emotion out of timing market shifts entirely."
            "Fitness" -> "Step 2: Prioritize rest and fuel. Muscle protein synthesis occurs during deep REM cycles, not only at the gym."
            "Lifestyle" -> "Step 2: Curate high-quality mental signals. Subtractor systems out of your routine is as powerful as addition."
            "Comedy" -> "Step 2: The standard comedic callback. Repeat your strongest setup line near the climax with an unexpected pivot."
            else -> "Step 2: Maintain a strong value feedback loop. Listen intensely to early user complaints to refine value."
        }

        val cta = when (tone.lowercase()) {
            "energetic" -> "Hit follow for your daily dose of creative mastery. Don't fall behind!"
            "educational" -> "Save this script for later, and share your biggest struggle in the comments!"
            "dramatic" -> "Share this with a content creator who needs to wake up. Link in bio."
            else -> "Let's talk in the comments. What is your take on this?"
        }

        val caption = when (niche) {
            "Tech & AI" -> "Double down on your productivity secrets list. 🤖 Here's the absolute truth about $topic: most people are wasting hours on tasks that are 100% automatable.\n\nSave this post so you have the layout ready for your next session, and make sure to drop your biggest content bottleneck below! 👇"
            "Finance" -> "Stop leaving money on the table. 💸 If you've been putting off organizing your setup for $topic, consider this your daily reminder to automate your systems.\n\nComment 'STRATEGY' and I'll send you our step-by-step checklist to get started!"
            "Fitness" -> "No shortcuts, just pure progressive science. 🏋️‍♂️ When optimizing for $topic, consistency in routine outperforms random maximum loads every time.\n\nShare this with a workout partner who needs to refocus their routine!"
            else -> "Ready to take your game to the next level? 🚀 Analyzing the core mechanics of $topic reveals exactly how the top 1% structure their daily operations.\n\nLike and follow for daily actionable strategies!"
        }

        val cleanNiche = niche.replace(" & ", "").replace(" ", "")
        val cleanTopic = topic.replace(" ", "").take(12)
        val hashtags = "#$cleanNiche #$cleanTopic #viralreel #contentstrategy #productivitytips"

        val thumbnailText = when (tone.lowercase()) {
            "energetic" -> "DO THIS NOW!"
            "educational" -> "THE 10X SECRET"
            "dramatic" -> "THEY LIED TO YOU"
            else -> "TRY THIS NEXT"
        }

        val scriptDraft = Draft(
            title = title,
            niche = niche,
            topic = topic,
            tone = tone,
            duration = duration,
            hook = hook,
            body = "Scene 1: [Visual: High energy close-up, warm neon backdrop] '$hook'\n\nScene 2: [Visual: Fast-cut B-roll demonstrating step 1 details] '$point1'\n\nScene 3: [Visual: Screen share highlighting step 2 workflow] '$point2'",
            callToAction = cta,
            caption = caption,
            hashtags = hashtags,
            thumbnailText = thumbnailText
        )
        _generatedDraft.value = scriptDraft
        emitToast("Generated script (Local AI Sandbox)")
        incrementGenerationCountIfNeeded()
    }

    // Room & Cloud Firestore Draft Operations
    fun saveDraft(draft: Draft) {
        viewModelScope.launch {
            try {
                // First save to local SQLite Room database & obtain primary key
                val newId = repository.insertDraft(draft)
                val updatedDraft = draft.copy(id = newId.toInt())

                val user = FirebaseManager.auth?.currentUser
                if (FirebaseManager.isInitialized && user != null) {
                    val firestore = FirebaseManager.firestore
                    if (firestore != null) {
                        val draftData = hashMapOf(
                            "id" to updatedDraft.id,
                            "title" to updatedDraft.title,
                            "niche" to updatedDraft.niche,
                            "topic" to updatedDraft.topic,
                            "tone" to updatedDraft.tone,
                            "duration" to updatedDraft.duration,
                            "hook" to updatedDraft.hook,
                            "body" to updatedDraft.body,
                            "callToAction" to updatedDraft.callToAction,
                            "caption" to updatedDraft.caption,
                            "hashtags" to updatedDraft.hashtags,
                            "thumbnailText" to updatedDraft.thumbnailText,
                            "timestamp" to updatedDraft.timestamp,
                            "userEmail" to user.email
                        )

                        firestore.collection("users")
                            .document(user.uid)
                            .collection("drafts")
                            .document(updatedDraft.id.toString())
                            .set(draftData)
                            .addOnSuccessListener {
                                emitToast("Draft saved to Cloud & local database!")
                            }
                            .addOnFailureListener { e ->
                                emitToast("Saved locally (Cloud sync failed: ${e.localizedMessage})")
                            }
                    } else {
                        emitToast("Draft saved directly to Database!")
                    }
                } else {
                    emitToast("Draft saved directly to Database!")
                }
            } catch (e: Exception) {
                emitToast("Failed to save draft: ${e.localizedMessage}")
            }
        }
    }

    fun deleteDraft(draft: Draft) {
        viewModelScope.launch {
            try {
                // Delete from Room SQLite first
                repository.deleteDraft(draft)

                val user = FirebaseManager.auth?.currentUser
                if (FirebaseManager.isInitialized && user != null) {
                    val firestore = FirebaseManager.firestore
                    if (firestore != null) {
                        firestore.collection("users")
                            .document(user.uid)
                            .collection("drafts")
                            .document(draft.id.toString())
                            .delete()
                            .addOnSuccessListener {
                                emitToast("Draft deleted from Cloud & local.")
                            }
                            .addOnFailureListener { e ->
                                emitToast("Deleted locally (Cloud sync failed: ${e.localizedMessage})")
                            }
                    } else {
                        emitToast("Draft deleted.")
                    }
                } else {
                    emitToast("Draft deleted.")
                }
            } catch (e: Exception) {
                emitToast("Failed to delete: ${e.localizedMessage}")
            }
        }
    }

    fun syncDraftsFromFirestore() {
        val user = FirebaseManager.auth?.currentUser
        if (FirebaseManager.isInitialized && user != null) {
            val firestore = FirebaseManager.firestore
            if (firestore != null) {
                firestore.collection("users")
                    .document(user.uid)
                    .collection("drafts")
                    .get()
                    .addOnSuccessListener { result ->
                        viewModelScope.launch {
                            try {
                                var syncedCount = 0
                                for (document in result) {
                                    val id = document.getLong("id")?.toInt() ?: 0
                                    val title = document.getString("title") ?: ""
                                    val niche = document.getString("niche") ?: ""
                                    val topic = document.getString("topic") ?: ""
                                    val tone = document.getString("tone") ?: ""
                                    val duration = document.getString("duration") ?: ""
                                    val hook = document.getString("hook") ?: ""
                                    val body = document.getString("body") ?: ""
                                    val callToAction = document.getString("callToAction") ?: ""
                                    val caption = document.getString("caption") ?: ""
                                    val hashtags = document.getString("hashtags") ?: ""
                                    val thumbnailText = document.getString("thumbnailText") ?: ""
                                    val timestamp = document.getLong("timestamp") ?: System.currentTimeMillis()

                                    val cloudDraft = Draft(
                                        id = id,
                                        title = title,
                                        niche = niche,
                                        topic = topic,
                                        tone = tone,
                                        duration = duration,
                                        hook = hook,
                                        body = body,
                                        callToAction = callToAction,
                                        caption = caption,
                                        hashtags = hashtags,
                                        thumbnailText = thumbnailText,
                                        timestamp = timestamp
                                    )
                                    repository.insertDraft(cloudDraft)
                                    syncedCount++
                                }
                                if (syncedCount > 0) {
                                    emitToast("Synced $syncedCount scripts from Cloud Firestore!")
                                }
                            } catch (ex: Exception) {
                                Log.e("MainViewModel", "Error storing Firestore draft into Room", ex)
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        emitToast("Could not sync cloud scripts: ${e.localizedMessage}")
                    }
            }
        }
    }

    // Toast emitter helper
    fun emitToast(message: String) {
        viewModelScope.launch {
            _toastMessage.emit(message)
        }
    }
}
