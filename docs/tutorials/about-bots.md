# About Bots

Actor Platform is named after Akka Actors. We like idea of Actors: autonomious isolated pieces of software that can perform various actions have some internal state. We treat bots as actors. They can talk not only with people, but also with each other even without human at all. You can think about Actor as just big event bus where every bot can receive and send data in reliable way from any part of the planet.

In future releases, we will use bots are extension mechanizm for server. For example, it is much easier to implement authentication module in bot than by hacking server.

## Architecture

MagicRootBot is an entry point of building bots and consist consist of multiple internal actors:

* MagicRootBot
  * MagicChildBot
    * [Overlord]
    * Conversation(PRIVATE_1234)
    * Conversation(PRIVATE_1236)
    * Conversation(GROUP_1234)
    * Conversation(GROUP_1236)
    * ...

The most important one is conversation actors - subclasses of MagicBotFork - this is bot developer's implementation.

As you can see, we launch separate instance for each conversation. This will help you not to share state of different conversations in one single class. This Actors are created dynamically and not restored on bot restart. They are created right before processing first income message from conversation. If you want something more permanent(for example: reminders), you can use [Overlords](Overlord.md).

[Overlord](Overlord.md) is an optional actor that receive information that is not connected to specific conversation. This actor, unlike conversation actors, always launched on bot startup. Keep all cross-conversation state in it.

First two are root bots and not important for bot developer: they just hold everything together.

## Next Step

[Registering your first bot.](register-bot.md)
