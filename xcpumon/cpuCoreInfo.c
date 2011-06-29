/**********************************************************************
 * Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 * Copyright 2011- kotemaru@kotemaru.org
 **********************************************************************/
#include <stdio.h>
#include <unistd.h>
#include <string.h>
#include <stdlib.h>
#include <kvm.h>
#include <sys/resource.h>
#include <sys/types.h>
#include <sys/sysctl.h>

#include "cpumon.h"



static Mib* mibCoreNum;
static Mib* mibCpuTimes;
static Mib* mibTemp[4];
static Mib* mibFreq[4];
  
static int coreNum = 1;                   
                     
static void errorM(int lnum, char* msg) {
	char buff[512];
	sprintf(buff, "%s:%d:%s:",__FILE__,lnum,msg);
	perror(buff);
	exit(-1);
}
static void error(int lnum) {
	char buff[128];
	sprintf(buff, "%s:%d:",__FILE__,lnum);
	perror(buff);
	exit(-1);
}

static Mib* getSysctlMib(char* nameFmt, int n) {
	char name[128];
	sprintf(name, nameFmt, n);

	Mib* mib = calloc(1, sizeof(Mib));
	if (mib == NULL) error(__LINE__);
	mib->len = 16;
	if (sysctlnametomib(name, mib->mib, &mib->len)) errorM(__LINE__,name);
	return mib;
}
static long getSysctl(Mib* mib) {
	long val[10];
	size_t len = 10;
	if (sysctl(mib->mib, mib->len, val, &len, 0, 0)) error(__LINE__);
	return val[0];
}


static void calcCpuTimes(long* result) {
	long val[1024];
	size_t len = 1024;
	if (sysctl(mibCpuTimes->mib, mibCpuTimes->len, val, &len, 0, 0)) error(__LINE__);

	int i, n;
	static long oldIdol[4], oldTotal[4];
	for (n=0; n<coreNum; n++) {
		long* data = &val[CPUSTATES*n];
		long curIdol  = data[CP_IDLE];
		long curTotal = 0;
		for (i=0; i<CPUSTATES; i++) curTotal += data[i];

		long idol = curIdol - oldIdol[n];
		long total = curTotal - oldTotal[n];
		if (total > 0) {
			result[n] = (1000L * (total-idol)) / total;
		} else {
			result[n] = 0;
		}

		oldIdol[n] = curIdol;
		oldTotal[n] = curTotal;
	}
}

//------------------------------------------------------------------------
// External function

/**
The initialization of this library.
@return number of cpu core.
*/
int cpumon_init() {
	int n;
	mibCoreNum  = getSysctlMib(NAME_CORE_NUM,0);
	mibCpuTimes = getSysctlMib(NAME_CPU_TIMES,0);
	coreNum = (int)getSysctl(mibCoreNum);
	for (n=0; n<coreNum; n++) {
		mibTemp[n] = getSysctlMib(NAME_TEMP,n);
		mibFreq[n] = getSysctlMib(NAME_FREQ,n);
	}

	long used[coreNum];
	calcCpuTimes(used);
	return coreNum;
}

/**
The acquisition of the current CPU information.
Distance of around one second is necessary for a second summons.
@params infos Write back by CPU information.
              There being more numbers of element of the sequence than the number of CPU cores.
@return infos
*/
CpuCoreInfo* cpumon_getInfos(CpuCoreInfo* infos) {
	int n;
	long used[coreNum];

	if (infos == NULL) {
		infos = calloc(coreNum, sizeof(CpuCoreInfo)); 
		if (infos == NULL) error(__LINE__);
	}

	calcCpuTimes(used);
	for (n=0; n<coreNum; n++) {
		infos[n].freq = (int)getSysctl(mibFreq[n]);
		infos[n].temp = (int)getSysctl(mibTemp[n]) - 2732; // K->C
		infos[n].used = used[n];
	}
	return infos;
}


//EOF
