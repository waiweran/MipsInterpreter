# MipsInterpreter

Interpreter for MIPS I Assembly Code

Does not implement unsigned instructions

Implements instructions listed here:
<http://www.mrc.uidaho.edu/mrc/people/jff/digital/MIPSir.html>
- Excludes all unsigned operations listed

Implements Floating Point Coprocessor with limited functionality
- Add, Sub, Mult, Div (Float and Double)
- Load, Store (Float and Double)
- Type Conversion (Int, Float, Double)
- Comparisons (Float and Double)
- Branching based on comparison value

Uses program format and Syscall conventions listed here:
<http://logos.cs.uic.edu/366/notes/mips%20quick%20tutorial.htm>

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
