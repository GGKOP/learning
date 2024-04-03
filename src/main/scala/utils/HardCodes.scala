package utils

import chisel3._

object OP_TYPES{
    val OP_TYPES_WIDTH = 4
    val OP_NOP = "b0000".U
    val OP_ADD = "b0001".U
    val OP_SUB = "b0010".U
    val OP_AND = "b0100".U
    val OP_OR = "b0101".U
    val OP_XOR = "b0111".U
    val OP_SLL = "b1000".U
    val OP_SRL = "b1001".U
    val OP_SRA = "b1011".U
    val OP_EQ = "b1100".U
    val OP_NEQ = "b1101".U
    val OP_LT = "b1110".U
    val OP_GE = "b1111".U
}

object LS_TYPES {
    val LS_TYPE_WIDTH = 2
    val LS_B = "b00".U
    val LS_H = "b01".U
    val LS_W = "b10".U
}

object CSR_OP{
    val CSR_OP_WIDTH =3
    val CSR_W = "b001".U
    val CSR_WI = "b010".U
    val CSR_S = "b011".U
    val CSR_SI = "b100".U
    val CSR_C = "b101".U
    val CSR_CI = "b110".U

}

object Consts {
  val WORD_LEN      = 32
  val START_ADDR    = 0.U(WORD_LEN.W)
  val BUBBLE        = 0x00000013.U(WORD_LEN.W)  // [ADDI x0,x0,0] = BUBBLE
  val UNIMP         = "x_c0001073".U(WORD_LEN.W) // [CSRRW x0, cycle, x0]
  val ADDR_LEN      = 5 // rs1,rs2,wb
  val CSR_ADDR_LEN  = 12
  val VLEN          = 128
  val LMUL_LEN      = 2
  val SEW_LEN       = 11
  val VL_ADDR       = 0xC20
  val VTYPE_ADDR    = 0xC21

  val EXE_FUN_LEN = 5
  val ALU_X       =  0.U(EXE_FUN_LEN.W)
  val ALU_ADD     =  1.U(EXE_FUN_LEN.W)
  val ALU_SUB     =  2.U(EXE_FUN_LEN.W)
  val ALU_AND     =  3.U(EXE_FUN_LEN.W)
  val ALU_OR      =  4.U(EXE_FUN_LEN.W)
  val ALU_XOR     =  5.U(EXE_FUN_LEN.W)
  val ALU_SLL     =  6.U(EXE_FUN_LEN.W)
  val ALU_SRL     =  7.U(EXE_FUN_LEN.W)
  val ALU_SRA     =  8.U(EXE_FUN_LEN.W)
  val ALU_SLT     =  9.U(EXE_FUN_LEN.W)
  val ALU_SLTU    = 10.U(EXE_FUN_LEN.W)
  val BR_BEQ      = 11.U(EXE_FUN_LEN.W)
  val BR_BNE      = 12.U(EXE_FUN_LEN.W)
  val BR_BLT      = 13.U(EXE_FUN_LEN.W)
  val BR_BGE      = 14.U(EXE_FUN_LEN.W)
  val BR_BLTU     = 15.U(EXE_FUN_LEN.W)
  val BR_BGEU     = 16.U(EXE_FUN_LEN.W)
  val ALU_JALR    = 17.U(EXE_FUN_LEN.W)
  val ALU_COPY1   = 18.U(EXE_FUN_LEN.W)
  val ALU_VADDVV  = 19.U(EXE_FUN_LEN.W)
  val VSET        = 20.U(EXE_FUN_LEN.W)
  val ALU_PCNT    = 21.U(EXE_FUN_LEN.W)

  val OP1_LEN = 2
  val OP1_RS1 = 0.U(OP1_LEN.W)
  val OP1_PC  = 1.U(OP1_LEN.W)
  val OP1_X   = 2.U(OP1_LEN.W)
  val OP1_IMZ = 3.U(OP1_LEN.W)

  val OP2_LEN = 3
  val OP2_X   = 0.U(OP2_LEN.W)
  val OP2_RS2 = 1.U(OP2_LEN.W)
  val OP2_IMI = 2.U(OP2_LEN.W)
  val OP2_IMS = 3.U(OP2_LEN.W)
  val OP2_IMJ = 4.U(OP2_LEN.W)
  val OP2_IMU = 5.U(OP2_LEN.W)

  val MEN_LEN = 2
  val MEN_X   = 0.U(MEN_LEN.W)
  val MEN_S   = 1.U(MEN_LEN.W) 
  val MEN_V   = 2.U(MEN_LEN.W) 

  val REN_LEN = 2
  val REN_X   = 0.U(REN_LEN.W)
  val REN_S   = 1.U(REN_LEN.W) 
  val REN_V   = 2.U(REN_LEN.W) 

  val WB_SEL_LEN = 3
  val WB_X       = 0.U(WB_SEL_LEN.W)
  val WB_ALU     = 0.U(WB_SEL_LEN.W)
  val WB_MEM     = 1.U(WB_SEL_LEN.W)
  val WB_PC      = 2.U(WB_SEL_LEN.W)
  val WB_CSR     = 3.U(WB_SEL_LEN.W)
  val WB_MEM_V   = 4.U(WB_SEL_LEN.W)
  val WB_ALU_V   = 5.U(WB_SEL_LEN.W)
  val WB_VL      = 6.U(WB_SEL_LEN.W)

  val MW_LEN = 3
  val MW_X   = 0.U(MW_LEN.W)
  val MW_W   = 1.U(MW_LEN.W)
  val MW_H   = 2.U(MW_LEN.W)
  val MW_B   = 3.U(MW_LEN.W)
  val MW_HU  = 4.U(MW_LEN.W)
  val MW_BU  = 5.U(MW_LEN.W)

  val CSR_LEN = 3
  val CSR_X   = 0.U(CSR_LEN.W)
  val CSR_W   = 1.U(CSR_LEN.W)
  val CSR_S   = 2.U(CSR_LEN.W)
  val CSR_C   = 3.U(CSR_LEN.W)
  val CSR_E   = 4.U(CSR_LEN.W)
  val CSR_V   = 5.U(CSR_LEN.W)
}