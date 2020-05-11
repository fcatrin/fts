#ifndef __GRAPHICS_H
#define __GRAPHICS_H

void graphics_init();
void graphics_done();
void graphics_frame_begin(int width, int height);
void graphics_frame_end();

void graphics_set_color(int red, int green, int blue, int alpha);
void graphics_draw_text(int x, int y, const char *text);
void graphics_draw_rect(int x, int y, int width, int height, int radius);
void graphics_draw_filled_rect(int x, int y, int width, int height, int radius);

bool graphics_create_font(const char *alias, const char *path);
void graphics_set_font_size(int size);
void graphics_set_font_name(const char *name);
int* graphics_get_text_size(const char *text);

#endif
