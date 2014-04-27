#!/bin/sh

BASE=`dirname $0`
cd $BASE

scp -r bin pi@rpi:NicoNamaAlert/
ssh pi@rpi sudo service nnalert restart

