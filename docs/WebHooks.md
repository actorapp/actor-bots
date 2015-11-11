# WebHooks Module

This module allows you to create web hooks for bots and receive information from extenal services. With web hooks you can not only receive data but also implement OAuth2 authentication in external services.

### Intro

WebHooks are not that simple, mostly because they are not connected to some conversation and you can't directly receive data in your bot. For receiving WebHooks there separate "bot" named "overlord" for receiving web hooks and you need to route them to specific web hook. May be you don't need to route messages to specific bot and you can do everything in overlord (like sending messages to specific chat)

### Registering WebHook

Creating new web hook is simple: you just need to call a method from API Module - ```createHook(name: String): String?``` and in result you will get web hook url.

### Overlord

For implementing overlord, you need to subclass MagicOverlord class and implement web hook methods:
```kotlin
class ExampleOverlord(scope: MagicOverlordScope) : MagicOverlord(scope) {
    override fun onRawWebHookReceived(name: String, body: ByteArray, headers: JSONObject) {
        // Implement Hook Processing
    }
}
```

Also you need to register overlord class:
```kotlin
farm("BotFarm") {
    bot(EchoBot::class) {
        name = "echo"
        token = "<YOUR_TOKEN_HERE>"
        traceHook = "<OPTIONAL_TRACE_WEBHOOK>"
        overlord = ExampleOverlord::class
    }
}
```

Each bot have method for sending message to overlord:
```kotlin
fun sendToOverlord(object: Any)
```

### Sending WebHook

Sending WebHook is simply POST request to a given URL. Framework pass all headers and binary body to overlord and you can, for example, chekc authentication.
