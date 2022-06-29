package mdx

import java.io.RandomAccessFile
import java.util.*

/**
 * 用于读取Mdx文件
 */
class MdxReader(val file: RandomAccessFile) {



    /**
     * 文件的前4个字节为文件头
     */
    fun keyword(): String {
        val bytes = ByteArray(4)
        file.readFully(bytes)
        return String(bytes)
    }

    /**
     * 读取一个int，需要4个字节little-endian
     */
    fun readInt(): Int {
        // 以little endian的方式读取4个字节
        val ch0 = file.read()
        val ch1 = file.read()
        val ch2 = file.read()
        val ch3 = file.read()
        if (ch0 == -1 || ch1 == -1 || ch2 == -1 || ch3 == -1) {
            throw Exception("文件读取异常")
        }
        return (ch3 shl 24) + (ch2 shl 16) + (ch1 shl 8) + ch0
    }

    /**
     * 读取一个字符串，长度为length，忽略结尾的0
     */
    fun readString(length: Int): String {
        val bytes = ByteArray(length)
        file.readFully(bytes)

        var realLength = length;
        while (realLength > 0 && bytes[realLength - 1] == 0.toByte()) {
            realLength--
        }
        return String(bytes, 0, realLength)
    }
}