cmake_minimum_required(VERSION 3.4.1)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_POSITION_INDEPENDENT_CODE ON)

set(C_FLAGS "-Wall -Wextra -fvisibility=hidden -fvisibility-inlines-hidden -fno-exceptions -fno-rtti -flto=thin")
set(LINKER_FLAGS "-fuse-ld=lld -flto=thin -ffixed-x18 -Wl,--hash-style=both -Wl,--unresolved-symbols=ignore-all")

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} ${C_FLAGS}")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} ${C_FLAGS}")

set(CMAKE_SHARED_LINKER_FLAGS "${CMAKE_SHARED_LINKER_FLAGS} ${LINKER_FLAGS}")
set(CMAKE_MODULE_LINKER_FLAGS "${CMAKE_MODULE_LINKER_FLAGS} ${LINKER_FLAGS}")
set(CMAKE_EXE_LINKER_FLAGS "${CMAKE_EXE_LINKER_FLAGS} ${LINKER_FLAGS}")

ENABLE_LANGUAGE(ASM)
project(blackbox)
find_package(shadowhook REQUIRED CONFIG)
# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.
include_directories(JniHook)
include_directories(Hook)
include_directories(Utils)
include_directories(./)

aux_source_directory(./ SRC1)
aux_source_directory(Utils SRC2)
aux_source_directory(Hook SRC3)
aux_source_directory(JniHook SRC4)
add_compile_options(-w)
add_library( # Sets the name of the library.
        blackbox
        # Sets the library as a shared library.
        SHARED
        # Provides a relative path to your source file(s).
        BoxCore.cpp
        ${SRC1}
        ${SRC2}
        ${SRC3}
        ${SRC4}
        )

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
        blackbox
        # Links the target library to the log library
        # included in the NDK.
        ${log-lib}
        z
        shadowhook::shadowhook
        )