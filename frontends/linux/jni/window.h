#ifndef __WINDOW_H
#define __WINDOW_H

#define event_size_int 5

struct event_touch {
	int button;
	int x;
	int y;
};

struct event_key {
	int key_code;
};

struct event {
	int family;
	int type;
	union {
		struct event_touch touch;
		struct event_key key;
	};
};

void window_open(const char *title, int req_width, int req_height);
void window_swap_buffers();
int  window_process_events();
struct event *window_get_events();
void window_close();


#endif
