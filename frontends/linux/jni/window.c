#include <SDL2/SDL.h>
#include <SDL2/SDL_opengl.h>
#include <GL/gl.h>

#include <utils.h>
#include <window.h>

static bool running;

static int width;
static int height;
static SDL_Window *window;

enum {
	FTS_WINDOW_EVENT = 1,
	FTS_TOUCH_EVENT
};

enum {
	FTS_WINDOW_CLOSE = 1,
	FTS_MOUSE_DOWN,
	FTS_MOUSE_UP,
	FTS_MOUSE_MOVE,
};

#define MAX_EVENT_QUEUE 100

struct event event_queue[MAX_EVENT_QUEUE];
int event_queue_index;

void window_open(const char *title, int req_width, int req_height) {
	width  = req_width;
	height = req_height;

	uint32 window_flags = SDL_WINDOW_OPENGL;
	window = SDL_CreateWindow(title, 0, 0, width, height, window_flags);
	SDL_GLContext context = SDL_GL_CreateContext(window);
}

static void push_event_window(int type) {
	struct event *event = &event_queue[event_queue_index++];
	event->family = FTS_WINDOW_EVENT;
	event->type = type;
}

static void push_event_touch(int type, int button, int x, int y) {
	struct event *event = &event_queue[event_queue_index++];
	event->family = FTS_TOUCH_EVENT;
	event->type = type;
	event->touch.button = button;
	event->touch.x = x;
	event->touch.y = y;
}

int window_process_events() {
	SDL_Event event;

	while (SDL_PollEvent(&event) && event_queue_index < MAX_EVENT_QUEUE) {
		switch (event.type) {
		case SDL_WINDOWEVENT:
			switch (event.window.event) {
				case SDL_WINDOWEVENT_CLOSE:   // exit
					push_event_window(FTS_WINDOW_CLOSE);
					break;
				default:
					break;
			}
			break;
		case SDL_MOUSEBUTTONDOWN:
			push_event_touch(FTS_MOUSE_DOWN, event.button.button, event.button.x, event.button.y);
			break;
		case SDL_MOUSEBUTTONUP:
			push_event_touch(FTS_MOUSE_UP, event.button.button, event.button.x, event.button.y);
			break;
		case SDL_MOUSEMOTION:
			push_event_touch(FTS_MOUSE_MOVE, 0, event.motion.x, event.motion.y);
			break;
		}
	}
	return event_queue_index;
}

struct event *window_get_events() {
	event_queue_index = 0;
	return event_queue;
}

void window_swap_buffers() {
   SDL_GL_SwapWindow(window);
}

void window_close() {
	running = false;
}
