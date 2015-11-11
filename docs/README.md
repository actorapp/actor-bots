## Guides
* [About Bots](tutorials/bot-about.md)
* [Registering new bot](tutorials/bot-register.md)
* [Implementing bot](tutorials/bot-implement.md)
* [Running bots](tutorials/bot-farm.md)
* [Implementing Overlord](tutorials/bot-overlord.md)
* [Working with incoming WebHooks](tutorial/web-hooks.md)

## Modules
* Server key-value storage. One for bot and for one for each fork. (can be accessed with scope)
* Local key-value storage. Useful for keeping state of actor on disk.
* Natural language processing with api.ai
* [HTTP helpers](api/HTTP.md)
* [i18n support](api/I18N.md)
* Command parser
* Parse.com helpers
* Helpers for admin users of bot
* Notification bot implementation

## Examples
* [BotFather Implementation](../actor-bots/src/main/java/im/actor/bots/embedded/BotFather.kt)
* [Notification bot with Overlord](../actor-bots/src/main/java/im/actor/bots/blocks/Notification.kt)
