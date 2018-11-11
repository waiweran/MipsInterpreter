.text
main:
nop # Advanced Control Test with Bypassing
nop # Values initialized using addi (positive only)
nop # Registers 10,11 track correct and 20,21 track incorrect
nop # Values in the first two tests be updated if the number of lines is modified
nop # Author: Nathaniel Brooke
nop
nop
nop # Test Bypassing into JR
addi $ra $0 12		# r31 = 12
nop					# Avoid RAW hazard for jr
addi $ra $0 14		# r31 = 14 (with RAW hazard)
jr $ra 				# PC = r31 = 14
addi $20 $20 1		# r20 += 1 (Incorrect)
addi $20 $20 1		# r20 += 1 (Incorrect)
addi $10 $10 1		# r10 += 1 (Correct)
add $11 $11 $10		# r10 should be 1
add $21 $21 $20		# r20 should be 0
and $10 $0 $0		# r10 = 0
and $20 $0 $0		# r20 = 0
nop
nop # Test JAL into JR
addi $ra $0 28		# r31 = 28
nop					# Avoid RAW hazard for jr
jal j1				# jal to jr (with RAW hazard)
nop					# Spacer
j end2				# Jump to test cleanup
nop					# Spacer
j1: jr $ra 			# jr immediately after jal
addi $20 $20 1		# r20 += 1 (Incorrect)
addi $20 $20 1		# r20 += 1 (Incorrect)
end2: nop			# Landing pad for jump
add $21 $21 $20		# r20 should be 0
and $20 $0 $0		# r20 = 0
nop
nop # Test Bypassing into Branch (with loops)
addi $1 $0 5		# r1 = 5
b1: addi $2 $2 1	# r2 += 1
blt $2 $1 b1		# if r2 < r1 take branch (5 times)
b2: addi $1 $1 1	# r1 += 1
addi $3 $3 2		# r3 += 2
blt $3 $1 b2		# if r3 < r1 take branch (4 times)
add $10 $2 $3		# r10 = r2 + r3
add $11 $11 $10		# r10 should be 15
and $10 $0 $0		# r10 = 0
nop
nop # Test bypassing into bex
addi $30 0				# r30 = 0
nop					# Avoid RAW hazard from first setx
addi $30 10				# r30 = 10 (with RAW hazard)
bgtz $30 e1				# Branch should be taken
addi $20 $20 1		# r20 += 1 (Incorrect)
e1: addi $10 $10 1	# r10 += 1 (Correct)
add $11 $11 $10		# r10 should be 1
add $21 $21 $20		# r20 should be 0
and $10 $0 $0		# r10 = 0
and $20 $0 $0		# r20 = 0
nop
nop # Test Branch/Jump Race Condition
addi $4 $0 1		# r4 = 1
nop					# Avoid RAW hazard
bne $1 $0 rgood		# Branch racing (should branch)
j rbad				# Jump racing (should not jump)
nop					# Spacer
rbad: nop			# Landing pad for jump
addi $20 $20 1		# r20 += 1 (Incorrect)
addi $20 $20 1		# r20 += 1 (Incorrect)
j end1				# Jump to test cleanup
nop					# Spacer
rgood: nop			# Landing pad for branch
addi $10 $10 1		# r10 += 1 (Correct)
addi $10 $10 1		# r10 += 1 (Correct)
j end1				# Jump to test cleanup
nop					# Spacer
end1: nop			# Landing pad for jump
add $11 $11 $10		# r10 should be 2
add $21 $21 $20		# r20 should be 0
and $10 $0 $0		# r10 = 0
and $20 $0 $0		# r20 = 0
nop
nop # Final Check (All Correct)
and $0 $11 $11		# r11 should be 19
and $0 $21 $21		# r21 should be 0
