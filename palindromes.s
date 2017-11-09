.data

intro: .asciiz "Enter the number of palindromes "
newline: .asciiz "\n"

.text

main:
	# Take Input Argument
	li $v0, 4
	la $a0, intro
	syscall
	li $v0, 5
	syscall

	# Initialize Variables
	li $t0, 0 #count of palindromes
	move $t1, $v0 #number of palindromes requested
	li $t2, 10 #current number

loop:
	# Loop Exit
	beq $t0, $t1, end

	# Increment current number
	addi $t2, 1

	# Set up variables
	move $t3, $t2 # Number being reversed
	li $t4, 0 # Reversed number
	li $t5, 0 # Digit of number
	li $t6, 10 # The number 10

innerloop: 

	# Are we done getting digits?
	beqz $t3, innerend

	# Move current digits in reversed number over
	mult $t4 $t6
	mflo $t4

	# Get next digit and add it
	div $t3 $t6
	mfhi $t5
	mflo $t3
	add $t4, $t4, $t5

	# Jump for loop
	j innerloop

innerend:
	# branch if reversed number equals original
	bne $t2,$t4, noprint

	# Print the number, increment counter
	li $v0, 1
	move $a0, $t2
	syscall
	li $v0, 4
	la $a0, newline
	syscall
	addi $t0, 1

noprint:
	# Loop Command
	j loop

end:
	jr $ra