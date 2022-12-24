CC = gcc
CFLAGS = -g -Wall -fPIC -I. -DNANOVG_GLES2_IMPLEMENTATION -DGL_GLEXT_PROTOTYPES -Wno-misleading-indentation -DFONS_USE_FREETYPE
AR = ar
ARFLAGS =
LIBS =
LDFLAGS = -shared
BUILD_DIR = ../../../build/jni

DEBUG_FLAGS  = -DDEBUG_FONTS
# DEBUG_FLAGS += -DDEBUG_TEXT_SIZE

CFLAGS += $(DEBUG_FLAGS)

CFLAGS  += $(shell pkg-config --cflags freetype2)
LDFLAGS += $(shell pkg-config --libs freetype2 glesv2)

LIBPREFIX = lib

OBJS += fts_gl.o \
	graphics.o \
	utils.o \
	libs/nanovg/nanovg.o

OBJDIR = $(BUILD_DIR)/obj

ALL_OBJS = $(addprefix $(OBJDIR)/, $(OBJS))

TARGET = $(BUILD_DIR)/libs/$(LIBPREFIX)fts-gl.so

UNAME_S := $(shell uname -s)
UNAME_M := $(shell uname -m)
ifeq ($(UNAME_S),Linux)
	ifeq ($(UNAME_M),armv7l)
	    JAVA_HOME = /usr/lib/jvm/java-11-openjdk-armhf
	else
	    JAVA_HOME = /usr/lib/jvm/java-8-oracle
	endif
	CFLAGS += -D LINUX -I$(JAVA_HOME)/include  -I$(JAVA_HOME)/include/linux
endif

all: $(TARGET)

$(OBJDIR)/%.o: %.cpp
	@mkdir -p $(dir $@) 2> /dev/null 
	$(CC) -c -o $@ -I. $(CFLAGS) $<

$(OBJDIR)/%.o: %.c
	@mkdir -p $(dir $@) 2> /dev/null 
	$(CC) -c -o $@ -I. $(CFLAGS) $<
	
	
$(TARGET): $(ALL_OBJS)
	@mkdir -p $(dir $(TARGET)) 2> /dev/null
	$(LD) -o $@ $(LDFLAGS) $(ALL_OBJS) $(LIBS)

clean:
	rm -f $(ALL_OBJS) 2>/dev/null
	rm -f $(TARGET) 2>/dev/null