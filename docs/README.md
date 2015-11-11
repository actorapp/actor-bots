Actor Platform is named after Akka Actors. We like idea of Actors, this is autonomious pieces of software that can perform various actions have some internal state. We treat bots in a such way as actors. They can talk not only with people, but also with each other even without human at all. You can think about Actor as just big event bus and every bot can receive and send data in reliable way from any part of the planet.

## Guides
* Bots intro
* Registering new bot
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
* [BotFather implementation](../actor-bots/src/main/java/im/actor/bots/embedded/BotFather.kt)
