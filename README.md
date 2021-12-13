# MusicPlayer

## Package Name
- `sakuraba.saki.player.music`

## Maintenance period
- In long-term
```
  This project is a long-term project, which has a long maintenance period,
  in adjust the values in projects, in order to maximize the user-experience
  during the usage of this project software. 
  Particular components, e.g., animation, are all trying the best to provide 
  to the user, to enhance the visual feedback.
  Different characterics will provided related to the maintaining stage and
  the release of beta and release of Android SDK.
  Added, this is a totally free and open-sourced project, with all component 
  projects, implemented dependencies, are all open-sourced. 
  Different open-source licenses are implemented in different component
  projects,  implemented dependencies, so for better implementation, 
  participation, supporting, and respecting the open source movement of 
  software, a COPYLEFT is gained by repository owner, repository creater, 
  repository forkers, main contributor, and other contributors who 
  contributed, are contributing and have contributed any contribution to 
  this project and related repository, and the right of viewing, private
  usage, commerical usage, as well as copying, sharing and editing are all 
  provided to the public and are welcome. 
  A GNU Gernal Public License Version 3 (GNI GPL v3) is applied in this 
  project, with a median COPYLEFT policy.
  As a result, all related projects and repositories, including any code 
  usage, the source code should be provide to the public, as well as the 
  origin owner and contributors' code name or name should be provided in
  related open-sourced projects.
```

## Supported API
- Android L - 5.0 (API 21)
- Android L_MR1 - 5.1 (API 22)
- Android M - 6.0 (API 23)
- Android N - 7.0 (API 24)
- Android N_MR1 - 7.1 (API 25)
- Android O - 8.0 (API 26)
- Android O_MR1 - 8.1 (API 27)
- Android P - 9.0 (API 28)
- Android Q - 10.0 (API 29)
  - `Update SystemUtil.kt: Update Activity.pixelHeight`
    - Commit: [fd02e36](https://github.com/1552980358/MusicPlayer/commit/fd02e36df7813b745babeafaa427f3cc90b4e170)
- Android R - 11.0 (API 30)
  - `SystemUtil.kt: Update deprecated method`
    - Commit: [6e5e1e8](https://github.com/1552980358/MusicPlayer/commit/6e5e1e86a643c1be2a5d002d4c6b900b97baf23c)
    - Google Document Source: [getSize](https://developer.android.com/reference/android/view/Display#getSize(android.graphics.Point))
- Android S - 12.0 (API 31)
  - `NotificationUtil.kt: Fix "Warning: Missing PendingIntent mutability flag" on Android 12`
    - Commit: [298b132](https://github.com/1552980358/MusicPlayer/commit/298b132064cbcb492ed818fb286a21de531366af)
    - Google Document Source: [Pending intents mutability](https://developer.android.com/about/versions/12/behavior-changes-12#pending-intent-mutability)
  - Safer component exporting
    - `If your app targets Android 12 or higher and contains activities, services, or broadcast receivers that use intent filters, you must explicitly declare the android:exported attribute for these app components.`
    - `AndroidManifest.xml: Implement WebService`
      - Commit: [3ec15b9](https://github.com/1552980358/MusicPlayer/commit/3ec15b932f6f872be5d29f9fac9e96b251d3c1fd#diff-7fa6aef292187a049f7a4d6060d8df3ba212d838789c78940bd363344b1c38cd)
    - `Update and implement android build gradle into 7.0.0`
      - Commit: [f1e7bbc](https://github.com/1552980358/MusicPlayer/commit/f1e7bbce27b9c2eba5096493fa3d0883aa1673df#diff-7fa6aef292187a049f7a4d6060d8df3ba212d838789c78940bd363344b1c38cd)
    - Google Document Source: [Safer component exporting](https://developer.android.com/about/versions/12/behavior-changes-12#exported)
  - `Add support Android 12.0 SplashScreen`
    - Commit: [9096b6c](https://github.com/1552980358/MusicPlayer/commit/9096b6c2220c422703cc90a96db8bb49208690b2)
    - Google Document Source: [Splash screens](https://developer.android.com/guide/topics/ui/splash-screen)
- Android SL/Sv2 (API 32) [Beta]

## Lyric format
- Time statement before lyric text
- One line per one line of lyric
- Format: `[mm:ss.ss]Lyric text`
- For example: 
  ```
  [00:00.50]First lyric text
  [00:01.10]Second lyric text
  ```

## Webpage Server
`src/main/assets/web/*`: Please refers to [here](https://github.com/1552980358/MusicPlayer-Webpage)

## Required Permission
- `android.permission.FOREGROUND_SERVICE`
- `android.permission.READ_EXTERNAL_STORAGE`
- `android.permission.WAKE_LOCK`
- `android.permission.INTERNET`
- `android.permission.ACCESS_WIFI_STATE`
- `android.permission.MODIFY_AUDIO_SETTINGS`
- `android.permission.BLUETOOTH_CONNECT`

## Dependencies
- `org.jetbrains.kotlin:kotlin-stdlib`
- `org.jetbrains.kotlinx:kotlinx-coroutines-core`
- `org.jetbrains.kotlinx:kotlinx-coroutines-android`
- `androidx.core:core-ktx`
- `androidx.core:core-splashscreen`
- `androidx.appcompat:appcompat`
- `androidx.constraintlayout:constraintlayout`
- `androidx.navigation:navigation-fragment-ktx`
- `androidx.navigation:navigation-ui-ktx`
- `androidx.lifecycle:lifecycle-livedata-ktx`
- `androidx.lifecycle:lifecycle-viewmodel-ktx`
- `androidx.media:media`
- `androidx.swiperefreshlayout:swiperefreshlayout`
- `androidx.preference:preference-ktx`
- `androidx.databinding:databinding-compiler-common`
- `com.google.android.material:material`
- `com.google.android.exoplayer:exoplayer-core`
- `com.google.android.exoplayer:exoplayer-hls`
- `com.google.code.gson:gson`
- `com.github.android:renderscript-intrinsics-replacement-toolkit`
- `com.github.promeg:tinypinyin`
- `com.github.1552980358:KtExtension`
- `com.github.1552980358:KtExtensionAndroid`
- `com.github.mkaflowski:Media-Style-Palette`
- `org.nanohttpd:nanohttpd`
- `org.nanohttpd:nanohttpd-websocket`
- Dependencies detail can be found here [build.gradle](build.gradle) and [build.gradle](app/build.gradle)

## Maintenance Language
- Kotlin

## IDE
- Intellij Idea
- Android Studio