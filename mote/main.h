static const int FREQ[3] = {11, 18, 26};

static const uint8_t PTYPE_BLIND_BEACON = 1;
static const uint8_t PTYPE_ACTION_COLLECT_FREQ = 2;
static const uint8_t PTYPE_ACTION_SLEEP = 3;


static const uint8_t STATE_BOOT = 0;
static const uint8_t STATE_RUNNING = 1;


static const uint8_t MODE_ANCHOR = 0;
static const uint8_t MODE_GW = 1;
static const uint8_t MODE_BLIND = 2;


static const uint8_t TIMEOUT_BOOT = 5; //s; timeout after bootup before anything happens, so that buttons can be pressed (= modes can be changed)
static const uint16_t TIMER_BEACON = 1000; //ms; length of blind beaconing cycle
static const uint16_t TIMER_FREQ = 9000; //ms; time between channel changes sending
static const uint8_t TIMEOUT_SLEEP = 20; //s; time between last blind seen and sending of the sleep action
static const uint16_t TIMEOUT_SLEEP_TURNOVER = 1000; //ms; timeout after the action sleep change received before the mote should go to sleep
static const uint16_t TIMEOUT_SLEEP_DURATION = 10000; //ms; for how long should the motes sleep
static const uint16_t TIMEOUT_FREQ_TURNOVER = 1000; //ms; timeout after the action frequency change received before the frequency should be actually changed
static const uint16_t TIMEOUT_ERASE = 15000; //ms; the maximum time between receiving a trickle message and erasing all saved measurements
static const uint8_t TIMEOUT_COLLECT_SELF = 8; //s, maximum time between data sending to gw


struct listItem {
  void *value;
  struct listItem *next;
};

struct listHeader {
  uint8_t size;
  struct listItem *first;
  struct listItem *last;
};

struct measurementAnchor {
	uint8_t tx;
	int8_t rssi;
	uint16_t seqno;
};

struct measurementByBlind {
	uint8_t tx;
	uint8_t rx;
	int8_t rssi;
	uint16_t seqno;
};

struct measurementOfBlind {
	uint8_t tx;
	uint8_t movement;
	int8_t rssi;
	uint16_t seqno;
};

