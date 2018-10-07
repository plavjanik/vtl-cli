# Velocity Template Language (VTL) Command-line Interface

[![Build Status](https://travis-ci.com/plavjanik/vtl-cli.svg?branch=master)](https://travis-ci.com/plavjanik/vtl-cli)
[![codecov](https://codecov.io/gh/plavjanik/vtl-cli/branch/master/graph/badge.svg)](https://codecov.io/gh/plavjanik/vtl-cli)

This is simple Java command-line application that uses Apache Velocity to 'merge' VTL templates from console.

## Build

```
./gradlew build
```

## Usage

```
java -jar build/libs/vtl-cli.jar templates/hello.vtl -c name=world
```

If the `hello.vtl` file contains:

    Hello, ${name}!

Then the standard output of `vtl-cli` is:

    Hello, world!

## Syntax

```
Apache Velocity Template Language CLI
Usage: vtl [-o=<outputFile>] [-c=<String=String>]... FILE
      FILE                 File with a Velocity template to process
  -c, --context=<String=String>
                           Context variable for Velocity (can be repeated)
  -o, --out=<outputFile>   Output file (default: print to console)
```