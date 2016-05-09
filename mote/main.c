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

static uint8_t state = 0;
static uint8_t mode = 0;

static uint16_t broadcast_seqno = 1;
static uint16_t last_blind = 0;
static uint16_t last_freq_change = 0;

static struct broadcast_conn broadcast;
static struct collect_conn tc;
static struct trickle_conn trickle;

static struct ctimer ctInitWait;
static struct ctimer ctResponse;
static struct ctimer ctAction;
static struct ctimer ctBeacon;

static struct listHeader *measurementsAnchor;
static struct listHeader *measurementsByBlind; //anchor to blind measurements (contain sender id)
static struct listHeader *measurementsOfBlind; //blind to anchor measurements (contain movement data)

/*######################################################################################################################*/
/*######################################################################################################################*/
/*######################################################################################################################*/
/*                                                  CTIMERS CALLBACKS                                                   */
/*######################################################################################################################*/
/*######################################################################################################################*/
/*######################################################################################################################*/
static void actionTimerExpired(){
  printf("### TRICKLE ### - sending ACTION_COLLECT with seqno: %d\n", broadcast_seqno);
  uint8_t *values;
  values = malloc(5*sizeof(uint8_t));
  values[0] = PTYPE_ACTION_COLLECT;
  values[1] = broadcast_seqno >> 8;
  values[2] = broadcast_seqno;
  values[3] = TIMEOUT_COLLECT >> 8;
  values[4] = TIMEOUT_COLLECT;

  int i;
  for (i = 0; i < 5; i++){
    printf("|   data[%d]: %d\n",i, values[i]);
  }

  packetbuf_copyfrom(values, 5*sizeof(uint8_t));
  trickle_send(&trickle);
  broadcast_seqno++;
  //ctimer_set(&ctInitWait, CLOCK_SECOND*TIMEOUT_INIT, initPhaseFinished, NULL);
  ctimer_set(&ctAction, CLOCK_SECOND*TIMER_CYCLE, actionTimerExpired, NULL);
}

