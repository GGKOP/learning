package mypack

import chisel3._
import chisel3.util._

import config.Configs._
import utils._
import utils.OP_TYPES._
import utils.LS_TYPES._

class DecoderIO extends Bundle{
    val inst = Input(UInt(INST_WIDTH.W))
    val bundleReg = new BundleReg()
    val bundleCtrl = new BundleControl()
    val imm = Output(UInt(DATA_WIDTH.W))
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

    val imm_shamt = Cat(Fill(27,0.U), io.inst(24,20))

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

    switch (io.inst(6,2)){
        is ("b011101".U,"b00101".U){
            ctrlALUSrc :=true.B
            ctrlOP :=OP_ADD
            imm :=imm_u
        }


        is("b11011".U){
            ctrlALUSrc :=true.B
            ctrlJump := true.B
            ctrlOP := OP_ADD
            ctrlJAL :=true.B
            imm :=imm_j
        }
        
        is("b11001".U,"b00000".U,"b00100".U){
            ctrlALUSrc :=true.B
            when(io.inst(6,2)==="b11001".U){
                ctrlJump :=true.B
                ctrlOP :=OP_ADD
                imm :=imm_i
            }
            .elsewhen (io.inst(6,2)==="b00000".U){
                ctrlLoad :=true.B
                ctrlOP := OP_ADD
                imm :=imm_i
                when(io.inst(14,12)=== "b100".U | io.inst(14,12)=== "b101".U){
                    ctrlSigned :=false.B

                }
            }
            .elsewhen(io.inst(6,2)==="b00100".U && (io.inst(14,12)==="b001".U || io.inst(14,12)=== "b101".U)){
                imm :=imm_shamt
                switch(Cat(io.inst(30),io.inst(14,12))){
                    is ("b0001".U){
                        ctrlOP :=OP_SLL
                    }
                    is("b0101".U){
                        ctrlOP := OP_SRL
                    }
                    is("b1101".U){
                        ctrlOP := OP_SRA
                    }
                }
            }
            .otherwise{
                imm:=imm_i
                switch(io.inst(14,12)){
                    is("b000".U){
                        ctrlOP :=OP_ADD
                    }
                    is("b010".U){
                        ctrlOP := OP_LT
                    }
                    is("b100".U){
                        ctrlOP :=OP_LT
                        ctrlSigned :=false.B
                    }
                    is("b110".U){
                        ctrlOP :=OP_OR
                    }
                    is("b111".U){
                        ctrlOP :=OP_AND
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
                    ctrlOP := OP_EQ
                }
                // BNE
                is ("b001".U) {
                    ctrlOP := OP_NEQ
                }
                // BLT
                is ("b100".U) {
                    ctrlOP := OP_LT
                }
                // BGE
                is ("b101".U) {
                    ctrlOP := OP_GE
                }
                // BLTU
                is ("b110".U) {
                    ctrlOP := OP_LT
                    ctrlSigned := false.B
                }
                // BGEU
                is ("b111".U) {
                    ctrlOP := OP_GE
                    ctrlSigned := false.B
                }
            }
        }
        is ("b01000".U) {
            ctrlALUSrc := true.B
            ctrlStore := true.B
            ctrlRegWrite := false.B
            ctrlOP := OP_ADD
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
                        ctrlOP := OP_SUB
                    } .otherwise {
                        ctrlOP := OP_ADD
                    }
                }
                // SLL
                is ("b001".U) {
                    ctrlOP := OP_SLL
                }
                // SLT
                is ("b010".U) {
                    ctrlOP := OP_LT
                }
                // SLTU
                is ("b011".U) {
                    ctrlOP := OP_LT
                    ctrlOP := false.B
                }
                // XOR
                is ("b100".U) {
                    ctrlOP := OP_XOR
                }
                // SRL, SRA
                is ("b101".U) {
                    when (io.inst(30)) {
                        ctrlOP := OP_SRA
                    } .otherwise {
                        ctrlOP := OP_SRL
                    }
                }
                // OR
                is ("b110".U) {
                    ctrlOP := OP_OR
                }
                // AND
                is ("b111".U) {
                    ctrlOP := OP_AND
                }
            }
        }
    }
    io.bundleCtrl.ctrlALUSrc := ctrlALUSrc
    io.bundleCtrl.ctrlBranch := ctrlBranch
    io.bundleCtrl.ctrlJAL := ctrlJAL
    io.bundleCtrl.ctrlJump := ctrlJump
    io.bundleCtrl.ctrlLoad := ctrlLoad
    io.bundleCtrl.ctrlOP := ctrlOP
    io.bundleCtrl.ctrlRegWrite := ctrlRegWrite
    io.bundleCtrl.ctrlSigned := ctrlSigned
    io.bundleCtrl.ctrlStore := ctrlStore
    io.bundleCtrl.ctrlLSType := ctrlLSType
    io.imm := imm
}