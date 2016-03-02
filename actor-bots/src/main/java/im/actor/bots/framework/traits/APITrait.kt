package im.actor.bots.framework.traits

import im.actor.botkit.RemoteBot
import im.actor.bots.BotMessages
import im.actor.bots.framework.OutPeer
import org.json.JSONObject
import scala.Option
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import java.util.*
import java.util.concurrent.TimeUnit

interface APITrait {

    //
    // Messaging
    //

    fun sendText(peer: OutPeer, text: String)

    fun sendJson(peer: OutPeer, dataType: String, json: JSONObject)

    fun sendModernText(peer: OutPeer, message: ModernMessage)

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
    fun userIsAdmin(uid: Int): Boolean

    fun createStickerPack(ownerUserId: Int): Int
    fun addSticker(ownerUserId: Int, packId: Int, emoji: Optional<String>, image128: ByteArray, w128: Int, h128: Int, image256: ByteArray, w256: Int, h256: Int, image512: ByteArray, w512: Int, h512: Int): Boolean
    fun showStickerPacks(ownerUserId: Int): List<String>
    fun showStickers(ownerUserId: Int, packId: Int): List<String>
    fun deleteSticker(ownerUserId: Int, packId: Int, stickerId: Int): Boolean
    fun makeStickerPackDefault(userId: Int, packId: Int): Boolean
    fun unmakeStickerPackDefault(userId: Int, packId: Int): Boolean

    fun getFile(fileId: Long, accessHash: Long): ByteArray
}


class ModernMessage(val compatText: String) {
    var text: String? = null
    var paragraphStyle: ParagraphStyle? = null
    var attaches = ArrayList<ModernAttach>()
}

class ParagraphStyle {
    var showParagraph: Boolean = false
    var paragraphColor: Color? = null
    var backgroundColor: Color? = null
}

class ModernAttach {
    var text: String? = null
    var title: String? = null
    var titleUrl: String? = null
    var paragraphStyle: ParagraphStyle? = null
    var fields = ArrayList<ModernField>()
}

class ModernField(val name: String, val value: String, val isShort: Boolean) {
}

sealed class Color {
    class RGB(val number: Int) : Color()

    object Red : Color()

    object Green : Color()

    object Yellow : Color()
}

