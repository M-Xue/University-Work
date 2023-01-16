########################################################################
# COMP1521 21T2 -- Assignment 1 -- Snake!
# <https://www.cse.unsw.edu.au/~cs1521/21T2/assignments/ass1/index.html>
#
#
# !!! IMPORTANT !!!
# Before starting work on the assignment, make sure you set your tab-width to 8!
# For instructions, see: https://www.cse.unsw.edu.au/~cs1521/21T2/resources/mips-editors.html
# !!! IMPORTANT !!!
#
#
# This program was written by Max Xue (z5267325)
# on 07/07/2021
#
# Version 1.0 (2021-06-24): Team COMP1521 <cs1521@cse.unsw.edu.au>
#

	# Requires:
	# - [no external symbols]
	#
	# Provides:
	# - Global variables:
	.globl	symbols
	.globl	grid
	.globl	snake_body_row
	.globl	snake_body_col
	.globl	snake_body_len
	.globl	snake_growth
	.globl	snake_tail

	# - Utility global variables:
	.globl	last_direction
	.globl	rand_seed
	.globl  input_direction__buf

	# - Functions for you to implement
	.globl	main
	.globl	init_snake
	.globl	update_apple
	.globl	move_snake_in_grid
	.globl	move_snake_in_array

	# - Utility functions provided for you
	.globl	set_snake
	.globl  set_snake_grid
	.globl	set_snake_array
	.globl  print_grid
	.globl	input_direction
	.globl	get_d_row
	.globl	get_d_col
	.globl	seed_rng
	.globl	rand_value


########################################################################
# Constant definitions.

N_COLS          = 15
N_ROWS          = 15
MAX_SNAKE_LEN   = N_COLS * N_ROWS

EMPTY           = 0
SNAKE_HEAD      = 1
SNAKE_BODY      = 2
APPLE           = 3

NORTH       = 0
EAST        = 1
SOUTH       = 2
WEST        = 3


########################################################################
# .DATA
	.data

# const char symbols[4] = {'.', '#', 'o', '@'};
symbols:
	.byte	'.', '#', 'o', '@'

	.align 2
# int8_t grid[N_ROWS][N_COLS] = { EMPTY };
grid:
	.space	N_ROWS * N_COLS

	.align 2
# int8_t snake_body_row[MAX_SNAKE_LEN] = { EMPTY };
snake_body_row:
	.space	MAX_SNAKE_LEN

	.align 2
# int8_t snake_body_col[MAX_SNAKE_LEN] = { EMPTY };
snake_body_col:
	.space	MAX_SNAKE_LEN

# int snake_body_len = 0;
snake_body_len:
	.word	0

# int snake_growth = 0;
snake_growth:
	.word	0

# int snake_tail = 0;
snake_tail:
	.word	0

# Game over prompt, for your convenience...
main__game_over:
	.asciiz	"Game over! Your score was "


########################################################################
#
# Your journey begins here, intrepid adventurer!
#
# Implement the following 6 functions, and check these boxes as you
# finish implementing each function
#
#  - [6] main
#  - [1] init_snake
#  - [2] update_apple
#  - [5] update_snake
#  - [4] move_snake_in_grid
#  - [3] move_snake_in_array
#



########################################################################
# .TEXT <main>
	.text
main:

	# Args:     void
	# Returns:
	#   - $v0: int
	#
	# Frame:    $ra, $s0, $s1, $s2
	# Uses:	    $a0, $v0, $t0, $t1, $s0, $s1, $s2
	# Clobbers: $a0, $v0, $t0, $t1
	#
	# Locals:
	#   - direction in $s0
	#   - update_snake(direction) return value in $s1
	#   - score in $s2
	#
	# Structure:
	#   main
	#   -> [prologue]
	#   -> body
	#      -> update_snake_eq_1
	#      -> update_snake_eq_1_end
	#   -> [epilogue]

	# Code:
main__prologue:
	# set up stack frame
	addiu   $sp, $sp, -16
	sw	$ra, 12($sp)
        sw      $s0, 8($sp)
        sw      $s1, 4($sp)
        sw      $s2, ($sp)
 

main__body:
        # TODO ... complete this function.

        jal init_snake                  # init_snake();
        jal update_apple                # update_apple();

