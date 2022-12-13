#ifndef __WINDOW_H
#define __WINDOW_H

#define event_size_int 5

struct event_touch {
	int button;
	int x;
	int y;
};

struct event_key {
	int code;
	int modifier;
};

struct event {
	int family;
	int type;
	union {
		struct event_touch touch;
		struct event_key key;
	};
};

void window_open(const char *title, int req_x, int req_y, int req_width, int req_height, int req_flags);
void window_swap_buffers();
int  window_process_events();
struct event *window_get_events();
void window_set_icon(void *data, int size);
void window_close();


#endif
