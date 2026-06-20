package com.example.model

import androidx.compose.runtime.Immutable

@Immutable
data class User(
    val uid: String = "",
    val profilePhoto: String = "",
    val name: String = "",
    val username: String = "",
    val email: String = "",
    val about: String = "Hello! I am using AmitCHAT.",
    val onlineStatus: Boolean = false,
    val lastSeen: Long = 0L,
    val visibleProfilePhoto: Boolean = true,
    val visibleLastSeen: Boolean = true,
    val visibleAbout: Boolean = true
)

@Immutable
data class Chat(
    val id: String = "",
    val user1Id: String = "",
    val user2Id: String = "",
    val lastMessageText: String = "",
    val lastMessageTimestamp: Long = 0L,
    val unreadCountUser1: Int = 0,
    val unreadCountUser2: Int = 0,
    val typingUser1: Boolean = false,
    val typingUser2: Boolean = false,
    val pinnedByUser1: Boolean = false,
    val pinnedByUser2: Boolean = false
)

enum class MessageType {
    TEXT, IMAGE, VIDEO, AUDIO, DOCUMENT
}

enum class MessageStatus {
    SENT, DELIVERED, READ
}

@Immutable
data class Message(
    val id: String = "",
    val chatId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val text: String = "",
    val type: MessageType = MessageType.TEXT,
    val mediaUrl: String = "",
    val mediaDuration: Long = 0L, // For voice notes/audio/video (ms)
    val mediaFileName: String = "", // For documents
    val replyToId: String = "",
    val replyToText: String = "",
    val replyToSenderName: String = "",
    val forwarded: Boolean = false,
    val edited: Boolean = false,
    val deletedForMe: List<String> = emptyList(), // Store user UIDs
    val deletedForEveryone: Boolean = false,
    val pinned: Boolean = false,
    val starredBy: List<String> = emptyList(), // Store user UIDs
    val reactions: Map<String, String> = emptyMap(), // UID to Emoji mapping
    val timestamp: Long = System.currentTimeMillis(),
    val status: MessageStatus = MessageStatus.SENT
)

@Immutable
data class Group(
    val id: String = "",
    val name: String = "",
    val photoUrl: String = "",
    val description: String = "",
    val creatorId: String = "",
    val adminIds: List<String> = emptyList(),
    val memberIds: List<String> = emptyList(),
    val lastMessageText: String = "",
    val lastMessageTimestamp: Long = 0L
)

@Immutable
data class GroupMessage(
    val id: String = "",
    val groupId: String = "",
    val senderId: String = "",
    val text: String = "",
    val type: MessageType = MessageType.TEXT,
    val mediaUrl: String = "",
    val mediaDuration: Long = 0L,
    val mediaFileName: String = "",
    val replyToId: String = "",
    val replyToText: String = "",
    val replyToSenderName: String = "",
    val forwarded: Boolean = false,
    val edited: Boolean = false,
    val deletedForMe: List<String> = emptyList(),
    val deletedForEveryone: Boolean = false,
    val pinned: Boolean = false,
    val starredBy: List<String> = emptyList(),
    val reactions: Map<String, String> = emptyMap(),
    val timestamp: Long = System.currentTimeMillis()
)

enum class StatusType {
    IMAGE, VIDEO
}

@Immutable
data class Status(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userPhoto: String = "",
    val mediaUrl: String = "",
    val type: StatusType = StatusType.IMAGE,
    val timestamp: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + 86400000L, // 24 Hours
    val views: List<String> = emptyList(), // User UIDs
    val reactions: Map<String, String> = emptyMap() // User UID to emoji
)

enum class CallType {
    VOICE, VIDEO
}

enum class CallStatus {
    DIALING, RINGING, CONNECTED, DISCONNECTED, REJECTED, COMPLETED, MISSED, BUSY, FAILED
}

@Immutable
data class CallHistory(
    val id: String = "",
    val callerId: String = "",
    val receiverId: String = "",
    val callerName: String = "",
    val receiverName: String = "",
    val callerPhoto: String = "",
    val receiverPhoto: String = "",
    val type: CallType = CallType.VOICE,
    val status: CallStatus = CallStatus.MISSED,
    val timestamp: Long = System.currentTimeMillis(),
    val duration: Long = 0L
)

@Immutable
data class CallSignaling(
    val id: String = "",
    val callerId: String = "",
    val receiverId: String = "",
    val type: CallType = CallType.VOICE,
    val status: CallStatus = CallStatus.DIALING,
    val offerSdp: String = "",
    val answerSdp: String = "",
    val iceCandidates: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis(),
    val enableCamera: Boolean = true,
    val enableMic: Boolean = true
)

@Immutable
data class PrivacySettings(
    val hideLastSeen: Boolean = false,
    val hideProfilePhoto: Boolean = false,
    val hideAbout: Boolean = false
)

@Immutable
data class BlockedUserRelation(
    val blockerId: String = "",
    val blockedId: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Immutable
data class AppNotification(
    val id: String = "",
    val recipientId: String = "",
    val title: String = "",
    val body: String = "",
    val type: String = "CHAT", // CHAT, CALL, GROUP, STATUS
    val timestamp: Long = System.currentTimeMillis()
)
