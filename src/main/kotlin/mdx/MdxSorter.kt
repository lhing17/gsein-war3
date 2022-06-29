package mdx

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.name

object MdxSorter {

    /**
     * 整理rootPath及子文件夹下的mdx文件，将mdx文件和对应的blp文件以文件夹的形式存放在targetPath下，可以设置是否删除原文件
     */
    fun sort(rootPath: String, targetPath: String, deleteOriginFile: Boolean = false) {
        Files.walk(File(rootPath).toPath()).forEach {
            if (!it.toString().endsWith(".mdx")) {
                return@forEach
            }
            // 将mdx文件和对应的blp文件以文件夹的形式存放在targetPath下，以mdx文件名为文件夹名
            val mdxName = it.name.substring(0, it.name.lastIndexOf("."))
            val targetMdxDirectory = File("$targetPath/$mdxName")
            targetMdxDirectory.mkdirs()

            copyBlpFiles(rootPath, it, targetMdxDirectory, deleteOriginFile)

            // 将mdx文件拷贝到目标文件夹，目标文件名为targetMdxDirectory下的mdx文件名
            val target = targetMdxDirectory.toPath().resolve(it.name)
            if (!target.exists()) {
                Files.copy(it, target)
            }
            if (deleteOriginFile) {
                it.toFile().delete()
            }
        }
    }

    private fun copyBlpFiles(rootPath: String, it: Path, targetMdxDirectory: File, deleteOriginFile: Boolean) {
        MdxParser.parse(it.toString()).forEach { blpRelativePath ->

            if (blpRelativePath.isBlank()) {
                return@forEach
            }

            val absoluteRootPath = File(rootPath).absolutePath
            val blpFile = File("$absoluteRootPath/$blpRelativePath")
            if (!blpFile.exists()) {
                return@forEach
            }

            // 目标文件名为targetMdxDirectory下的blp文件名
            val resolve = targetMdxDirectory.toPath().resolve(blpFile.name)
            Files.createDirectories(resolve.parent)
            println(blpFile.name + "," + resolve)
            if (!resolve.exists()) {
                Files.copy(blpFile.toPath(), resolve)
            }

            if (deleteOriginFile) {
                blpFile.delete()
            }

        }
    }
}

fun main() {
    MdxSorter.sort("E:\\War3Map\\拆地图\\杰八零\\jbl", "E:\\War3Map\\拆地图\\杰八零\\output3")
}