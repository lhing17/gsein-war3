package mdx

import java.io.RandomAccessFile

object MdxParser {
    private fun parseTextures(reader: MdxReader, size: Int): List<Map<String, Any>> {
        val file = reader.file
        val start = file.filePointer

        val list = mutableListOf<Map<String, Any>>()
        while (file.filePointer < start + size) {
            // texture格式
            // replaceId int
            // image 256字节string
            // unknown int
            // flags int
            val map = mutableMapOf<String, Any>()
            map["replaceId"] = reader.readInt()
            map["image"] = reader.readString(256)
            map["unknown"] = reader.readInt()
            map["flags"] = reader.readInt()
            list.add(map)
        }
        return list
    }

    fun parse(filename: String): List<String> {
        RandomAccessFile(filename, "r").use { file ->
            val reader = MdxReader(file)
            // check whether file reader is MDLX
            if (reader.keyword() != "MDLX") {
                throw IllegalArgumentException("Not a MDX file")
            }

            // 循环读取，格式为header-size-textures
            val keywordMap = mutableMapOf<String, Any>()
            val textures = mutableListOf<Map<String, Any>>()
            while (file.filePointer < file.length()) {
                val header = reader.keyword()
                val size = reader.readInt()
                keywordMap[header] = size
                when (header) {
                    "TEXS" -> textures.addAll(parseTextures(reader, size))
                    else -> file.skipBytes(size)
                }
            }
            return textures.map { it["image"] as String }
        }
    }
}