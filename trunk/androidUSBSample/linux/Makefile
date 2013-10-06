
# for linux
#PREFIX=/usr
#USB=usb-1.0

# for FreeBSD
PREFIX=/usr/local
USB=usb

#DEBUG=-DDEBUG -g
CFLAGS=-o -I$(PREFIX)/include -I$(PREFIX)/include/lib$(USB) $(DEBUG)
LDFLAGS=-L$(PREFIX)/lib

all: AdkEcho

AdkEcho : AdkEcho.o AOA.o
	g++ $(LDFLAGS) -o AdkEcho AdkEcho.o AOA.o  -l$(USB)

AOA.o : AOA/AOA.cpp
	g++ -c $(CFLAGS) -o AOA.o AOA/AOA.cpp

AdkEcho.o : AdkEcho.cpp
	g++ -c $(CFLAGS) -o AdkEcho.o AdkEcho.cpp

clean:
	rm -f *.o AdkEcho *.core
