#include <jni.h>
#include "include/decode_mp3.h"
#include <iostream>

bool has_id3_tag(const int8_t &byte0, const int8_t &byte1, const int8_t &byte2);

int id3_version(const int8_t *);
int id3_revision(const int8_t *);
int id3_flag(const int8_t *);

int label_frame_size(const int8_t *);
mp3_label *extract_label_frame(int &, int &, const int8_t *, mp3_label *);
void audio_frame_sync(int &);
int8_t audio_frame_version(const int &, const int8_t *);
int8_t audio_frame_layer(int &, const int8_t *);
int audio_frame_bitrate(int &, const int8_t *, const uint8_t &, const uint8_t &);
int audio_frame_sample_rate(const uint8_t &, const int8_t *, const uint8_t &);

bool is_mp3_file(const int8_t *file_byte_array, const int &size) {
    if (size < 10) {
        return false;
    }
    return has_id3_tag(file_byte_array[0], file_byte_array[1], file_byte_array[2]);
}

long decode_mp3(const long &ptr, const int8_t *mp3_byte_array, const int &size) {
    auto version = id3_version(mp3_byte_array);
    auto revision = id3_revision(mp3_byte_array);
    auto flag = id3_flag(mp3_byte_array);

    int frame_size = label_frame_size(mp3_byte_array);
    int frame_offset_index = 10;

    mp3_label *mp3_label_ptr = nullptr;
    while (frame_size) {
        mp3_label_ptr = extract_label_frame(frame_offset_index, frame_size, mp3_byte_array,mp3_label_ptr);
    }

    audio_frame_sync(frame_offset_index);

    auto frame_version = audio_frame_version(frame_offset_index, mp3_byte_array);
    auto frame_layer = audio_frame_layer(frame_offset_index, mp3_byte_array);
    auto frame_bitrate = audio_frame_bitrate(frame_offset_index, mp3_byte_array, frame_version, frame_layer);
    auto frame_sample_rate = audio_frame_sample_rate(frame_offset_index, mp3_byte_array, frame_version);

    auto *mp3_metadata_ptr = (mp3_metadata *) ptr;
    if (!ptr) {
        mp3_metadata_ptr = new mp3_metadata();
    }
    mp3_metadata_ptr->update(mp3_label_ptr, frame_bitrate, frame_sample_rate, true);
    return ptr;
}

bool has_id3_tag(const int8_t &byte0, const int8_t &byte1, const int8_t &byte2) {
    return (byte0 == 'I') && (byte1 == 'D') && (byte2 == '3');
}

int id3_version(const int8_t *mp3_byte_array) {
    return mp3_byte_array[3];
}

int id3_revision(const int8_t *mp3_byte_array) {
    return mp3_byte_array[4];
}

int id3_flag(const int8_t *mp3_byte_array) {
    return mp3_byte_array[5];
}

int label_frame_size(const int8_t *mp3_byte_array) {
    return (mp3_byte_array[6] & 0x7F << 21) | (mp3_byte_array[7] & 0x7F << 14) | (mp3_byte_array[8] & 0x7F << 7) | (mp3_byte_array[9] & 0x7F);
}

mp3_label *extract_label_frame(int &offset, int &read_frame_size, const int8_t *mp3_byte_array, mp3_label *mp3_label_ptr) {
    char label[4];
    label[0] = mp3_byte_array[offset++];
    label[1] = mp3_byte_array[offset++];
    label[2] = mp3_byte_array[offset++];
    label[3] = mp3_byte_array[offset++];

    int frame_size = ((mp3_byte_array[offset++] & 0xFF) << 24);
    frame_size |= ((mp3_byte_array[offset++] & 0xFF) << 16);
    frame_size |= ((mp3_byte_array[offset++] & 0xFF) << 8);
    frame_size |= (mp3_byte_array[offset++] & 0xFF);

    auto flag = (int16_t) (mp3_byte_array[offset++] << 8);
    flag |= mp3_byte_array[offset++]; // NOLINT(cppcoreguidelines-narrowing-conversions)

    auto data = (int8_t *) malloc(frame_size);
    for (int i = 0; i < frame_size; ++i) {
        data[i] = mp3_byte_array[offset++];
    }

    return new mp3_label((char *) &label, data, frame_size, flag, mp3_label_ptr);
}

void audio_frame_sync(int &offset) {
    offset += 2;
}

int8_t audio_frame_version(const int &offset, const int8_t *mp3_byte_array) {
    switch ((mp3_byte_array[offset] >> 3) & 0x03) {
        case 0x0:
            return MPEG_2_5;
        case 0x2:
            return MPEG_2;
        case 0x3:
            return MPEG_1;
        case 0x1:
        default:
            return MPEG_UNKNOWN;
    }
}

int8_t audio_frame_layer(int &offset, const int8_t *mp3_byte_array) {
    switch ((mp3_byte_array[offset++] >> 1) & 0x03) {
        case 0x1:
            return LAYER_III;
        case 2:
            return LAYER_II;
        case 3:
            return LAYER_I;
        case 0:
        default:
            return LAYER_UNKNOWN;
    }
}

const int bitrate_mpeg1_layer1[] = {-1, 32, 64, 96, 128, 160, 192, 224, 256, 288, 320, 352, 384, 416, 448, 0 };
const int bitrate_mpeg1_layer2[] = {-1, 32, 48, 56, 64, 80, 96, 112, 128, 160, 192, 224, 256, 320, 384, 0 };
const int bitrate_mpeg1_layer3[] = {-1, 32, 40, 48, 56, 64, 80, 96, 112, 128, 160, 192, 224, 256, 320, 0 };
const int bitrate_mpeg2_layer1[] = {-1, 32, 48, 56, 64, 80, 96, 112, 128, 144, 160, 176, 192, 224, 256, 0 };
const int bitrate_mpeg2_layer2_3[] = {-1, 8, 16, 24, 32, 40, 48, 56, 64, 80, 96, 112, 128, 144, 160, 0 };

int audio_frame_bitrate(int &offset, const int8_t *mp3_byte_array, const uint8_t &version, const uint8_t &layer) {
    auto bit_pattern = (mp3_byte_array[offset] >> 4) & 0xF;
    switch (version) {
        case MPEG_2:
        case MPEG_2_5:
            switch (layer) {
                case LAYER_II:
                case LAYER_III:
                    return bitrate_mpeg2_layer2_3[bit_pattern];
                case LAYER_I:
                default:
                    return bitrate_mpeg2_layer1[bit_pattern];
            }
        case MPEG_1:
        default:
            switch (layer) {
                case LAYER_II:
                    return bitrate_mpeg1_layer2[bit_pattern];
                case LAYER_III:
                    return bitrate_mpeg1_layer3[bit_pattern];
                case LAYER_I:
                default:
                    return bitrate_mpeg1_layer1[bit_pattern];
            }
    }
}

const int sample_rate_mpeg1[] = { 44100, 48000, 32000 };
const int sample_rate_mpeg2[] = { 22050, 24000, 16000 };
const int sample_rate_mpeg2_5[] = { 11025, 12000, 8000 };

int audio_frame_sample_rate(const uint8_t &offset, const int8_t *mp3_byte_array, const uint8_t &version) {
    auto bit_pattern = (mp3_byte_array[offset] >> 2) & 0x3;
    switch (version) {
        case MPEG_2:
            return sample_rate_mpeg2[bit_pattern];
        case MPEG_2_5:
            return sample_rate_mpeg2_5[bit_pattern];
        case MPEG_1:
        default:
            return sample_rate_mpeg1[bit_pattern];
    }
}