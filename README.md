# Actor Messenger Bot Platform

Entry point for programming useful bots. All bots are written in Kotlin, JVM-based language that is often called "lightweight scala".
We use this language because of it's ability to make languages with static typing.

# Getting Started

For getting started visit [documentation page](doc/GettingStarted.md).

# Modules

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

# License

Licensed under [Apache 2.0](LICENSE)
