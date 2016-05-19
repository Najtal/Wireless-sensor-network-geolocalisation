/**
 * \file
 *         asdf
 * \author
 *         asdf
 */
#include <stdlib.h>
#include <stdio.h>

#include "main.h"
#include "random.h"

#include "contiki.h"
#include "net/rime/rime.h"
#include "net/rime/collect.h"
#include "sys/etimer.h"
#include "sys/ctimer.h"
#include "dev/button-sensor.h"
#include "dev/leds.h"
#include "dev/adxl345.h"
#include "dev/cc2420/cc2420.c"
//#include "platform/z1/contiki-z1-main.c"

#define MAX(x, y) (((x) > (y)) ? (x) : (y))
#define MIN(x, y) (((x) < (y)) ? (x) : (y))
#define ABS(x) (((x) < 0) ? ((x) * (-1)) : (x))

static uint8_t state = 0; //default state is booting
static uint8_t mode = 0; //default mode anchor

static uint16_t broadcast_seqno = 0;
static uint8_t last_freq = 0; //index of the last frequency

static struct broadcast_conn broadcast;
static struct collect_conn tc;
static struct trickle_conn trickle;

static struct ctimer ctBeacon; //blind timer for beaconing
static struct ctimer ctFreq; //grace timer between change freq action received and its actual changing
static struct ctimer ctErase; //blind timer after which all measurements are erased
static struct ctimer ctCollect; //anchor timer for maximum time between sending data to GW
static struct ctimer ctFreqFlood; //sending of freq change
static struct ctimer ctSleepFlood; //time between last blind seen and flooding the sleep action
static struct ctimer ctSleepTurnover; //grace timer for grace period before going to sleep
static struct ctimer ctWakeUp; //timer for waking up

static struct listHeader *measurementsAnchor; //anchor to anchor measuremetns
static struct listHeader *measurementsByBlind; //anchor to blind measurements (contain sender id)
static struct listHeader *measurementsOfBlind; //blind to anchor measurements (contain movement data)

/*######################################################################################################################*/
/*######################################################################################################################*/
/*######################################################################################################################*/
/*                                                  CTIMERS CALLBACKS                                                   */
/*######################################################################################################################*/
/*######################################################################################################################*/
/*######################################################################################################################*/
static void changeFrequency(uint8_t freq){
  printf("### CHANNEL ### - Changing the channel to: %d\n", freq);
  cc2420_set_channel(freq);
}

static void erase(){
  struct listItem *next = measurementsAnchor->first;
  int j;
  for (j = 0; j < measurementsAnchor->size; j++){
    struct listItem *temp = next->next;
    free((struct measurementAnchor*)next->value);
    free(next);
    next = temp;            
    if (next == NULL){
      break; //just in case
    }
  }
  measurementsAnchor->size = 0;

  next = measurementsOfBlind->first;
  for (j = 0; j < measurementsOfBlind->size; j++){
    struct listItem *temp = next->next;
    free((struct measurementOfBlind*)next->value);
    free(next);
    next = temp;            
    if (next == NULL){
      break; //just in case
    }
  }
  measurementsOfBlind->size = 0;

  next = measurementsByBlind->first;
  for (j = 0; j < measurementsByBlind->size; j++){
    struct listItem *temp = next->next;
    free((struct measurementByBlind*)next->value);
    free(next);
    next = temp;            
    if (next == NULL){
      break; //just in case
    }
  }
  measurementsByBlind->size = 0;

  ctimer_set(&ctErase, CLOCK_SECOND*(((float)TIMEOUT_ERASE)/1000), erase, NULL); //set timer so that lists are erased cyclicly after some time
  printf("### MEASUREMENT ### - cleared all saved measurements\n");
}

