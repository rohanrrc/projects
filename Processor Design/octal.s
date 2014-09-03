andi $r2, $r2, 0
andi $r0, $r0, 0
lui $r0,0x34
ori $r0, $r0, 0x54
sw $r0, 0($r2)


andi $r2, $r2, 0
sw $r3, 1($r2)
lw $r2, 0($r2)
andi $r0, $r2, 0x07
andi $r1, $r2, 0x38
andi $r3, $r3, 0
ori $r3, $r3, 0x06
srlv $r2, $r2, $r3
andi $r2, $r2, 0x07
andi $r3, $r3, 0
ori $r3, $r3, 0x01
sllv $r1, $r1, $r3
andi $r3, $r3, 0
ori $r3, $r3, 0x08
sllv $r2, $r2, $r3
add $r2, $r2, $r1
add $r2, $r2, $r0
andi $r3, $r3, 0
lw $r3, 1($r3)
disp $r2, 0
jr $r3