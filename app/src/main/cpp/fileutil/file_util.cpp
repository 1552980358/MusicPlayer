#include "file_util.h"

void write_byte_array(const char *path, jbyte *data, const int &size) {
    auto file = fopen(path, "wb+");
    fwrite(data, sizeof(jbyte), size * sizeof(jbyte), file);
    fflush(file);
    fclose(file);
}