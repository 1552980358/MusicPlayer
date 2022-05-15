#include <jni.h>
#include "include/decode_mp3.h"

/**
 * https://www.codeproject.com/Articles/8295/MPEG-Audio-Frame-Header
 *
 * AAAAAAAA AAABBCCD EEEEFFGH IIJJKLMM
 *
 * A [0, 11]: Frame sync to find the header (all bits are always set)
 *
 * B [11, 2]: Audio version ID (see table 3.2 also)
 *    00 - MPEG Version 2.5 (unofficial extension of MPEG 2)
 *    01 - reserved
 *    10 - MPEG Version 2 (ISO/IEC 13818-3)
 *    11 - MPEG Version 1 (ISO/IEC 11172-3)
 *
 * C [13, 2]: Layer index
 *    01 - Layer III
 *    10 - Layer II
 *    11 - Layer I
 *
 * D [15, 1]: Protection bit
 *    0 - protected by 16 bit CRC following header
 *    1 - no CRC
 *
 * E [16, 4]: Bitrate index
 *    @see https://www.codeproject.com/Articles/8295/MPEG-Audio-Frame-Header#Bitrate
 *
 * F [20, 2]: Sampling rate index
 *    @see https://www.codeproject.com/Articles/8295/MPEG-Audio-Frame-Header#SamplingRate
 *
 */

extern "C" {

JNIEXPORT jlong JNICALL
Java_projekt_cloud_piece_music_player_util_MP3Decoder_decodeMp3(JNIEnv *env, jobject, jlong ptr, jbyteArray file_byte_array) {
    return decode_mp3(ptr, env->GetByteArrayElements(file_byte_array, nullptr), env->GetArrayLength(file_byte_array)); // NOLINT(cppcoreguidelines-narrowing-conversions)
}

JNIEXPORT jboolean JNICALL
Java_projekt_cloud_piece_music_player_util_MP3Decoder_isMp3File(JNIEnv *env, jobject, jbyteArray file_byte_array) {
    return is_mp3_file(env->GetByteArrayElements(file_byte_array, nullptr), env->GetArrayLength(file_byte_array));
}

JNIEXPORT jint JNICALL
Java_projekt_cloud_piece_music_player_util_MP3Decoder_getBitrate(JNIEnv *env, jobject, jlong pointer) {
    return get_mp3_metadata_ptr((long) pointer)->get_bit_rate();
}

JNIEXPORT jint JNICALL
Java_projekt_cloud_piece_music_player_util_MP3Decoder_getSampleRate(JNIEnv *, jobject , jlong pointer) {
    return get_mp3_metadata_ptr((long) pointer)->get_sample_rate();
}

}