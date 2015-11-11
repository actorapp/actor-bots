# Local Key-Value storage

For storing information on bot's machine, it is built-in key-value storage based on leveldb.
API is a bit overwhelming and we suggest you to use server key-value instead.

## Create KeyValue

```kotlin
val stateKeyValue: SimpleKeyValueJava<String> = ShardakkaExtension.get(context().system()).simpleKeyValue("<KEY_VALUE_NAME>").asJava()
```

## Using KeyValue

```kotlin

// Reading value 
val value = stateKeyValue.get("<record_name>")

// Writing value
stateKeyValue.syncUpsert("<record_name>", "<record_value")
```