static void beacon(){
  uint8_t *values;
  int len = (5 + 4*measurementsAnchor->size);
  values = malloc(len*sizeof(uint8_t));
  values[0] = broadcast_seqno >> 8;
  values[1] = broadcast_seqno;

  //TODO modify movement data
  uint16_t x, y, z;
  uint8_t movement;
  x = ABS(adxl345.value(X_AXIS));
  y = ABS(adxl345.value(Y_AXIS));
  z = ABS(adxl345.value(Z_AXIS));
  movement = MAX(MAX(x,y),z) >> 8;

  values[2] = movement;
  values[3] = measurementsAnchor->size; //number of anchor to this blind rssi measurements
  int i = 3;
  struct listItem *next = measurementsAnchor->first;
  int j = 0;
  for (j; j < measurementsAnchor->size; j++){
    values[++i] = ((struct measurementAnchor*)next->value)->tx; //Transmitter ID
    values[++i] = ((struct measurementAnchor*)next->value)->rssi; //the RSSI values
    values[++i] = ((struct measurementAnchor*)next->value)->seqno >> 8; //first byte of the sequence number
    values[++i] = ((struct measurementAnchor*)next->value)->seqno; //second byte of the sequence number
    next = next->next;            
    if (next == NULL){
      break; //just in case
    }
  }

  printf("### BEACON ### - blind sending beacon with seqno: %d\n", broadcast_seqno);
  for (i = 0; i < len; i++){
    printf("|   data[%d]: %d\n",i, values[i]);
  }

  packetbuf_copyfrom(values, len*sizeof(uint8_t));
  broadcast_send(&broadcast);
  broadcast_seqno++;
  ctimer_reset(&ctBeacon);
  free(values);
}

static void sendDataToGW(){
  uint8_t *values;
  int len = (4 + 4*measurementsAnchor->size + 5*measurementsByBlind->size + 5*measurementsOfBlind->size);
  //int len = (3 + 4*measurementsAnchor->size + 5*measurementsByBlind->size /*+  5*measurementsOfBlind->size*/);
  //int len = (4 + 4*measurementsAnchor->size + 5*measurementsByBlind->size + (4+MOVEMENT_SIZE)*measurementsOfBlind->size);
  values = malloc(len*sizeof(uint8_t));

  /////////////////////////////////////////////////////////////the ANCHOR to ANCHOR measurements
  values[0] = measurementsAnchor->size; //number of anchor to anchor measurements
  int i = 0;
  struct listItem *next = measurementsAnchor->first;
  int j;
  for (j = 0; j < measurementsAnchor->size; j++){
    values[++i] = ((struct measurementAnchor*)next->value)->tx; //Transmitter ID
    values[++i] = ((struct measurementAnchor*)next->value)->rssi; //the RSSI values
    values[++i] = ((struct measurementAnchor*)next->value)->seqno >> 8; //first byte of the sequence number
    values[++i] = ((struct measurementAnchor*)next->value)->seqno; //second byte of the sequence number
    next = next->next;            
    if (next == NULL){
      break; //just in case
    }
  }

  /////////////////////////////////////////////////////////////the ANCHOR to BLIND measurements
  values[++i] = measurementsByBlind->size;
  next = measurementsByBlind->first;
  for (j = 0; j < measurementsByBlind->size; j++){
    values[++i] = ((struct measurementByBlind*)next->value)->tx; //Transmitter ID
    values[++i] = ((struct measurementByBlind*)next->value)->rx; //Receiver ID
    values[++i] = ((struct measurementByBlind*)next->value)->rssi; //the RSSI values
    values[++i] = ((struct measurementByBlind*)next->value)->seqno >> 8; //first byte of the sequence number
    values[++i] = ((struct measurementByBlind*)next->value)->seqno; //second byte of the sequence number
    next = next->next;            
    if (next == NULL){
      break; //just in case
    }
  }

  /////////////////////////////////////////////////////////////the BLIND to ANCHOR measurements
  //values[++i] = MIN(measurementsOfBlind->size, 4);
  values[++i] = measurementsOfBlind->size;
  next = measurementsOfBlind->first;
  for (j = 0; j < measurementsOfBlind->size; j++){
  //for (j = 0; j < MIN(measurementsOfBlind->size, 4); j++){
    values[++i] = ((struct measurementOfBlind*)next->value)->tx; //Transmitter ID
    values[++i] = ((struct measurementOfBlind*)next->value)->movement; //Transmitter ID
    values[++i] = ((struct measurementOfBlind*)next->value)->rssi; //the RSSI values
    values[++i] = ((struct measurementOfBlind*)next->value)->seqno >> 8; //first byte of the sequence number
    values[++i] = ((struct measurementOfBlind*)next->value)->seqno; //second byte of the sequence number
    next = next->next;            
    if (next == NULL){
      break; //just in case
    }
  }

  printf("### COLLECT ### - sending data to GW; A->A: %d, A->B: %d, B->A: %d\n", measurementsAnchor->size, measurementsByBlind->size, measurementsOfBlind->size);
  for (i = 0; i < len; i++){
    printf("|   data[%d]: %d\n",i, values[i]);
  }

  packetbuf_clear();
  packetbuf_copyfrom(values, len*sizeof(uint8_t));
  collect_send(&tc, 3);
  //collect_send(&tc, 15);
  free(values);
  erase();
  ctimer_set(&ctCollect, CLOCK_SECOND*(TIMEOUT_COLLECT_SELF), sendDataToGW, NULL);
}

