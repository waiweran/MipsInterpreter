.text
main:
nop # Basic Control Test with no Data Hazards
nop # Values initialized using addi (positive only) and sub
nop # Registers 10,11 track correct and 20,21 track incorrect
nop # Values and comments in the test of JR must be updated if code modified.
nop # Author: Nathaniel Brooke
nop
nop
nop # Initialize Values
addi $1 $0 4		# r1 = 4
addi $2 $0 5		# r2 = 5
sub $3 $0 $1		# r3 = -4
sub $4 $0 $2		# r4 = -5
nop
nop # Basic Test of BNE
bne $1 $2 b1		# r1 != r2 --> taken
addi $20 $20 1		# r20 += 1 (Incorrect)
b1: addi $10 $10 1	# r10 += 1 (Correct)
bne $2 $2 b2		# r2 == r2 --> not taken
addi $10 $10 1		# r10 += 1 (Correct) 
b2: nop				# Landing pad for branch
add $11 $11 $10		# r10 should be 2
add $21 $21 $20		# r20 should be 0
and $10 $0 $0		# r10 = 0
and $20 $0 $0		# r20 = 0
nop
nop # Basic Test of BLT
blt $1 $2 b3		# r1 < r2 --> taken
addi $20 $20 1		# r20 += 1 (Incorrect)
b3: addi $10 $10 1	# r10 += 1 (Correct)
blt $2 $2 b4		# r2 == r2 --> not taken
addi $10 $10 1		# r10 += 1 (Correct) 
b4: nop				# Landing pad for branch
blt $4 $1 b5		# r4 < r1 --> taken
addi $20 $20 1		# r20 += 1 (Incorrect)
b5: addi $10 $10 1	# r10 += 1 (Correct)
blt $2 $1 b6		# r2 > r1 --> not taken
addi $10 $10 1		# r10 += 1 (Correct) 
b6: nop				# Landing pad for branch
blt $4 $3 b7		# r4 < r3 --> taken
addi $20 $20 1		# r20 += 1 (Incorrect)
b7: addi $10 $10 1	# r10 += 1 (Correct)
blt $3 $4 b8		# r3 > r4 --> not taken
addi $10 $10 1		# r10 += 1 (Correct) 
b8: nop				# Landing pad for branch
add $11 $11 $10		# r10 should be 6
add $21 $21 $20		# r20 should be 0
and $10 $0 $0		# r10 = 0
and $20 $0 $0		# r20 = 0
nop
nop # Basic Test of J
j j1				# Jump to j1
addi $20 $20 1		# r20 += 1 (Incorrect)
j1: addi $10 $10 1	# r10 += 1 (Correct)
add $11 $11 $10		# r10 should be 1
add $21 $21 $20		# r20 should be 0
and $10 $0 $0		# r10 = 0
and $20 $0 $0		# r20 = 0
nop
nop # Basic test of JAL, JR
addi $ra $0 68		# r31 = 68
jal j2				# Jump to j2, r31 = PC + 1 = 61
addi $20 $20 1		# r20 += 1 (Incorrect)
addi $20 $20 1		# r20 += 1 (Incorrect)
addi $20 $20 1		# r20 += 1 (Incorrect)
j2: addi $10 $10 1	# r10 += 1 (Correct)
nop					# nop to avoid JAL hazard
addi $31 $31 10		# r31 = r31 + 10 = 71
jr $ra				# PC = r31 = 71
addi $20 $20 1		# r20 += 1 (Incorrect)
addi $20 $20 1		# r20 += 1 (Incorrect)
addi $20 $20 1		# r20 += 1 (Incorrect)
addi $10 $10 1		# r10 += 1 (Correct)
add $11 $11 $10		# r10 should be 2
add $21 $21 $20		# r20 should be 0
and $10 $0 $0		# r10 = 0
and $20 $0 $0		# r20 = 0
nop
nop # Basic Text of BEX, SETX
addi $30 0			# r30 = 0
nop					# Avoid setx RAW hazard
nop					# Avoid setx RAW hazard
bgtz $30 e1			# r30 == 0 --> not taken
addi $10 $10 1		# r10 += 1 (Correct)
e1: nop				# Landing pad for branch
addi $30 10			# r30 = 10
nop					# Avoid setx RAW hazard
nop					# Avoid setx RAW hazard
bgtz $30 e2			# r30 != 0 --> taken
addi $20 $20 1		# r20 += 1 (Incorrect)
e2: addi $10 $10 1	# r10 += 1 (Correct)
add $11 $11 $10		# r10 should be 2
add $21 $21 $20		# r20 should be 0
and $10 $0 $0		# r10 = 0
and $20 $0 $0		# r20 = 0
nop
nop # Final Check (All Correct)
and $0 $11 $11		# r11 should be 13
and $0 $21 $21		# r21 should be 0
