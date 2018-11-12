# Stress Test
# Author: Nathaniel Brooke

.data
	arrays: .space 1200

.text

main:
	la $25 arrays
	addi $2 $0 5
	and $3 $25 $25
	addi $4 $25 200
	jal initArr		# Array 1 = [5:5:600] in data section from 0 to 200
	add $3 $0 $4	
	addi $2 $2 -1	
	addi $4 $4 200
	jal initArr		# Array 2 = [4:4:800] in data section from 200 to 400
	addi $2 $25 0
	addi $4 $0 200
	sll $5 $0 18
	addi $6 $25 400
	jal arrOp		# Array 3 = Array 1 + Array 2 in data section from 400 to 600
	addi $3 $3 200
	sll $5 $5 2
	addi $6 $6 200
	jal arrOp		# Array 4 = Array 1 | Array 3 in data section from 600 to 800
	addi $2 $25 600
	addi $3 $25 200
	addi $5 $0 1
	addi $6 $6 200
	jal arrOp		# Array 5 = Array 4 - Array 2 in data section from 800 to 1000
	addi $2 $25 800
	addi $3 $25 0
	addi $5 $0 2
	addi $6 $6 200
	jal arrOp		# Array 6 = Array 5 & Array 1 in data section from 1000 to 1200
	addi $2 $25 1000
	addi $3 $25 200
	addi $5 $0 0
	jal arrOp		# Array 6 = Array 6 + Array 2 in data section from 1000 to 1200
	jal arrOp		# Array 6 = Array 6 + Array 2 in data section from 1000 to 1200
	jal arrOp		# Array 6 = Array 6 + Array 2 in data section from 1000 to 1200
	add $3 $3 $2
	jal sumArr		# Sum elements in Array 6
	j end

# Initialize Array: $2 = stepVal, $3 = startIndex, $4 = stopIndex
initArr: or $11 $0 $3 
	and $10 $0 $0
arrinitl: add $10, $10, $2 
	sw $10, 0($11)
	addi $11, $11, 4
	blt $11 $4 arrinitl
	jr $ra

# Array Operation: $2 = start1, $3 = start2, $4 = length, $5 = operation $6 = outIndex
arrOp: addi $10 $0 1  
	and $11 $2 $2
	sub $12 $3 $0
	addi $13 $6 0
	blt $5 $10 addArray
	addi $10 $10 1
	blt $5 $10 subArray
	addi $10 $10 1
	blt $5 $10 andArray
	addi $10 $10 1
	blt $5 $10 orArray
	jr $ra
addArray: lw $14 0($11)
	lw $15 0($12)
	add $14 $15 $14
	sw $14 0($13)
	addi $11 $11 4
	addi $12 $12 4
	addi $13 $13 4
	sub $15 $13 $6
	blt $15 $4 addArray
	jr $ra
subArray: lw $14 0($11)
	lw $15 0($12)
	sub $14 $14 $15
	sw $14 0($13)
	addi $11 $11 4
	addi $12 $12 4
	addi $13 $13 4
	sub $15 $13 $6
	blt $15 $4 subArray
	jr $ra
andArray: lw $14 0($11)
	lw $15 0($12)
	and $14 $15 $14
	sw $14 0($13)
	addi $11 $11 4
	addi $12 $12 4
	addi $13 $13 4
	sub $15 $13 $6
	blt $15 $4 andArray
	jr $ra
orArray: lw $14 0($11)
	lw $15 0($12)
	or $14 $15 $14
	sw $14 0($13)
	addi $11 $11 4
	addi $12 $12 4
	addi $13 $13 4
	sub $15 $13 $6
	blt $15 $4 orArray
	jr $ra

# Sum Array: $2 = startIndex, $3 = stopIndex, output = $4
sumArr: or $10 $2 $0 
	and $4 $4 $0
sumlp: lw $11 0($10)
	add $4 $4 $11
	addi $10 $10 4
	bne $10 $3 sumlp
	jr $ra

end: add $2 $4 $0
	addi $3 $4 0
	sub $5 $4 $0
	and $6 $4 $4
	or $7 $4 $0
	sll $8 $4 0
	sra $9 $4 0
	addi $12 $0 1
	mul $10 $4 $12
	div $11 $4 $12



