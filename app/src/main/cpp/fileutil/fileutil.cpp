#include "file_util.h"

#include <jni.h>

extern "C"
JNIEXPORT void JNICALL
Java_projekt_cloud_piece_music_player_util_FileUtil_00024FileUtilImpl_write(JNIEnv *env, jobject,jstring path, jbyteArray data) {
    auto path_char = env->GetStringUTFChars(path, JNI_FALSE);
    if (path_char) {
        auto bytes = env->GetByteArrayElements(data, JNI_FALSE);
        if (bytes) {
            write_byte_array(path_char, bytes, env->GetArrayLength(data));
            // Release
            env->ReleaseByteArrayElements(data, bytes, JNI_ABORT);
        }
        // Release
        env->ReleaseStringUTFChars(path, path_char);
    }
}