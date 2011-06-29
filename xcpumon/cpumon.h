#include <stdio.h>
#include <unistd.h>
#include <string.h>
#include <stdlib.h>
#include <kvm.h>
#include <sys/resource.h>
#include <sys/types.h>
#include <sys/sysctl.h>


#define NAME_CORE_NUM  "hw.ncpu"
#define NAME_CPU_TIMES "kern.cp_times"
#define NAME_TEMP    "dev.cpu.%d.temperature"
#define NAME_FREQ    "dev.cpu.0.freq" // dev.cpu.1.freq ¤ÏÌµ¤¤
                  
typedef struct CpuCoreInfo{  
	int freq; // 1000 == 1GHz
	int temp; // 1000 == 100.0C
	int used; // 1000 == 100%
} CpuCoreInfo;

typedef struct Mib{
	int mib[16];
	u_int len;
} Mib;
