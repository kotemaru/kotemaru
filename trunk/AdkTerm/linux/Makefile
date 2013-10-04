
# for linux
#PREFIX=/usr
#USB=usb-1.0

# for FreeBSD
PREFIX=/usr/local
USB=usb

#DEBUG=-DDEBUG -g
CFLAGS=-o -I$(PREFIX)/include -I$(PREFIX)/include/lib$(USB) $(DEBUG)
LDFLAGS=-L$(PREFIX)/lib

all: AdkTerm

AdkTerm : AdkTerm.o AOA.o
	g++ $(LDFLAGS) -o AdkTerm AdkTerm.o AOA.o  -l$(USB)

AOA.o : AOA/AOA.cpp
	g++ -c $(CFLAGS) -o AOA.o AOA/AOA.cpp

AdkTerm.o : AdkTerm.cpp
	g++ -c $(CFLAGS) -o AdkTerm.o AdkTerm.cpp

clean:
	rm -f *.o AdkTerm *.core
