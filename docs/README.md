## Guides
* [About Bots](tutorials/bot-about.md)
* [Registering new bot](tutorials/bot-register.md)
* [Implementing bot](tutorials/bot-implement.md)
* [Running bots](tutorials/bot-farm.md)
* [Implementing Overlord](tutorials/bot-overlord.md)
* [Incoming WebHooks](tutorials/web-hooks.md)
* Message Types

## Modules
* Server key-value storage. One for bot and for one for each fork. (can be accessed with scope)
* Local key-value storage. Useful for keeping state of actor on disk.
* Natural language processing with api.ai
* [HTTP helpers](api/HTTP.md)
* [i18n support](api/I18N.md)
* Helpers for admin users of bot

## Examples
* [BotFather Implementation](../actor-bots/src/main/java/im/actor/bots/embedded/BotFather.kt)
* [Notification bot with Overlord](../actor-bots/src/main/java/im/actor/bots/blocks/Notification.kt)
