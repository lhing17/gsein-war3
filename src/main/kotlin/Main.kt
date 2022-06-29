// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.material.MaterialTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }

    MaterialTheme {
        Button(onClick = {
            text = "Hello, Desktop!"
        }) {
            Text(text)
        }
    }
}


fun main() = application {
    var isDialogOpen by remember { mutableStateOf(false) }
    val version = "1.0.0"
    Window(onCloseRequest = ::exitApplication, title = "吉森War3工具集") {
        // First, add a menu bar to the window, which contains a file menu and an about menu.
        MenuBar {
            Menu("File") {
                Item("Open", onClick = {
                    println("Open clicked")
                })
            }
            Menu("Help") {
                Item("About", onClick = {
                    // 弹出一个对话框，显示一个文本框，并且显示当前的版本号

                    if (!isDialogOpen) {
                        isDialogOpen = true
                    }
                })
            }
        }
        if (isDialogOpen) {
            Dialog(onCloseRequest = { isDialogOpen = false }, title = "关于") {
                Text("版本号：$version")
            }
        }


        App()
    }
}
