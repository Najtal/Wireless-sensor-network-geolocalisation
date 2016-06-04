#!/usr/bin/python

# USAGE:
# python capture.py /dev/ttyUSB2 [filename.txt]
#
# filename is optional, default is: "capture.txt" and overwrites possible previous files
#
import sys
import serial
from datetime import datetime
from dateutil.relativedelta import relativedelta

def main():
  print '#############################################################\nReading from: ', sys.argv[1], "\n#############################################################"
  filename = "trace.txt"
  zerotime = datetime.now()
  if len(sys.argv) > 2:
	filename = sys.argv[2]
  f = open(filename,'w')
  ser = serial.Serial(sys.argv[1], timeout=None, baudrate=115200, xonxoff=False, rtscts=False, dsrdtr=False)
  while True:
	line = ser.readline()
	if line.startswith("OUT"):
		d = relativedelta(datetime.now(),zerotime)
		string = "%02d:%02d.%03d%s%s" % (d.minutes,d.seconds,d.microseconds/1000,"\tID:1\t",line)
		f.write(string)
		print string.rstrip('\n') #so there arent blank lines
  f.close()


if __name__ == '__main__':
  main()