static void checkForSending(){
  //check if data to be sent is bigger than 35B
  if ((4 + 4*measurementsAnchor->size + 5*measurementsByBlind->size + 5*measurementsOfBlind->size) > 35){
    sendDataToGW();
  }
}

static void wakeUp(){
  //TODO wake up
  printf("### SLEEP ### - waking up, good morning!\n");
}

static void sleep(){
  //TODO go to sleep
  printf("### SLEEP ### - going to sleep\n");
}

static void sendFreqChange(){
  uint8_t *values;
  uint8_t len = 6;
  last_freq = (last_freq+1) % 3;
  printf("### TRICKLE ### - sending ACTION_COLLECT_FREQ with seqno: %d\n", broadcast_seqno);
  //TODO printf("CON,type:FREQ,seqno:%d,freq:%d,grace:%d\n", broadcast_seqno, FREQ[last_freq], TIMEOUT_FREQ_TURNOVER);

  values = malloc(len*sizeof(uint8_t));
  values[0] = PTYPE_ACTION_COLLECT_FREQ;
  values[1] = broadcast_seqno >> 8;
  values[2] = broadcast_seqno;
  values[3] = last_freq;
  values[4] = TIMEOUT_FREQ_TURNOVER >> 8;
  values[5] = TIMEOUT_FREQ_TURNOVER;


  int i;
  for (i = 0; i < len; i++){
    printf("|   data[%d]: %d\n",i, values[i]);
  }

  packetbuf_copyfrom(values, len*sizeof(uint8_t));
  trickle_send(&trickle);
  broadcast_seqno++;

  ctimer_set(&ctFreq, CLOCK_SECOND*(((float)TIMEOUT_FREQ_TURNOVER)/1000), changeFrequency, FREQ[last_freq]); //set timer for next frequency change sending
  ctimer_set(&ctFreqFlood, CLOCK_SECOND*(((float)TIMER_FREQ)/1000), sendFreqChange, NULL); //set timer to change the frequency
  free(values);
}

static void sendSleep(){
    uint8_t *values;
  uint8_t len = 7;
  printf("### TRICKLE ### - sending ACTION_SLEEP with seqno: %d\n", broadcast_seqno);
  //TODO printf("CON,type:SLEEP,seqno:%d,duration:%d,grace:%d\n", broadcast_seqno, TIMEOUT_SLEEP_DURATION, TIMEOUT_SLEEP_TURNOVER);

  values = malloc(len*sizeof(uint8_t));
  values[0] = PTYPE_ACTION_SLEEP;
  values[1] = broadcast_seqno >> 8;
  values[2] = broadcast_seqno;
  values[3] = TIMEOUT_SLEEP_DURATION >> 8;
  values[4] = TIMEOUT_SLEEP_DURATION;
  values[5] = TIMEOUT_SLEEP_TURNOVER >> 8;
  values[6] = TIMEOUT_SLEEP_TURNOVER;


  int i;
  for (i = 0; i < len; i++){
    printf("|   data[%d]: %d\n",i, values[i]);
  }

  packetbuf_copyfrom(values, len*sizeof(uint8_t));
  trickle_send(&trickle);
  broadcast_seqno++;

  //the GW doesnt need to sleep since it is connected to the server
  //ctimer_set(&ctSleepTurnover, CLOCK_SECOND*(((float)TIMEOUT_SLEEP_TURNOVER)/1000), sleep, NULL); //set timer for grace period before going to sleep
  //ctimer_set(&ctWakeUp, CLOCK_SECOND*((((float)TIMEOUT_SLEEP_TURNOVER)/1000)+TIMEOUT_SLEEP_DURATION), wakeUp, NULL); //set timer for waking up
  ctimer_set(&ctSleepFlood, CLOCK_SECOND*TIMEOUT_SLEEP, sendSleep, NULL); //set timeout for sending the next sleep action
  ctimer_set(&ctFreqFlood, CLOCK_SECOND*((((float)TIMER_FREQ+TIMEOUT_SLEEP_TURNOVER)/1000)+TIMEOUT_SLEEP_DURATION), sendFreqChange, NULL); //set timeout for sending the freq change 

  free(values);
}

