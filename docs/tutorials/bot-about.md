# About Bots

Actor Platform is named after Akka Actors. We like the idea of Actors: autonomious isolated pieces of software that can perform various actions and have some internal state. We treat bots as actors. They can talk not only to people, but also to each other even without human at all. You can think of Actor as just of a big event bus where every bot can receive and send data in reliable way from any part of the planet.

In future releases, we will use bots as an extension mechanizm for server. For example, it is much easier to implement authentication module via bot than by hacking server.

## Before you Begin

Before the beginning we recomend to read about [Kotlin Language](https://kotlinlang.org). This is a very easy to study language and if you are familiar with Java, you will learn it in a day.

Also for working with bots, you need IntelliJ IDEA 15 CE and import project from the root of this repository.

## Architecture

MagicRootBot is an entry point of building bots and consists of multiple internal actors:

* MagicRootBot
  * MagicChildBot
    * [Overlord]
    * Conversation(PRIVATE_1234)
    * Conversation(PRIVATE_1236)
    * Conversation(GROUP_1234)
    * Conversation(GROUP_1236)
    * ...

The most important ones are conversation actors - subclasses of MagicBotFork - this is bot developer's implementation.

As you can see, we launch separate instance for each conversation. This will help you not to share state of different conversations in one single class. This Actors are created dynamically and not restored on bot restart. They are created right before processing first income message from conversation. If you want something more permanent(for example: reminders), you can use [Overlords](bot-overlord.md).

[Overlord](bot-overlord.md) is an optional actor that receives information that is not connected to specific conversation. This actor, unlike conversation actors, is always launched on bot startup. Keep all cross-conversation states in it.

First two are root bots and not important for bot developer: they just hold everything together.

## Next Step

[Registering your first bot](bot-register.md)
