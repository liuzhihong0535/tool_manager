package com.liuzhihong.demo
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

/**
 * @author  Programmer_Liu.
 * @version 1.0  2024/11/21
 *
 *
 */
class ToolLauncherAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val mainWindow = MainWindow()
        mainWindow.show()
    }
}
