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
- Flags: -h, -v, -c, --help, --verbose, --callcheck, --endian, --runs
	- -h, --help prints help message
	- -v, --verbose prints program lines as they execute
	- -c, --callcheck checks procedure calls for register saving conventions
	- --endian can be set to big or little (Ex. --endian=big) Little-Endian default
	- --runs sets the max number of instructions to execute (Ex. --runs=100)
- Argument 1: program file
- Argument 2 (Optional): input file
- Argument 3 (Optional, only available if Argument 2 present): output file

