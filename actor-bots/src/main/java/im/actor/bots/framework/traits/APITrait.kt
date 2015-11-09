package im.actor.bots.framework.traits

import im.actor.botkit.RemoteBot
import im.actor.bots.BotMessages
import im.actor.bots.framework.OutPeer
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import java.util.*
import java.util.concurrent.TimeUnit

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

open class APITraitImpl(val bot: RemoteBot) : APITrait {

    override fun inviteUserToGroup(group: BotMessages.GroupOutPeer, user: BotMessages.UserOutPeer): Boolean {
        try {
            Await.result(bot.requestInviteUser(group, user), Duration.create(50, TimeUnit.SECONDS))
            return true
        } catch(e: Exception) {
            return false
        }
    }

    override fun sendText(peer: OutPeer, text: String) {
        bot.requestSendMessage(peer.toKit(), bot.nextRandomId(), BotMessages.TextMessage(text))
    }

    override fun findUser(query: String): BotMessages.User? {
        try {
            val res = Await.result(bot.requestFindUser(query), Duration.create(50, TimeUnit.SECONDS))
            if (res.users.isEmpty()) {
                return null
            }
            return res.users[0]
        } catch(e: Exception) {
            return null
        }
    }

    override fun getUser(uid: Int): BotMessages.User {
        return bot.getUser(uid)
    }

    override fun getGroup(gid: Int): BotMessages.Group {
        return bot.getGroup(gid)
    }

    override fun createGroup(groupTitle: String): BotMessages.ResponseCreateGroup? {
        try {
            return Await.result(bot.requestCreateGroup(groupTitle), Duration.create(50, TimeUnit.SECONDS))
        } catch(e: Exception) {
            return null
        }
    }

    override fun createHook(hookName: String): String? {
        try {
            return Await.result(bot.requestRegisterHook(hookName), Duration.create(50, TimeUnit.SECONDS)).value()
        } catch(e: Exception) {
            return null
        }
    }

    override fun createBot(userName: String, name: String): BotMessages.BotCreated? {
        try {
            return Await.result(bot.requestCreateBot(userName, name), Duration.create(50, TimeUnit.SECONDS))
        } catch(e: Exception) {
            return null
        }
    }

    override fun changeUserName(uid: Int, name: String): Boolean {
        try {
            Await.result(bot.requestChangeUserName(uid, name),
                    Duration.create(50, TimeUnit.SECONDS))
            return true
        } catch(e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    override fun changeUserAvatar(uid: Int, fileId: Long, accessHash: Long): Boolean {
        try {
            Await.result(bot.requestChangeUserAvatar(uid,
                    BotMessages.FileLocation(fileId, accessHash)),
                    Duration.create(50, TimeUnit.SECONDS))
            return true
        } catch(e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    override fun changeUserAbout(uid: Int, name: String): Boolean {
        try {
            Await.result(bot.requestChangeUserAbout(uid, Optional.of(name)),
                    Duration.create(50, TimeUnit.SECONDS))
            return true
        } catch(e: Exception) {
            e.printStackTrace()
        }
        return false
    }
}

class APITraitScopedImpl(val peer: OutPeer, bot: RemoteBot) : APITraitImpl(bot), APITraitScoped {
    override fun sendText(text: String) {
        sendText(peer, text)
    }
}