/*######################################################################################################################*/
/*######################################################################################################################*/
/*######################################################################################################################*/
/*                                                  RIME CALLBACKS                                                      */
/*######################################################################################################################*/
/*######################################################################################################################*/
/*######################################################################################################################*/


static void broadcast_recv(struct broadcast_conn *c, const linkaddr_t *from){
  char *copy = malloc(packetbuf_datalen());
  packetbuf_copyto(copy);
 
  if (mode != MODE_BLIND){
    printf("### BEACON ### - received BLIND_BEACON from mote: %d\n", from->u8[0]);
    int i;
    for (i = 0; i <= packetbuf_datalen(); i++){
      printf("|   data[%d]: %d\n", i, copy[i]);
    }
    uint16_t seqno = (((uint8_t) copy[0]) << 8) | ((uint8_t) copy[1]);
    saveMeasurementOfBlind(from->u8[0], copy[2], packetbuf_attr(PACKETBUF_ATTR_RSSI), seqno);

    uint8_t count = copy[3];
    for (i = 0; i < count; i++){
      uint8_t tx = copy[4 + 4*i];
      int8_t rssi = copy[4 + 4*i + 1];
      seqno = (((uint8_t) copy[4 + 4*i + 2]) << 8) | ((uint8_t) copy[4 + 4*i + 3]);
      saveMeasurementByBlind(tx, from->u8[0], rssi, seqno);
    }

  }
  free(copy);
}


static void collect_recv(const linkaddr_t *originator, uint8_t seqno_collect, uint8_t hops)
{
  char *copy = malloc(packetbuf_datalen());
  packetbuf_copyto(copy); //the copying is probably not needed and the data could be accessed directly from the buffer
  printf("### COLLECT ### - received DATA from mote: %d\n", originator->u8[0]);
  int i;
  for (i = 0; i < packetbuf_datalen(); i++){
    printf("|   data[%d]: %d\n", i, copy[i]);
  }

  i = 0;
  int c = 0;
  for (c; c < ((uint8_t) copy[0]); c++){
    uint8_t tx = copy[++i];
    int8_t rssi = copy[++i];
    uint16_t seqno = (((uint8_t) copy[++i]) << 8) | ((uint8_t) copy[++i]);

    printf("### COLLECT ### - received Anchor->Anchor from: %d to: %d rssi: %d seqno: %d\n", tx, originator->u8[0], rssi, seqno);
    //TODO printf("OUT,type:DATA_AA,tx:%d,rx:%d,rssi:%d,seqno:%d\n", tx, originator->u8[0], rssi, seqno);
  }
  if (copy[++i] == 0 && copy[i+1] == 0){
    printf("### COLLECT ### - NO blind seen\n");
  } else {
    ctimer_set(&ctSleepFlood, CLOCK_SECOND*TIMEOUT_SLEEP, sendSleep, NULL); //reset the sleep timer so it starts from zero
  }

  int limit = copy[i];
  for (c = 0; c < limit; c++){
    uint8_t tx = copy[++i];
    uint8_t rx = copy[++i];
    int8_t rssi = copy[++i];  
    uint16_t seqno = (((uint8_t) copy[++i]) << 8) | ((uint8_t) copy[++i]);
    saveMeasurementByBlind(tx, rx, rssi, seqno);
  }

  limit = copy[++i];
  for (c = 0; c < limit; c++){
    uint8_t tx = copy[++i];
    uint8_t movement = copy[++i]; 
    int8_t rssi = copy[++i];
    uint16_t seqno = (((uint8_t) copy[++i]) << 8) | ((uint8_t) copy[++i]);

    printf("### COLLECT ### - received Blind->Anchor from: %d to: %d rssi: %d movement: %d seqno: %d\n", tx, originator->u8[0], rssi, movement, seqno);
    //TODO printf("OUT,type:DATA_BA,tx:%d,rx:%d,rssi:%d,movement:%d,seqno:%d\n", tx, originator->u8[0], rssi, movement, seqno);
  }
  free(copy); 
}

