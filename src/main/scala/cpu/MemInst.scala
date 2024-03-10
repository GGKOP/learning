package mypack

import chisel3._
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFile
import config.Configs._
import firrtl.annotations.MemoryLoadFileType



class MemInstIO extends Bundle{
    val addr = Input(UInt(ADDR_WIDTH.W))
    val inst = Output(UInt(INST_WIDTH.W))
}


class MemInst(memTest: Boolean = false) extends Module {
    val io = IO(new MemInstIO()) 

    val mem = Mem(MEM_INST_SIZE, UInt(INST_WIDTH.W))

    if (memTest) {
        loadMemoryFromFile(
            mem,
            "src/test/scala/randMemInst.hex"
        )
    } else {
        loadMemoryFromFile(
            mem,
            "src/test/scala/MemInst.hex"
        )
    }
    io.inst := mem.read(io.addr >> INST_BYTE_WIDTH_LOG.U) 
}