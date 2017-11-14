.data

intro: .asciiz "Enter a Player\n"
enter: .asciiz "\n"
.align 2
head: .space 4
done: .word 1146048069

.text

main:
	# Save Return Address
	addi $sp, -4
	sw $ra, 0($sp)

	# Initial value of Head
	sw $zero, head


# Generate list of Players
makeList:
	# Print the intro
	li $v0, 4
	la $a0, intro
	syscall

	# Get a Player
	jal makePlayer
	beqz $v0, endList

	# Store head
	lw $t0, head
	bnez $t0, skipHead
	sw $v0, head
	j savedHead
skipHead:

	# Save location of Player
	sw $v0, 0($s1)
savedHead:
	move $s0, $v0
	move $s1, $v1
	j makeList

# Fill end of linked list
endList:
	sw $zero, 0($s1)


# Sorts the linked list by JPG
sortList: 
	# Variable to determine whether it's sorted
	li $s0, 0

	# Compare the head with the next one
	lw $t0, head     # t0 is the head player address
	lw $t1, 100($t0) # t1 is the head JPG
	lw $t2, 104($t0) # t2 is the next player address
	beqz $t2, endSort
	lw $t3, 100($t2) # t3 is the next JPG
	ble $t1, $t3, endFirstSwap
	
	# Swap the head and the next one
	li $s0, 1
	lw $t4, 104($t2) # t4 is the next next player address
	sw $t2, head
	sw $t4, 104($t0)
	sw $t0, 104($t2)
endFirstSwap:

# Sort the rest of the list
	lw $t0, head
innerSort:
	# t0 is the current player address
	lw $t1, 104($t0) # t1 is the next player address
	lw $t2, 100($t1) # t2 is the next JPG
	lw $t3, 104($t1) # t3 is the next next player address
	beqz $t3, endInnerLoop
	lw $t4, 100($t3) # t4 is the next next JPG
	ble $t2, $t4, dontSwap

	# Swap the two values
	li $s0, 1
	lw $t5, 104($t3)
	sw $t3, 104($t0)
	sw $t1, 104($t3)
	sw $t5, 104($t1)

# End of the loops
dontSwap: 
	lw $t0, 104($t0) 
	j innerSort
endInnerLoop:
	bnez $s0, sortList
endSort:

# Prints the linked list
	lw $t0, head
printList:
	# Load and print the name
	li $v0, 4
	move $a0, $t0
	syscall

	# Load and print the JPG
	li $v0, 1
	lw $a0, 100($t0)
	syscall

	# Print an enter
	li $v0, 4
	la $a0, enter
	syscall

	# Load the next player
	lw $t0, 104($t0)
	bnez $t0, printList


# End Program
	lw $ra, 0($sp)
	addi $sp, 4
	jr $ra


# Creates a player
# v0 contains the address of the player
# v1 contains the player's next slot
makePlayer:
	
	# Save Registers
	addi $sp, -8
	sw $ra, 0($sp)
	sw $s0, 4($sp)

	# Allocate memory for string, int, address
	li $v0, 9
	li $a0, 108
	syscall
	move $s0, $v0	# s0 is the memory address of the player

	# Read a string from the console
	li $v0, 8
	move $a0, $s0
	li $a1, 100
	syscall

	# Check if done
	jal stringComp
	li $t1, 1
	beq $v0, $t1, lastPlayer

	# Get rid of last enter
	jal enterRemove

	# Read and subtract ints from the console
	li $v0, 5
	syscall
	move $t0, $v0 # t0 is the JPG
	li $v0, 5
	syscall
	sub $t0, $t0, $v0

	# Put the JPG in player
	sw $t0, 100($s0)

	# Return the player
	move $v0, $s0
	addi $v1, $s0, 104

	# Reset Register values
	lw $ra, 0($sp)
	lw $s0, 4($sp)
	addi $sp, 8

	# Return
	jr $ra

lastPlayer:
	# Set Return Values to 0
	li $v0, 0
	li $v1, 0

	# Reset Register values
	lw $ra, 0($sp)
	lw $s0, 4($sp)
	addi $sp, 8

	# Return
	jr $ra


# Determines whether a string equals "DONE"
# a0 contains the address of the string
# v0 contains 1 if they're the same, 0 otherwise
stringComp:
	lw $t0, 0($a0)
	lw $t1, done
	seq $v0 $t0 $t1
	jr $ra


# Removes enter (10) off end of a given string
# Replaces the enter with a space (32)
# a0 contains the address of the string
enterRemove: 
	# Loop checking for enter byte
	li $t9, 10
	lb $t0 0($a0)
	beq $t0, $t9, foundEnter
	addi $a0, 1
	j enterRemove

# Change to space and exit
foundEnter:
	li $t9, 32
	sb $t9, 0($a0)
	jr $ra