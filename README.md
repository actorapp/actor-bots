# Actor Messenger Bot Platform

Actor Bot platfrom allows you implement your own bots for Actor with various modules that helps you easier integrate with external services, store bot's state in built-in key-value storage or even implement your Siri-like bot with [api.api](https://api.ai/) integration. It is based on [Kotlin language](https://kotlinlang.com).

## Getting Started

All documentation and tutorials are at [docs](docs) directory.

## Example Bot

```kotlin

import im.actor.bots.framework.*

class EchoBot(scope: MagicForkScope) : MagicBotFork(scope) {

    override fun onMessage(message: MagicBotMessage) {
        when (message) {
            is MagicBotTextMessage -> {
                sendText("Received: ${message.text}")
            }
        }
    }
}

fun main(args: Array<String>) {
    farm("BotFarm") {
        bot(EchoBot::class) {
             name = "echo"
             token = "<YOUR_TOKEN_HERE>"
        }
    }
}
```

## Community

You reach Actor community in our [Actor Open Source group](https://actor.im/oss).

## License

Licensed under [Apache 2.0](LICENSE)
