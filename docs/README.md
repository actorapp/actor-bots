# Getting Started

Actor Platform is named after Akka Actors. We like idea of Actors, this is autonomious pieces of software that can perform various actions have some internal state. We treat bots in a such way as actors. They can talk not only with people, but also with each other even without human at all. You can think about Actor as just big event bus and every bot can receive and send data in reliable way from any part of the planet.

# Magic Bot

Magic Bot is structure of multiple actors:

* MagicRootBot
  * MagicChildBot
    * [Overlord]
    * Conversation(PRIVATE_1234)
    * Conversation(PRIVATE_1236)
    * Conversation(GROUP_1234)
    * Conversation(GROUP_1236)
    * ...

First two are root bots and bot developer doesn't need to have access to them. They just hold averything together.

[Overlord](Overlord.md) is an optional actor that receive information not connected to specific conversation.

Conversation(...) is an dynamically created actors for each conversation named "forks". This actors are created right before processing first incoming message. If you want to keep something running between launches, better to use Overlord for this task.

# Bot Farm

To launch bots, you need to create farm and register all required information. Read more at [Farm page](Farm.md).

# Modules

Bots have various built-in modules for easier bot development.

* Server key-value storage. One for bot and for one for each fork. (can be accessed with scope)
* Local key-value storage. Useful for keeping state of actor on disk.
* Natural language processing with api.ai
* [HTTP helpers](api/HTTP.md)
* [i18n support](api/I18N.md)
* Command parser
* Parse.com helpers
* Helpers for admin users of bot
* [BotFather implementation](../actor-bots/src/main/java/im/actor/bots/embedded/BotFather.kt)
* Notification bot implementation
* [WebHooks support](api/WebHooks.md)
