USE_FB = false
BASE_DIR = ../../..
BUILD_DIR = $(BASE_DIR)/build/jni

CC = gcc
LD = gcc

CFLAGS = -O2 -g -Wall -I . -I../../..
CFLAGS += $(shell pkg-config --cflags SDL2_image)

LDFLAGS = -lm -shared -lSDL2 -lGL
LIBARCH =

LIBPREFIX = lib
LDFLAGS += -lstdc++
LDFLAGS += $(shell pkg-config --libs freetype2)
LDFLAGS += $(shell pkg-config --libs SDL2_image)

FTS_GL_DRIVER = $(BASE_DIR)/../../drivers/fts-gl/build/jni

FTS_GL_LIB = $(FTS_GL_DRIVER)/libs/libfts-gl.so

OBJS = main.o \
    window.o \
    utils.o

UNAME_S := $(shell uname -s)
UNAME_M := $(shell uname -m)
ifeq ($(UNAME_S),Linux)
   ifeq ($(UNAME_M),armv7l)
      JAVA_HOME = /usr/lib/jvm/java-11-openjdk-armhf
      LIBARCH = armv7l
      ifeq ($(USE_FB),true)
      	CFLAGS += -DRPI4FB -I/usr/include/libdrm
      	LDFLAGS += -ldrm -lgbm -lEGL
      	OBJS += rpi4fb.o
      endif
   else
      JAVA_HOME = /usr/lib/jvm/java-8-oracle
      ifeq ($(UNAME_M),x86_64)
	      LIBARCH = x86_64
      endif
   endif
   CFLAGS += -D LINUX -fPIC -I$(JAVA_HOME)/include/linux/
   LIBEXT = so
   ifneq ($(filter %86,$(UNAME_M)),)
   endif
endif
 
OBJDIR = $(BUILD_DIR)/obj
LIBSDIR = $(BUILD_DIR)/libs

LINK_TARGET = $(LIBSDIR)/$(LIBARCH)/$(LIBPREFIX)fts-linux.$(LIBEXT)

CFLAGS += -I$(JAVA_HOME)/include/ 
 
CFLAGS_ONLY = -std=gnu99
  
ALL_OBJS = $(addprefix $(OBJDIR)/, $(OBJS))
		
$(OBJDIR)/%.o: %.c 
	$(CC) -c $(CFLAGS) $(CFLAGS_ONLY) -o $@ $< 

$(OBJDIR)/%.o: %.cpp
	$(CC) -c $(CFLAGS) -o $@ $< 

all : prepare corebuild $(FTS_GL_LIB)  $(LINK_TARGET)

$(LINK_TARGET) : $(ALL_OBJS)
	$(LD) -o $@ $^ $(LDFLAGS) 

prepare :
	@mkdir -p $(LIBSDIR)/$(LIBARCH)
	@mkdir -p $(OBJDIR)

corebuild:
	cp $(FTS_GL_LIB) $(LIBSDIR)/$(LIBARCH)

clean :
	find $(OBJDIR) -name "*.o" -exec rm {} \;
	find $(LIBSDIR) -name "*.$(LIBEXT)" -exec rm {} \;
