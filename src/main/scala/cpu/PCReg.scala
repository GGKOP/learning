package mypack

import chisel3._
import chisel3.util._
import config.Configs._


class PCRegIO extends Bundle {
    val ctrlBranch = Input(Bool())
    val ctrlJump =Input(Bool())
    val resultBranch =Input(Bool())
    val result = Input(UInt(ADDR_WIDTH.W))
    val addr = Output(UInt(ADDR_WIDTH.W))
}


class PCReg extends Module{
    val io = IO(new PCRegIO())
    
    val regPC = RegInit(UInt(ADDR_WIDTH.W),STAR_ADDR.U) // 

    when (io.ctrlJump||(io.ctrlBranch && io.resultBranch)){
        regPC := io.result
    } .otherwise{
        regPC := regPC + ADDR_BYTE_WIDTH.U
    }

    io.addr := regPC
}