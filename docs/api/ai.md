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

## Configuration

Before using ai methods you need to provide subscription key and register your agents.
Module can work with multiple agents. First registered agent is considered as default.

```kotlin
aiSubscriptionKey = "<YOUR_SUBSCRIPTION_KEY>"
registerAiAgent("<agent_id>", "<agent_lang>", "<agent_key>")
```

## Queries

### Performing AI query

```fun aiQuery(agent: String, query: String): AiResponse?```
With default agent: ```fun aiQuery(query: String): AiResponse?```

### Query with closure

```fun aiQuery(agent: String, query: String): AiResponse?```
With default agent: ```fun aiQuery(query: String, closure: AiResponse.() -> Unit)```

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