/**********************************************************************
 * Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 * Copyright 2011- kotemaru@kotemaru.org
 **********************************************************************/
/**
CPU monitor for XWindow/FreeBSD.
*/
#include <X11/Xlib.h>
#include <X11/Xutil.h>
#include <X11/Xresource.h>
#include <stdio.h>
#include <unistd.h>
#include <string.h>
#include <stdlib.h>
#include <kvm.h>
#include <sys/resource.h>
#include <sys/types.h>
#include <sys/sysctl.h>

#include "cpumon.h"

#define XCPUMON "xcpumon"
#define   MAX(a,b)  ( ((a)>(b) ) ? (a) : (b) )


static XColor TEMP_COLOR;
static XColor FREQ_COLOR;
static XColor CPUA_COLOR;
static XColor CPUM_COLOR;
static XColor BG_COLOR;
static XColor BGLINE_COLOR;
static XFontStruct* FONT;

static int opt_x  = 0;
static int opt_y  = 0;
static int opt_w  = 100;
static int opt_h  = 50;
static char* opt_ct = "#f040c0";
static char* opt_cf = "yellow";
static char* opt_cc = "green";
static char* opt_ccm = "#00f0f0";
static char* opt_cb = "#004000";
static char* opt_cbl = "#008000";
static int opt_mt = 100;
static int opt_mf = 4000;
static int opt_mc = 100;
static int opt_l = -1;
static int opt_i = 3;
static int opt_v = 0;
static char* opt_font = "-*-helvetica-medium-r-*-*-8-*-*-*-*-*-*-*";
static int opt_wm = 0;


static int coreNum = 1;
static CpuCoreInfo cpuCoreInfos[4];

static int* tempLog;
static int* freqLog;
static int* cpuAvgLog;
static int* cpuMaxLog;


static void error(int lnum) {
	char buff[128];
	sprintf(buff, "%s:%d",__FILE__,lnum);
	perror(buff);
	exit(-1);
}

static void drawLog(Display *disp, Window win, GC gc, int log[], int max, XColor color) {
	int i;
	int x1,y1,x2,y2;
	int h = opt_h-10;

	XSetForeground(disp, gc, color.pixel);
	y1 = opt_h-(h * log[0] / max);
	for (i=1; i<opt_l; i++) {
		x1 = opt_w-(opt_w*(i-1)/opt_l);
		x2 = opt_w-(opt_w*(i)/opt_l);
		y2 = opt_h-(h * log[i] / max);
		XDrawLine(disp, win, gc, x1, y1, x2, y2);
		y1 = y2;
	}

}

static void update(Display *disp, Window win, GC gc) {
	int i,n;

	for (i=opt_l-2; i>=0; i--) {
		tempLog[i+1] = tempLog[i];
		freqLog[i+1] = freqLog[i];
		cpuAvgLog[i+1] = cpuAvgLog[i];
		cpuMaxLog[i+1] = cpuMaxLog[i];
	}

	cpumon_getInfos(cpuCoreInfos);
	tempLog[0] = 0;
	freqLog[0] = cpuCoreInfos[0].freq;
	cpuAvgLog[0] = 0;
	cpuMaxLog[0] = 0;

	for (n=0; n<coreNum; n++) {
		tempLog[0] = MAX(tempLog[0], cpuCoreInfos[n].temp);
		cpuAvgLog[0] += cpuCoreInfos[n].used;
		cpuMaxLog[0] = MAX(cpuMaxLog[0], cpuCoreInfos[n].used);
	}
	tempLog[0] = tempLog[0] / 10; 
	cpuMaxLog[0] = cpuMaxLog[0] / 10;
	cpuAvgLog[0] = (cpuAvgLog[0] / 10) / coreNum;
	

	XSetForeground(disp, gc, BG_COLOR.pixel);
	XFillRectangle(disp, win, gc, 0, 0, opt_w, opt_h);
	int h = opt_h - 10;
	int y = opt_h - h;
	XSetForeground(disp, gc, BGLINE_COLOR.pixel);
	XDrawLine(disp, win, gc, 0, y, opt_w, y);
	y = opt_h - (h/2);
	XDrawLine(disp, win, gc, 0, y, opt_w, y);

	char buff[30];

	drawLog(disp, win, gc, freqLog, opt_mf, FREQ_COLOR);
	sprintf(buff, "%3.1fGHz", ((float)freqLog[0]/1000) );
	XDrawString(disp, win, gc, 0, 8, buff, strlen(buff));

	drawLog(disp, win, gc, tempLog, opt_mt, TEMP_COLOR);
	sprintf(buff, "%dC", tempLog[0] );
	int width = XTextWidth(FONT, buff, strlen(buff));
	XDrawString(disp, win, gc, opt_w-width, 8, buff, strlen(buff));

	drawLog(disp, win, gc, cpuMaxLog,  opt_mc, CPUM_COLOR);
	drawLog(disp, win, gc, cpuAvgLog,  opt_mc, CPUA_COLOR);
	sprintf(buff, "%d(%d)%%", cpuMaxLog[0],cpuAvgLog[0] );
	width = XTextWidth(FONT, buff, strlen(buff));
	XDrawString(disp, win, gc, (opt_w/2)-(width/3), 8, buff, strlen(buff));

	XFlush(disp);
}

