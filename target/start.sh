#!/bin/bash

BINDIR=$(dirname "$(readlink -fn "$0")")
cd "$BINDIR"

screen -S Minecraftbackend-0.0.1-SNAPSHOT.jar bash -c "sh ./loop.sh"
