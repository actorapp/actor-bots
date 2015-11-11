# Overlord

Overlord is an Actor that is not binded to specific conversation. Currently overlords is responsible to receiving web hooks.

### Implementing

For implementing overlord, you need to subclass MagicOverlord class and implement required methods:
```kotlin
class ExampleOverlord(scope: MagicOverlordScope) : MagicOverlord(scope) {
    override fun onRawWebHookReceived(name: String, body: ByteArray, headers: JSONObject) {
        // Implement Hook Processing
    }
}
```

### Registering

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

### Sendimg Message to Overlord

This message can be received in `onReceive` method of overlord. WARRING: Do not forget to call super class method.

```kotlin
fun sendToOverlord(object: Any)
```
