package mypack

import chisel3._
import chisel3.util._

import config.Configs._
import utils._
import utils.OP_TYPES._
import utils.LS_TYPES._
import utils.

class DecoderIO extends Bundle{
    val inst = Input(UInt(INST_WIDTH.W))
    val bundleReg = new BundleReg()
    val bundleCtrl = new BundleControl()
    val imm = Output(UInt(DATA_WIDTH.W))
    val addr_Csr = Output(CSR_SIZE.W)
}


class  Decoder extends Module{
    val io = IO(new DecoderIO())

    io.bundleReg.rs1 :=io.inst(19,15)
    io.bundleReg.rs2 := io.inst(24,20)
    io.bundleReg.rd := io.inst(11,7)


    val imm_i = Cat(Fill(20, io.inst(31)), io.inst(31, 20))
    val imm_s = Cat(Fill(20, io.inst(31)), io.inst(31, 25), io.inst(11, 7))
    val imm_b = Cat(Fill(20, io.inst(31)), io.inst(7), io.inst(30, 25), io.inst(11, 8), 0.U(1.W))
    val imm_u = Cat(io.inst(31, 12), Fill(12, 0.U))
    val imm_j = Cat(Fill(12, io.inst(31)), io.inst(31), io.inst(19, 12), io.inst(20), io.inst(30, 21), Fill(1, 0.U))
    val imm_z = Cat(Fill(27,0.U),io.inst(19,15))
    val imm_csr =Cat(Fill(27,0.U),io.inst(19,15) )

    val imm_shamt = Cat(Fill(27,0.U), io.inst(24,20))

    val addr_Csr = Cat(io.inst(31,20))

    val imm =WireDefault(0.U(32.W))


    val ctrlJump =WireDefault(false.B)
    val ctrlBranch = WireDefault(false.B)


    val ctrlRegWrite = WireDefault(true.B)
    val ctrlLoad = WireDefault(false.B)
    val ctrlStore = WireDefault(false.B)
    val ctrlALUSrc =WireDefault(false.B)
    val ctrlJAL = WireDefault(false.B)
    val ctrlOP = WireDefault(0.U(OP_TYPES_WIDTH.W))
    val ctrlSigned =WireDefault(true.B)
    val ctrlLSType = WireDefault(LS_W)
    val ctrlcsr =WireDefault(false.B)
    val ctrlcsrsign = WireDefault(0.U(CSR_OP_WIDTH.W)) 

