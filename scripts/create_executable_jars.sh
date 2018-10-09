#!/bin/sh
cat scripts/launch_script_stub.sh build/vtl-cli.jar > build/vtl && chmod +x build/vtl 

mkdir -p build/zos
cat scripts/launch_script_stub.zos.sh build/vtl-cli.jar > build/zos/vtl && chmod +x build/zos/vtl
