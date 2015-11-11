# HTTP Module

This module contains helpers for making HTTP Requests and JSON-HTTP.

```kotlin
interface HTTPTrait {
    fun urlGetText(url: String, vararg headers: String): String?
    fun urlPostJson(url: String, content: JSONObject, vararg headers: String): JSONObject?
    fun urlGetJson(url: String, vararg headers: String): JSONObject?
    fun urlGetJsonArray(url: String, vararg headers: String): JSONArray?
}
```

## Usage

For GET request, call `fun urlGetText(url: String, vararg headers: String): String?`. Result is not-null if request was successfull and null if not. Headers are array of keys and values.

For JSON POST request, call `fun urlPostJson(url: String, content: JSONObject, vararg headers: String): JSONObject?` and this method witll post content to `url` with json content type and read response to json object

For JSON GET requests, call `fun urlGetJson(url: String, vararg headers: String): JSONObject?` or `fun urlGetJsonArray(url: String, vararg headers: String): JSONArray?`. First one if you expect json object and second one if json array.
