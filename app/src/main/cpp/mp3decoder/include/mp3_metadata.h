#ifndef CLOUDPIECE_MP3_METADATA_H
#define CLOUDPIECE_MP3_METADATA_H

#include "mp3_label.h"

class mp3_metadata {

private:
    mp3_label *_mp3_label_ptr;
    int _bitrate;
    int _sample_rate;

public:
    void update(mp3_label *, const int &, const int &, const bool &);
    int get_bit_rate() const;
    int get_sample_rate() const;

};

mp3_metadata *get_mp3_metadata_ptr(long);

#endif //CLOUDPIECE_MP3_METADATA_H
