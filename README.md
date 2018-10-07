# Velocity Template Language (VTL) Command-line Interface

This is simple Java command-line application that uses Apache Velocity to 'merge' VTL templates from console.

## Build

```
./gradlew build
```

## Usage

```
java -jar builds/libs/vtl-cli.jar templates/hello.vtl -c name=world
```

If the `hello.vtl` file contains:

    Hello, ${name}!

Then the standard output of `vtl-cli` is:

    Hello, world!
