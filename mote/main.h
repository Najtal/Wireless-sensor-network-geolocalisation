static const uint8_t PTYPE_HELLO_INIT = 1;
static const uint8_t PTYPE_HELLO_BLIND = 2;
static const uint8_t PTYPE_HELLO_ANCHOR = 3;
static const uint8_t PTYPE_HELLO_ACK = 4;
static const uint8_t PTYPE_ACTION_DISCOVERY = 5;
static const uint8_t PTYPE_DISCOVERED_BLIND = 7;
static const uint8_t PTYPE_DISCOVERED_ANCHOR = 8;
static const uint8_t PTYPE_DISCOVERED_ACK = 9;

static const uint8_t PTYPE_ACTION_COLLECT = 6;
static const uint8_t PTYPE_DATA = 10;
static const uint8_t PTYPE_BLIND_BEACON = 11;

static const uint8_t STATE_BOOT = 0;
static const uint8_t STATE_COLLECTING = 1;

static const uint8_t MODE_ANCHOR = 0;
static const uint8_t MODE_GW = 1;
static const uint8_t MODE_BLIND = 2;

static const uint8_t LIMIT_FREQ = 3; //number of cycles between frequency switching
static const uint8_t LIMIT_BLIND = 3; //number of cycles without any blind data received before sending the sleep command


static const uint8_t TIMEOUT_BOOT = 5; //timeout after bootup before anything happens, so that buttons can be pressed (= modes can be changed)
static const uint8_t TIMER_CYCLE = 10; //length ofone cycle
static const uint16_t TIMER_BEACON = 1000; //ms; length of blind beaconing cycle
static const uint16_t TIMEOUT_COLLECT = 3000; //ms; lenght of collect window propagated in the ACTION_COLLECT packet
static const uint16_t TIMEOUT_COLLECT_GRACE = 1000; //ms; additional grace period to wait for responses to ACTION_COLLECT

static const uint8_t TIMEOUT_INIT = 20;
static const uint8_t TIMEOUT_INIT_ACK = 8;
static const uint16_t COLLECTION_DELAY = 500;
static const uint16_t COLLECTION_LENGTH = 1000;

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

static const uint8_t MOVEMENT_SIZE = 0; //TODO change; number of bytes taken by the movement data
struct measurementOfBlind {
	uint8_t tx;
	void *movement;
	int8_t rssi;
	uint16_t seqno;
};
