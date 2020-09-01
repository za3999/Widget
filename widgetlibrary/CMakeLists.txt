
cmake_minimum_required(VERSION 3.4.1)

set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${PROJECT_SOURCE_DIR}/../jniLibs/${ANDROID_ABI})
add_library(
        jni_image
        SHARED
        src/main/cpp/image.cpp)

find_library(
        log-lib
        log)


target_link_libraries(
        jni_image
        ${log-lib})