# TetherHub

## What it is?

Social network app to demonstrate the capabilities of Kotlin Multiplatform.

## Project structure

This is a Kotlin Multiplatform project targeting Android, iOS, Server.

- `/composeApp` is for code that will be shared across the multiplatform applications.
  It contains several subfolders:

  - `commonMain` is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the
    folder name.

- `/iosApp` is the entry point for the iOS app.

- `/server` is for the Ktor server application.

- `/shared` is for the code that will be shared between all targets in the project.

## Features

|                |                                    |                                           |                                           |                                |
|----------------|------------------------------------|-------------------------------------------|-------------------------------------------|--------------------------------|
| Authentication | ![image](screenshots/login.png)    | ![image](screenshots/registration.png)    |                                           |                                |
| Posts          | ![image](screenshots/feed.png)     | ![image](screenshots/new_post.png)        |                                           |                                |
| Chat           | ![image](screenshots/messages.png) | ![image](screenshots/new_chat.png)        | ![image](screenshots/new_chat_filled.png) | ![image](screenshots/chat.png) |
| Friends        | ![image](screenshots/friends.png)  | ![image](screenshots/friends_search.png)  | ![image](screenshots/friend_request.png)  |                                |
| Profile        | ![image](screenshots/profile.png)  | ![image](screenshots/account_options.png) |                                           |                                |