main__body_update_snake_eq_1:

        jal print_grid                  # print_grid();
        jal input_direction             # input_direction();
        move $s0, $v0                   # $s0 = direction 
        

        move $a0, $s0 
        jal  update_snake               # update_snake(direction)
        move $s1, $v0                   # $s2 = update_snake(direction)

    
    
                                        # while (update_snake(direction))
        bne  $s1, 1, main__body_update_snake_eq_1_end 
        j main__body_update_snake_eq_1

main__body_update_snake_eq_1_end:

        lw   $t0, snake_body_len
        li   $t1, 3
        div  $t0, $t1                   # score = snake_body_len / 3
        mflo $s2                        # $s3 = score

        la $a0, main__game_over         # printf("Game over! Your score was");
        li $v0, 4 
        syscall 

        move $a0, $s2                   # printf("%d", score);
        li   $v0, 1
        syscall 

        li $a0, '\n'                    # printf("\n");
        li $v0, 11
        syscall 


main__epilogue:
        # tear down stack frame
        lw  $ra, 12($sp)
        lw  $s0, 8($sp)
        lw  $s1, 4($sp)
        lw  $s2, ($sp)
        addiu 	$sp, $sp, 16

	li	$v0, 0
	jr	$ra			# return 0;


########################################################################
# .TEXT <init_snake>
	.text
init_snake:

	# Args:     void
	# Returns:  void
	#
	# Frame:    $ra
	# Uses:     $a0, $a1, $a2
	# Clobbers: $a0, $a1, $a2
	#
	# Locals:
	#   - none
	#
	# Structure:
	#   init_snake
	#   -> [prologue]
	#   -> body
	#   -> [epilogue]

	# Code:
init_snake__prologue:
	# set up stack frame
	addiu	$sp, $sp, -4
	sw	$ra, ($sp)


init_snake__body:
	# TODO ... complete this function.

	li $a0, 7               
        li $a1, 7
        li $a2, SNAKE_HEAD
        jal set_snake                       # set_snake(7, 7, SNAKE_HEAD);

        li $a0, 7               
        li $a1, 6
        li $a2, SNAKE_BODY
        jal set_snake                       # set_snake(7, 6, SNAKE_BODY);

        li $a0, 7               
        li $a1, 5
        li $a2, SNAKE_BODY
        jal set_snake                       # set_snake(7, 5, SNAKE_BODY);

        li $a0, 7               
        li $a1, 4
        li $a2, SNAKE_BODY
        jal set_snake                       # set_snake(7, 4, SNAKE_BODY);
	

init_snake__epilogue:
	# tear down stack frame
	lw	$ra, ($sp)
	addiu 	$sp, $sp, 4

	jr	$ra			# return;


########################################################################
# .TEXT <update_apple>
	.text
update_apple:

	# Args:     void
	# Returns:  void
	#
	# Frame:    $ra, $s0, $s1
	# Uses:     $a0, $v0, $t0, $t1, $t2, $s0, $s1
	# Clobbers: $a0, $v0, $t0, $t1, $t2
	#
	# Locals:
	#   - apple_row in $s0
	#   - apple_col in $s1
	#
	# Structure:
	#   update_apple
	#   -> [prologue]
	#   -> body
	#      -> grid_ne_0
	#   -> [epilogue]

	# Code:
update_apple__prologue:
	# set up stack frame
	addiu	$sp, $sp, -12
	sw	$ra, 8($sp)
	sw	$s0, 4($sp)
	sw	$s1, ($sp)

update_apple__body:
	# TODO ... complete this function.


update_apple__body_grid_ne_0:

        li   $a0, N_ROWS           
        jal  rand_value             # apple_row = rand_value(N_ROWS);
        move $s0, $v0               # $s0 = apple_row
    

        li   $a0, N_COLS           
        jal  rand_value             # apple_col = rand_value(N_COLS);
        move $s1, $v0               # $s1 = apple_col
    
        mul  $t0, $s0, N_COLS       # apple_row * N_COLS
        add  $t0, $t0, $s1          # (apple_row * N_COLS) + apple_col
        lb   $t1, grid($t0)         # grid((apple_row * N_COLS) + apple_col)
                                        
        # while (grid[apple_row][apple_col] != EMPTY);
        bne  $t1, 0, update_apple__body_grid_ne_0  
    
        li   $t2, APPLE
        sb   $t2, grid($t0)         # grid[apple_row][apple_col] = APPLE;


