# Persist bot

For much easier save/restore of bot's state there is `MagicPersistentBot` that saves it's state to disk after each incoming message.

Everyting you need is only subclass from `MagicPersistentBot` and implement `onRestoreState` and `onSaveState`. On bot restart onRestoreState is called and you can easily restore saved state.

```kotlin
import im.actor.bots.framework.*
import im.actor.bots.framework.persistence.*
import org.json.JSONObject

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
```