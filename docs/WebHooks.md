# WebHooks Module

This module allows you to create web hooks for bots and receive information from extenal services. With web hooks you can not only receive data but also implement OAuth2 authentication in external services.

### Create New WebHook

Creating new web hook is simple: you just need to call a method from API Module - ```createHook(name: String): String?``` and in result you will get web hook url.

### Receve WebHook

Receiving web hooks must be implemented in [Overlord](Overlord.md).

### Send WebHook

Sending WebHook is simply POST request to a given URL. Framework pass all headers and binary body to overlord and you can, for example, chekc authentication.