update_apple__epilogue:
	# tear down stack frame
	lw	$ra, 8($sp)
	lw	$s0, 4($sp)
	lw	$s1, ($sp)
	addiu 	$sp, $sp, 12

	jr	$ra		    # return;


########################################################################
# .TEXT <update_snake>
	.text
update_snake:

	# Args:
	#   - $a0: int direction
	# Returns:
	#   - $v0: bool
	#
	# Frame:    $ra, $s0, $s1, $s2, $s3, $s4, $s5, $s6, $s7
	# Uses:     $a0, $a1, $v0, $t0, $t1, $t2, $t3, $t4, $t5, $t6, $t7,
	#           $s0, $s1, $s2, $s3, $s4, $s5, $s6, $s7
	# Clobbers: $a0, $a1, $v0, $t0, $t1, $t2, $t3, $t4, $t5, $t6, $t7,
	#
	# Locals:
	#   - direction in $s0
	#   - d_row in $s1
	#   - d_col in $s2
	#   - head_row in $s3
	#   - head_col in $s4
	#   - new_head_row in $s5
	#   - new_head_col in $s6
	#   - apple in $s7
	#
	# Structure:
	#   update_snake
	#   -> [prologue]
	#   -> body
	#      -> if_statments_false
	#      -> if_statments_end
	#      -> apple_bool_true
	#      -> apple_bool_false
	#      -> move_snake_in_grid_true
	#      -> apple_false
	#   -> [epilogue]

	# Code:
update_snake__prologue:
	# set up stack frame
	addiu	$sp, $sp, -36
	sw	$ra, 32($sp)
        sw  $s0, 28($sp)
        sw  $s1, 24($sp)
        sw  $s2, 20($sp)
        sw  $s3, 16($sp)
        sw  $s4, 12($sp)
        sw  $s5, 8($sp)
        sw  $s6, 4($sp)
        sw  $s7, ($sp)

update_snake__body:
	# TODO ... complete this function.

        move $s0, $a0               # $s0 = direction

        jal  get_d_row              # get_d_row(direction);

        move $a0, $s0               # restoring argument $a0 = direction
        move $s1, $v0               # $s1 = d_row 

        jal  get_d_col              # get_d_col(direction);

        move $a0, $s0               # restoring argument $a0 = direction 
        move $s2, $v0               # $s2 = d_col 

        lb   $s3, snake_body_row    # $s3 = head_row
        lb   $s4, snake_body_col    # $s4 = head_col

        mul  $t0, $s3, N_COLS       # head_row * N_COLS
        add  $t0, $t0, $s4          # (head_row * N_COLS) + head_col
        li   $t1, SNAKE_BODY
        sb   $t1, grid($t0)         # grid[head_row][head_col] = SNAKE_BODY;

        add  $s5, $s3, $s1          # $s5 = new_head_row
        add  $s6, $s4, $s2          # $s6 = new_head_col


        # if (new_head_row < 0) return false;
        blt  $s5, 0, update_snake__body_if_statments_false    
        # if (new_head_row >= N_ROWS) return false;
        bge  $s5, N_ROWS, update_snake__body_if_statments_false
        # if (new_head_col < 0) return false;     
        blt  $s6, 0, update_snake__body_if_statments_false
        # if (new_head_col >= N_COLS) return false;          
        bge  $s6, N_COLS, update_snake__body_if_statments_false     

        j    update_snake__body_if_statments_end

update_snake__body_if_statments_false:

        li   $v0, 0                 # return false;
        j    update_snake__epilogue

update_snake__body_if_statments_end:

        mul  $t0, $s5, N_COLS       # new_head_row * N_COLS
        add  $t0, $t0, $s6          # (new_head_row * N_COLS) + new_head_col
        lb   $t2, grid($t0)         # grid[new_head_row][new_head_col]

        # bool apple = (grid[new_head_row][new_head_col] == APPLE);
        bne  $t2, APPLE, update_snake__body_apple_bool_false

update_snake__body_apple_bool_true:

        li   $s7, 1                 # $s7 = apple 
        j    update_snake__body_apple_bool_end

update_snake__body_apple_bool_false:

        li   $s7, 0

