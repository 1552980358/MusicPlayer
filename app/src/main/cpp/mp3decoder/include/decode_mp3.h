#ifndef CLOUDPIECE_DECODE_MP3_H
#define CLOUDPIECE_DECODE_MP3_H

#include "mp3_metadata.h"

#include <jni.h>

#define MPEG_1 1
#define MPEG_2 2
#define MPEG_2_5 3
#define MPEG_UNKNOWN -1

#define LAYER_UNKNOWN 0
#define LAYER_I 1
#define LAYER_II 2
#define LAYER_III 3

bool is_mp3_file(const int8_t *, const int &);

long decode_mp3(const long &, const int8_t *, const int &);

#endif //CLOUDPIECE_DECODE_MP3_H
