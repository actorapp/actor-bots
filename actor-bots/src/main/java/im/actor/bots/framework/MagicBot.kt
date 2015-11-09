package im.actor.bots.framework

import akka.actor.ActorRef
import akka.actor.Props
import akka.actor.UntypedActor
import im.actor.botkit.RemoteBot
import im.actor.bots.BotMessages
import im.actor.bots.framework.parser.MessageCommand
import im.actor.bots.framework.parser.ParsedMessage
import im.actor.bots.framework.persistence.ServerKeyValue
import im.actor.bots.framework.traits.*
import org.json.JSONObject
import scala.concurrent.Future
import java.util.*

/**
 * Configuration of Bot
 * @param name Unique name of Bot
 * @param clazz Class of bot
 * @param overlordClazz Optional class of bot's overlord
 * @param token Bot's token in Actor Platform
 * @param endpoint Bot's API Endpoint
 * @param traceHook Optional WebHook for logging
 */
class MagicBotConfig(val name: String,
                     val clazz: Class<*>,
                     val overlordClazz: Class<*>?,
                     val token: String,
                     val endpoint: String,
                     val traceHook: String?) {

}

/**
 * Bot's Scope: Context information for bot
 * @param name Unique name of bot
 * @param peer Fork's peer
 * @param sender Sender of message
 * @param bot Magical Remote Bot, required to make requests
 * @param forkKeyValue Fork's key-value. Useful for storing conversation state.
 * @param botKeyValue All bot's key value. Useful for storing bot's state.
 * @param overlord Optional Overlord's actor ref
 */
class MagicForkScope(val name: String,
                     var peer: OutPeer,
                     var sender: BotMessages.User,
                     val bot: MagicalRemoteBot,
                     val forkKeyValue: ServerKeyValue,
                     val botKeyValue: ServerKeyValue,
                     val overlord: ActorRef?) {
}

/**
 * Overlord's scope
 * @param botKeyValue Bot's key value
 * @param bot Magical Remote Bot, required to make requests
 */
class MagicOverlordScope(val botKeyValue: ServerKeyValue,
                         val bot: MagicalRemoteBot)

/**
 * Magical Remote Bot
 * @param config Configuration for bot
 */
open class MagicalRemoteBot(val config: MagicBotConfig) :
        HTTPTrait by HTTPTraitImpl(),
        RemoteBot(config.token, config.endpoint) {

    private var child: ActorRef? = null

    override fun preStart() {
        super.preStart()
        child = context().actorOf(Props.create(MagicChildBot::class.java, this, config), "child")
    }

    /**
     * Handling New Message received
     */
    override fun onMessage(p0: BotMessages.Message?) {

        child?.tell(p0, self())

        // Tracing
        var message = "<<<<<< ${p0!!.peer().toUsable().toUniqueId()}"
        val sender = getUser(p0.sender().id())
        if (sender.emailContactRecords.size > 0) {
            message += "\nby *${sender.name()}* (${sender.emailContactRecords.first().email()})"
        } else if (sender.phoneContactRecords.size > 0) {
            message += "\nby *${sender.name()}* (+${sender.phoneContactRecords.first().phone()})"
        } else {
            return
        }
        message += "\n```\n${p0.message()}```"
        trace(message)
    }

    /**
     * Handling Raw Update received
     */
    override fun onRawUpdate(p0: BotMessages.RawUpdate?) {
        child?.tell(p0, self())

        // Tracing
        trace("<<<<<< Update\n```\n$p0\n```")
    }

    override fun requestSendMessage(peer: BotMessages.OutPeer?, randomId: Long, message: BotMessages.MessageBody?): Future<BotMessages.MessageSent>? {
        val res = super.requestSendMessage(peer, randomId, message)
        // Tracing
        trace(">>>>>> ($peer)\n```\n$message\n```")
        return res
    }

    private fun trace(msg: String) {
        if (config.traceHook != null) {
            urlPostJson(config.traceHook, JSONObject().apply {
                put("text", msg)
            })
        }
    }

    /**
     * Child Bot to avoid dead locks in RPC requests
     */
    class MagicChildBot(val bot: MagicalRemoteBot, val config: MagicBotConfig) :
            UntypedActor() {

        /**
         * Bot's key value storage
         */
        private var botKeyValue = ServerKeyValue(bot)

        /**
         * Overlord for bot
         */
        private var overlord: ActorRef? = null

        override fun preStart() {
            super.preStart()

            if (config.overlordClazz != null) {
                overlord = context().actorOf(Props.create(config.overlordClazz, MagicOverlordScope(botKeyValue, bot)), "overlord")
            }
        }

        /**
         * Message received handler: Forwarding to specific fork
         */
        final override fun onReceive(message: Any?) {
            when (message) {
                is BotMessages.Message -> {
                    peerActor(message.peer(), message.sender()).tell(message, self())
                }
                is BotMessages.RawUpdate -> {
                    overlord?.tell(message, self())
                }
            }
        }

        /**
         * Building peer's Actor
         */
        fun peerActor(peer: BotMessages.OutPeer, sender: BotMessages.UserOutPeer): ActorRef {
            val peerId = (if (peer.isPrivate) "PRIVATE" else "GROUP") + "_" + peer.id()
            val cached = context().child(peerId)
            if (cached.nonEmpty()) {
                return cached.get()
            } else {
                val scope = MagicForkScope(bot.config.name, peer.toUsable(), bot.getUser(sender.id()), bot,
                        ServerKeyValue(bot, "peer_$peerId"), botKeyValue, overlord)
                return context().actorOf(Props.create(bot.config.clazz, scope), peerId)
            }
        }
    }
}