update_snake__body_apple_bool_end:

        la   $t3, snake_tail          
        lw   $t4, snake_body_len
        addi $t4, $t4, -1           # snake_body_len - 1;
        sw   $t4, ($t3)             # snake_tail = snake_body_len - 1;


        move $a0, $s5                   
        move $a1, $s6
        # move_snake_in_grid(new_head_row, new_head_col) 
        jal  move_snake_in_grid     
        move $t5, $v0
        move $a0, $s0               # restoring $a0


        # if (! move_snake_in_grid(new_head_row, new_head_col))
        beq  $t5, 1, update_snake__body_move_snake_in_grid_true   

        li   $v0, 0                 # return false
        j    update_snake__epilogue  

update_snake__body_move_snake_in_grid_true:

        move $a0, $s5              
        move $a1, $s6
        # move_snake_in_array(new_head_row, new_head_col)
        jal  move_snake_in_array
        move $t6, $v0 
        move $a0, $s0               # restoring arguments $a0 = direction 

        # if (apple)
        bne  $s7, 1, update_snake__body_apple_false               
        lw   $t7, snake_growth        
        addi $t7, $t7, 3
        la   $t0, snake_growth
        sw   $t7, ($t0)             # snake_growth += 3;

        jal  update_apple           # update_apple();

update_snake__body_apple_false:

        li   $v0, 1                 # return true;

update_snake__epilogue:
	# tear down stack frame
	lw    $ra, 32($sp)
        lw    $s0, 28($sp)
        lw    $s1, 24($sp)
        lw    $s2, 20($sp)
        lw    $s3, 16($sp)
        lw    $s4, 12($sp)
        lw    $s5, 8($sp)
        lw    $s6, 4($sp)
        lw    $s7, ($sp)
        addiu $sp, $sp, 36

	jr    $ra		    # return;


########################################################################
# .TEXT <move_snake_in_grid>
	.text
move_snake_in_grid:

	# Args:
	#   - $a0: new_head_row
	#   - $a1: new_head_col
	# Returns:
	#   - $v0: bool
	#
	# Frame:    $ra, $s0, $s1, $s2
	# Uses:     $a0, $a1, $v0, $t0, $t1, $t2, $t3, $t4, $t5, $s0, $s1, $s2
	# Clobbers: $a0, $a1, $v0, $t0, $t1, $t2, $t3, $t4, $t5
	#
	# Locals:
	#   - tail in $s0
	#   - tail_row in $s1
	#   - tail_col in $s2
	#
	# Structure:
	#   move_snake_in_grid
	#   -> [prologue]
	#   -> body
	#      -> snake_growth_gt_0
	#      -> snake_growth_le_0
	#      -> snake_growth_le_0_end
	#      -> grid_ne_SNAKE_BODY
	#      -> grid_ne_SNAKE_BODY_end
	#   -> [epilogue]

	# Code:
move_snake_in_grid__prologue:
	# set up stack frame
	addiu $sp, $sp, -16
	sw    $ra, 12($sp)
	sw    $s0, 8($sp)
	sw    $s1, 4($sp)
	sw    $s2,  ($sp)

move_snake_in_grid__body:
	# TODO ... complete this function.
	
	lw $t0, snake_growth            # snake_growth data in $t0
        lw $t1, snake_tail              # snake_tail data in $t1 
        lw $t2, snake_body_len          # snake_body_len data in $t2 

        la $t3, snake_growth            # snake_growth address in $t3
        la $t4, snake_tail              # snake_tail address in $t4
        la $t5, snake_body_len          # snake_body_len address in $t5                                             
       


        # if (snake_growth > 0)
        ble  $t0, 0, move_snake_in_grid__body_snake_growth_le_0
move_snake_in_grid__body_snake_growth_gt_0:                          
        addi $t1, $t1, 1                # snake_tail++;

        addi $t2, $t2, 1                # snake_body_len++;
        addi $t0, $t0, -1               # snake_growth--;

        sw   $t0, ($t3)
        sw   $t1, ($t4)
        sw   $t2, ($t5)

        j    move_snake_in_grid__body_snake_growth_le_0_end
    
        # } else {
