package  mypack

import chisel3._
import chisel3.util._
import config.Configs._

import utils._

class ControllerIO extends Bundle{
    val bundleControlIn = Flipped(new BundleControl())
    val bundleAluControl =  Flipped(new BundleAluControl())
    val bundleMemDataControl = Flipped(new BundleMemDataControl)
    val bundleControlOut = new BundleControl()
}


class Controller extends Module {
    val io =  IO (new ControllerIO())

    io.bundleAluControl.ctrlALUSrc :=  io.bundleControlIn.ctrlALUSrc
    io.bundleAluControl.ctrlJAL  := io.bundleControlIn.ctrlJAL
    io.bundleAluControl.ctrlSigned := io.bundleControlIn.ctrlSigned
    io.bundleAluControl.ctrlOP := io.bundleControlIn.ctrlOP
    io.bundleAluControl.ctrlBranch :=io.bundleControlIn.ctrlBranch


    io.bundleMemDataControl.ctrlLoad := io.bundleControlIn.ctrlLoad
    io.bundleMemDataControl.ctrlStore := io.bundleControlIn.ctrlStore
    io.bundleMemDataControl.ctrlSigned := io.bundleControlIn.ctrlSigned
    io.bundleMemDataControl.ctrlLSType := io.bundleControlIn.ctrlLSType

    io.bundleControlOut <> io.bundleControlIn
}