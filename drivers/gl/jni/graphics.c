#include <stdio.h>
#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>
#include <libs/nanovg/nanovg.h>
#include <libs/nanovg/nanovg_gl.h>
#include "utils.h"

static struct NVGcontext* vg;

static int r, g, b, a;

void graphics_init() {
	vg = nvgCreateGLES2(NVG_ANTIALIAS | NVG_STENCIL_STROKES | NVG_DEBUG);
	if (!vg) {
		fts_gl_log_error("Could not init nanovg ");
	}
}

void graphics_frame_begin(int width, int height) {
	nvgBeginFrame(vg, width, height, 1.0f);
}

void graphics_frame_end() {
	nvgEndFrame(vg);
}

void graphics_set_color(int red, int green, int blue, int alpha) {
	r = red;
	g = green;
	b = blue;
	a = alpha;
}

void graphics_draw_filled_rect(int x, int y, int width, int height, int radius) {
	nvgBeginPath(vg);
	nvgRect(vg, x, y, width, height);
	nvgFillColor(vg, nvgRGBA(r, g, b, a));
	nvgFill(vg);
}

void graphics_draw_rect(int x, int y, int width, int height, int radius) {
}

void graphics_done() {

}
