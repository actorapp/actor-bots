## Guides
* [About Bots](tutorials/about-bots.md)
* [Registering new bot](tutorials/register-bot.md)
* Implementing bot
* Running bots
* Implementing Overlord

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
* [WebHooks support](api/WebHooks.md)

## Examples
* [BotFather Implementation](../actor-bots/src/main/java/im/actor/bots/embedded/BotFather.kt)
* [Notification bot with Overlord](../actor-bots/src/main/java/im/actor/bots/blocks/Notification.kt)
