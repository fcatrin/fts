#ifndef __GRAPHICS_H
#define __GRAPHICS_H

void graphics_init();
void graphics_done();
void graphics_frame_begin(int width, int height);
void graphics_frame_end();

void graphics_view_start(int x, int y, int width, int height);
void graphics_view_end();

void graphics_set_color(int red, int green, int blue, int alpha);
void graphics_set_color_start(int red, int green, int blue, int alpha);
void graphics_set_color_end(int red, int green, int blue, int alpha);

void graphics_draw_text(int x, int y, const char *text);
void graphics_draw_rect(int x, int y, int width, int height, int radius, int strokeWidth);
void graphics_draw_filled_rect(int x, int y, int width, int height, int radius);
void graphics_draw_gradient_rect(int x, int y, int width, int height, int raius, int angle);
void graphics_draw_line(int x, int y, int dx, int dy);

bool graphics_create_font(const char *alias, const char *path);
void graphics_set_font_size(int size);
void graphics_set_font_name(const char *name);
int* graphics_get_text_size(const char *text);

int  graphics_backbuffer_create(int width, int height);
void graphics_backbuffer_destroy(int handle);
void graphics_backbuffer_bind(int handle);
void graphics_backbuffer_unbind(int handle);
void graphics_backbuffer_draw(int handle, int x, int y, int width, int height);


#endif
