# MipsInterpreter

Interpreter for MIPS I Assembly Code

## Instructions

Does not implement exception handling

Implements most instructions listed here:
<http://spimsimulator.sourceforge.net/HP_AppA.pdf>
- Most unimplemented instructions are pseudo-instructions.
- Does not implement exceptions or coprocessor 0.

## Data

Supported Data Types:
- .space
- .word
- .byte
- .float
- .double
- .ascii
- .asciiz

Automatically word-aligns all data.

## Graphical Interface

Starts if program run with no command line arguments.

## Terminal Interface

Command Line Arguments:
- Flags: -h, -v, -c, -b, -l, --help, --verbose, --callcheck, --bigendian, --littlendian
	- -h, --help prints help message
	- -v, --verbose prints program lines as they execute
	- -c, --callcheck checks procedure calls for register saving conventions
	- -b, --bigendian sets memory to Big-Endian
	- -l, --littleendian sets memory to Little-Endian
- Argument 1: program file
- Argument 2 (Optional): console input file
- Argument 3 (Optional, only available if Argument 2 present): console output file
- Argument 4 (Optional, other arguments not required): max instructions to execute

## Implementation Details
- Memory defaults to Little-Endian

