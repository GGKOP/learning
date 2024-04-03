package mypack

import chisel3._
import chisel3.util._

import config.Configs._
import utils._

import utils.OP_TYPES._
import utils.LS_TYPES._
import utils.CSR_OP._


class RegistersIO extends Bundle {
    val ctrlwrite = Input(Bool())
    val ctrlJump  = Input(Bool())
    val pc = Input(UInt(ADDR_WIDTH.W))
    val dataWrite = Input(UInt(DATA_WIDTH.W))
    val bundleReg = Flipped(new BundleReg)
    val dataRead1 = Output(UInt(DATA_WIDTH.W))
    val dataRead2 = Output(UInt(DATA_WIDTH.W))
    val ctrlcsr =Input(Bool())
    val ctrlcsrsign =Input(OP.CSR_OP_WIDTH.W)
    val addr_Csr = Input(CSR_SIZE.W)
}


class Registers extends Module{
    val io =IO(new RegistersIO())
    
    val regs =Reg(Vec(REG_NUMS,UInt(DATA_WIDTH.W)))
    val regscsr =Reg(Vec(4096,UInt(DATA_WIDTH.W)))


    val csr_rdata = regscsr(io.addr_Csr)

    io.dataRead1 := regs(io.bundleReg.rs1)
    io.dataRead2 := regs(io.bundleReg.rs2)
 
    when(io.ctrlwrite && io.bundleReg.rd =/= 0.U) {
        when(io.ctrlJump) {
            regs(io.bundleReg.rd) := io.pc + INST_BYTE_WIDTH.U
        }.otherwise {
            regs(io.bundleReg.rd) := io.dataWrite
        }
    }

    when(io.ctrlcsr){
       switch(io.ctrlcsrsign){
            is(CSR_W){
                regscsr(io.addr_Csr) := io.dataRead1
            }
            is(CSR_WI){
                regscsr(io.addr_Csr) := io.bundleReg.rs1
            }
            is(CSR_S){
                 regscsr(io.addr_Csr) :=(csr_rdata | io.dataRead1)
            }
            is(CSR_SI){
                 regscsr(io.addr_Csr) :=(csr_rdata | io.bundleReg.rs1)
            }
            is(CSR_C){
                regscsr(io.addr_Csr) :=(csr_rdata & ~ io.dataRead1)
            }
            is(CSR_C){
                regscsr(io.addr_Csr) :=(csr_rdata & ~ io.bundleReg.rs1)
            }

       }
    }
}