    //chisel-listlookup, 
    //ListLookup component operates by mapping 
    //input values to output values based on a predefined list or sequence of data. 
    //The component searches through this list to find the appropriate output corresponding to the given input. 
    //This process is akin to an indexed retrieval where the input value acts as an index to access the desired data from the list.
    val csignals = ListLookup(io.inst,
                List(ALU_X    , OP1_RS1, OP2_RS2, MEN_X, REN_X, WB_X  , CSR_X),
        Array(
        LW    -> List(ALU_ADD  , OP1_RS1, OP2_IMI, MEN_X, REN_S, WB_MEM, CSR_X),
        SW    -> List(ALU_ADD  , OP1_RS1, OP2_IMS, MEN_S, REN_X, WB_X  , CSR_X),
        ADD   -> List(ALU_ADD  , OP1_RS1, OP2_RS2, MEN_X, REN_S, WB_ALU, CSR_X),
        ADDI  -> List(ALU_ADD  , OP1_RS1, OP2_IMI, MEN_X, REN_S, WB_ALU, CSR_X),
        SUB   -> List(ALU_SUB  , OP1_RS1, OP2_RS2, MEN_X, REN_S, WB_ALU, CSR_X),
        AND   -> List(ALU_AND  , OP1_RS1, OP2_RS2, MEN_X, REN_S, WB_ALU, CSR_X),
        OR    -> List(ALU_OR   , OP1_RS1, OP2_RS2, MEN_X, REN_S, WB_ALU, CSR_X),
        XOR   -> List(ALU_XOR  , OP1_RS1, OP2_RS2, MEN_X, REN_S, WB_ALU, CSR_X),
        ANDI  -> List(ALU_AND  , OP1_RS1, OP2_IMI, MEN_X, REN_S, WB_ALU, CSR_X),
        ORI   -> List(ALU_OR   , OP1_RS1, OP2_IMI, MEN_X, REN_S, WB_ALU, CSR_X),
        XORI  -> List(ALU_XOR  , OP1_RS1, OP2_IMI, MEN_X, REN_S, WB_ALU, CSR_X),
        SLL   -> List(ALU_SLL  , OP1_RS1, OP2_RS2, MEN_X, REN_S, WB_ALU, CSR_X),
        SRL   -> List(ALU_SRL  , OP1_RS1, OP2_RS2, MEN_X, REN_S, WB_ALU, CSR_X),
        SRA   -> List(ALU_SRA  , OP1_RS1, OP2_RS2, MEN_X, REN_S, WB_ALU, CSR_X),
        SLLI  -> List(ALU_SLL  , OP1_RS1, OP2_IMI, MEN_X, REN_S, WB_ALU, CSR_X),
        SRLI  -> List(ALU_SRL  , OP1_RS1, OP2_IMI, MEN_X, REN_S, WB_ALU, CSR_X),
        SRAI  -> List(ALU_SRA  , OP1_RS1, OP2_IMI, MEN_X, REN_S, WB_ALU, CSR_X),
        SLT   -> List(ALU_SLT  , OP1_RS1, OP2_RS2, MEN_X, REN_S, WB_ALU, CSR_X),
        SLTU  -> List(ALU_SLTU , OP1_RS1, OP2_RS2, MEN_X, REN_S, WB_ALU, CSR_X),
        SLTI  -> List(ALU_SLT  , OP1_RS1, OP2_IMI, MEN_X, REN_S, WB_ALU, CSR_X),
        SLTIU -> List(ALU_SLTU , OP1_RS1, OP2_IMI, MEN_X, REN_S, WB_ALU, CSR_X),
        BEQ   -> List(BR_BEQ   , OP1_RS1, OP2_RS2, MEN_X, REN_X, WB_X  , CSR_X),
        BNE   -> List(BR_BNE   , OP1_RS1, OP2_RS2, MEN_X, REN_X, WB_X  , CSR_X),
        BGE   -> List(BR_BGE   , OP1_RS1, OP2_RS2, MEN_X, REN_X, WB_X  , CSR_X),
        BGEU  -> List(BR_BGEU  , OP1_RS1, OP2_RS2, MEN_X, REN_X, WB_X  , CSR_X),
        BLT   -> List(BR_BLT   , OP1_RS1, OP2_RS2, MEN_X, REN_X, WB_X  , CSR_X),
        BLTU  -> List(BR_BLTU  , OP1_RS1, OP2_RS2, MEN_X, REN_X, WB_X  , CSR_X),
        JAL   -> List(ALU_ADD  , OP1_PC , OP2_IMJ, MEN_X, REN_S, WB_PC , CSR_X),
        JALR  -> List(ALU_JALR , OP1_RS1, OP2_IMI, MEN_X, REN_S, WB_PC , CSR_X),
        LUI   -> List(ALU_ADD  , OP1_X  , OP2_IMU, MEN_X, REN_S, WB_ALU, CSR_X),
        AUIPC -> List(ALU_ADD  , OP1_PC , OP2_IMU, MEN_X, REN_S, WB_ALU, CSR_X),
        CSRRW -> List(ALU_COPY1, OP1_RS1, OP2_X  , MEN_X, REN_S, WB_CSR, CSR_W),
        CSRRWI-> List(ALU_COPY1, OP1_IMZ, OP2_X  , MEN_X, REN_S, WB_CSR, CSR_W),
        CSRRS -> List(ALU_COPY1, OP1_RS1, OP2_X  , MEN_X, REN_S, WB_CSR, CSR_S),
        CSRRSI-> List(ALU_COPY1, OP1_IMZ, OP2_X  , MEN_X, REN_S, WB_CSR, CSR_S),
        CSRRC -> List(ALU_COPY1, OP1_RS1, OP2_X  , MEN_X, REN_S, WB_CSR, CSR_C),
        CSRRCI-> List(ALU_COPY1, OP1_IMZ, OP2_X  , MEN_X, REN_S, WB_CSR, CSR_C),
        ECALL -> List(ALU_X    , OP1_X  , OP2_X  , MEN_X, REN_X, WB_X  , CSR_E)
            )
        )
    val ctrlOP :: op1_sel :: op2_sel :: mem_wen :: rf_wen :: wb_sel :: csr_cmd :: Nil = csignals