static GC initGc(Display *disp, Window win) {
	GC gc = XCreateGC(disp, win, 0, 0);

	Colormap cmap = DefaultColormap(disp, 0);
	XColor dummy;

	XAllocNamedColor(disp, cmap, opt_ct, &TEMP_COLOR, &dummy);
	XAllocNamedColor(disp, cmap, opt_cf, &FREQ_COLOR, &dummy);
	XAllocNamedColor(disp, cmap, opt_cc, &CPUA_COLOR, &dummy);
	XAllocNamedColor(disp, cmap, opt_ccm, &CPUM_COLOR, &dummy);
	XAllocNamedColor(disp, cmap, opt_cb, &BG_COLOR, &dummy);
	XAllocNamedColor(disp, cmap, opt_cbl, &BGLINE_COLOR, &dummy);

	XSetBackground(disp, gc, BG_COLOR.pixel);
	XSetLineAttributes(disp, gc, 0, LineSolid, CapNotLast, JoinRound);

	FONT = XLoadQueryFont(disp, opt_font);
	Font font = XLoadFont(disp, opt_font);
	XSetFont(disp, gc, font); 
	return gc;
}

void usage(char *name) {
	printf("usage: %s [<options>...]\n", name);
	printf("    -g <width>x<height>+<x>+<y> : window size.\n");
	printf("    -mt <val>   : max value of temperature.\n");
	printf("    -mf <val>   : max value of freq.\n");
	printf("    -l <val>    : max logging count.\n");
	printf("    -i <val>    : interval sec.\n");
	printf("    -v          : verbose mode.\n");
	printf("    -wm         : enable window manager.\n");
	printf("    -font <font>: 8px X font name.\n");
	exit(0);
}

void initOpt( int argc, char **argv ) {
	int i;
	for (i=1; i<argc; i++) {
		if (strcmp(argv[i],"-g")==0) sscanf(argv[++i],"%dx%d+%d+%d",&opt_w,&opt_h,&opt_x,&opt_y);
		else if (strcmp(argv[i],"-ct")==0) opt_ct = argv[++i];
		else if (strcmp(argv[i],"-cf")==0) opt_cf = argv[++i];
		else if (strcmp(argv[i],"-cb")==0) opt_cb = argv[++i];
		else if (strcmp(argv[i],"-cbl")==0) opt_cbl = argv[++i];
		else if (strcmp(argv[i],"-mt")==0) sscanf(argv[++i],"%d",&opt_mt);
		else if (strcmp(argv[i],"-mf")==0) sscanf(argv[++i],"%d",&opt_mf);
		else if (strcmp(argv[i],"-l")==0) sscanf(argv[++i],"%d",&opt_l);
		else if (strcmp(argv[i],"-i")==0) sscanf(argv[++i],"%d",&opt_i);
		else if (strcmp(argv[i],"-v")==0) opt_v = 1;
		else if (strcmp(argv[i],"-wm")==0) opt_wm = 1;
		else if (strcmp(argv[i],"-font")==0) opt_font = argv[++i];
		else usage(argv[0]);
	}

	if (opt_l == -1) opt_l = opt_w;

	tempLog = (int*)calloc(opt_l, sizeof(int));
	if (tempLog == NULL) error(__LINE__);
	freqLog = (int*)calloc(opt_l, sizeof(int));
	if (freqLog == NULL) error(__LINE__);
	cpuAvgLog = (int*)calloc(opt_l, sizeof(int));
	if (cpuAvgLog == NULL) error(__LINE__);
	cpuMaxLog = (int*)calloc(opt_l, sizeof(int));
	if (cpuMaxLog == NULL) error(__LINE__);
}


int main( int argc, char **argv )
{
	Display *disp = XOpenDisplay(NULL);
	initOpt(argc, argv);

	Window win = XCreateWindow(disp,DefaultRootWindow(disp) ,
			0,0,opt_w,opt_h, /*x,y,w,h*/
			1, /*border_width*/
			CopyFromParent, /* depth */
			InputOutput, /*class*/
			CopyFromParent, /*visual*/
			0, /*valuemask*/
			NULL /*attributs*/
	);

	if (opt_wm == 0) {
		XSetWindowAttributes att;
		att.override_redirect=True;
		XChangeWindowAttributes (disp, win, CWOverrideRedirect, &att);
	}
	XStoreName(disp, win, XCPUMON);
	XSetIconName(disp, win, XCPUMON);
	XClearWindow(disp,win);

	XMoveWindow(disp, win, opt_x, opt_y);
	XSetWindowBorderWidth(disp, win, 0);
	XMapWindow(disp,win);
	XFlush(disp);

	coreNum = cpumon_init();
	sleep(1);

	GC gc = initGc(disp, win);
	while (1) {
		update(disp, win, gc);
		sleep(opt_i);
	}
	return 0;
}

