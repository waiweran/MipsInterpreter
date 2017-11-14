.data

intro: .asciiz "Enter the number to be n "

.text

main:
	# Take Input Argument
	li $v0, 4
	la $a0, intro
	syscall
	li $v0, 5
	syscall
	move $a0, $v0
	addi $sp, -4
	sw $ra, 0($sp)
	jal fn
	lw $ra, 0($sp)
	addi $sp, 4
	move $a0, $v0
	li $v0 1
	syscall
	jr $ra


fn:
	# Branch to base case
	beqz $a0 base

# Regular Case 

	# Save variables
	addi $sp -8
	sw $s0, 0($sp)
	sw $ra, 4($sp)

	# s0 = n + 1
	addi $s0, $a0, 1

	# s0 = 3*(n + 1)
	li $t1, 3
	mult $s0, $t1
	mflo $s0

	# v0 = fn(n - 1)
	addi $a0, -1
	jal fn

	# v0 = fn(n - 1) - 1
	addi $v0, -1

	# v0 = 3*(n + 1) + fn(n - 1) - 1
	add $v0, $v0, $s0

	# reload variables
	lw $s0, 0($sp)
	lw $ra, 4($sp)
	addi $sp 8

	# return
	jr $ra

# Base Case
base: 
	li $v0, 1
	jr $ra