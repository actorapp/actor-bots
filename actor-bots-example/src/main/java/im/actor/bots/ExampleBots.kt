package im.actor.bots

import im.actor.bots.framework.MagicBotFork
import im.actor.bots.framework.MagicBotMessage
import im.actor.bots.framework.MagicBotTextMessage
import im.actor.bots.framework.MagicForkScope
import im.actor.bots.framework.persistence.MagicPersistentBot
import org.json.JSONObject

/**
 * Very simple echo bot that forwards message
 */
class EchoBot(scope: MagicForkScope) : MagicBotFork(scope) {

    override fun onMessage(message: MagicBotMessage) {
        when (message) {
            is MagicBotTextMessage -> {
                sendText("Received: ${message.text}")
            }
        }
    }
}

/**
 * Echo persistent bot that keeps it's state between restart
 */
class EchoPersistentBot(scope: MagicForkScope) : MagicPersistentBot(scope) {

    var receivedCount: Int = 0

    override fun onRestoreState(state: JSONObject) {
        receivedCount = state.optInt("counter", 0)
    }

    override fun onMessage(message: MagicBotMessage) {
        sendText("Received ${receivedCount++} messages")
    }

    override fun onSaveState(state: JSONObject) {
        state.put("counter", receivedCount)
    }
}