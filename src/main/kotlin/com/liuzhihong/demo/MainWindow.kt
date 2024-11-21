package com.liuzhihong.demo

import com.intellij.openapi.ui.DialogWrapper
import javax.swing.*

/**
 * @author  Programmer_Liu.
 * @version 1.0  2024/11/21
 */
class MainWindow : DialogWrapper(true) {
    private val toolNameField = JTextField(20)
    private val toolPathField = JTextField(20)

    init {
        title = "工具管理器"
        init()
    }

    override fun createCenterPanel(): JComponent {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)

        panel.add(JLabel("名字"))
        panel.add(toolNameField)

        panel.add(JLabel("位置"))
        panel.add(toolPathField)

        return panel
    }

}
