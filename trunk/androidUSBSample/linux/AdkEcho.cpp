/**
Android USB connection sample.

@Author kotemru.org
@Licence apache/2.0
*/

#include <stdlib.h>
#include <fcntl.h>
#include <errno.h>
#include <unistd.h>
#include <stdio.h>
#include <signal.h>
#include <string.h>

#include "AOA/AOA.h"

// USB Connector opend.
AOA acc("kotemaru.org",
        "AdkSample",
        "Sample for ADK",
        "1.0",
        "http://blog.kotemaru.org/androidUSBSample",
        "000000000000001") ;

/**
 * Disconnect USB innterrupt aborted.
 */
void signal_callback_handler(int signum)
{
    fprintf(stderr, "\ninterrupt %d\n",signum);
    acc.disconnect();
    exit(0);
}

static void error(char *msg, int rc) {
	fprintf(stderr,"Error(%d,%s): %s\n",rc,strerror(errno),msg);
    	acc.disconnect();
	exit(0);
}

int main(int argc, char *argv[])
{
    signal(SIGINT, signal_callback_handler);
    signal(SIGTERM, signal_callback_handler);

	unsigned char buff[1024];

	acc.connect(100);
	// Echo back.
	while (1) {
		int len = acc.read(buff, sizeof(buff), 1000000);
		if (len < 0) error("acc.read",len);
		buff[len+1] = '\0';
		printf("USB>%s\n", buff);
		for (int i=0; i<len; i++) buff[i] = buff[i] - 0x20;
		acc.write(buff, len, 1000);
	}
}
