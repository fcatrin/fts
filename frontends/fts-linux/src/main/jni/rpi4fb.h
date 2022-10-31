#ifndef _RPI4FB_H_
#define _RPI4FB_H_

int  rpi4fb_init(int width, int height);
void rpi4fb_swap_buffers();
void rpi4fb_done();

#endif