static void beacon(){
  uint8_t *values;
  int len = (4 + MOVEMENT_SIZE + 4*measurementsAnchor->size);
  values = malloc(len*sizeof(uint8_t));
  values[0] = PTYPE_BLIND_BEACON;
  values[1] = broadcast_seqno >> 8;
  values[2] = broadcast_seqno;

  //TODO add movement data

  int i = MOVEMENT_SIZE + 3;
  values[i] = measurementsAnchor->size; //number of anchor to this blind rssi measurements
  i++;
  struct listItem *next = measurementsAnchor->first;
  int j = 0;
  for (j; j < measurementsAnchor->size; j++){
    values[i] = ((struct measurementAnchor*)next->value)->tx; //Transmitter ID
    i++;
    values[i] = ((struct measurementAnchor*)next->value)->rssi; //the RSSI values
    i++;
    values[i] = ((struct measurementAnchor*)next->value)->seqno >> 8; //first byte of the sequence number
    i++;
    values[i] = ((struct measurementAnchor*)next->value)->seqno; //second byte of the sequence number
    i++;
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
  int len = (4 + 4*measurementsAnchor->size + 5*measurementsByBlind->size + (4+MOVEMENT_SIZE)*measurementsOfBlind->size);
  values = malloc(len*sizeof(uint8_t));
  values[0] = PTYPE_DATA;

  /////////////////////////////////////////////////////////////the ANCHOR to ANCHOR measurements
  values[1] = measurementsAnchor->size; //number of anchor to anchor measurements
  int i = 2;
  struct listItem *next = measurementsAnchor->first;
  int j = 0;
  for (j; j < measurementsAnchor->size; j++){
    values[i] = ((struct measurementAnchor*)next->value)->tx; //Transmitter ID
    i++;
    values[i] = ((struct measurementAnchor*)next->value)->rssi; //the RSSI values
    i++;
    values[i] = ((struct measurementAnchor*)next->value)->seqno >> 8; //first byte of the sequence number
    i++;
    values[i] = ((struct measurementAnchor*)next->value)->seqno; //second byte of the sequence number
    i++;
    next = next->next;            
    if (next == NULL){
      break; //just in case
    }
  }

  values[i] = measurementsByBlind->size;
  i++;
  next = measurementsByBlind->first;
  j = 0;
  for (j; j < measurementsByBlind->size; j++){
    values[i] = ((struct measurementByBlind*)next->value)->tx; //Transmitter ID
    i++;
    values[i] = ((struct measurementByBlind*)next->value)->rx; //Receiver ID
    i++;
    values[i] = ((struct measurementByBlind*)next->value)->rssi; //the RSSI values
    i++;
    values[i] = ((struct measurementByBlind*)next->value)->seqno >> 8; //first byte of the sequence number
    i++;
    values[i] = ((struct measurementByBlind*)next->value)->seqno; //second byte of the sequence number
    i++;
    next = next->next;            
    if (next == NULL){
      break; //just in case
    }
  }

  values[i] = measurementsOfBlind->size;
  i++;
  next = measurementsOfBlind->first;
  j = 0;
  for (j; j < measurementsOfBlind->size; j++){
    values[i] = ((struct measurementOfBlind*)next->value)->tx; //Transmitter ID
    i++;
    //TODO send the movement values
    values[i] = ((struct measurementOfBlind*)next->value)->rssi; //the RSSI values
    i++;
    values[i] = ((struct measurementOfBlind*)next->value)->seqno >> 8; //first byte of the sequence number
    i++;
    values[i] = ((struct measurementOfBlind*)next->value)->seqno; //second byte of the sequence number
    i++;
    next = next->next;            
    if (next == NULL){
      break; //just in case
    }
  }
  printf("### COLLECT ### - sending data to GW\n");
  for (i = 0; i < len; i++){
    printf("|   data[%d]: %d\n",i, values[i]);
  }

  packetbuf_clear();
  packetbuf_copyfrom(values, len*sizeof(uint8_t));
  collect_send(&tc, 15);
  free(values);
  //TODO erase all the lists so we start tabula rasa
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
 
  uint8_t ptype = copy[0];
    if (ptype == PTYPE_BLIND_BEACON && mode != MODE_BLIND){
      printf("### BEACON ### - received BLIND_BEACON from mote: %d\n", from->u8[0]);
      int i;
      for (i = 0; i <= packetbuf_datalen(); i++){
        printf("|   data[%d]: %d\n", i, copy[i]);
      }
      uint16_t seqno = (((uint8_t) copy[1]) << 8) | ((uint8_t) copy[2]);
      
      //TODO save MeasurementOfBlind
      //TODO parse the MeasurementsByBlind contained in the packet
      uint8_t count = copy[3+MOVEMENT_SIZE];

      for (i = 1; i <= count; i++){
        uint8_t tx = copy[MOVEMENT_SIZE + 4*i];
        uint8_t rssi = copy[MOVEMENT_SIZE + 4*i + 1];
        seqno = (((uint8_t) copy[MOVEMENT_SIZE + 4*i + 2]) << 8) | ((uint8_t) copy[MOVEMENT_SIZE + 4*i + 3]);
        saveMeasurementByBlind(tx, from->u8[0], rssi, seqno);
      }

    }
  free(copy);
}


static void collect_recv(const linkaddr_t *originator, uint8_t seqno, uint8_t hops)
{
  char *copy = malloc(packetbuf_datalen());
  packetbuf_copyto(copy); //the copying is probably not needed and the data could be accessed directly from the buffer
  
  uint8_t ptype = copy[0];
  if (ptype == PTYPE_DATA){
    printf("### COLLECT ### - received DATA from mote: %d\n", originator->u8[0]);
    int i;
    for (i = 0; i < packetbuf_datalen(); i++){
      printf("|   data[%d]: %d\n", i, copy[i]);
    }
    
    /*
    uint8_t *sender = malloc(sizeof(uint8_t));
    *sender = originator->u8[0];
    addlistItem(anchors, sender);
    */
  }
  free(copy); 
}

static void trickle_recv(struct trickle_conn *c)
{
  char *copy = malloc(packetbuf_datalen());
  packetbuf_copyto(copy);

  uint8_t ptype = copy[0];
  if (ptype == PTYPE_ACTION_COLLECT && mode == MODE_ANCHOR){ //ACTION_COLLECT packet has been received
    uint16_t seqno = (((uint8_t) copy[1]) << 8) | ((uint8_t) copy[2]);
    uint16_t timeout = (((uint8_t)copy[3]) << 8) | ((uint8_t) copy[4]);
    printf("### TRICKLE ### - received ACTION_COLLECT with seqno: %d, timeout: %d\n", seqno, timeout);
    ctimer_set(&ctResponse, CLOCK_SECOND*(((float)timeout)/1000), sendDataToGW, NULL);
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

  init();
  etimer_set(&etBoot, CLOCK_SECOND*TIMEOUT_BOOT);

  broadcast_open(&broadcast, 129, &broadcast_call);
  trickle_open(&trickle, CLOCK_SECOND, 145, &trickle_call);

  PROCESS_WAIT_EVENT();
  while(1) {
    if (ev == sensors_event && data == &button_sensor && state == STATE_BOOT){
      mode = (mode+1);
      //mode = (mode+1) & 3;
      //TODO change mote address
      printf ("### MODE ### - changing mode to: %d\n", mode);
    }
    if (etimer_expired(&etBoot)){ //timeout for pressing the buttons and changing modes has expired
      if (mode != MODE_BLIND){ //dont open collect if its a blind mote
        collect_open(&tc, 130, COLLECT_ROUTER, &callbacksc);
        if(linkaddr_node_addr.u8[0] == 1 && linkaddr_node_addr.u8[1] == 0) { //if the address is 1.0 (is GW) set this mote as collect sink
          collect_set_sink(&tc, 1);
          printf("### COLLECT ### - I have been setted as a sink\n");
        }
      }
      if (mode == MODE_GW){
        //actionTimerExpired();
        ctimer_set(&ctAction, CLOCK_SECOND*(120), actionTimerExpired, NULL);
      }
      if (mode == MODE_BLIND){
        ctimer_set(&ctBeacon, CLOCK_SECOND*(((float)TIMER_BEACON)/1000), beacon, NULL);
      }

    }
    PROCESS_WAIT_EVENT();
  }
  PROCESS_END();
}
/*---------------------------------------------------------------------------*/

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
}

/**
 *  Save the measurement to the measurements list.
 */
void saveMeasurementAnchor(uint8_t tx, int8_t rssi, uint16_t seqno){
  if (mode != MODE_GW){
    //TODO if its the same seqno dont save new but update the old value
    struct measurementAnchor *measurement = malloc(sizeof(struct measurementAnchor)); // create a measurement pointer
    measurement->tx = tx;
    measurement->rssi = rssi;
    measurement->seqno = seqno;
    addlistItem(measurementsAnchor, measurement);
    printf("### MEASUREMENT ### - saved measurementAnchor from: %d rssi: %d seqno: %d\n", tx, rssi, seqno);
  } else {
    //TODO print the values to console
  }
}

void saveMeasurementByBlind(uint8_t tx, uint8_t rx, int8_t rssi, uint16_t seqno){
  if (mode != MODE_GW){
    struct listItem *next = measurementsByBlind->first;
    int j = 0;  
    for (j; j < measurementsByBlind->size; j++){
      if (((struct measurementByBlind*)next->value)->tx == tx && ((struct measurementByBlind*)next->value)->rx == rx && ((struct measurementByBlind*)next->value)->rssi == rssi && ((struct measurementByBlind*)next->value)->seqno == seqno){
        //TODO make work
        printf("### MEASUREMENT ### - didnt save anchor to blind (already present)from:%d rssi:%d seqno: %d\n", tx, rssi, seqno);
        return; //if the measurement is already in the list -> disregard it
      }
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
    printf("### MEASUREMENT ### - saved anchor to blind from:%d rssi:%d seqno: %d\n", tx, rssi, seqno);
  } else {
    //TODO print the values to console
  }

}