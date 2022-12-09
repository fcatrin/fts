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

void fts_log_debug(const char *msg, ...);
void fts_log_error(const char *msg, ...);
void fts_log_fatal(const char *msg, ...);
void fts_check_mem(void *p);

#endif
