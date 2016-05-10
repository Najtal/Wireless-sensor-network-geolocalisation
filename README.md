# Wireless-sensor-network-geolocalisation
A Contiki based Z1-mote system linked to a dashboard that allow you to track moving-motes real position.

## Description
This project is part of the FRI Wireless Sensor Network 2016 project. It implement a system for localization of a slowly moving node in 2D from signal strength measurements between a single blind node (whose unknown position needs to be determined) and a set of “anchor” nodes in fixed known position. 

The project comprises several sub‐phases:
1. Preliminary: characterization of measurements (stability, noise, variability across devices, correlation with distance), separately for RSSI and LQI (i.e., CORR).
2. Implementation of an energy‐efficient protocol for periodic collection of measurements across multiple channels and reporting of RSSI/LQI measurements at blind and anchors to the gateway. To do so:
- past measurements are piggybacked into new packets, radio sleep, compression of RSSI reports, etc. 
- the nodes dont lie within the same broadcast domain, therefore the system resort to multi‐hop for data collection. 
- anchor-to-anchor RSSI is collect (in addition to blind-to-anchor and anchor-to-blind), as they are needed for on-line calibration. 
3. Detection and reporting of blind node motion status (moving or stationary) based on accelerometer data.  
4. Implementation of a Localization algorithm based on Non-linear Least-Squares sub-instances, for calibration and localization on the external server, with data feed from the gateway node, and reporting GUI.
5. Exploit frequency diversity (à perform measurements on different radio channels and fuse the measurements in a clever manner). 
6. benefit the final accuracy with spatial diversity of moving motes


## Code Milestones
#### Mote code
- [x] Mapping the anchor network
- [x] Measuring RSSI's during anchor-to-anchor communication
- [x] Data structures to save measurements
- [x] Floading the anchor network with actions
- [x] Moving mote beaconing
- [x] Concatenate data on collecting data
- [x] Collecting RSSI measurements to GateWay
- [ ] Printing out properly all data on GateWay
- [ ] Changing frequency between actions
- [ ] Sending movement velocity information from moving mote
- [ ] Sending alternative data from moving mote to complete data structure

#### Dashboard code
- [ ] Main, error handler, properties reader, model saver.
- [ ] Interfacing with command line
- [ ] Design the raw data model
- [ ] Design the analyzes data model
- [ ] Making spatial calculations from raw data model to analyzed data model
- [ ] Gui - anchors settings
- [ ] Gui - raw output display
- [ ] Gui - anchor map visualization
- [ ] Gui - moving mote vizualisation

## Usage
To come ;)