/**
 * Main Magic Bot
 */
abstract class MagicBotFork(val scope: MagicForkScope) :
        HTTPTrait by HTTPTraitImpl(),
        AiTrait by AiTraitImpl(),
        I18NTrait by I18NTraitImpl(),
        LogTrait by LogTraitImpl(),
        APITraitScoped by APITraitScopedImpl(scope.peer, scope.bot),
        AdminTrait by AdminTraitImpl(),
        UntypedActor() {

    /**
     * Enable bot handling in groups.
     */
    var enableInGroups = true

    /**
     * Enable message handling only with mentions.
     */
    var onlyWithMentions = true

    /**
     * Bot's nickname. Used for filtering messages.
     */
    var ownNickname: String? = null

    /**
     * Constructing Bot
     */
    init {
        initLog(this)
    }

    /**
     * Handling messages
     */
    abstract fun onMessage(message: MagicBotMessage)

    /**
     * Called after onMessage. Useful for saving state.
     */
    open fun afterMessage() {

    }

    final override fun onReceive(p0: Any?) {

        val message = p0 as BotMessages.Message
        val content = message.message()
        var msg: MagicBotMessage

        when (content) {
        //
        // Handling Text Message
        //
            is BotMessages.TextMessage -> {

                //
                // Group message filtration
                //
                if (scope.peer.isGroup) {
                    if (!enableInGroups) {
                        return
                    }
                    if (onlyWithMentions) {
                        if (ownNickname != null) {
                            if (!content.text().contains("@$ownNickname")) {
                                return
                            }
                        } else {
                            return
                        }
                    }
                }

                //
                // Building Text Message
                //
                val mText = MagicBotTextMessage(message.peer(), message.sender(), message.randomId(),
                        content.text())
                msg = mText

                // Setting Command parameters if needed
                val pMsg = ParsedMessage.matchType(content.text())
                if (pMsg is MessageCommand) {
                    mText.command = pMsg.command
                    mText.commandArgs = pMsg.data
                }
            }
        //
        // Handling JSON messages
        //
            is BotMessages.JsonMessage -> {
                try {
                    val pJson = JSONObject(content.rawJson())
                    msg = MagicBotJsonMessage(message.peer(), message.sender(),
                            message.randomId(), pJson)
                } catch(e: Exception) {
                    e.printStackTrace()
                    return
                }
            }
        //
        // Handling Document Messages
        //
            is BotMessages.DocumentMessage -> {
                msg = MagicBotDocMessage(message.peer(), message.sender(),
                        message.randomId(), content)
            }
        //
        // Ignoring unknown message
        //
            else -> {
                return
            }
        }

        scope.peer = msg.peer.toUsable()
        scope.sender = getUser(msg.sender.id())
        onMessage(msg)
        afterMessage()
    }

    /**
     * Sending message to Overlord
     */
    fun sendToOverlord(message: Any) {
        scope.overlord?.tell(message, scope.bot.self())
    }
}


/**
 * Magic Bot Overlord. Actor that is used to receive various updates that is not connected
 * to specific conversation.
 */
abstract class MagicOverlord(val scope: MagicOverlordScope) :
        APITrait by APITraitImpl(scope.bot),
        UntypedActor() {

    /**
     * Called when Raw Web Hook are received
     * @param name Web Hook name (that is specified on hook creation)
     * @param body Hook body in bytes
     * @param headers Headers of request
     */
    abstract fun onRawWebHookReceived(name: String, body: ByteArray, headers: JSONObject)

    override fun onReceive(update: Any?) {
        when (update) {
            is BotMessages.RawUpdate -> {
                if (update.type.isPresent && update.type.get() == "HookData") {
                    val res = Base64.getDecoder().decode(update.data())
                    val resS = String(res, "UTF-8")
                    val resJ = JSONObject(resS)
                    if (resJ.getString("dataType") != "HookData") {
                        return
                    }
                    val data = resJ.getJSONObject("data")
                    val name = data.getString("name")
                    val body = Base64.getDecoder().decode(data.getString("body"))
                    val headers = data.getJSONObject("headers")
                    onRawWebHookReceived(name, body, headers)
                }
            }
        }
    }
}