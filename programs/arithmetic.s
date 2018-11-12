# Basic Arithmetic Test
# Author: Nathaniel Brooke
.text
main: # Initialize Values
	addi $3 $0 1	# r3 = 1
	addi $4 $0 35	# r4 = 35
	addi $20 $0 3	# r1 = 3
	addi $2 $0 21	# r2 = 21
	sub $3 $0 $3	# r3 = -1
	sub $4 $0 $4	# r4 = -35
	 
	# Positive Value Tests
	add $5 $2 $20	# r5 = r2 + r1 = 24
	sub $6 $2 $20	# r6 = r2 - r1 = 18
	and $7 $2 $20	# r7 = r2 & r1 = 1
	or $8 $2 $20 	# r8 = r2 | r1 = 23
	sll $9 $20 4 	# r9 = r1 << 4 = 48
	sra $10 $2 2	# r10 = r2 >> 2 = 5
	 
	# Negative Value Tests
	addi $11 $2 -89	# r11 = r2 - 89 = -68
	add $12 $4 $2	# r12 = r4 + r2 = -14
	sub $13 $4 $2	# r13 = r4 - r2 = -56
	sll $14 $3 16	# r14 = r3 << 30 = -65536
	sra $15 $4 16	# r15 = r4 >> 16 = -1