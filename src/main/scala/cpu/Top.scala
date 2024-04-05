package mypack

import chisel3._
import chisel3.util._

import config.Configs._
import utils._


class TopIO extends Bundle {
    val addr = Output(UInt(ADDR_WIDTH.W))
    val inst = Output(UInt(INST_WIDTH.W))
    val bundleCtrl = new BundleControl()
    val resultALU = Output(UInt(DATA_WIDTH.W))
    val rs1 = Output(UInt(DATA_WIDTH.W))
    val rs2 = Output(UInt(DATA_WIDTH.W))
    val imm = Output(UInt(DATA_WIDTH.W))
    val resultBranch = Out 
class Top extends Module {
    val io = IO (new TopIO())
    
    val pcReg = Module (new PCReg())
    val meminst = Module(new MemInst())
    val decoder = Module(new Decoder())
    val alu = Module(new ALU())
    val memdata =Module(new MemData())
    val registers = Module(new Registers())
    val controller = Module(new Controller())


    pcReg.io.ctrlBranch <> controller.io.bundleControlOut.ctrlBranch
    pcReg.io.ctrlJump <> controller.io.bundleControlOut.ctrlJump
    pcReg.io.resultBranch <> alu.io.resultBranch
    pcReg.io.result <> memdata.io.result



    meminst.io.addr <> pcReg.io.addr


    decoder.io.inst <> meminst.io.inst

    registers.io.ctrlwrite <> controller.io.bundleControlOut.ctrlRegWrite
    registers.io.ctrlJump <> controller.io.bundleControlOut.ctrlJump
    registers.io.ctrlCsr <> controller.io.bundleControlOut.ctrlCsr
    registers.io.ctrlCsrAddr <>controller.io.bundleControlOut.ctrlCsrAddr
    registers.io.pc <> pcReg.io.addr
    registers.io.dataWrite <> memdata.io.result
    registers.io.bundleReg <> decoder.io.bundleReg
    alu.io.dataRead1 <> registers.io.dataRead1
    alu.io.dataRead2 <> registers.io.dataRead2
    alu.io.imm <> decoder.io.imm
    alu.io.pc <> pcReg.io.addr
    alu.io.bundleAluControl <> controller.io.bundleAluControl
    

    controller.io.bundleControlIn <> decoder.io.bundleCtrl

    memdata.io.resultALU <> alu.io.resultAlu
    memdata.io.dataStore <> registers.io.dataRead2
    memdata.io.bundleMemDataControl <> controller.io.bundleMemDataControl

    io.addr <> pcReg.io.addr
    io.bundleCtrl <> decoder.io.bundleCtrl
    io.inst <> meminst.io.inst
    io.result <> memdata.io.result
    io.resultALU <> alu.io.resultAlu
    io.resultBranch <> alu.io.resultBranch
    io.imm <> decoder.io.imm
    io.rs1 <> registers.io.dataRead1
    io.rs2 <> registers.io.dataRead2

}


object main extends App {
    println(getVerilogString(new Top()))
}