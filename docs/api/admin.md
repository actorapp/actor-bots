# Admin

This module provide you simple helper for checking if some person is admin of this bot. All admins are hardcoded during bot initialization.

## Usage

In initialization method of your bot register all admins:

```kotlin
admins.add("steve")
admins.add("prettynatty")
```

After this you can check if user is admin:

```kotlin
isAdmin(<user_id>)
```
