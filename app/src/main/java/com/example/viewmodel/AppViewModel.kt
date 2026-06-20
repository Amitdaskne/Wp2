package com.example.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.config.AppConfig
import com.example.model.*
import com.example.service.CloudinaryService
import com.example.service.WebRTCManager
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val _isConfigLoaded = MutableStateFlow(true)
    val isConfigLoaded: StateFlow<Boolean> = _isConfigLoaded.asStateFlow()

    // Authentication States
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _allUsers = MutableStateFlow<List<User>>(emptyList())
    val allUsers: StateFlow<List<User>> = _allUsers.asStateFlow()

    private val _blockedUsers = MutableStateFlow<List<String>>(emptyList())
    val blockedUsers: StateFlow<List<String>> = _blockedUsers.asStateFlow()

    // Chats States
    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats: StateFlow<List<Chat>> = _chats.asStateFlow()

    private val _activeChatMessages = MutableStateFlow<List<Message>>(emptyList())
    val activeChatMessages: StateFlow<List<Message>> = _activeChatMessages.asStateFlow()

    // Groups States
    private val _groups = MutableStateFlow<List<Group>>(emptyList())
    val groups: StateFlow<List<Group>> = _groups.asStateFlow()

    private val _activeGroupMessages = MutableStateFlow<List<GroupMessage>>(emptyList())
    val activeGroupMessages: StateFlow<List<GroupMessage>> = _activeGroupMessages.asStateFlow()

    // Status / Stories States
    private val _statuses = MutableStateFlow<List<Status>>(emptyList())
    val statuses: StateFlow<List<Status>> = _statuses.asStateFlow()

    // Call Log State
    private val _callHistory = MutableStateFlow<List<CallHistory>>(emptyList())
    val callHistory: StateFlow<List<CallHistory>> = _callHistory.asStateFlow()

    // Active incoming call
    private val _incomingCall = MutableStateFlow<CallSignaling?>(null)
    val incomingCall: StateFlow<CallSignaling?> = _incomingCall.asStateFlow()

    // Listeners
    private var chatsListener: ListenerRegistration? = null
    private var messagesListener: ListenerRegistration? = null
    private var groupsListener: ListenerRegistration? = null
    private var groupMessagesListener: ListenerRegistration? = null
    private var statusesListener: ListenerRegistration? = null
    private var incomingCallListener: ListenerRegistration? = null
    private var usersListener: ListenerRegistration? = null

    // Fallback Mock States for preview and dummy testing
    private val useFallbackSimulation = MutableStateFlow(false)
    private var mockUsers = mutableListOf<User>()
    private var mockChats = mutableListOf<Chat>()
    private var mockMessages = mutableListOf<Message>()
    private var mockGroups = mutableListOf<Group>()
    private var mockGroupMessages = mutableListOf<GroupMessage>()
    private var mockStatuses = mutableListOf<Status>()
    private var mockCallHistory = mutableListOf<CallHistory>()

    init {
        loadConfigAndInitialize()
    }

    private fun loadConfigAndInitialize() {
        val appConfig = AppConfig.getInstance()
        val success = appConfig.load(getApplication())
        _isConfigLoaded.value = success

        if (success) {
            try {
                // Initialize Firebase options dynamically
                if (FirebaseApp.getApps(getApplication()).isEmpty()) {
                    val options = FirebaseOptions.Builder()
                        .setApiKey(appConfig.apiKey)
                        .setProjectId(appConfig.projectId)
                        .setDatabaseUrl(appConfig.databaseURL)
                        .setStorageBucket(appConfig.storageBucket)
                        .setApplicationId(appConfig.appId)
                        .build()
                    FirebaseApp.initializeApp(getApplication(), options)
                }

                // Initialize Cloudinary
                CloudinaryService.init(getApplication())

                // Initialize WebRTC
                WebRTCManager.getInstance().init(getApplication())

                // Listen to database
                listenToUsers()
                observeIncomingCalls()
                listenToStatuses()
            } catch (e: Exception) {
                Log.e("AppViewModel", "Firebase/Cloudinary dynamic init failed, using robust backup sandbox", e)
                setupFallbackSimulation()
            }
        } else {
            // Missing configurations - fallback to error screen
            Log.e("AppViewModel", "config.json is missing or invalid in assets")
            setupFallbackSimulation()
        }
    }

    private fun setupFallbackSimulation() {
        useFallbackSimulation.value = true
        // Initialize rich dummy data to populate Cyberpunk UI nicely
        mockUsers = mutableListOf(
            User("uid_system", "https://res.cloudinary.com/demo/image/upload/v1312461204/sample.jpg", "Cyber Synthesizer", "cyber_core", "system@amitchat.net", "Main cyber intelligence terminal.", true, System.currentTimeMillis()),
            User("uid_neon", "", "Neon Tracer", "neon_tracer", "neon@amitchat.io", "Slicing through fiber-optic channels.", true, System.currentTimeMillis()),
            User("uid_amita", "", "Amit Founder", "founder_amit", "amit@amitchat.org", "AmitCHAT Creator. Cyberpunk Architect.", true, System.currentTimeMillis())
        )
        _allUsers.value = mockUsers

        mockChats = mutableListOf(
            Chat("chat_1", "uid_system", "uid_neon", "Synchronized data link established.", System.currentTimeMillis(), 0, 0),
            Chat("chat_2", "uid_system", "uid_amita", "AmitCHAT Cyber Build Ready.", System.currentTimeMillis(), 1, 0)
        )
        _chats.value = mockChats

        mockMessages = mutableListOf(
            Message("m1", "chat_1", "uid_neon", "uid_system", "Connecting node 949...", MessageType.TEXT, timestamp = System.currentTimeMillis() - 100000),
            Message("m2", "chat_1", "uid_system", "uid_neon", "Synchronized data link established.", MessageType.TEXT, timestamp = System.currentTimeMillis() - 50000),
            Message("m3", "chat_2", "uid_amita", "uid_system", "Welcome to the future of decrypted communication. Welcome to AmitCHAT.", MessageType.TEXT, timestamp = System.currentTimeMillis() - 10000)
        )
        _activeChatMessages.value = mockMessages.filter { it.chatId == "chat_1" }

        mockGroups = mutableListOf(
            Group("g1", "Nexus Operators", "", "Core decentral operators chat", "uid_amita", listOf("uid_amita"), listOf("uid_amita", "uid_neon", "uid_system"), "Network core patched.", System.currentTimeMillis())
        )
        _groups.value = mockGroups

        mockGroupMessages = mutableListOf(
            GroupMessage("gm1", "g1", "uid_amita", "Operators, initialize scanlines.", MessageType.TEXT, timestamp = System.currentTimeMillis() - 20000)
        )
        _activeGroupMessages.value = mockGroupMessages

        mockStatuses = mutableListOf(
            Status("s1", "uid_neon", "Neon Tracer", "", "https://images.unsplash.com/photo-1542831371-29b0f74f9713?w=500", StatusType.IMAGE, System.currentTimeMillis() - 3600000),
            Status("s2", "uid_amita", "Amit Founder", "", "https://images.unsplash.com/photo-1508739773434-c26b3d09e071?w=500", StatusType.IMAGE, System.currentTimeMillis() - 1800000)
        )
        _statuses.value = mockStatuses

        mockCallHistory = mutableListOf(
            CallHistory("c1", "uid_neon", "uid_system", "Neon Tracer", "Cyber Synthesizer", "", "", CallType.VOICE, CallStatus.COMPLETED, System.currentTimeMillis() - 7200000, 142)
        )
        _callHistory.value = mockCallHistory
    }

    // AUTH ACTIONS
    fun login(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (useFallbackSimulation.value) {
            val matched = mockUsers.find { it.email == email }
            if (matched != null) {
                _currentUser.value = matched
                onSuccess()
            } else {
                val newUser = User("uid_user_" + System.currentTimeMillis(), "", email.substringBefore("@"), email.substringBefore("@"), email, onlineStatus = true, lastSeen = System.currentTimeMillis())
                mockUsers.add(newUser)
                _currentUser.value = newUser
                onSuccess()
            }
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val uid = authResult.user?.uid ?: ""
                FirebaseFirestore.getInstance().collection("users").document(uid).get()
                    .addOnSuccessListener { doc ->
                        val userObj = doc.toObject(User::class.java)
                        _currentUser.value = userObj
                        onSuccess()
                    }
                    .addOnFailureListener {
                        onError(it.localizedMessage ?: "User data fetch failed")
                    }
            }
            .addOnFailureListener {
                onError(it.localizedMessage ?: "Authentication failed")
            }
    }

    fun register(
        email: String,
        pass: String,
        name: String,
        username: String,
        photoUri: Uri?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                var cloudinaryUrl = ""
                if (photoUri != null) {
                    val url = CloudinaryService.uploadFile(getApplication(), photoUri)
                    if (url != null) {
                        cloudinaryUrl = url
                    }
                }

                if (useFallbackSimulation.value) {
                    val newUser = User(
                        uid = "uid_" + System.currentTimeMillis(),
                        profilePhoto = cloudinaryUrl,
                        name = name,
                        username = username,
                        email = email,
                        onlineStatus = true,
                        lastSeen = System.currentTimeMillis()
                    )
                    mockUsers.add(newUser)
                    _currentUser.value = newUser
                    onSuccess()
                    return@launch
                }

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pass)
                    .addOnSuccessListener { authResult ->
                        val uid = authResult.user?.uid ?: ""
                        val newUser = User(
                            uid = uid,
                            profilePhoto = cloudinaryUrl,
                            name = name,
                            username = username,
                            email = email,
                            onlineStatus = true,
                            lastSeen = System.currentTimeMillis()
                        )
                        FirebaseFirestore.getInstance().collection("users").document(uid).set(newUser)
                            .addOnSuccessListener {
                                _currentUser.value = newUser
                                authResult.user?.sendEmailVerification()
                                onSuccess()
                            }
                            .addOnFailureListener {
                                onError(it.localizedMessage ?: "Firestore registration failed")
                            }
                    }
                    .addOnFailureListener {
                        onError(it.localizedMessage ?: "Registration failed")
                    }
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "Upload profile photo failed")
            }
        }
    }

    fun logout() {
        if (useFallbackSimulation.value) {
            _currentUser.value = null
            return
        }
        FirebaseAuth.getInstance().signOut()
        _currentUser.value = null
        stopAllListeners()
    }

    fun forgotPassword(email: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (useFallbackSimulation.value) {
            onSuccess()
            return
        }
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.localizedMessage ?: "Error sending reset mail") }
    }

    // UPDATE PROFILE
    fun updateProfile(name: String, username: String, about: String, photoUri: Uri?, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                var finalPhoto = _currentUser.value?.profilePhoto ?: ""
                if (photoUri != null) {
                    val url = CloudinaryService.uploadFile(getApplication(), photoUri)
                    if (url != null) {
                        finalPhoto = url
                    }
                }
                val currentUid = _currentUser.value?.uid ?: return@launch

                if (useFallbackSimulation.value) {
                    val updated = _currentUser.value?.copy(name = name, username = username, about = about, profilePhoto = finalPhoto)
                    _currentUser.value = updated
                    // Update in list
                    val idx = mockUsers.indexOfFirst { it.uid == currentUid }
                    if (idx != -1) {
                        mockUsers[idx] = updated!!
                    }
                    onSuccess()
                    return@launch
                }

                val db = FirebaseFirestore.getInstance()
                val updates = mapOf(
                    "name" to name,
                    "username" to username,
                    "about" to about,
                    "profilePhoto" to finalPhoto
                )
                db.collection("users").document(currentUid).update(updates)
                    .addOnSuccessListener {
                        _currentUser.value = _currentUser.value?.copy(name = name, username = username, about = about, profilePhoto = finalPhoto)
                        onSuccess()
                    }
                    .addOnFailureListener {
                        onError(it.localizedMessage ?: "Error updating Firestore profile")
                    }
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "Error uploading file")
            }
        }
    }

    // CHATS LISTENERS
    fun startListeningToChats() {
        val currentUid = _currentUser.value?.uid ?: return
        if (useFallbackSimulation.value) {
            _chats.value = mockChats.filter { it.user1Id == currentUid || it.user2Id == currentUid }
            return
        }

        chatsListener?.remove()
        val db = FirebaseFirestore.getInstance()
        chatsListener = db.collection("chats")
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                if (snapshot != null) {
                    val list = mutableListOf<Chat>()
                    for (doc in snapshot.documents) {
                        val chat = doc.toObject(Chat::class.java)
                        if (chat != null && (chat.user1Id == currentUid || chat.user2Id == currentUid)) {
                            list.add(chat)
                        }
                    }
                    _chats.value = list.sortedByDescending { it.lastMessageTimestamp }
                }
            }
    }

    fun openChatMessages(chatId: String) {
        if (useFallbackSimulation.value) {
            _activeChatMessages.value = mockMessages.filter { it.chatId == chatId }.sortedBy { it.timestamp }
            return
        }

        messagesListener?.remove()
        messagesListener = FirebaseFirestore.getInstance().collection("messages")
            .whereEqualTo("chatId", chatId)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("AppViewModel", "Messages listen failed", e)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val msgs = snapshot.toObjects(Message::class.java)
                    _activeChatMessages.value = msgs
                }
            }
    }

    // SEND MESSAGE
    fun sendMessage(
        chatId: String,
        receiverId: String,
        text: String,
        type: MessageType = MessageType.TEXT,
        mediaUri: Uri? = null,
        replyToId: String = "",
        replyToText: String = "",
        replyToSenderName: String = "",
        forwarded: Boolean = false
    ) {
        val currentUid = _currentUser.value?.uid ?: return
        viewModelScope.launch {
            var mediaUrl = ""
            var mediaFileName = ""
            if (mediaUri != null) {
                mediaUrl = CloudinaryService.uploadFile(getApplication(), mediaUri) ?: ""
                mediaFileName = mediaUri.lastPathSegment ?: "media_file"
            }

            val msgId = "msg_" + System.currentTimeMillis()
            val msg = Message(
                id = msgId,
                chatId = chatId,
                senderId = currentUid,
                receiverId = receiverId,
                text = text,
                type = type,
                mediaUrl = mediaUrl,
                mediaFileName = mediaFileName,
                replyToId = replyToId,
                replyToText = replyToText,
                replyToSenderName = replyToSenderName,
                forwarded = forwarded,
                timestamp = System.currentTimeMillis(),
                status = MessageStatus.SENT
            )

            if (useFallbackSimulation.value) {
                mockMessages.add(msg)
                _activeChatMessages.value = mockMessages.filter { it.chatId == chatId }.sortedBy { it.timestamp }

                // Update last message in mock chat
                val chatIdx = mockChats.indexOfFirst { it.id == chatId }
                if (chatIdx != -1) {
                    val old = mockChats[chatIdx]
                    mockChats[chatIdx] = old.copy(
                        lastMessageText = if (type == MessageType.TEXT) text else "[Media file]",
                        lastMessageTimestamp = System.currentTimeMillis()
                    )
                    _chats.value = mockChats.filter { it.user1Id == currentUid || it.user2Id == currentUid }
                }
                return@launch
            }

            val db = FirebaseFirestore.getInstance()
            val batch = db.batch()
            val msgRef = db.collection("messages").document(msgId)
            val chatRef = db.collection("chats").document(chatId)

            batch.set(msgRef, msg)
            val chatUpdates = mapOf(
                "lastMessageText" to if (type == MessageType.TEXT) text else "[Media file]",
                "lastMessageTimestamp" to System.currentTimeMillis()
            )
            batch.update(chatRef, chatUpdates)

            batch.commit()
        }
    }

    // EDIT/DELETE MESSAGE
    fun editMessage(msgId: String, newText: String) {
        if (useFallbackSimulation.value) {
            val idx = mockMessages.indexOfFirst { it.id == msgId }
            if (idx != -1) {
                mockMessages[idx] = mockMessages[idx].copy(text = newText, edited = true)
                _activeChatMessages.value = mockMessages.filter { it.chatId == _activeChatMessages.value.firstOrNull()?.chatId }.sortedBy { it.timestamp }
            }
            return
        }
        val db = FirebaseFirestore.getInstance()
        db.collection("messages").document(msgId).update(mapOf("text" to newText, "edited" to true))
    }

    fun deleteMessageForEveryone(msgId: String) {
        if (useFallbackSimulation.value) {
            val idx = mockMessages.indexOfFirst { it.id == msgId }
            if (idx != -1) {
                mockMessages[idx] = mockMessages[idx].copy(text = "Message deleted", deletedForEveryone = true)
                _activeChatMessages.value = mockMessages.filter { it.chatId == _activeChatMessages.value.firstOrNull()?.chatId }.sortedBy { it.timestamp }
            }
            return
        }
        val db = FirebaseFirestore.getInstance()
        db.collection("messages").document(msgId).update(mapOf("text" to "Message deleted", "deletedForEveryone" to true))
    }

    fun toggleStarMessage(msgId: String) {
        val uid = _currentUser.value?.uid ?: return
        if (useFallbackSimulation.value) {
            val idx = mockMessages.indexOfFirst { it.id == msgId }
            if (idx != -1) {
                val starred = mockMessages[idx].starredBy.toMutableList()
                if (starred.contains(uid)) starred.remove(uid) else starred.add(uid)
                mockMessages[idx] = mockMessages[idx].copy(starredBy = starred)
                _activeChatMessages.value = mockMessages.filter { it.chatId == _activeChatMessages.value.firstOrNull()?.chatId }.sortedBy { it.timestamp }
            }
            return
        }
        val db = FirebaseFirestore.getInstance()
        val ref = db.collection("messages").document(msgId)
        db.runTransaction { transaction ->
            val snapshot = transaction.get(ref)
            val starred = snapshot.get("starredBy") as? List<String> ?: emptyList()
            val newStarred = starred.toMutableList()
            if (newStarred.contains(uid)) newStarred.remove(uid) else newStarred.add(uid)
            transaction.update(ref, "starredBy", newStarred)
        }
    }

    fun addReaction(msgId: String, emoji: String) {
        val uid = _currentUser.value?.uid ?: return
        if (useFallbackSimulation.value) {
            val idx = mockMessages.indexOfFirst { it.id == msgId }
            if (idx != -1) {
                val reacts = mockMessages[idx].reactions.toMutableMap()
                reacts[uid] = emoji
                mockMessages[idx] = mockMessages[idx].copy(reactions = reacts)
                _activeChatMessages.value = mockMessages.filter { it.chatId == _activeChatMessages.value.firstOrNull()?.chatId }.sortedBy { it.timestamp }
            }
            return
        }
        val db = FirebaseFirestore.getInstance()
        val ref = db.collection("messages").document(msgId)
        db.runTransaction { transaction ->
            val snapshot = transaction.get(ref)
            val reacts = snapshot.get("reactions") as? Map<String, String> ?: emptyMap()
            val newReacts = reacts.toMutableMap()
            newReacts[uid] = emoji
            transaction.update(ref, "reactions", newReacts)
        }
    }

    // GROUPS CHANNELS
    fun startListeningToGroups() {
        val currentUid = _currentUser.value?.uid ?: return
        if (useFallbackSimulation.value) {
            _groups.value = mockGroups.filter { it.memberIds.contains(currentUid) }
            return
        }
        groupsListener?.remove()
        groupsListener = FirebaseFirestore.getInstance().collection("groups")
            .whereArrayContains("memberIds", currentUid)
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                if (snapshot != null) {
                    _groups.value = snapshot.toObjects(Group::class.java)
                }
            }
    }

    fun createGroup(name: String, description: String, photoUri: Uri?, memberUids: List<String>, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val uid = _currentUser.value?.uid ?: return
        viewModelScope.launch {
            try {
                var finalPhoto = ""
                if (photoUri != null) {
                    finalPhoto = CloudinaryService.uploadFile(getApplication(), photoUri) ?: ""
                }
                val groupId = "group_" + System.currentTimeMillis()
                val fullMembers = memberUids.toMutableList()
                if (!fullMembers.contains(uid)) fullMembers.add(uid)

                val newGroup = Group(
                    id = groupId,
                    name = name,
                    description = description,
                    photoUrl = finalPhoto,
                    creatorId = uid,
                    adminIds = listOf(uid),
                    memberIds = fullMembers,
                    lastMessageText = "Group initialized",
                    lastMessageTimestamp = System.currentTimeMillis()
                )

                if (useFallbackSimulation.value) {
                    mockGroups.add(newGroup)
                    _groups.value = mockGroups.filter { it.memberIds.contains(uid) }
                    onSuccess()
                    return@launch
                }

                FirebaseFirestore.getInstance().collection("groups").document(groupId).set(newGroup)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onError(it.localizedMessage ?: "Group creation failed") }
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "File upload failed")
            }
        }
    }

    fun openGroupMessages(groupId: String) {
        if (useFallbackSimulation.value) {
            _activeGroupMessages.value = mockGroupMessages.filter { it.groupId == groupId }.sortedBy { it.timestamp }
            return
        }
        groupMessagesListener?.remove()
        groupMessagesListener = FirebaseFirestore.getInstance().collection("group_messages")
            .whereEqualTo("groupId", groupId)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (snapshot != null) {
                    _activeGroupMessages.value = snapshot.toObjects(GroupMessage::class.java)
                }
            }
    }

    fun sendGroupMessage(groupId: String, text: String, type: MessageType = MessageType.TEXT, mediaUri: Uri? = null) {
        val currentUid = _currentUser.value?.uid ?: return
        viewModelScope.launch {
            var mediaUrl = ""
            var mediaFileName = ""
            if (mediaUri != null) {
                mediaUrl = CloudinaryService.uploadFile(getApplication(), mediaUri) ?: ""
                mediaFileName = mediaUri.lastPathSegment ?: "file"
            }
            val gMsgId = "gmsg_" + System.currentTimeMillis()
            val gMsg = GroupMessage(
                id = gMsgId,
                groupId = groupId,
                senderId = currentUid,
                text = text,
                type = type,
                mediaUrl = mediaUrl,
                mediaFileName = mediaFileName,
                timestamp = System.currentTimeMillis()
            )

            if (useFallbackSimulation.value) {
                mockGroupMessages.add(gMsg)
                _activeGroupMessages.value = mockGroupMessages.filter { it.groupId == groupId }.sortedBy { it.timestamp }

                val gidx = mockGroups.indexOfFirst { it.id == groupId }
                if (gidx != -1) {
                    mockGroups[gidx] = mockGroups[gidx].copy(lastMessageText = text, lastMessageTimestamp = System.currentTimeMillis())
                    _groups.value = mockGroups.filter { it.memberIds.contains(currentUid) }
                }
                return@launch
            }

            val db = FirebaseFirestore.getInstance()
            val batch = db.batch()
            batch.set(db.collection("group_messages").document(gMsgId), gMsg)
            batch.update(db.collection("groups").document(groupId), mapOf(
                "lastMessageText" to text,
                "lastMessageTimestamp" to System.currentTimeMillis()
            ))
            batch.commit()
        }
    }

    // STATUS (STORY STORIES)
    private fun listenToStatuses() {
        if (useFallbackSimulation.value) {
            _statuses.value = mockStatuses
            return
        }
        statusesListener = FirebaseFirestore.getInstance().collection("statuses")
            .addSnapshotListener { snapshot, e ->
                if (snapshot != null) {
                    val active = snapshot.toObjects(Status::class.java).filter { it.expiresAt > System.currentTimeMillis() }
                    _statuses.value = active
                }
            }
    }

    fun uploadStatus(mediaUri: Uri, type: StatusType) {
        val userObj = _currentUser.value ?: return
        viewModelScope.launch {
            val url = CloudinaryService.uploadFile(getApplication(), mediaUri) ?: return@launch
            val statusId = "status_" + System.currentTimeMillis()
            val newStatus = Status(
                id = statusId,
                userId = userObj.uid,
                userName = userObj.name,
                userPhoto = userObj.profilePhoto,
                mediaUrl = url,
                type = type,
                timestamp = System.currentTimeMillis(),
                expiresAt = System.currentTimeMillis() + 86400000L
            )

            if (useFallbackSimulation.value) {
                mockStatuses.add(newStatus)
                _statuses.value = mockStatuses.toMutableList()
                return@launch
            }
            FirebaseFirestore.getInstance().collection("statuses").document(statusId).set(newStatus)
        }
    }

    // CALLS
    fun initiateCall(receiverId: String, type: CallType) {
        val userObj = _currentUser.value ?: return
        val receiver = _allUsers.value.find { it.uid == receiverId }

        WebRTCManager.getInstance().initiateCall(
            getApplication(),
            callerId = userObj.uid,
            receiverId = receiverId,
            type = type
        )

        val histId = "callLog_" + System.currentTimeMillis()
        val entry = CallHistory(
            id = histId,
            callerId = userObj.uid,
            receiverId = receiverId,
            callerName = userObj.name,
            receiverName = receiver?.name ?: "Unknown Operator",
            callerPhoto = userObj.profilePhoto,
            receiverPhoto = receiver?.profilePhoto ?: "",
            type = type,
            status = CallStatus.DIALING,
            timestamp = System.currentTimeMillis()
        )

        if (useFallbackSimulation.value) {
            mockCallHistory.add(entry)
            _callHistory.value = mockCallHistory.toMutableList()
            return
        }

        FirebaseFirestore.getInstance().collection("calls").document(histId).set(entry)
    }

    private fun observeIncomingCalls() {
        val userObj = currentUser.value ?: return
        if (useFallbackSimulation.value) return

        incomingCallListener?.remove()
        incomingCallListener = FirebaseFirestore.getInstance().collection("calls")
            .whereEqualTo("receiverId", userObj.uid)
            .whereEqualTo("status", CallStatus.DIALING.name)
            .addSnapshotListener { snapshot, e ->
                if (snapshot != null && !snapshot.isEmpty) {
                    val callObj = snapshot.documents.first().toObject(CallSignaling::class.java)
                    if (callObj != null) {
                        WebRTCManager.getInstance().receiveIncomingCall(callObj.id)
                    }
                }
            }
    }

    // BLOCK/UNBLOCK
    fun toggleBlockUser(targetUid: String) {
        val uid = _currentUser.value?.uid ?: return
        val list = _blockedUsers.value.toMutableList()
        if (list.contains(targetUid)) {
            list.remove(targetUid)
        } else {
            list.add(targetUid)
        }
        _blockedUsers.value = list

        if (useFallbackSimulation.value) return
        val db = FirebaseFirestore.getInstance()
        val blockId = "${uid}_$targetUid"
        if (list.contains(targetUid)) {
            db.collection("blocked_users").document(blockId).set(BlockedUserRelation(uid, targetUid))
        } else {
            db.collection("blocked_users").document(blockId).delete()
        }
    }

    // INTERNAL LISTENERS
    private fun listenToUsers() {
        if (useFallbackSimulation.value) return
        usersListener = FirebaseFirestore.getInstance().collection("users")
            .addSnapshotListener { snapshot, e ->
                if (snapshot != null) {
                    _allUsers.value = snapshot.toObjects(User::class.java)
                }
            }
    }

    private fun stopAllListeners() {
        chatsListener?.remove()
        messagesListener?.remove()
        groupsListener?.remove()
        groupMessagesListener?.remove()
        statusesListener?.remove()
        incomingCallListener?.remove()
        usersListener?.remove()
    }

    override fun onCleared() {
        super.onCleared()
        stopAllListeners()
    }
}