static void trickle_recv(struct trickle_conn *c)
{
  char *copy = malloc(packetbuf_datalen());
  packetbuf_copyto(copy);

  uint8_t ptype = copy[0];
  if (ptype == PTYPE_ACTION_COLLECT_FREQ){ //ACTION_COLLECT_FREQ packet has been received so after some delay we should switch the channel
    uint16_t seqno = (((uint8_t) copy[1]) << 8) | ((uint8_t) copy[2]);
    uint8_t freq = ((uint8_t) copy[3]);
    uint16_t timeoutFreq = (((uint8_t)copy[4]) << 8) | ((uint8_t) copy[5]);
    ctimer_set(&ctFreq, CLOCK_SECOND*(((float)timeoutFreq)/1000), changeFrequency, FREQ[freq]);
    if (mode == MODE_BLIND){
      //erase measurement but first copy the last one from anchor (since its this packet) and put it to the freshly cleared list
      struct measurementAnchor *next = ((struct measurementAnchor*)measurementsAnchor->first->value);
      uint8_t tx = next->tx; //Transmitter ID
      int8_t rssi = next->rssi; //the RSSI values
      uint16_t seqno = next->seqno;
      erase();
      printf("### MEASUREMENT ### - saving last received anchor measurement tx: %d, rssi: %d, seqno:%d\n", tx, rssi, seqno);
      saveMeasurementAnchor(tx,rssi,seqno);
    }
    ctimer_set(&ctErase, CLOCK_SECOND*(((float)TIMEOUT_ERASE)/1000), erase, NULL); //erase the data after sometime if nothing is received
    printf("### TRICKLE ### - received ACTION_COLLECT_FREQ with seqno: %d, newFreq: %d, timeoutFreq: %d\n", seqno, FREQ[freq], timeoutFreq);
  }
  if (ptype == PTYPE_ACTION_SLEEP){ //ACTION_SLEEP packet has been received -> go to sleep and wake up after some time
    uint16_t seqno = (((uint8_t) copy[1]) << 8) | ((uint8_t) copy[2]);
    uint16_t sleepTime = (((uint8_t)copy[3]) << 8) | ((uint8_t) copy[4]);
    uint16_t sleepTurnover = (((uint8_t)copy[5]) << 8) | ((uint8_t) copy[6]);
    ctimer_set(&ctSleepTurnover, CLOCK_SECOND*(((float)sleepTurnover)/1000), sleep, NULL); //set timer for grace period before going to sleep
    ctimer_set(&ctWakeUp, CLOCK_SECOND*((((float)sleepTurnover)/1000)+sleepTime), wakeUp, NULL); //set timer for waking up
    printf("### TRICKLE ### - received ACTION_SLEEP with seqno: %d, sleepTime: %d\n", seqno, sleepTime);
  }
  free(copy);
}

static void trickle_rssi(int8_t rssi, uint8_t from, uint8_t seqno){
  saveMeasurementAnchor(from, rssi, seqno);
}

static const struct broadcast_callbacks broadcast_call = {broadcast_recv};
static const struct collect_callbacks callbacksc = {collect_recv};
const static struct trickle_callbacks trickle_call = {trickle_recv, trickle_rssi};

