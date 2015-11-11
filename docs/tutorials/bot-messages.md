# Message Types

Bot Platform have several message types: text, document (with photo, video, audio extensions), service messages and abstract JSON-message.

```kotlin
public abstract class MagicBotMessage(val peer: BotMessages.OutPeer, val sender: BotMessages.UserOutPeer,
                                      val rid: Long) {

}

public class MagicBotTextMessage(peer: BotMessages.OutPeer, sender: BotMessages.UserOutPeer, rid: Long,
                                 val text: String) : MagicBotMessage(peer, sender, rid) {
    var command: String? = null
    var commandArgs: String? = null
}

public class MagicBotJsonMessage(peer: BotMessages.OutPeer, sender: BotMessages.UserOutPeer, rid: Long,
                                 val json: JSONObject) : MagicBotMessage(peer, sender, rid) {

}

public class MagicBotDocMessage(peer: BotMessages.OutPeer, sender: BotMessages.UserOutPeer, rid: Long,
                                val doc: BotMessages.DocumentMessage) : MagicBotMessage(peer, sender, rid) {

}
```