move_snake_in_grid__body_snake_growth_le_0:                         

        lw   $s0, ($t4)                 # $s0 = tail = snake_tail;

        lb   $s1, snake_body_row($s0)   # $s1 = tail_row = snake_body_row[tail]; 
        lb   $s2, snake_body_col($s0)   # $s2 = tail_col = snake_body_col[tail];

        mul  $t0, $s1, N_COLS           # tail_row * N_COLS
        add  $t0, $t0, $s2              # (tail_row * N_COLS) + tail_col

        li   $t1, EMPTY

        sb   $t1, grid($t0)             # grid[tail_row][tail_col] = EMPTY;

move_snake_in_grid__body_snake_growth_le_0_end:                    



        mul  $t0, $a0, N_COLS           # new_head_row * N_COLS
        add  $t0, $t0, $a1              # (new_head_row * N_COLS) + new_head_col

        lb   $t2, grid($t0)             # grid[new_head_row][new_head_col]


        # if (grid[new_head_row][new_head_col] == SNAKE_BODY)
        li   $t3, SNAKE_BODY                                              
        bne  $t2, $t3, move_snake_in_grid__body_grid_ne_SNAKE_BODY_end
    
        # grid[new_head_row][new_head_col] == SNAKE_BODY
move_snake_in_grid__body_grid_ne_SNAKE_BODY:   

        li   $v0, 0                     # return false;
        j    move_snake_in_grid__epilogue

move_snake_in_grid__body_grid_ne_SNAKE_BODY_end:
        
        # grid[new_head_row][new_head_col] = SNAKE_HEAD;
        li   $t4, SNAKE_HEAD                                              
        sb   $t4, grid($t0)           
	li   $v0, 1                     # return true;
    

move_snake_in_grid__epilogue:
	# tear down stack frame
	lw    $ra, 12($sp)
        lw    $s0, 8($sp)
	lw    $s1, 4($sp)
	lw    $s2,  ($sp)
	addiu $sp, $sp, 16
    
	jr	$ra			# return



########################################################################
# .TEXT <move_snake_in_array>
	.text
move_snake_in_array:

	# Arguments:
	#   - $a0: int new_head_row
	#   - $a1: int new_head_col
	# Returns:  void
	#
	# Frame:    $ra, $s0, $s1, $s2
	# Uses:     $a0, $a1, $a2, $t0, $t1, $s0, $s1, $s2
	# Clobbers: $a0, $a1, $a2, $t0, $t1
	#
	# Locals:
	#   - new_head_row in $s0
	#   - new_head_col in $s1
	#   - i in $s2
	#
	# Structure:
	#   move_snake_in_array
	#   -> [prologue]
	#   -> body
	#      -> i_ge_1
	#   -> [epilogue]

	# Code:
move_snake_in_array__prologue:
	# set up stack frame
	addiu $sp, $sp, -16
	sw    $ra, 12($sp)
	sw    $s0, 8($sp)
	sw    $s1, 4($sp)
	sw    $s2,  ($sp)

move_snake_in_array__body:
	# TODO ... complete this function.
	
        move $s0, $a0                   # $s0 = new_head_row
        move $s1, $a1                   # $s1 = new_head_col

        la   $t0, snake_tail
        lw   $s2, ($t0)                 # $s2 = i = snake_tail

move_snake_in_array__body_i_ge_1:

        addi $t0, $s2, -1               # [i - 1]

        la   $t1, snake_body_row        # snake_body_row: label address in $t1 
        add  $t1, $t1, $t0              # byte address of snake_body_row[i - 1]
        lb   $a0, ($t1)                 # $a0 = (snake_body_row[i - 1])

        la   $t1, snake_body_col        # snake_body_col: label address in $t1 
        add  $t1, $t1, $t0              # byte address of snake_body_col[i - 1]
        lb   $a1, ($t1)                 # $a1 = (snake_body_col[i - 1])

        move $a2, $s2                   # $a2 = i

        # set_snake_array(snake_body_row[i - 1], snake_body_col[i - 1], i);
        jal  set_snake_array      
        addi $s2, $s2, -1               # i--;

        # while (i >= 1)
        bge  $s2, 1, move_snake_in_array__body_i_ge_1   

        # set_snake_array(new_head_row, new_head_col, 0);
        move $a0, $s0 
        move $a1, $s1 
        li   $a2, 0
        jal  set_snake_array

