#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>
#include <utils.h>

void fts_gl_log_debug(const char *msg, ...) {
	va_list ap;

	va_start(ap, msg);
	vfprintf(stdout, msg, ap);
	va_end(ap);
	fputc('\n', stdout);
	fflush(stdout);
}

void fts_gl_log_error(const char *msg, ...) {
	va_list ap;

	va_start(ap, msg);
	vfprintf(stderr, msg, ap);
	va_end(ap);
	fputc('\n', stderr);
	fflush(stderr);
}

void fts_gl_log_fatal(const char *msg, ...) {
	va_list ap;

	va_start(ap, msg);
	vfprintf(stderr, msg, ap);
	va_end(ap);
	fputc('\n', stderr);
	fflush(stderr);
	exit(EXIT_FAILURE);
}

void fts_gl_check_mem(void *p) {
	if (!p) fts_gl_log_fatal("Out of memory");
}
