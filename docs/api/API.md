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

## Messaging API

#### Sending Text

```fun sendText(peer: OutPeer, text: String)```

#### Finding user (or bot) by phone number, email or nickname.

```fun findUser(query: String): BotMessages.User?```

#### Getting cached User object by uid.
NOTE: Cache doesn't saved on disk and lost on actor restart.

```fun getUser(uid: Int): BotMessages.User```

#### Getting cached Group object by gid. 
NOTE: Cache doesn't saved on disk and lost on actor restart.

```fun getGroup(gid: Int): BotMessages.Group```

### Creating New Group
```fun createGroup(groupTitle: String): BotMessages.ResponseCreateGroup?```

#### Inviting People to Group
```fun inviteUserToGroup(group: BotMessages.GroupOutPeer, user: BotMessages.UserOutPeer): Boolean```



## WebHooks API
Useful for receiving messages in bot from external services

#### Create New Hook
Returns null if unable to create hook or it is already exists.

```fun createHook(hookName: String): String?```



## Super Bot API
This API is only for bots that have admin privilegies.

#### Create new bot

```fun createBot(userName: String, name: String): BotMessages.BotCreated?```

#### Change User's name

```fun changeUserName(uid: Int, name: String): Boolean```

#### Change User's avatar

```fun changeUserAvatar(uid: Int, fileId: Long, accessHash: Long): Boolean```

#### Change User's about

```fun changeUserAbout(uid: Int, name: String): Boolean```
