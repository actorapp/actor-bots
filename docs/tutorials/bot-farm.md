# Farm

Farm is an entry point for launching your bots. Each application can have only one launched farm.

### Example

This is an example of using farm with single ```EchoBot``` bot.

`name` - unique name for a bot, lowcase latin/numbers are recomended.

`token` - access token for your bot, generated with BotFather.

`traceHook` - For debugging purpoises you can create group, generate integration web hook and write it here to receive all received/sent messages. WARRING: This is not secure and we recomend to do this only for debug.

`overlord` - Optional class for [Overlord](bot-overlord.md)


```kotlin
import im.actor.bots.framework.farm

fun main(args: Array<String>) {
    farm("bots") {
        bot(EchoBot::class) {
             name = "echo"
             token = "<YOUR_TOKEN_HERE>"
             traceHook = "<OPTIONAL_TRACE_WEBHOOK>"
             overlord = <OPTIONAL_OVERLORD_CLASS>
        }
    }
}
```
