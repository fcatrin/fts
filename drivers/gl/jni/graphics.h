#ifndef __GRAPHICS_H
#define __GRAPHICS_H

void graphics_init();
void graphics_done();
void graphics_frame_begin(int width, int height);
void graphics_frame_end();

void graphics_set_color(int red, int green, int blue, int alpha);
void graphics_draw_rect(int x, int y, int width, int height, int radius);
void graphics_draw_filled_rect(int x, int y, int width, int height, int radius);

#endif
