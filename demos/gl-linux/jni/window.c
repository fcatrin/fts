#include <SDL2/SDL.h>
#include <SDL2/SDL_opengl.h>
#include <GL/gl.h>

#include <utils.h>

static bool running;

static int width;
static int height;
static SDL_Window *window;

void window_open(int req_width, int req_height) {
	width  = req_width;
	height = req_height;

	uint32 window_flags = SDL_WINDOW_OPENGL;
	window = SDL_CreateWindow("FTS GL Demo", 0, 0, width, height, window_flags);
	SDL_GLContext context = SDL_GL_CreateContext(window);
}

static int process_events() {
	SDL_Event event;
	while (SDL_PollEvent(&event)) {
		switch (event.type) {
		case SDL_WINDOWEVENT:
			switch (event.window.event) {
				case SDL_WINDOWEVENT_CLOSE:   // exit
					return false;
					break;
				default:
					break;
			}
			break;
		}
	}
	return true;
}


int window_swap_buffers() {
   SDL_GL_SwapWindow(window);
   return process_events();
}

void window_close() {
	running = false;
}
