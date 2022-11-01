#include <SDL2/SDL.h>
#include <SDL2/SDL_opengl.h>
#include <GL/gl.h>

#include "utils.h"
#include "window.h"

#ifdef RPI4FB
#include "rpi4fb.h"
#else
static bool running;
static int width;
static int height;
static SDL_Window *window;
#endif

enum {
	FTS_WINDOW_EVENT = 1,
	FTS_TOUCH_EVENT,
	FTS_KEY_EVENT
};

enum {
	FTS_WINDOW_CLOSE = 1,
	FTS_MOUSE_DOWN,
	FTS_MOUSE_UP,
	FTS_MOUSE_MOVE,
	FTS_KEY_DOWN,
	FTS_KEY_UP
};

#define NATIVE_FLAGS_BORDERLESS    1
#define NATIVE_FLAGS_CENTER        2
#define NATIVE_FLAGS_FULLSCREEN    4
#define NATIVE_FLAGS_CAN_RESIZE    8
#define NATIVE_FLAGS_CAN_MINIMIZE 16
#define NATIVE_FLAGS_CAN_MAXIMIZE 32


#define MAX_EVENT_QUEUE 100

struct event event_queue[MAX_EVENT_QUEUE];
int event_queue_index;


void window_open(const char *title, int x, int y, int req_width, int req_height, int req_flags) {

#ifdef RPI4FB
	rpi4fb_init(req_width, req_height);
#else

	width  = req_width;
	height = req_height;

	uint32 window_flags = SDL_WINDOW_OPENGL;
	if (req_flags & NATIVE_FLAGS_BORDERLESS)   window_flags |= SDL_WINDOW_BORDERLESS;
	if (req_flags & NATIVE_FLAGS_FULLSCREEN)   window_flags |= SDL_WINDOW_FULLSCREEN;
	if (req_flags & NATIVE_FLAGS_CAN_RESIZE)   window_flags |= SDL_WINDOW_RESIZABLE;
	if (req_flags & NATIVE_FLAGS_CAN_MINIMIZE) window_flags |= SDL_WINDOW_MINIMIZED;
	if (req_flags & NATIVE_FLAGS_CAN_MAXIMIZE) window_flags |= SDL_WINDOW_MAXIMIZED;

	if (req_flags & NATIVE_FLAGS_CENTER) {
		x = SDL_WINDOWPOS_CENTERED;
		y = SDL_WINDOWPOS_CENTERED;
	}

	window = SDL_CreateWindow(title, x, y, width, height, window_flags);
	// SDL_GLContext context =
	SDL_GL_CreateContext(window);
#endif
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

static void push_event_key(int type, SDL_KeyboardEvent *key) {
	struct event *event = &event_queue[event_queue_index++];
	event->family = FTS_KEY_EVENT;
	event->type = type == SDL_KEYDOWN ? FTS_KEY_DOWN : FTS_KEY_UP;
	event->key.code = key->keysym.sym;
	event->key.modifier = key->keysym.mod;
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
		case SDL_KEYDOWN:
		case SDL_KEYUP:
			push_event_key(event.type, &event.key);
		}
	}
	return event_queue_index;
}

struct event *window_get_events() {
	event_queue_index = 0;
	return event_queue;
}

void window_swap_buffers() {
#ifdef RPI4FB
	rpi4fb_swap_buffers();
#else
	SDL_GL_SwapWindow(window);
#endif
}

void window_close() {
#ifdef RPI4FB
	rpi4fb_done();
#else
	running = false;
#endif
}
