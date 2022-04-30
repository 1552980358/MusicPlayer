# Projekt CloudPiece - MusicPlayer

## Package Name
`projekt.cloud.piece.music.player`

## Building
[![BuildProject](https://github.com/1552980358/MusicPlayer/actions/workflows/BuildProject.yml/badge.svg)](https://github.com/1552980358/MusicPlayer/actions)

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
- Android R - 11.0 (API 30)
- Android S - 12.0 (API 31)
- Android SL/Sv2 (API 32) [Beta]

## Lyric File (.lrc)
- A timestamp before lyric text
  - 3-digit and 2-digit millisecond both supported
    - Example: `[00:01.120]` and `[00:01.12]` gives the same time `1200ms` or `1 second and 120 millisecond`
- Multi lyric line supported
  - Multi-line of lyric is supported, with same timestamp, will be in the same lyric line item
  - Example
    ```
    [00:01.120]Upper Line
    [00:01.120]Bottom Line
    ```
    gives
    ```
    Upper Line
    Bottom Line
    ```
    otherwise if different timestamp
    ```
    Upper Line
    
    Bottom Line
    ```

## Permission
```
android.permission.FOREGROUND_SERVICE
android.permission.READ_EXTERNAL_STORAGE
```
Detail can be found from the the [AndroidManifest.xml](app/src/main/AndroidManifest.xml) with tag `<uses-permission>`

## Plugins
```
com.android.tools.build
org.jetbrains.kotlin.android
androidx.navigation.safeargs.kotlin
com.google.devtools.ksp
kotlin-kapt
navigation-safe-args-gradle-plugin
```
Detail can be found from the the [build.gradle.kts](build.gradle.kts)

## Dependencies
```
org.jetbrains.kotlinx:kotlinx-coroutines-core
org.jetbrains.kotlinx:kotlinx-coroutines-android
androidx.appcompat:appcompat
androidx.core:core-ktx
androidx.navigation:navigation-fragment-ktx
androidx.navigation:navigation-ui-ktx
androidx.lifecycle:lifecycle-viewmodel-ktx
androidx.media:media
androidx.preference:preference-ktx
androidx.room:room-runtime
androidx.room:room-compiler
androidx.room:room-ktx:$roomVersion
androidx.databinding:databinding-compiler-common
androidx.datastore:datastore
androidx.datastore:datastore-preferences
com.google.android.material:material
com.google.android.exoplayer:exoplayer-core
com.google.android.exoplayer:exoplayer-hls
com.github.1552980358:C2Pinyin
com.github.mkaflowski:Media-Style-Palette
```
Detail can be found from the the [build.gradle.kts](app/build.gradle.kts)

## Open-source License - [GNU GENERAL PUBLIC LICENSE Version 3](LICENSE)
```
Projekt Cloud Piece, including all git submodules are free software:
you can redistribute it and/or modify it under the terms of the
GNU General Public License as published by the Free Software Foundation,
either version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
```