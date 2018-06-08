# MipsInterpreter

Interpreter for MIPS I Assembly Code

Does not implement exception handling

Implements most instructions listed here:
<http://spimsimulator.sourceforge.net/HP_AppA.pdf>
- Most unimplemented instructions are pseudo-instructions.
- Does not implement exceptions or coprocessor 0.

Supported Data Types:
- .space
- .word
- .byte
- .float
- .double
- .asciiz

## Graphical Interface

Starts if program run with no command line arguments.

## Terminal Interface

Command Line Arguments:
- Flags: -h, -v, -c, --help, --verbose, --callcheck
	- -h, --help prints help message
	- -v, --verbose prints program lines as they execute
	- -c, --callcheck checks procedure calls for register saving conventions
- Argument 1: program file
- Argument 2 (Optional): console input file
- Argument 3 (Optional, only available if Argument 2 present): console output file
- Argument 4 (Optional, other arguments not required): max instructions to execute

## Notes
- Memory is Big-Endian
