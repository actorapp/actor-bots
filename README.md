<h3 align="center">
  <a href="https://github.com/actorapp/actor-platform">
    <img src="docs/assets/Actor_Logo.png" width="150" />
    <br />
    Actor
  </a>
</h3>
<p align="center">
  <a href="https://github.com/actorapp/actor-platform">Platform</a> &bull; 
  <a href="https://github.com/actorapp/actor-bootstrap">Bootstrap</a> &bull; 
  Bots
</p>
-------

# Actor Bot Platform

Actor Bot platfrom allows you to implement your own bots for Actor with various modules that help you easily integrate with external services, store bot's state in built-in key-value storage or even implement your Siri-like bot with [api.api](https://api.ai/) integration. It is based on [Kotlin language](https://kotlinlang.org).

Features
============
 |  Features
--------------------------|------------------------------------------------------------
:rocket: | Fast start with bot development
:wrench: | Rich framework for building bots
:squirrel: | Built-in Natural Language Processing with [api.ai](https://api.ai)
:computer: | Easy to host anywhere
:octocat: | Community supported

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

You can reach Actor community in our [Actor Open Source group](https://actor.im/oss).

## License

Licensed under [Apache 2.0](LICENSE)
