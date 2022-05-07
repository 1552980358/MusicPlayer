#ifndef CLOUDPIECE_MP3_HEADER_PTR_CONVERT_H
#define CLOUDPIECE_MP3_HEADER_PTR_CONVERT_H

#include "mp3_header.h"

short get_mp3_version(long address) {
    return get_mp3_header_ptr(address)->get_version();
}

short get_mp3_layer(long address) {
    return get_mp3_header_ptr(address)->get_layer();
}

int get_mp3_bit_rate(long address) {
    return get_mp3_header_ptr(address)->get_bit_rate();
}

int get_mp3_sample_rate(long address) {
    return get_mp3_header_ptr(address)->get_sample_rate();
}

#endif //CLOUDPIECE_MP3_HEADER_PTR_CONVERT_H
