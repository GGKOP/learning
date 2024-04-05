package mypack

import chisel3._
import chisel3.util._
import config.Configs._

import utils._
import utils.OP_TYPES._
import utils.LS_TYPES._
import utils.Consts._


class AluIO extends Bundle{
  val bundleAluControl = new BundleAluControl()
    val dataRead1 = Input(UInt(DATA_WIDTH.W))
    val dataRead2 = Input(UInt(DATA_WIDTH.W))
    val imm = Input(UInt(DATA_WIDTH.W))
    val pc = Input(UInt(ADDR_WIDTH.W))
    val resultBranch = Output(Bool())
    val resultAlu = Output(UInt(DATA_WIDTH.W))
    val ctrlcsr = Input(UInt(CSR_LEN.W))
}



class ALU extends Module{
    val io = IO(new AluIO())

    val resultBranch = WireDefault(false.B)
    val resultAlu = WireDefault(0.U(DATA_WIDTH.W))

    val oprand1 = WireDefault(0.U(DATA_WIDTH.W))
    val oprand2 = WireDefault(0.U(DATA_WIDTH.W))

    oprand1 := Mux(io.bundleAluControl.ctrlJAL, io.pc, io.dataRead1)
    oprand2 := Mux(io.bundleAluControl.ctrlALUSrc, io.imm, io.dataRead2)

    switch(io.bundleAluControl.ctrlOP) {
        is(OP_NOP) { 
            resultAlu := 0.U
            resultBranch := false.B
        }
        is(ALU_ADD) {
            resultAlu := oprand1 +& oprand2
        }
        is(ALU_SUB) {
            resultAlu := oprand1 -& oprand2
        }
        is(ALU_AND) {
            resultAlu := oprand1 & oprand2
        }
        is(ALU_OR) {
            resultAlu := oprand1 | oprand2
        }
        is(ALU_XOR) {
            resultAlu := oprand1 ^ oprand2
        }
        is(ALU_SLL) {
            resultAlu := oprand1 << oprand2(4, 0)
        }
        is(ALU_SRL) {
            resultAlu := oprand1 >> oprand2(4, 0)
        }
        is(ALU_SRA) { 
            resultAlu := (oprand1.asSInt >> oprand2(4, 0)).asUInt
        }
        is(BR_BEQ) {
            resultBranch := oprand1.asSInt === oprand2.asSInt
            resultAlu := io.pc +& io.imm
        }
        is(BR_BNE) {
            resultBranch := oprand1.asSInt =/= oprand2.asSInt
            resultAlu := io.pc +& io.imm
        }
        is(BR_BLT) { 
            when(io.bundleAluControl.ctrlBranch) {
                when(io.bundleAluControl.ctrlSigned) {
                    resultBranch := oprand1.asSInt < oprand2.asSInt
                }.otherwise {
                    resultBranch := oprand1 < oprand2
                }
                resultAlu := io.pc +& io.imm
            }.otherwise {
                when(io.bundleAluControl.ctrlSigned) {
                    resultAlu := oprand1.asSInt < oprand2.asSInt
                }.otherwise {
                    resultAlu := oprand1 < oprand2
                }
            }
        }
        is(BR_BGE) { 
            when(io.bundleAluControl.ctrlSigned) {
                resultBranch := oprand1.asSInt >= oprand2.asSInt
            }.otherwise {
                resultBranch := oprand1 >= oprand2
            }
            resultAlu := io.pc +& io.imm
        }
    }       

            val csr_addr = Mux(csr_cmd === CSR_E, 0x342.U(CSR_ADDR_LEN.W), inst(31,20))
            val csr_rdata = csr_regfile(csr_addr)
            val csr_wdata = MuxCase(0.U(WORD_LEN.W), Seq(
                (csr_cmd === CSR_W) -> op1_data,
                (csr_cmd === CSR_S) -> (csr_rdata | op1_data),
                (csr_cmd === CSR_C) -> (csr_rdata & ~op1_data),
                (csr_cmd === CSR_E) -> 11.U(WORD_LEN.W)
            ))
            
            when(csr_cmd > 0.U){
                csr_regfile(csr_addr) := csr_wdata
            }


    io.resultAlu := resultAlu
    io.resultBranch := resultBranch
}

