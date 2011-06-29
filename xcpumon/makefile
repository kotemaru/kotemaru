.c.o:
	$(CC) -c -g $<

all: cpumon xcpumon
	strip xcpumon

xcpumon.o : xcpumon.c cpumon.h
	$(CC) -I/usr/X11R6/include -c -g $<

cpuCoreInfo.o : cpuCoreInfo.c cpumon.h
cpumon.o : cpumon.c cpumon.h

cpumon : cpumon.o cpuCoreInfo.o
	$(CC) -g -o $@ cpumon.o cpuCoreInfo.o

xcpumon : xcpumon.o cpuCoreInfo.o
	$(CC) -g -o $@ xcpumon.o cpuCoreInfo.o -lm -L/usr/X11R6/lib -lXm -lX11


clean:
	rm xcpumon cpumon *.o

