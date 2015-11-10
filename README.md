# Actor Messenger Bot Platform

Entry point for programming useful bots. All bots are written in Kotlin, JVM-based language that is often called "lightweight scala".
We use this language because of it's ability to make cool DSL languages with static typing.

# Basic Principles

This framework includes implementation of ideas of "forks". Every bot have several child bots (or forks) - one for each conversation. This help easily maintain internal state of bot without overwhelming.

Framework includes:
* Server key-value storage. One for bot and for one for each fork. (can be accessed with scope)
* Local key-value storage. Useful for keeping state of actor on disk.
* Natural language processing with api.ai
* [HTTP helpers](docs/HTTP.md)
* [i18n support](docs/I18N.md)
* Command parser
* Parse.com helpers
* Helpers for admin users of bot
* [BotFather implementation](actor-bots/src/main/java/im/actor/bots/embedded/BotFather.kt)
* Notification bot implementation
* [WebHooks support](docs/WebHooks.md)

# Getting Started

Before begin, you need to create new Bot in Actor Cloud. Just send "/start" to @botfather and bot will guide you do how to create and configure your bot.

We recommend to have new fresh IntelliJ IDEA 15 Community Edition, import project from this repo and launch `MainBotFarmDebug.kt` in actor-bot-example.