/*######################################################################################################################*/
/*######################################################################################################################*/
/*######################################################################################################################*/
/*                                                  MAIN PROCESS                                                        */
/*######################################################################################################################*/
/*######################################################################################################################*/
/*######################################################################################################################*/
/*---------------------------------------------------------------------------*/
PROCESS(example_broadcast_process, "Main process");
AUTOSTART_PROCESSES(&example_broadcast_process);
/*---------------------------------------------------------------------------*/
PROCESS_THREAD(example_broadcast_process, ev, data)
{
  static struct etimer etBoot; //timer for mode switching after boot

  PROCESS_EXITHANDLER(broadcast_close(&broadcast);)

  PROCESS_BEGIN();
  SENSORS_ACTIVATE(button_sensor);  
  SENSORS_ACTIVATE(adxl345); //activate accelerometer

  init();
  etimer_set(&etBoot, CLOCK_SECOND*TIMEOUT_BOOT);

  broadcast_open(&broadcast, 129, &broadcast_call);
  trickle_open(&trickle, CLOCK_SECOND, 145, &trickle_call);

  PROCESS_WAIT_EVENT();
  while(1) {
    if (ev == sensors_event && data == &button_sensor && state == STATE_BOOT){
      mode = (mode+1);
      printf ("### MODE ### - changing mode to: %d\n", mode);
    }
    if (etimer_expired(&etBoot)){ //timeout for pressing the buttons and changing modes has expired
      state = STATE_RUNNING; //change state (used for button pressing limitations - so it cant be pressed later)
      //TODO printf("OUT,type:BOOT,mode:%d\n", mode);
      if (mode == MODE_GW){
        linkaddr_t addr;
        addr.u8[0] = 1;
        addr.u8[1] = 0;
        linkaddr_set_node_addr(&addr);

        collect_open(&tc, 130, COLLECT_ROUTER, &callbacksc);
        collect_set_sink(&tc, 1);
        //ctimer_set(&ctSleepFlood, CLOCK_SECOND*(120+TIMEOUT_SLEEP), sendSleep, NULL); //set timeout for sending the sleep action
        ctimer_set(&ctFreqFlood, CLOCK_SECOND*(120+((float)TIMER_FREQ)/1000), sendFreqChange, NULL);
        printf("### COLLECT ### - I have been setted as a sink\n");
      } else {
        //TODO change address based on HW address
        //printf("### ADDRESS ### - setting rime address[0]: %d\n", node_mac[7]);
        if (mode == MODE_BLIND){
          ctimer_set(&ctBeacon, CLOCK_SECOND*(((float)TIMER_BEACON)/1000), beacon, NULL);
        }
        if (mode == MODE_ANCHOR){
          collect_open(&tc, 130, COLLECT_ROUTER, &callbacksc);
          ctimer_set(&ctCollect, CLOCK_SECOND*(120+TIMEOUT_COLLECT_SELF), sendDataToGW, NULL);
        }
      }
    }
    PROCESS_WAIT_EVENT();
  }
  PROCESS_END();
}
/*---------------------------------------------------------------------------*/

/**
 * Initialize the lists
 */
void init(){
  measurementsAnchor = (struct listHeader*) malloc(sizeof(struct listHeader));
  measurementsByBlind = (struct listHeader*) malloc(sizeof(struct listHeader));
  measurementsOfBlind = (struct listHeader*) malloc(sizeof(struct listHeader));
  measurementsAnchor->size = 0;
  measurementsByBlind->size = 0;
  measurementsOfBlind->size = 0;
}

/**
 *  Appends the given value (void pointer) to the end of the list "under" the given head.
 */
void addlistItem(struct listHeader *head, void *val)
{
  struct listItem *newlistItem = malloc(sizeof(struct listItem)); // create a new list item pointer
  if (newlistItem == NULL){ //if memory was not allocated -> exit
    exit(1);
  }
  newlistItem->value = val; //Assign the value to the new element
  newlistItem->next = NULL; //When appending to the end no element is next

  struct listItem *temp = head->last;
  if (head->size == 0){ //if last item is null = the list was empty -> make the new item first AND last
    head->first = newlistItem;
    head->last = newlistItem;
  } else {
    temp->next = newlistItem;
    head->last = newlistItem;
  }
  head->size = head->size + 1;
  checkForSending(); //everytime we add something to a list check if we have reached the limit of data to send
}

