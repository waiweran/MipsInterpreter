# Basic Control Test
# Registers 10,11 track correct and 20,21 track incorrect
# Author: Nathaniel Brooke

.text
main: # Initialize Values
	addi $22 $0 4		# r1 = 4
	addi $2 $0 5		# r2 = 5
	sub $3 $0 $22		# r3 = -4
	sub $4 $0 $2		# r4 = -5
	
	# Basic Test of BNE
	bne $22 $2 b1		# r1 != r2 --> taken
	addi $20 $20 1		# r20 += 1 (Incorrect)
b1: addi $10 $10 1	# r10 += 1 (Correct)
	bne $2 $2 b2		# r2 == r2 --> not taken
	addi $10 $10 1		# r10 += 1 (Correct) 
b2: add $11 $11 $10		# r10 should be 2
	add $21 $21 $20		# r20 should be 0
	and $10 $0 $0		# r10 = 0
	and $20 $0 $0		# r20 = 0
	
	# Basic Test of BLT
	blt $22 $2 b3		# r1 < r2 --> taken
	addi $20 $20 1		# r20 += 1 (Incorrect)
b3: addi $10 $10 1	# r10 += 1 (Correct)
	blt $2 $2 b4		# r2 == r2 --> not taken
	addi $10 $10 1		# r10 += 1 (Correct) 
b4: blt $4 $22 b5		# r4 < r1 --> taken
	addi $20 $20 1		# r20 += 1 (Incorrect)
b5: addi $10 $10 1	# r10 += 1 (Correct)
	blt $2 $22 b6		# r2 > r1 --> not taken
	addi $10 $10 1		# r10 += 1 (Correct) 
b6: blt $4 $3 b7		# r4 < r3 --> taken
	addi $20 $20 1		# r20 += 1 (Incorrect)
b7: addi $10 $10 1	# r10 += 1 (Correct)
	blt $3 $4 b8		# r3 > r4 --> not taken
	addi $10 $10 1		# r10 += 1 (Correct) 
b8: add $11 $11 $10		# r10 should be 6
	add $21 $21 $20		# r20 should be 0
	and $10 $0 $0		# r10 = 0
	and $20 $0 $0		# r20 = 0
	
	# Basic Test of J
	j j1				# Jump to j1
	addi $20 $20 1		# r20 += 1 (Incorrect)
j1: addi $10 $10 1	# r10 += 1 (Correct)
	add $11 $11 $10		# r10 should be 1
	add $21 $21 $20		# r20 should be 0
	and $10 $0 $0		# r10 = 0
	and $20 $0 $0		# r20 = 0

	# Basic test of JAL, JR
	addi $ra $0 68		# r31 = 68
	jal j2				# Jump to j2, r31 = PC + 1 
	addi $20 $20 1		# r20 += 1 (Incorrect)
	addi $20 $20 1		# r20 += 1 (Incorrect)
	addi $20 $20 1		# r20 += 1 (Incorrect)
j2: addi $10 $10 1	# r10 += 1 (Correct)
	nop					# nop to avoid JAL hazard
	addi $31 $31 10		# r31 = r31 + 10 
	jr $ra				# PC = r31
	addi $20 $20 1		# r20 += 1 (Incorrect)
	addi $20 $20 1		# r20 += 1 (Incorrect)
	addi $20 $20 1		# r20 += 1 (Incorrect)
	addi $10 $10 1		# r10 += 1 (Correct)
	add $11 $11 $10		# r10 should be 2
	add $21 $21 $20		# r20 should be 0
	and $10 $0 $0		# r10 = 0
	and $20 $0 $0		# r20 = 0
	
	# Basic Text of BGTZ
	addi $30 0			# r30 = 0
	bgtz $30 e1			# r30 == 0 --> not taken
	addi $10 $10 1		# r10 += 1 (Correct)
e1: addi $30 10			# r30 = 10
	bgtz $30 e2			# r30 != 0 --> taken
	addi $20 $20 1		# r20 += 1 (Incorrect)
e2: addi $10 $10 1	# r10 += 1 (Correct)
	add $11 $11 $10		# r10 should be 2
	add $21 $21 $20		# r20 should be 0
	and $10 $0 $0		# r10 = 0
	and $20 $0 $0		# r20 = 0
	
	# Final Check (All Correct)
	and $0 $11 $11		# r11 should be 13
	and $0 $21 $21		# r21 should be 0
