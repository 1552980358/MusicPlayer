cmake_minimum_required(VERSION 3.18.1)

project("player")

add_subdirectory(mp3decoder)

find_library(log-lib log)

target_link_libraries(mp3decoder ${log-lib})