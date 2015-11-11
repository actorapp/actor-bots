# Server Key-Value storage

For each bot there are server-side key-value storage and you can access it with simple API. Bot provides two types of storages: shared key-value and one separate for each conversation.

Key-value storage can persist integers, strings, doubles, booleans and json-objects.

## Accessing KeyValue storage.

Each bot have scope and you can find forkKeyValue and botKeyValue. First one is for current conversation and second one is shared key-value.
