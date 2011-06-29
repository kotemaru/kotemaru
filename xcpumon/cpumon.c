/**********************************************************************
 * Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 * Copyright 2011- kotemaru@kotemaru.org
 **********************************************************************/
/**
Example of cpuCoreInfo.c.
*/

#include <stdio.h>
#include "cpumon.h"

int main(int argc, char **argv) {
	int n;
	int num = cpumon_init();
	CpuCoreInfo infos[num];
	while (1) {
		cpumon_getInfos(infos);
		for (n=0; n<num; n++) {
			printf("c%d=[%3.1fGHz,%5.1f%%,%5.1fC] ", n,
				(float)infos[n].freq/1000, 
				(float)infos[n].used/10, 
				(float)infos[n].temp/10
			);
		}
		printf("\n");
		sleep(1);
	}
}

//EOF