move_snake_in_array__epilogue:
	# tear down stack frame
	lw    $ra, 12($sp)
        lw    $s0, 8($sp)
	lw    $s1, 4($sp)
	lw    $s2,  ($sp)
	addiu $sp, $sp, 16

	jr    $ra		        # return;



########################################################################
####                                                                ####
####        STOP HERE ... YOU HAVE COMPLETED THE ASSIGNMENT!        ####
####                                                                ####
########################################################################

##
## The following is various utility functions provided for you.
##
## You don't need to modify any of the following.  But you may find it
## useful to read through --- you'll be calling some of these functions
## from your code.
##

	.data

last_direction:
	.word	EAST

rand_seed:
	.word	0

input_direction__invalid_direction:
	.asciiz	"invalid direction: "

input_direction__bonk:
	.asciiz	"bonk! cannot turn around 180 degrees\n"

	.align	2
input_direction__buf:
	.space	2



########################################################################
# .TEXT <set_snake>
	.text
set_snake:

	# Args:
	#   - $a0: int row
	#   - $a1: int col
	#   - $a2: int body_piece
	# Returns:  void
	#
	# Frame:    $ra, $s0, $s1
	# Uses:     $a0, $a1, $a2, $t0, $s0, $s1
	# Clobbers: $t0
	#
	# Locals:
	#   - `int row` in $s0
	#   - `int col` in $s1
	#
	# Structure:
	#   set_snake
	#   -> [prologue]
	#   -> body
	#   -> [epilogue]

	# Code:
set_snake__prologue:
	# set up stack frame
	addiu	$sp, $sp, -12
	sw	$ra, 8($sp)
	sw	$s0, 4($sp)
	sw	$s1,  ($sp)

set_snake__body:
	move	$s0, $a0		# $s0 = row
	move	$s1, $a1		# $s1 = col

	jal	set_snake_grid		# set_snake_grid(row, col, body_piece);

	move	$a0, $s0
	move	$a1, $s1
	lw	$a2, snake_body_len
	jal	set_snake_array		# set_snake_array(row, col, snake_body_len);

	lw	$t0, snake_body_len
	addiu	$t0, $t0, 1
	sw	$t0, snake_body_len	# snake_body_len++;

set_snake__epilogue:
	# tear down stack frame
	lw	$s1,  ($sp)
	lw	$s0, 4($sp)
	lw	$ra, 8($sp)
	addiu 	$sp, $sp, 12

	jr	$ra			# return;



########################################################################
# .TEXT <set_snake_grid>
	.text
set_snake_grid:

	# Args:
	#   - $a0: int row
	#   - $a1: int col
	#   - $a2: int body_piece
	# Returns:  void
	#
	# Frame:    None
	# Uses:     $a0, $a1, $a2, $t0
	# Clobbers: $t0
	#
	# Locals:   None
	#
	# Structure:
	#   set_snake
	#   -> body

	# Code:
	li	$t0, N_COLS
	mul	$t0, $t0, $a0		#  15 * row
	add	$t0, $t0, $a1		# (15 * row) + col
	sb	$a2, grid($t0)		# grid[row][col] = body_piece;

	jr	$ra			# return;



########################################################################
# .TEXT <set_snake_array>
	.text
set_snake_array:

	# Args:
	#   - $a0: int row
	#   - $a1: int col
	#   - $a2: int nth_body_piece
	# Returns:  void
	#
	# Frame:    None
	# Uses:     $a0, $a1, $a2
	# Clobbers: None
	#
	# Locals:   None
	#
	# Structure:
	#   set_snake_array
	#   -> body

	# Code:
	sb	$a0, snake_body_row($a2)	# snake_body_row[nth_body_piece] = row;
	sb	$a1, snake_body_col($a2)	# snake_body_col[nth_body_piece] = col;

	jr	$ra				# return;



########################################################################
# .TEXT <print_grid>
	.text
print_grid:

	# Args:     void
	# Returns:  void
	#
	# Frame:    None
	# Uses:     $v0, $a0, $t0, $t1, $t2
	# Clobbers: $v0, $a0, $t0, $t1, $t2
	#
	# Locals:
	#   - `int i` in $t0
	#   - `int j` in $t1
	#   - `char symbol` in $t2
	#
	# Structure:
	#   print_grid
	#   -> for_i_cond
	#     -> for_j_cond
	#     -> for_j_end
	#   -> for_i_end

	# Code:
	li	$v0, 11			# syscall 11: print_character
	li	$a0, '\n'
	syscall				# putchar('\n');

	li	$t0, 0			# int i = 0;

