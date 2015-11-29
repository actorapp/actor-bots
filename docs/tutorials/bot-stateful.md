# Stateful bot

Bots are usually have some states. For example, waiting for user command or expecting user input of specific type doin some operation and so on. It is very hard to maintain such states and `MagicStatefulBot` comes to the aid.

## Defining States

When you create subclass from `MagicStatefulBot`, you need to implement `fun configure()` method, where you should define all bots states. States are not dynamically created at the runtime, it's a static tree of states.

Every state has a name, full form of which is formed by spliting all parents state names with dots. State can easily goto any other state through `goto`, `tryGoto` and `gotoParent` methods. State names passed to these methods can be either short or full. If you pass short state name, method state lookup in all descendants states and it's childs for appropriate state. 

Root state "main" is a state, that expects slash-commands from user. When you need to go to the root state, you can simply call `goto("main")`.

## Persistent

Stateful Bot can persist its state automatically. You can enable this feature by calling `enablePersistent = false` at the beginning of your `configure` method.

## Example

```kotlin
class ExampleStatefulBot(scope: MagicForkScope) : MagicStatefulBot(scope) {

    override fun configure() {
        oneShot("/help") {
            sendText("Hello! I am example bot!")
        }
        raw("/test") {
            before {
              sendText("Send me something")
            }
            received {
                if (isText) {
                    sendText("Text received")
                } else if (isDoc) {
                    sendText("Doc received")
                } else {
                    sendText("Something other received")
                }
                goto("main")
            }
        }
        command("/input") {
            before {
                sendText("Please, send me text")
                goto("ask_text")
            }
            
            expectInput("ask_text") {
            
                received {
                    sendText("Yahoo! $text received!")
                    goto("main")
                }
                
                validate {
                    if (!isText) {
                        sendText("I need text!")
                        return@validate false
                    }
                    
                    return@validate true
                }
            }
        }
    }
}
```

## State types

### Expect Commands

Default initial bot state. ExpectCommands automatically parse commands and route it to particular child state. If there is no state for sent command or it is not a command, state tries to route to state with name "default".

```kotlin
expectCommands {
    oneShot("/help") {
        sendText("Help!")
    }
    oneShot("/hint") {
        sendText("Hint!")
    }
}
```

### One-Shot state
This state initially executes its body and immediately goes to parent. Very useful for responses that doesn't require user input.

```kotlin
oneShot("/hint") {
    sendText("Hello! I am example bot!")
}

oneShot("/weather") {
    val weather = getUrlJson("weather_service_url")
    sendText("Expected temperature ${weather.getString("temperature")}")
}
```

### Input state
State for expecting user's input. Before entering this state, `before` closure is called. `validate` closure is called within any new message, where you need to validate input text for correctness. If the values will return `true`, `received` closure will be called.

```kotlin
expectInput("ask_name") {
    before {
        sendText("Please, enter your name")
    }
    received {
        sendText("Thank you, $text")
    }
    validate {
        if (!isText) {
            sendText("Please, send text")
            return@validate false
        }
        return@validate true
    }
}
```

### Raw state
This is state that doesn't have any specific behaviour. Before entering state `before` closure is executed and on any new message `receive` closure is executed.

```kotlin
raw("/translate") {
    before {
        sendText("Send me messages for translation! and /cancel to stop.")
    }
    received {
        if (isText) {
            sendText("Translated ${translate(text)}")
        } else if (isCommand) {
            if (command == "cancel") {
                gotoParent()
            }
        }
    }
    
}
```

### Command state
Obsolete state that acts like `raw`, but marks state as state for some specific command.
