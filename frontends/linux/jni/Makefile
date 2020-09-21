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

UNAME_S := $(shell uname -s)
UNAME_P := $(shell uname -p)
ifeq ($(UNAME_S),Linux)
	JAVA_HOME = /usr/lib/jvm/java-8-oracle/
	CFLAGS += -D LINUX -fPIC -I$(JAVA_HOME)/include/linux/
	LIBEXT = so
	ifeq ($(UNAME_P),x86_64)
		LIBARCH = x86_64
	endif
	ifneq ($(filter %86,$(UNAME_P)),)
	endif
endif
 
OBJDIR = ../obj
LIBSDIR = ../libs

LINK_TARGET = $(LIBSDIR)/$(LIBARCH)/$(LIBPREFIX)fts-linux.$(LIBEXT)

CFLAGS += \
 -O2 -Wall -I . -g -I$(JAVA_HOME)/include/ 
 
CFLAGS_ONLY = -std=gnu99
  
OBJS = $(addprefix $(OBJDIR)/, \
	main.o \
	window.o \
	)
		
$(OBJDIR)/%.o: %.c 
	$(CC) -c $(CFLAGS) $(CFLAGS_ONLY) -o $@ $< 

$(OBJDIR)/%.o: %.cpp
	$(CC) -c $(CFLAGS) -o $@ $< 

all : prepare corebuild $(FTS_GL_LIB)  $(LINK_TARGET)

$(LINK_TARGET) : $(OBJS)
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
