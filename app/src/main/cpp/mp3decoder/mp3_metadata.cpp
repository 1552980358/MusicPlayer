#include "include/mp3_metadata.h"

#include <iostream>

void recycle_label_ptr(mp3_label *);

void mp3_metadata::update(mp3_label *mp3_label_ptr, const int &bitrate, const int &sample_rate, const bool &need_recycle) {
    if (need_recycle) {
        recycle_label_ptr(_mp3_label_ptr);
    }
    _bitrate = bitrate;
    _sample_rate = sample_rate;
    _mp3_label_ptr = mp3_label_ptr;
}

int mp3_metadata::get_bit_rate() const {
    return _bitrate;
}

int mp3_metadata::get_sample_rate() const {
    return _sample_rate;
}

mp3_metadata *get_mp3_metadata_ptr(long address) {
    return (mp3_metadata *) address;
}

void recycle_label_ptr(mp3_label *mp3_label_ptr) {
    mp3_label *current = mp3_label_ptr;
    mp3_label *next;
    while (current) {
        next = current->get_next();

        current->recycle();
        delete current;

        current = next;
    }
}
