# Implementing your first bot

For fast start, download sources of this repository and import it to IntelliJ IDEA 15 CE. Open actor-bots-example project and add new kotlin file `MyEchoBot.kt`.

For your first bot, you only need to subclass from MagicBotFork and implement onMessage method:

```kotlin
import im.actor.bots.framework.*

class MyEchoBot(scope: MagicForkScope) : MagicBotFork(scope) {

    override fun onMessage(message: MagicBotMessage) {
        when (message) {
            is MagicBotTextMessage -> {
                sendText("Received: ${message.text}")
            }
        }
    }
}
```

That's all!

## Next Step

Now you can [run](bot-farm.md) your first bot.
