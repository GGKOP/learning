package mypack


import org.scalatest._ 
import chiseltest._
import chisel3._
import org.scalatest.flatspec.AnyFlatSpec

import config.Configs._

trait PCRegTestFunc {
    val target_list = 
        Seq.fill(10)(scala.util.Random.nextInt().toLong & 0x00ffffffffL)
    def testFn(dut:PCReg): Unit = {
        dut.io.ctrlBranch.poke(false.B)
        dut.io.ctrlJump.poke(false.B)
        dut.io.resultBranch.poke(false.B)
        dut.io.result.poke(STAR_ADDR.U)
        dut.io.addr.expect(STAR_ADDR.U)

        var addr: Long = STAR_ADDR
        for(target <- target_list){
            dut.io.result.poke(target.U)
            addr +=ADDR_BYTE_WIDTH
            dut.clock.step()
            dut.io.addr.expect(addr.U)
        }

        dut.io.ctrlJump.poke(true.B)
        for(target <- target_list){
            dut.io.result.poke(target.U)
            dut.clock.step()
            dut.io.addr.expect(target.U)
            addr = target
        }
        dut.io.ctrlJump.poke(false.B)

        dut.io.ctrlBranch.poke(true.B)
        for(target <- target_list){
            dut.io.result.poke(target.U)
            addr += ADDR_BYTE_WIDTH
            dut.clock.step()
            dut.io.addr.expect(addr.U)
        }


        dut.io.resultBranch.poke(true.B)
        for(target <- target_list){
            dut.io.result.poke(target.U)
            dut.clock.step()
            dut.io.addr.expect(target.U)
        }
    }
}


class PCRegTest extends AnyFlatSpec with ChiselScalatestTester with PCRegTestFunc{
    "PCReg" should "pass" in{
        test(new PCReg) { dut =>
            testFn(dut)
        }
    }
}