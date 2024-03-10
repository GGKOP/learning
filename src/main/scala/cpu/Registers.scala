package mypack

import chisel3._
import chisel3.util._

import config.Configs._
import utils._


class RegistersIO extends Bundle {
    val ctrlwrite = Input(Bool())
    val ctrlJump  = Input(Bool())
    val pc = Input(UInt(ADDR_WIDTH.W))
    val dataWrite = Input(UInt(DATA_WIDTH.W))
    val bundleReg = Flipped(new BundleReg)
    val dataRead1 = Output(UInt(DATA_WIDTH.W))
    val dataRead2 = Output(UInt(DATA_WIDTH.W))
    
}


class Registers extends Module{
    val io =IO(new RegistersIO())
    
    val regs =Reg(Vec(REG_NUMS,UInt(DATA_WIDTH.W)))

    io.dataRead1 := regs(io.bundleReg.rs1)
    io.dataRead2 := regs(io.bundleReg.rs2)
 
    when(io.ctrlwrite && io.bundleReg.rd =/= 0.U) {
        when(io.ctrlJump) {
            regs(io.bundleReg.rd) := io.pc + INST_BYTE_WIDTH.U
        }.otherwise {
            regs(io.bundleReg.rd) := io.dataWrite
        }
    }
}