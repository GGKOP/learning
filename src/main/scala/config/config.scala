package config

import chisel3._

import  math._


object Configs{
    val ADDR_WIDTH =32
    val ADDR_BYTE_WIDTH = ADDR_WIDTH/8
    val DATA_WIDTH = 32
    val DATA_WIDTH_H = 16
    val DATA_WIDTH_B = 8

    val STAR_ADDR: Long = 0x00000084

    val INST_WIDTH = 32
    val INST_BYTE_WIDTH = INST_WIDTH / 8
    val MEM_INST_SIZE =1024
    val INST_BYTE_WIDTH_LOG = ceil(log(INST_BYTE_WIDTH) / log(2)).toInt 
    val DATA_BYTE_WIDTH = DATA_WIDTH/8
    val DATA_BYTE_WIDTH_LOG = ceil(log(DATA_BYTE_WIDTH) / log(2)).toInt
    val REG_NUMS = 32
    val REG_NUMS_LOG= ceil(log(REG_NUMS) / log(2)).toInt
    val MEM_DATA_SIZE = 1024
}
