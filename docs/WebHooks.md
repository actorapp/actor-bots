# WebHooks Module

This module allows you to create web hooks for bots and receive information from extenal services. With web hooks you can not only receive data but also implement OAuth2 authentication in external services.

### Intro

WebHooks are not that simple, mostly because they are not connected to some conversation and you can't directly receive data in your bot. For receiving WebHooks there separate "bot" for receiving web hooks and you need to route them to specific web hook.
