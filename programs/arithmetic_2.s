.data
.space 64
.text
main:
noop # Full Arithmetic Hazard and Bypassing Test
noop # Values initialized using addi (positive only)
noop # Author: Nathaniel Brooke
noop
noop 
noop # Basic Bypassing to ALU: X->D and M->D
addi $1 $0 80	# r1 = 80
addi $2 $1 22	# r2 = r1 + 22 = 102
addi $3 $1 37	# r3 = r1 + 37 = 117
sub $4 $1 $3	# r4 = r1 - r3 = -37
and $5 $1 $3	# r5 = r1 & r3 = 100
sra $6 $4 4 	# r6 = r4 >> 4 = -3
sll $7 $6 5 	# r7 = r6 << 5 = -96
noop
noop # Bypassing 2 values to ALU
addi $1 $0 37	# r1 = 37
addi $2 $0 41	# r2 = 41
sub $3 $2 $1 	# r3 = r2 - r1 = 4
sub $4 $2 $3 	# r4 = r2 - r3 = 37
noop
noop # Basic Bypassing to Memory
addi $1 $0 12	# r1 = 12
sw $1 4($0)		# mem[1] = r1 = 12
addi $2 $0 16	# r2 = 16
addi $3 $0 51	# r3 = 51
sw $3 0($2) 	# mem[r2] = r3 = 51
noop
noop # Bypassing Register 0
addi $0 $0 12	# r0 = 12 (doesn't write)
addi $1 $0 3	# r1 = r0 + 3 = 3
add $2 $0 $0	# r2 = r0 + r0 = 0
sub $3 $0 $0	# r3 = r0 - r0 = 0
sw $1 12($0) 	# mem[3] = r1 = 3
lw $0 12($0) 	# r0 = mem[3] (doesn't write)
add $4 $0 $0	# r4 = r0 + r0 = 0
noop
noop # Exception Bypassing
addi $1 $0 32767# r1 = 32767
sll $1 $1 16	# r1 = 2147418112
addi $1 $1 65535# r1 = 2147483647 (Max positive integer)
addi $2 $0 2	# r2 = 2
addi $3 $0 1	# r3 = 1
add $5 $1 $3	# add ovfl --> rstatus = 1
add $4 $2 $30	# r4 = r2 + rstatus = 3
noop
noop # Bypassing from lw to sw (M->X)
addi $1 $0 830	# r1 = 830
noop				# Avoid RAW hazard to test only lw/sw
noop				# Avoid RAW hazard to test only lw/sw
sw $1 8($0) 	# mem[2] = r1 = 830
noop				# Avoid RAW hazard to test only lw/sw
noop				# Avoid RAW hazard to test only lw/sw
lw $2 8($0) 	# r2 = mem[2] = 830
sw $2 12($0) 	# mem[3] = r2 = 830
lw $3 12($0) 	# r4 = mem[3] = 830
noop
noop # EDGE CASE: Stalling 1 cycle for lw to ALUop
lw $5 8($0) 	# r5 = mem[2] = 830
addi $6 $5 12	# r6 = r5 + 12 = 842
noop
noop # Bypassing to and from Multdiv
addi $1 $0 12	# r1 = 12
noop				# Avoid RAW hazard to test bypassing 1 value
addi $2 $0 13	# r2 = 13
mul $3 $1 $2	# r3 = r1 * r2 = 156
sub $4 $3 $1	# r4 = r3 = r1 = 144
addi $5 $0	4	# r5 = 4
div $6 $1 $5	# r6 = r1 / r5 = 3
addi $7 $0 8	# r7 = 8
addi $8 $0 3	# r8 = 3
mul $9 $8 $7	# r9 = r8 * r7 = 24
div $10 $9 $1	# r10 = r9 / r1 = 2
div $11 $9 $10	# r11 = r9 / r10 = 12
