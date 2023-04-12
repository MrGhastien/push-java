#!/bin/bash
/usr/lib/jvm/java-19-openjdk/bin/java -Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8 -classpath /home/mrghastien/dev/dorset/push/out/production/push push.Main "$@"
