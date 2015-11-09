# API Module

This module provide access to Actor Bot API.

```kotlin
interface APITrait {

    //
    // Messaging
    //

    fun sendText(peer: OutPeer, text: String)

    fun findUser(query: String): BotMessages.User?

    fun getUser(uid: Int): BotMessages.User

    fun getGroup(gid: Int): BotMessages.Group

    fun createGroup(groupTitle: String): BotMessages.ResponseCreateGroup?

    fun inviteUserToGroup(group: BotMessages.GroupOutPeer, user: BotMessages.UserOutPeer): Boolean

    //
    // Managing Hooks
    //

    fun createHook(hookName: String): String?

    //
    // Super Bot methods
    //

    fun createBot(userName: String, name: String): BotMessages.BotCreated?
    fun changeUserName(uid: Int, name: String): Boolean
    fun changeUserAvatar(uid: Int, fileId: Long, accessHash: Long): Boolean
    fun changeUserAbout(uid: Int, name: String): Boolean
}

interface APITraitScoped : APITrait {
    fun sendText(text: String)
}
```