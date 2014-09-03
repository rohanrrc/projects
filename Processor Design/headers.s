#main for mod.s testing purposes
main:
		lui $r0, 0
		ori $r0, $r0, 0
		lui $r1, 0x11
		ori $r1, $r1, 0x99
		lui $r2, 0x84
		ori $r2, $r2, 0x78
		sw $r1, 0($r0)
		sw $r2, 1($r0)

		addp8 $r3, $r1, $r2
		subp8 $r0, $r1, $r2


