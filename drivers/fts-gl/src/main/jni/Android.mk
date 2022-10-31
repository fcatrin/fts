LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE    := fts-gl
LOCAL_SRC_FILES := fts_gl.cpp graphics.c utils.c libs/nanovg/nanovg.c
LOCAL_LDLIBS    += -llog -lGLESv2

LOCAL_CFLAGS += -DNANOVG_GLES2_IMPLEMENTATION -DGL_GLEXT_PROTOTYPES 
LOCAL_CFLAGS += -g -Wall -fPIC -I.
ifeq ($(TARGET_ARCH),x86)
#
else
	#LOCAL_CFLAGS +=  -marm -O3 -mfpu=neon
endif 
include $(BUILD_SHARED_LIBRARY)


