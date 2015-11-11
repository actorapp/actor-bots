# Getting Started

Actor Platform is named after Akka Actors. We like idea of Actors, this is autonomious pieces of software that can perform various actions have some internal state.

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
