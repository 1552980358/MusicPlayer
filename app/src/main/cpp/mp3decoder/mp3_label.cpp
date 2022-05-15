#include "include/mp3_label.h"

#include <iostream>

mp3_label::mp3_label(const char *id, int8_t *data, const int &size, const int16_t &flag, mp3_label *prev) {
    strcpy((char *) &_id, id);
    _data = data;
    _size = size;
    _prev = prev;
    _flag = flag;
    if (prev) {
        prev->set_next(this);
    }
}

void mp3_label::recycle() {
    free(_data);
}

int8_t *mp3_label::get_data() {
    return _data;
}

int mp3_label::get_size() const {
    return _size;
}

mp3_label *mp3_label::get_prev() {
    return _prev;
}

mp3_label *mp3_label::get_next() {
    return _next;
}

void mp3_label::set_next(mp3_label *next) {
    _next = next;
}
