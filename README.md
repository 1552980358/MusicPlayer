# Fokkusu Music Player

### PackageName
```
app.github1552980358.android.musicplayer
```
### Maintainer
```
1552980358
```

### Compile Project

1. Download and Install [Intellij Idea](https://www.jetbrains.com/idea/) or [Android Studio](https://developer.android.google.cn/studio/)
2. Download Android SDK
    - `File`
    - `Setting`
    - `Appearance and Action`
    - `System Settingss`
    - `Android SDK`
    - Edit the Android SDK Location
    - Select SDK Version (Advised to be same as `targetSdkVersion` set in [build.gradle](https://github.com/1552980358/MusicPlayer/blob/dev/app/build.gradle) in module `app`)
    - Confirm to start download
3. Download Git
    - Windows 
        - [Git](https://git-scm.com/)
    - Ubuntu
        ```bash
        sudo apt update && sudo apt upgrade -y
        sudo apt install git -y
        ```
    - ArchLinux
        ```bash
        sudo pacman -Sy git
        ```
4. Clone Project
    ```
    cd <directory wanted>
    git clone https://github.com/1552980358/MusicPlayer
    ```
5. Import Project
    - `File`, `Open…`
    - Select Project
6. Select JDK
    - `File`
    - `Settings`
    - `Build, Execute and Deployment`
    - `Build Tools`
    - `Gradle`
    - `Gradle JVM`
    - `Download JDK`
    - Select Directory and `Download`
7. Build
    - `Build`
    - `Generate Signed Bundle / APK`
    - Select `APK`
    - `Next`
    - Create new signature `Create new…`
    - Input all required key alias and key passwords
    - Select `Release`, and `V1` and `V2`
    - Start building
8. Get APK
    - Signed APK is at `<Project Directory>\app\release`

### Project License
```
                    GNU GENERAL PUBLIC LICENSE
                      Version 3, 29 June 2007

 Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 Everyone is permitted to copy and distribute verbatim copies
 of this license document, but changing it is not allowed.
```

### Open Source License
Refer to project

[https://github.com/1552980358/MusicPlayer/blob/beta-v2/LICENSE](https://github.com/1552980358/MusicPlayer/blob/beta-v2/LICENSE)