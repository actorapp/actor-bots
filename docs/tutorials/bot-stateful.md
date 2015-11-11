# Statful bot

Bot's usually have some state. For example, waiting for user command, or expecting user input of specific type, doin some operation and so on. It is very hard to maintain such states and there is `MagicStatefulBot`.

## Defining States

When you subclass from `MagicStatefulBot` you need to implement `fun configure()` method and in it you need to define all bot's states. States are not dynamicaly created at runtime, it is static tree of states.

Every state have name and full name is formed by contagenating all parents state names with dots. State can easily goto any other state by it's name with `goto`, `tryGoto` and `gotoParent` methods. State names passed to this methods can be either short or full. If you will pass short state name, method will state lookup in all descendance states and it's childs for approriate state. 

Root state is state that expects slash-commands from user and named "main". When you need to go to root state, you can simply call `goto("main")`.

## Persistent

Stateful Bot can persist it's state automatically. You can enable it feature by simply calling `enablePersistent = false` in the begining of your `configure` method.

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

Default initial state for bot. ExpectCommands state is state that automatically parse commands and route it to particular child state. If there are no state for sent command or it is not command at all, state tries to route to state with name "default".

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
This is state that is executes it's body on entering state and immediately goes to parent. Very useful for responses that doesn't require user's input.

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
State for expecting user's input. Before entering state `before` closure is called. On any new message `validate` closure is called where you need to validate input for correctness and if you will return `true`, `received` closure is called.

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
This is state that doesn't have some specific behaviour. Before entering state `before` closure is executed and on any new message `receive` closure is executed.

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
