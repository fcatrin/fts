CC = gcc
LD = gcc

CFLAGS = -g -Wall -I../../..

LDFLAGS = -lm -shared -lSDL2 -lGL
LIBARCH =

LIBPREFIX = lib
LDFLAGS += -lstdc++
LDFLAGS += $(shell pkg-config --libs freetype2)

FTS_GL_DRIVER = ../../../drivers/gl

FTS_GL_LIB = $(FTS_GL_DRIVER)/libs/libfts-gl.so

OBJS = main.o \
    window.o


UNAME_S := $(shell uname -s)
UNAME_P := $(shell uname -p)
UNAME_M := $(shell uname -m)
ifeq ($(UNAME_S),Linux)
   ifeq ($(UNAME_M),armv7l) // assume RPI4
      JAVA_HOME = /usr/lib/jvm/java-11-openjdk-armhf
      LIBARCH = armv7l
      CFLAGS += -DRPI4FB -ldrm -lgbm -I/usr/include/libdrm -lEGL
      OBJS += rpi4fb.o
   else
      JAVA_HOME = /usr/lib/jvm/java-8-oracle
      ifeq ($(UNAME_P),x86_64)
	      LIBARCH = x86_64
      endif
   endif
   CFLAGS += -D LINUX -fPIC -I$(JAVA_HOME)/include/linux/
   LIBEXT = so
   ifneq ($(filter %86,$(UNAME_P)),)
   endif
endif
 
OBJDIR = ../obj
LIBSDIR = ../libs

LINK_TARGET = $(LIBSDIR)/$(LIBARCH)/$(LIBPREFIX)fts-linux.$(LIBEXT)

CFLAGS += \
 -O2 -Wall -I . -g -I$(JAVA_HOME)/include/ 
 
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
	cd $(FTS_GL_DRIVER)/jni && $(MAKE)
	cp $(FTS_GL_LIB) $(LIBSDIR)/$(LIBARCH)

clean :
	find $(OBJDIR) -name "*.o" -exec rm {} \;
	find $(LIBSDIR) -name "*.$(LIBEXT)" -exec rm {} \;
	cd $(FTS_GL_DRIVER)/jni && $(MAKE) clean
