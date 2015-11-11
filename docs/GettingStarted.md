# Getting Started

Actor Platform is named after Akka Actors. We like idea of Actors, this is autonomious pieces of software that can perform various actions have some internal state.

# Magic Bot

Magic Bot is structure of multiple actors:

- MagicRootBot
-- MagicChildBot
--- Overlord
--- Conversation(PRIVATE_1234)
--- Conversation(PRIVATE_1236)
--- Conversation(GROUP_1234)
--- Conversation(GROUP_1236)
--- ...