#include "include/mp3_header.h"

mp3_header::mp3_header(short version, short layer, int bit_rate, int sample_rate) { // NOLINT(cppcoreguidelines-pro-type-member-init)
    update(version, layer, bit_rate, sample_rate);
}

void mp3_header::update(short version, short layer, int bit_rate, int sample_rate) {
    _version = version;
    _layer = layer;
    _bit_rate = bit_rate;
    _sample_rate = sample_rate;
}

short mp3_header::get_version() const {
    return _version;
}

short mp3_header::get_layer() const {
    return _layer;
}

int mp3_header::get_bit_rate() const {
    return _bit_rate;
}

int mp3_header::get_sample_rate() const {
    return _sample_rate;
}
