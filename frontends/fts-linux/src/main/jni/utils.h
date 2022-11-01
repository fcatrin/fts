#ifndef _COMMON_H_
#define _COMMON_H_

#include <stdint.h>

typedef int8_t   int8;
typedef int16_t  int16;
typedef int32_t  int32;

typedef uint8_t  uint8;
typedef uint16_t uint16;
typedef uint32_t uint32;

#define true  1
#define false 0
#define bool uint8

void log_debug(const char *msg, ...);
void log_error(const char *msg, ...);
void log_fatal(const char *msg, ...);
void check_mem(void *p);

#endif