print_grid__for_i_cond:
	bge	$t0, N_ROWS, print_grid__for_i_end	# while (i < N_ROWS)

	li	$t1, 0			# int j = 0;

print_grid__for_j_cond:
	bge	$t1, N_COLS, print_grid__for_j_end	# while (j < N_COLS)

	li	$t2, N_COLS
	mul	$t2, $t2, $t0		#                             15 * i
	add	$t2, $t2, $t1		#                            (15 * i) + j
	lb	$t2, grid($t2)		#                       grid[(15 * i) + j]
	lb	$t2, symbols($t2)	# char symbol = symbols[grid[(15 * i) + j]]

	li	$v0, 11			# syscall 11: print_character
	move	$a0, $t2
	syscall				# putchar(symbol);

	addiu	$t1, $t1, 1		# j++;

	j	print_grid__for_j_cond

print_grid__for_j_end:

	li	$v0, 11			# syscall 11: print_character
	li	$a0, '\n'
	syscall				# putchar('\n');

	addiu	$t0, $t0, 1		# i++;

	j	print_grid__for_i_cond

print_grid__for_i_end:
	jr	$ra			# return;



########################################################################
# .TEXT <input_direction>
	.text
input_direction:

	# Args:     void
	# Returns:
	#   - $v0: int
	#
	# Frame:    None
	# Uses:     $v0, $a0, $a1, $t0, $t1
	# Clobbers: $v0, $a0, $a1, $t0, $t1
	#
	# Locals:
	#   - `int direction` in $t0
	#
	# Structure:
	#   input_direction
	#   -> input_direction__do
	#     -> input_direction__switch
	#       -> input_direction__switch_w
	#       -> input_direction__switch_a
	#       -> input_direction__switch_s
	#       -> input_direction__switch_d
	#       -> input_direction__switch_newline
	#       -> input_direction__switch_null
	#       -> input_direction__switch_eot
	#       -> input_direction__switch_default
	#     -> input_direction__switch_post
	#     -> input_direction__bonk_branch
	#   -> input_direction__while

	# Code:
input_direction__do:
	li	$v0, 8			# syscall 8: read_string
	la	$a0, input_direction__buf
	li	$a1, 2
	syscall				# direction = getchar()

	lb	$t0, input_direction__buf

input_direction__switch:
	beq	$t0, 'w',  input_direction__switch_w	# case 'w':
	beq	$t0, 'a',  input_direction__switch_a	# case 'a':
	beq	$t0, 's',  input_direction__switch_s	# case 's':
	beq	$t0, 'd',  input_direction__switch_d	# case 'd':
	beq	$t0, '\n', input_direction__switch_newline	# case '\n':
	beq	$t0, 0,    input_direction__switch_null	# case '\0':
	beq	$t0, 4,    input_direction__switch_eot	# case '\004':
	j	input_direction__switch_default		# default:

input_direction__switch_w:
	li	$t0, NORTH			# direction = NORTH;
	j	input_direction__switch_post	# break;

input_direction__switch_a:
	li	$t0, WEST			# direction = WEST;
	j	input_direction__switch_post	# break;

input_direction__switch_s:
	li	$t0, SOUTH			# direction = SOUTH;
	j	input_direction__switch_post	# break;

input_direction__switch_d:
	li	$t0, EAST			# direction = EAST;
	j	input_direction__switch_post	# break;

input_direction__switch_newline:
	j	input_direction__do		# continue;

input_direction__switch_null:
input_direction__switch_eot:
	li	$v0, 17			# syscall 17: exit2
	li	$a0, 0
	syscall				# exit(0);

input_direction__switch_default:
	li	$v0, 4			# syscall 4: print_string
	la	$a0, input_direction__invalid_direction
	syscall				# printf("invalid direction: ");

	li	$v0, 11			# syscall 11: print_character
	move	$a0, $t0
	syscall				# printf("%c", direction);

	li	$v0, 11			# syscall 11: print_character
	li	$a0, '\n'
	syscall				# printf("\n");

	j	input_direction__do	# continue;

