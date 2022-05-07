#ifndef CLOUDPIECE_MP3_HEADER_H
#define CLOUDPIECE_MP3_HEADER_H

class mp3_header {

private:
    short _version;
    short _layer;
    int _bit_rate;
    int _sample_rate;

public:
    mp3_header(short, short, int, int);

    short get_version() const;

    short get_layer() const;

    int get_bit_rate() const;

    int get_sample_rate() const;

};

#endif //CLOUDPIECE_MP3_HEADER_H