interface APITraitScoped : APITrait {
    fun sendText(text: String)
    fun sendJson(dataType: String, json: JSONObject)
    fun sendModernText(message: ModernMessage)
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
        bot.requestSendMessage(peer.toKit(), bot.nextRandomId(), BotMessages.TextMessage(text, Option.empty()))
    }

    override fun sendJson(peer: OutPeer, dataType: String, data: JSONObject) {
        val jsonMsg = JSONObject()
        jsonMsg.put("dataType", dataType);
        jsonMsg.put("data", data)
        bot.requestSendMessage(peer.toKit(), bot.nextRandomId(), BotMessages.JsonMessage(jsonMsg.toString()))
    }

    override fun sendModernText(peer: OutPeer, message: ModernMessage) {
        val paragraphStyle = convertParagraphStyle(message.paragraphStyle)
        var attachesList = ArrayList<BotMessages.TextModernAttach>()
        for (m in message.attaches) {
            var attrs = ArrayList<BotMessages.TextModernField>()
            for (f in m.fields) {
                attrs.add(BotMessages.TextModernField(f.name,
                        f.value, Option.apply(f.isShort)))
            }
            attachesList.add(BotMessages.TextModernAttach(m.title,
                    m.titleUrl, null, m.text, convertParagraphStyle(m.paragraphStyle), attrs))
        }
        bot.requestSendMessage(peer.toKit(), bot.nextRandomId(), BotMessages.TextMessage(message.compatText,
                Option.apply(BotMessages.TextModernMessage(message.text,
                        null,
                        null,
                        paragraphStyle, attachesList))))
    }

    private fun convertParagraphStyle(style: ParagraphStyle?): BotMessages.ParagraphStyle? {
        if (style == null) {
            return null
        }

        return BotMessages.ParagraphStyle(Option.apply(style.showParagraph),
                convertColor(style.paragraphColor),
                convertColor(style.backgroundColor))
    }

    private fun convertColor(color: Color?): Option<BotMessages.Color> {
        if (color == null) {
            return Option.empty()
        }
        when (color) {
            Color.Green -> {
                return Option.apply(BotMessages.PredefinedColor(BotMessages.`Green$`()))
            }
            Color.Red -> {
                return Option.apply(BotMessages.PredefinedColor(BotMessages.`Red$`()))
            }
            Color.Yellow -> {
                return Option.apply(BotMessages.PredefinedColor(BotMessages.`Yellow$`()))
            }
            is Color.RGB -> {
                return Option.apply(BotMessages.RgbColor(color.number))
            }
        }
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

    override fun userIsAdmin(uid: Int): Boolean {
        try {
            return Await.result(bot.requestIsAdmin(uid), Duration.create(50, TimeUnit.SECONDS)).getIsAdmin()
        } catch(e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    //TODO: remove magic numbers
    override fun createStickerPack(ownerUserId: Int): Int {
        try {
            return Await.result(bot.requestCreateStickerPack(ownerUserId), Duration.create(50, TimeUnit.SECONDS)).value().toInt()
        } catch(e: Exception) {
            e.printStackTrace()
        }
        return -1
    }

    override fun addSticker(ownerUserId: Int, packId: Int, emoji: Optional<String>, image128: ByteArray, w128: Int, h128: Int, image256: ByteArray, w256: Int, h256: Int, image512: ByteArray, w512: Int, h512: Int): Boolean {
        try {
            Await.result(bot.requestAddSticker(ownerUserId, packId, emoji, image128, w128, h128, image256, w256, h256, image512, w512, h512),
                    Duration.create(50, TimeUnit.SECONDS))
            return true
        } catch(e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    override fun showStickerPacks(ownerUserId: Int): List<String> {
        try {
            return Await.result(bot.requestShowStickerPacks(ownerUserId), Duration.create(50, TimeUnit.SECONDS)).getIds()
        } catch(e: Exception) {
            e.printStackTrace()
        }
        return ArrayList()
    }

    override fun showStickers(ownerUserId: Int, packId: Int): List<String> {
        try {
            return Await.result(bot.requestShowStickers(ownerUserId, packId), Duration.create(50, TimeUnit.SECONDS)).getIds()
        } catch(e: Exception) {
            e.printStackTrace()
        }
        return ArrayList()
    }

    override fun deleteSticker(ownerUserId: Int, packId: Int, stickerId: Int): Boolean {
        try {
            Await.result(bot.requestDeleteSticker(ownerUserId, packId, stickerId), Duration.create(50, TimeUnit.SECONDS))
            return true
        } catch(e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    override fun makeStickerPackDefault(userId: Int, packId: Int): Boolean {
        try {
            Await.result(bot.requestMakeStickerPackDefault(userId, packId), Duration.create(50, TimeUnit.SECONDS))
            return true
        } catch(e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    override fun unmakeStickerPackDefault(userId: Int, packId: Int): Boolean {
        try {
            Await.result(bot.requestUnmakeStickerPackDefault(userId, packId), Duration.create(50, TimeUnit.SECONDS))
            return true
        } catch(e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    override fun getFile(fileId: Long, accessHash: Long): ByteArray {
        try {
            return Await.result(bot.requestDownloadFile(BotMessages.FileLocation(fileId, accessHash)), Duration.create(50, TimeUnit.SECONDS)).fileBytes()
        } catch(e: Exception) {
            e.printStackTrace()
        }
        return ByteArray(0)
    }

}

class APITraitScopedImpl(val peer: OutPeer, bot: RemoteBot) : APITraitImpl(bot), APITraitScoped {
    override fun sendText(text: String) {
        sendText(peer, text)
    }

    override fun sendJson(dataType: String, json: JSONObject) {
        sendJson(peer, dataType, json)
    }

    override fun sendModernText(message: ModernMessage) {
        sendModernText(peer, message)
    }
}