input_direction__switch_post:
	blt	$t0, 0, input_direction__bonk_branch	# if (0 <= direction ...
	bgt	$t0, 3, input_direction__bonk_branch	# ... && direction <= 3 ...

	lw	$t1, last_direction	#     last_direction
	sub	$t1, $t1, $t0		#     last_direction - direction
	abs	$t1, $t1		# abs(last_direction - direction)
	beq	$t1, 2, input_direction__bonk_branch	# ... && abs(last_direction - direction) != 2)

	sw	$t0, last_direction	# last_direction = direction;

	move	$v0, $t0
	jr	$ra			# return direction;

input_direction__bonk_branch:
	li	$v0, 4			# syscall 4: print_string
	la	$a0, input_direction__bonk
	syscall				# printf("bonk! cannot turn around 180 degrees\n");

input_direction__while:
	j	input_direction__do	# while (true);



########################################################################
# .TEXT <get_d_row>
	.text
get_d_row:

	# Args:
	#   - $a0: int direction
	# Returns:
	#   - $v0: int
	#
	# Frame:    None
	# Uses:     $v0, $a0
	# Clobbers: $v0
	#
	# Locals:   None
	#
	# Structure:
	#   get_d_row
	#   -> get_d_row__south:
	#   -> get_d_row__north:
	#   -> get_d_row__else:

	# Code:
	beq	$a0, SOUTH, get_d_row__south	# if (direction == SOUTH)
	beq	$a0, NORTH, get_d_row__north	# else if (direction == NORTH)
	j	get_d_row__else			# else

get_d_row__south:
	li	$v0, 1
	jr	$ra				# return 1;

get_d_row__north:
	li	$v0, -1
	jr	$ra				# return -1;

get_d_row__else:
	li	$v0, 0
	jr	$ra				# return 0;



########################################################################
# .TEXT <get_d_col>
	.text
get_d_col:

	# Args:
	#   - $a0: int direction
	# Returns:
	#   - $v0: int
	#
	# Frame:    None
	# Uses:     $v0, $a0
	# Clobbers: $v0
	#
	# Locals:   None
	#
	# Structure:
	#   get_d_col
	#   -> get_d_col__east:
	#   -> get_d_col__west:
	#   -> get_d_col__else:

	# Code:
	beq	$a0, EAST, get_d_col__east	# if (direction == EAST)
	beq	$a0, WEST, get_d_col__west	# else if (direction == WEST)
	j	get_d_col__else			# else

get_d_col__east:
	li	$v0, 1
	jr	$ra				# return 1;

get_d_col__west:
	li	$v0, -1
	jr	$ra				# return -1;

get_d_col__else:
	li	$v0, 0
	jr	$ra				# return 0;



########################################################################
# .TEXT <seed_rng>
	.text
seed_rng:

	# Args:
	#   - $a0: unsigned int seed
	# Returns:  void
	#
	# Frame:    None
	# Uses:     $a0
	# Clobbers: None
	#
	# Locals:   None
	#
	# Structure:
	#   seed_rng
	#   -> body

	# Code:
	sw	$a0, rand_seed		# rand_seed = seed;

	jr	$ra			# return;



########################################################################
# .TEXT <rand_value>
	.text
rand_value:

	# Args:
	#   - $a0: unsigned int n
	# Returns:
	#   - $v0: unsigned int
	#
	# Frame:    None
	# Uses:     $v0, $a0, $t0, $t1
	# Clobbers: $v0, $t0, $t1
	#
	# Locals:
	#   - `unsigned int rand_seed` cached in $t0
	#
	# Structure:
	#   rand_value
	#   -> body

	# Code:
	lw	$t0, rand_seed		#  rand_seed

	li	$t1, 1103515245
	mul	$t0, $t0, $t1		#  rand_seed * 1103515245

	addiu	$t0, $t0, 12345		#  rand_seed * 1103515245 + 12345

	li	$t1, 0x7FFFFFFF
	and	$t0, $t0, $t1		# (rand_seed * 1103515245 + 12345) & 0x7FFFFFFF

	sw	$t0, rand_seed		# rand_seed = (rand_seed * 1103515245 + 12345) & 0x7FFFFFFF;

	rem	$v0, $t0, $a0
	jr	$ra			# return rand_seed % n;

