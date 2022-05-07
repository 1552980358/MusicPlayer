#include <jni.h>
#include "include/decode_mp3.h"
#include "include/mp3_header.h"

short extract_mp3_version(const int8_t &);
short extract_mp3_layer(const int8_t &);

short extract_mp3_bit_rate(const short &, const short &, const int8_t &);
short get_mp3_bit_rate_version_1(const short &, const int &);
short get_mp3_bit_rate_version_2_3(const short &, const int &);

int extract_mp3_sample_rate(const short &, const int8_t &);

long decode_mp3(const int8_t *mp3_byte_array) {
    auto mp3_version = extract_mp3_version(mp3_byte_array[1]);
    if (!mp3_version) {
        return 0;
    }

    auto mp3_layer = extract_mp3_layer(mp3_byte_array[1]);
    if (!mp3_layer) {
        return 0;
    }

    auto mp3_bit_rate = extract_mp3_bit_rate(mp3_version, mp3_layer, mp3_byte_array[2]);
    if (!mp3_bit_rate) {
        return 0;
    }

    auto mp3_sample_rate = extract_mp3_sample_rate(mp3_version, mp3_byte_array[2]);
    if (!mp3_sample_rate) {
        return 0;
    }

    return (long) new mp3_header(mp3_version, mp3_layer, mp3_bit_rate, mp3_sample_rate);
}

/**
 * extract_mp3_version
 * AAABBCCD -> BB
 * @param byte
 * @return version of mp3
 *    0 - Unknown
 *    1 - MPEG Version 1 (ISO/IEC 11172-3)
 *    2 - MPEG Version 2 (ISO/IEC 13818-3)
 *    3 - MPEG Version 2.5 (unofficial extension of MPEG 2)
 *
 */
short extract_mp3_version(const int8_t &byte) {
    auto bits = (byte >> 3)   // AAABBCCD -> AAABB
                & 0b11;         // AAABB -> BB

    /**
     * 00 - MPEG Version 2.5 (unofficial extension of MPEG 2)
     * 01 - reserved
     * 10 - MPEG Version 2 (ISO/IEC 13818-3)
     * 11 - MPEG Version 1 (ISO/IEC 11172-3)
     */
    switch (bits) {
        case 0b11:
            return VERSION_1;
        case 0b10:
            return VERSION_2;
        case 0b00:
            return VERSION_2_5;
        default:
            return VERSION_UNKNOWN;
    }
}

/**
 * get_mp3_layer
 * AAABBCCD -> CC
 * @return
 *         0 - Unknown
 *         1 - Layer I
 *         2 - Layer II
 *         3 - Layer III
 */
short extract_mp3_layer(const int8_t &byte) {
    auto bits = (byte >> 1)   // AAABBCCD -> AAABBCC
                & 0b11;         // AAABBCC -> CC
    /**
     * 00 - reserved
     * 01 - Layer III
     * 10 - Layer II
     * 11 - Layer I
     **/
    switch (bits) {
        case 0b11:
            return LAYER_I;
        case 0b10:
            return LAYER_II;
        case 0b01:
            return LAYER_III;
        default:
            return LAYER_UNKNOWN;
    }
}

/**
 * get_mp3_bit_rate
 * EEEEFFGH -> EEEE
 * @param version
 * @param layer
 * @param byte
 * @return bit rate
 */
short extract_mp3_bit_rate(const short &version, const short &layer, const int8_t &byte) {
    auto bits = (byte >> 4) & 0b1111;  // EEEEFFGH -> EEEE
    if (bits == 0b1111) {
        return 0;
    }
    return version == VERSION_1 ? get_mp3_bit_rate_version_1(layer, bits) : get_mp3_bit_rate_version_2_3(layer, bits);
}

short get_mp3_bit_rate_version_1(const short &layer, const int &value) {
    switch (layer) {
        case LAYER_I:
            return ((short[]) { -1, 32, 64, 96, 128, 160, 192, 224, 256, 288, 320, 352, 384, 416, 0 })[value];
        case LAYER_II:
            return ((short[]) { -1, 32, 48, 56, 64, 80, 96, 112, 160, 192, 224, 256, 320, 384, 0 })[value];
        default:
            return ((short[]) { -1, 32, 40, 48, 56, 64, 80, 96, 112, 128, 160, 192, 224, 256, 320, 0 })[value];
    }
}

short get_mp3_bit_rate_version_2_3(const short &layer, const int &value) {
    if (layer == 1) {
        return ((short[]) { -1, 32, 48, 56, 64, 80, 96, 112, 128, 144, 160, 176, 192, 224, 256, 0 })[value];
    }
    return ((short[]) { -1, 8, 16, 24, 32, 40, 48, 56, 64, 80, 96, 112, 128, 144, 160, 0 })[value];
}

/**
 * extract_mp3_sample_rate
 * EEEEFFGH -> FF
 * @param version
 * @param byte
 * @return sample rate
 */
int extract_mp3_sample_rate(const short &version, const int8_t &byte) {
    auto bits = (byte >> 2)     // EEEEFFGH -> EEEEFF
            & 0b11;             // EEEEFF -> FF

    switch (version) {
        case VERSION_1:
            return ((int[]) { 44100, 48000, 32000, 0 })[bits];
        case VERSION_2:
            return ((int[]) { 22050 , 24000, 16000, 0 })[bits];
        default:
            return ((int[]) { 11025 , 12000, 8000, 0 })[bits];
    }
}