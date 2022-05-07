#ifndef CLOUDPIECE_DECODE_MP3_H
#define CLOUDPIECE_DECODE_MP3_H

#include <jni.h>
#include "mp3_header.h"

#define VERSION_UNKNOWN 0
#define VERSION_1 1
#define VERSION_2 2
#define VERSION_2_5 3

#define LAYER_UNKNOWN 0
#define LAYER_I 1
#define LAYER_II 2
#define LAYER_III 3

bool is_mp3_file(const int8_t *);

long decode_mp3(const int8_t *mp3_byte_array);

#endif //CLOUDPIECE_DECODE_MP3_H
