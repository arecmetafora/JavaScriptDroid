cmake_minimum_required(VERSION 3.6)
project(library)

set(TARGET jsdroid)
set(ANDROID_DEPENDENCIES android dl log)
set(LIBS ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI})

add_library(${TARGET} SHARED
             src/main/cpp/JSCDebuggerCallback.cpp
             src/main/cpp/ConversionHelper.cpp
             src/main/cpp/ExceptionHelper.cpp
             src/main/cpp/JSCCallback.cpp
             src/main/cpp/JSJni.cpp
             src/main/cpp/Main.cpp
)

# Include the header files
include_directories(src/main/cpp)

# Add dependencies
add_library(jsc SHARED IMPORTED)
set_target_properties(jsc PROPERTIES IMPORTED_LOCATION ${LIBS}/libjs.so)
set(LIB_DEPENDENCIES jsc)


# Build the lib
target_link_libraries(${TARGET} ${LIB_DEPENDENCIES} ${ANDROID_DEPENDENCIES})