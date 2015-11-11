# Artifical Intelligence

This module provide you easy access to [api.ai](https://api.ai) to build your Siri-like bot.

Before begin we recommend to read api.ai [documentation](https://docs.api.ai/) first and understand basic principes.

```kotlin
interface AiTrait {
    var aiSubscriptionKey: String?
    fun registerAiAgent(key: String, lang: String, token: String)
    fun aiQuery(query: String): AiResponse?
    fun aiQuery(query: String, closure: AiResponse.() -> Unit)
    fun aiQuery(agent: String, query: String): AiResponse?
    fun aiQuery(agent: String, query: String, closure: AiResponse.() -> Unit)
}
```

## Query Response

```kotlin
class AiResponse {
    val raw: JSONObject
    val action: String
    val speech: String?

    val pQuery: String?
    val pSimplified: String?
    val pRequestType: String?
    val pSummary: String?
    val pTime: Date?
}
```

`raw` - Raw Response from API.AI
`action` - Recognized action name
`speech` - Suggested text response

`pQuery` - query parameter for some actions (like searching on the web)
`pSimplified` - simplified input string. For example, "hi", "hello", "nice to meet you!" will be simplified to "hello"
`pRequestType` - type of request (domain or agent)
`pSummary` - summary of input string
`pTime` - recognized time

## Configuration

Before using ai methods you need to provide subscription key and register your agents.
Module can work with multiple agents. First registered agent is considered as default.

```kotlin
aiSubscriptionKey = "<YOUR_SUBSCRIPTION_KEY>"
registerAiAgent("<agent_id>", "<agent_lang>", "<agent_key>")
```

## Queries

### Performing AI query

```kotlin
fun aiQuery(query: String): AiResponse?
fun aiQuery(agent: String, query: String): AiResponse?
```

### Query with closure

```kotlin
fun aiQuery(query: String, closure: AiResponse.() -> Unit)
fun aiQuery(agent: String, query: String, closure: AiResponse.() -> Unit)
```

Same as above, but provide nice syntax like:

```kotlin
aqQuery(text) {
	when(action) {
		"smalltalk.greetings" -> {
			sendText("Hello!")
		}
	}
}
```