    //op1_data,op2_data
    val op1_data = MuxCase(0.U(WORD_LEN.W), Seq(
        (op1_sel === OP1_RS1) -> io.bundleReg.rs1,
        (op1_sel === OP1_PC)  -> pc_reg,
        (op1_sel === OP1_IMZ) -> imm_z
    ))
    val op2_data = MuxCase(0.U(WORD_LEN.W), Seq(
        (op2_sel === OP2_RS2) -> io.bundleReg.rs2,
        (op2_sel === OP2_IMI) -> imm_i,
        (op2_sel === OP2_IMS) -> imm_s,
        (op2_sel === OP2_IMJ) -> imm_j,
        (op2_sel === OP2_IMU) -> imm_u
    ))

    val csr_addr = Mux(csr_cmd === CSR_E, 0x342.U(CSR_ADDR_LEN.W), inst(31,20))

    switch (io.inst(6,2)){
        is ("b011101".U,"b00101".U){
        }


        is("b11011".U){
            ctrlALUSrc :=true.B
            ctrlJump := true.B
            ctrlJAL :=true.B
            imm :=imm_j
        }
        
        is("b11001".U,"b00000".U,"b00100".U){
            ctrlALUSrc :=true.B
            when(io.inst(6,2)==="b11001".U){
                ctrlJump :=true.B
                imm :=imm_i
            }
            .elsewhen (io.inst(6,2)==="b00000".U){
                ctrlLoad :=true.B
                imm :=imm_i
                when(io.inst(14,12)=== "b100".U | io.inst(14,12)=== "b101".U){
                    ctrlSigned :=false.B

                }
            }
            .elsewhen(io.inst(6,2)==="b00100".U && (io.inst(14,12)==="b001".U || io.inst(14,12)=== "b101".U)){
                imm :=imm_shamt
                switch(Cat(io.inst(30),io.inst(14,12))){
                    is ("b0001".U){
                    }
                    is("b0101".U){
                    }
                    is("b1101".U){
                    }
                }
            }
            .otherwise{
                imm:=imm_i
                switch(io.inst(14,12)){
                    is("b000".U){
                    }
                    is("b010".U){              }
                    is("b100".U){
                        ctrlSigned :=false.B
                    }
                    is("b110".U){
                    }
                    is("b111".U){
                    }
                }
            }
        }

        is ("b11000".U) {
            ctrlALUSrc := false.B
            ctrlBranch := true.B
            ctrlRegWrite := false.B
            imm := imm_b
            switch (io.inst(14, 12)) {
                // BEQ
                is ("b000".U) {
                }
                // BNE
                is ("b001".U) {
                }
                // BLT
                is ("b100".U) {
                }
                // BGE
                is ("b101".U) {

                }
                // BLTU
                is ("b110".U) {
                    ctrlSigned := false.B
                }
                // BGEU
                is ("b111".U) {
                    ctrlSigned := false.B
                }
            }
        }
        is ("b01000".U) {
            ctrlALUSrc := true.B
            ctrlStore := true.B
            ctrlRegWrite := false.B
            imm := imm_s
            when (io.inst(14, 12) === "b000".U) {
                ctrlLSType := LS_B
            }
            when (io.inst(14, 12) === "b001".U) {
                ctrlLSType := LS_H
            }
        }

        is ("b01100".U) {
            switch (io.inst(14, 12)) {
                // ADD, SUB
                is ("b000".U) {
                    when (io.inst(30)) {
                    } .otherwise {

                    }
                }
                // SLL
                is ("b001".U) {
                }
                // SLT
                is ("b010".U) {
                }
                // SLTU
                is ("b011".U) {
                    ctrlOP := false.B
                }
                // XOR
                is ("b100".U) {
                }
                // SRL, SRA
                is ("b101".U) {
                    when (io.inst(30)) {
                    } .otherwise {
                    }
                }
                // OR
                is ("b110".U) {
                }
                // AND
                is ("b111".U) {
                }
            }
        }

    }
    io.bundleCtrl.ctrlALUSrc := ctrlALUSrc
    io.bundleCtrl.ctrlBranch := ctrlBranch
    io.bundleCtrl.ctrlCsr := csr_cmd
    io.bundleCtrl.ctrlJAL := ctrlJAL
    io.bundleCtrl.ctrlJump := ctrlJump
    io.bundleCtrl.ctrlLoad := ctrlLoad
    io.bundleCtrl.ctrlOP := ctrlOP
    io.bundleCtrl.ctrlRegWrite := ctrlRegWrite
    io.bundleCtrl.ctrlSigned := ctrlSigned
    io.bundleCtrl.ctrlStore := ctrlStore
    io.bundleCtrl.ctrlLSType := ctrlLSType
    io.bundleCtrl.ctrlCsrAddr :=csr_addr
    io.imm := imm
}