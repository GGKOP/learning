package mypack

import chisel3._
import chisel3.util._

import config.Configs._
import utils._

import utils.OP_TYPES._
import utils.LS_TYPES._
import utils.Consts._


class RegistersIO extends Bundle {
    val ctrlwrite = Input(Bool())
    val ctrlJump  = Input(Bool())
    val pc = Input(UInt(ADDR_WIDTH.W))
    val dataWrite = Input(UInt(DATA_WIDTH.W))
    val bundleReg = Flipped(new BundleReg)
    val dataRead1 = Output(UInt(DATA_WIDTH.W))
    val dataRead2 = Output(UInt(DATA_WIDTH.W))
    val ctrlCsr =Input(UInt(CSR_LEN.W))
    val ctrlCsrAddr =Input(UInt(CSR_ADDR_LEN.W))
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

}