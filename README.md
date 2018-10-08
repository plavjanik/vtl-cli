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
vtl [-e] [-o=<outputFile>] [-y=<yamlContextFile>] [-c=variable=value]... FILE

Parameters:
      FILE                 File with a Velocity template to process

Options:
  -c, --context=variable=value
                           Context variable for Velocity (can be repeated)
  -y, --yaml-context=<yamlContextFile>
                           YAML file with context variables
  -e, --env-context        Set the context variables from environment
  -o, --out=<outputFile>   Output file (default: print to console)
```

## Loading context from YAML file

You can write context variables into a YAML file - for example:

```yaml
name: world
```

and the use it:

```
vtl --yaml-context templates/hello.yml templates/hello.vtl
```

If you have nested YAML properties like:

```yaml
nested:
    name: world
```

you can references it as `${nested.name}`.

## VTL

The template language is described in[ Velocity User Guide](http://velocity.apache.org/engine/2.0/user-guide.html).