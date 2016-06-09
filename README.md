
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
- [x] Printing out properly all data on GateWay
- [x] Changing frequency between actions
- [x] Sending movement velocity information from moving mote
- [ ] Sending alternative data from moving mote to complete data structure

#### Dashboard code
- [x] Main, error handler, properties reader, model saver.
- [x] Interfacing with command line
- [x] Design the raw data model
- [x] Design the analyzes data model
- [x] Making spatial calculations from raw data model to analyzed data model
- [x] Gui - raw output display
- [x] Gui - anchor map visualization
- [x] Gui - moving mote vizualisation

## Usage

### If not compiled

Set path
```
set path=%path%;"C:\Program Files\Java\jdk1.8.0_60\bin"
```
compile app
```
javac Main.java
```

### Config property file
If the System.in is a cmd line, then the reader should be while(true), otherwise you should better limit it :).

* GUI parameters
guiTitle=WSN GeoLoc Dashboard
guiSizeWidth=800
guiSizeHeight=600
guiSizeMinimumWidth=600
guiSizeMinimumHeight=300

* mockupRSSI : Define which type of input you want in the system
* waitBeforeAnalyze : seconds to wait before analyzing a new received sequence (see mote code param for more details)
* receivedRssiAt1m : The received Rssi at 1 meter
* propagationCstOfPathLossExp : The propagation constant of path loss exp (=2 in open space)
* offset : The mote RSSI offset
* useBArssi : Use Blind-to-Anchor measurements [true, false]
* useABrssi : Use Anchor-to-Blind measurements [true, false]
* avgPosition : The amount of last position taken in account with AVG option

### Configuration to run mote

Before starting, you have to replace the trickle.c and trickle.h file in the Contiki system !

give access to device
```
sudo chmod 777 /dev/ttyUSB*
```

make the program
```
make TARGET=z1 main
```

make file and upload to device
```
make TARGET=z1 main.upload
```

### Launch app

It has been tested with a Zolertia Z1-mote and cooja (see mote code) installed in it.
there will be three different type of motes :
When starting a mote, you have 5 seconds to define the mote mode by clicking on the main button. This time lap starts and ends when the red led blinks.
0. The Anchors : Starting motes are by default anchor. Thereby just start them, you don't have to push the button.
0. The GateWay / Sync : To set a mote as a sync, push the main button once within the five first seconds. There may be only one GW per network.
The gateway should be started with the below commands to link the dashboard to the network properly.
0. The Blind : The blind mote need 2 pressures on the button within the 5 seconds.

Then the network will build, start communicating and looking for a blind mote. The process can take up to two minutes before showing an output.

(see repository ./dashboard/out/artifacts/dashboard_jar for testing files)


Launch program with file saved outputs

```
java -jar dashboard.jar [propertyfile.prop] < in_sx.txt
or
java Main -cp/commons-math3-3.6.1.jar:. < cjout.txt
```

Launch program by using a mote in a real network
```
java -jar dashboard.jar [propertyfile.prop] < ~/contiki/tools/sky/serialdump-linux -b115200 /dev/ttyUSB0
or
java Main -cp/commons-math3-3.6.1.jar:. < ~/contiki/tools/sky/serialdump-linux -b115200 /dev/ttyUSB0
```


## Credits

Crafted with love by
[JV](https://github.com/jvdurieu), [Matej](https://github.com/DanicMa) and [Goga](https://github.com/gogickaa)
