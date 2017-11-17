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
- Branching

Note FP instructions are not as well tested as other instructions.

Uses program format and Syscall conventions listed here:
<http://logos.cs.uic.edu/366/notes/mips%20quick%20tutorial.htm>

Supported Data Types:
- .space
- .word
- .float
- .double
- .asciiz
