CC = gcc
CFLAGS = -g -Wall -fPIC -I. -DNANOVG_GLES2_IMPLEMENTATION -DGL_GLEXT_PROTOTYPES -Wno-misleading-indentation -DFONS_USE_FREETYPE
AR = ar
ARFLAGS =
LIBS =
LDFLAGS = -shared

CFLAGS  += $(shell pkg-config --cflags freetype2)
LDFLAGS += $(shell pkg-config --libs freetype2)


LIBPREFIX = lib

OBJS += fts_gl.o \
	graphics.o \
	utils.o \
	libs/nanovg/nanovg.o

OBJDIR = ../obj

ALL_OBJS = $(addprefix $(OBJDIR)/, $(OBJS))

TARGET = ../libs/$(LIBPREFIX)fts-gl.so

UNAME_S := $(shell uname -s)
UNAME_P := $(shell uname -p)
ifeq ($(UNAME_S),Linux)
	JAVA_HOME = /usr/lib/jvm/java-8-oracle
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
	$(LD) -o $@ $(LDFLAGS) $(ALL_OBJS) $(LIBS)

clean:
	rm -f $(ALL_OBJS) 2>/dev/null
	rm -f $(TARGET) 2>/dev/null