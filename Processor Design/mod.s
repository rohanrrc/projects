		andi $r0, $r0, 0
		lui $r1, 0
		andi $r1, $r1, 0
		sw $r3, 2($r0)
		lw $r0, 0($r0)
		lw $r1, 1($r1)
		andi $r3, $r3, 0
		slt $r2, $r0, $r1
		beq $r2, $r3, loop
		andi $r1, $r1, 0
		lw $r3, 2($r3)
		sw $r0, 2($r1)
		jr $r3
loop:   
		sub $r0, $r0, $r1
		slt $r2, $r0, $r1
		beq $r2, $r3, loop
		andi $r1, $r1, 0
		lw $r3, 2($r3)
		sw $r0, 2($r1)
		jr $r3