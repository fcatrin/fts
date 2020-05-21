MAKE = make

UNAME_S := $(shell uname -s)
UNAME_P := $(shell uname -p)
ifeq ($(UNAME_S),Linux)
	JAVA_HOME = /usr/lib/jvm/java-8-oracle/
	LIBEXT = so
	ifeq ($(UNAME_P),x86_64)
		LIBARCH = x86_64
	endif
	ifneq ($(filter %86,$(UNAME_P)),)
	endif
endif
 
LIBSDIR = ../libs

FTS_GL_DRIVER = ../../../drivers/gl
FTS_GL_LIB = $(FTS_GL_DRIVER)/libs/libfts-gl.so

FTS_LINUX_FRONTEND = ../../../frontends/linux
FTS_LINUX_LIB = $(FTS_LINUX_FRONTEND)/libs/${LIBARCH}/libfts-linux.so

all : prepare corebuild

prepare :
	@mkdir -p $(LIBSDIR)/$(LIBARCH)

corebuild:
	cd $(FTS_GL_DRIVER)/jni && $(MAKE)
	cp $(FTS_GL_LIB) $(LIBSDIR)/$(LIBARCH)
	cd $(FTS_LINUX_FRONTEND)/jni && $(MAKE)
	cp $(FTS_LINUX_LIB) $(LIBSDIR)/$(LIBARCH)

clean :
	find $(LIBSDIR) -name "*.$(LIBEXT)" -exec rm {} \;
	cd $(FTS_GL_DRIVER)/jni && $(MAKE) clean
	cd $(FTS_LINUX_FRONTEND)/jni && $(MAKE) clean
	