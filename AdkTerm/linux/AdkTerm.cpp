/**
Android USB connection termial driver.

@Author kotemru.org
@Licence apache/2.0
*/

#include <stdlib.h>
#include <fcntl.h>
#include <errno.h>
#include <unistd.h>
#include <stdio.h>
#include <termios.h>
#include <sys/select.h>
#include <sys/ioctl.h>
#include <signal.h>
#include <string.h>

#include "AOA/AOA.h"


#define CALL(VAR, FUNC)\
    int VAR = FUNC;\
    if (VAR < 0) {\
        fprintf(stderr, "%s(%d) ERROR code=%d,%d call=%s\n", __FILE__, __LINE__, VAR, errno, #FUNC);\
        exit(-1);\
    }

// USB Connector opend.
AOA acc("kotemaru.org",
        "AdkTerm",
        "Terminal for ADK",
        "1.0",
        "http://blog.kotemaru.org/AdkTerm",
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

/**
 * usblib async read callback handler.
 */
static void usb_read_handler(struct libusb_transfer *transfer) {
    int   fdm = *(int*) transfer->user_data;
    char* buffer = (char*) transfer->buffer;
    int   bufferLen = transfer->actual_length;
    if (bufferLen > 0) { 
#ifdef DEBUG
        printf("->in:%d %c\n",bufferLen,buffer[0]);
#endif
        write(fdm, buffer, bufferLen);
    }
    int result = libusb_submit_transfer(transfer);
}

/**
 * Pipe process.
 */
static void master(int fdm, int fds) {
    close(fds);

    // Connect USB Device.
    CALL(rc1, acc.connect(20));

    // Sending connect check messeage.
    const char* msg = "\nConnect.\n";
    CALL(rc2, acc.write((unsigned char*)msg, strlen(msg), 30000));
    printf("%s\n",msg);


    struct timeval tv;
    tv.tv_sec  = 0;     // seconds
    tv.tv_usec = 100;   // milliseconds  ( .1 sec)

    fd_set fd_in;
    char inBuffer[512];
    unsigned char outBuffer[512];

    // setup USB->PC Data transfer callback.
    acc.readAsync(usb_read_handler,inBuffer,sizeof(inBuffer),&fdm, 2000);

    // Data transfer loop.
    while (1) {
	// USB->PC 
        acc.handleAsync(&tv);

	// PC->USB
        FD_ZERO(&fd_in);
        FD_SET(fdm, &fd_in);
        CALL(rc3, select(fdm+1, &fd_in, NULL, NULL, &tv));
        if (FD_ISSET(fdm, &fd_in)) {
            CALL(len, read(fdm, outBuffer, sizeof(outBuffer)));
            acc.write(outBuffer, len, 1000);
        }
    }
}


/**
 * Login shell child process execute.
 */
static void slave(int fdm, int fds, char* loginCmd) {
    close(fdm);

    // Terminal row mode setting.
    struct termios termios;
    CALL(rc1, tcgetattr(fds, &termios));
    cfmakeraw(&termios);
    CALL(rc2, tcsetattr(fds, TCSANOW, &termios));

    // Connect standard I/O to this terminal.
    close(0); close(1); close(2);
    dup2(fds,0); dup2(fds,1); dup2(fds,2);
    close(fds);

    // Connect child process controler to this terminal.
    setsid(); 
    ioctl(0, TIOCSCTTY, 1);

    execlp(loginCmd, loginCmd, NULL);

    fprintf(stderr, "Can not executed(%d) '%s'\n", errno, loginCmd);
} 

/**
 * Usage: AdkTerm [<login command>]
 *    <login command>: default is /bin/login.
 */
int main(int argc, char *argv[])
{
    signal(SIGINT, signal_callback_handler);
    signal(SIGTERM, signal_callback_handler);

    CALL(fdm, posix_openpt(O_RDWR));
    CALL(rc1, grantpt(fdm));
    CALL(rc2, unlockpt(fdm));
    CALL(fds, open(ptsname(fdm), O_RDWR));

    if (fork()) {
        master(fdm, fds);
    } else {
        char* loginCmd = (argc>=2) ? argv[1] : (char*)"/bin/login";
        slave(fdm, fds, loginCmd);
    }
    return 0;
}
