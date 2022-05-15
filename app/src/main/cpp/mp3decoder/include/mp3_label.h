#ifndef CLOUDPIECE_MP3_LABEL_H
#define CLOUDPIECE_MP3_LABEL_H

#include <jni.h>

class mp3_label {
private:
    char _id[4];
    int8_t *_data;
    int _size;
    int16_t _flag;
    mp3_label *_prev = nullptr;
    mp3_label *_next = nullptr;

public:
    mp3_label(const char *, int8_t *, const int &, const int16_t &, mp3_label *);

    void recycle();

    int8_t *get_data();

    int get_size() const;

    mp3_label *get_prev();

    mp3_label *get_next();

    void set_next(mp3_label *);

};

#endif //CLOUDPIECE_MP3_LABEL_H