/**
 *  Save the anchor to anchor measurement to the measurements list.
 */
void saveMeasurementAnchor(uint8_t tx, int8_t rssi, uint16_t seqno){
  if (mode != MODE_GW){
    struct listItem *next = measurementsAnchor->first;
    int j;  
    for (j = 0; j < measurementsAnchor->size; j++){
      if (((struct measurementAnchor*)next->value)->tx == tx){
        ((struct measurementAnchor*)next->value)->rssi = rssi;
        ((struct measurementAnchor*)next->value)->seqno = seqno;
        printf("### MEASUREMENT ### - updated anchor->anchor from:%d rssi:%d seqno: %d\n", tx, rssi, seqno);
        return; //if the measurement is already in the list -> update it
      }
      next = next->next;
      if (next == NULL){
        break; //just in case
      }
    }
    struct measurementAnchor *measurement = malloc(sizeof(struct measurementAnchor)); // create a measurement pointer
    measurement->tx = tx;
    measurement->rssi = rssi;
    measurement->seqno = seqno;
    addlistItem(measurementsAnchor, measurement);
    printf("### MEASUREMENT ### - saved anchor->anchor from: %d rssi: %d seqno: %d\n", tx, rssi, seqno);
  } else {
    //TODO printf("OUT,type:DATA_AA,tx:%d,rx:1,rssi:%d,seqno:%d\n", tx, rssi, seqno);
  }
}

/**
 * Save the anchor to blind measurement.
 */
void saveMeasurementByBlind(uint8_t tx, uint8_t rx, int8_t rssi, uint16_t seqno){
  if (mode != MODE_GW){
    struct listItem *next = measurementsByBlind->first;
    int j = 0;  
    for (j; j < measurementsByBlind->size; j++){
      if (((struct measurementByBlind*)next->value)->tx == tx && ((struct measurementByBlind*)next->value)->rx == rx && ((struct measurementByBlind*)next->value)->rssi == rssi && ((struct measurementByBlind*)next->value)->seqno == seqno){
        printf("### MEASUREMENT ### - didnt save anchor->blind (already present)from:%d rssi:%d seqno: %d\n", tx, rssi, seqno);
        return; //if the measurement is already in the list -> disregard it
      }
      next = next->next;
      if (next == NULL){
        break; //just in case
      }
    } 
    struct measurementByBlind *measurement = malloc(sizeof(struct measurementByBlind)); // create a measurement pointer
    measurement->tx = tx;
    measurement->rx = rx;
    measurement->rssi = rssi;
    measurement->seqno = seqno;
    addlistItem(measurementsByBlind, measurement);
    printf("### MEASUREMENT ### - saved anchor->blind from:%d to:%d rssi:%d seqno: %d\n", tx, rx, rssi, seqno);
  } else {
    printf("### COLLECT ### - received Anchor->Blind from: %d to: %d rssi: %d seqno: %d\n", tx, rx, rssi, seqno);
    //TODO printf("OUT,type:DATA_AB,tx:%d,rx:%d,rssi:%d,seqno:%d\n", tx, rx, rssi, seqno);
  }
}

/**
 * Save the blind to anchor measurement.
 */
void saveMeasurementOfBlind(uint8_t tx, uint8_t movement, int8_t rssi, uint16_t seqno){
  if (mode != MODE_GW){
    struct measurementOfBlind *measurement = malloc(sizeof(struct measurementOfBlind)); // create a measurement pointer
    measurement->tx = tx;
    measurement->movement = movement;
    measurement->rssi = rssi;
    measurement->seqno = seqno;
    addlistItem(measurementsOfBlind, measurement);
    printf("### MEASUREMENT ### - saved blind->anchor from: %d movement: %d rssi: %d seqno: %d\n", tx, movement, rssi, seqno);
  } else {
    //TODO printf("OUT,type:DATA_BA,tx:%d,rx:1,rssi:%d,movement:%d,seqno:%d\n", tx, rssi, movement, seqno);
  }
}
