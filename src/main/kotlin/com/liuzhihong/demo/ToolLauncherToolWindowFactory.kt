package com.liuzhihong.demo

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.table.JBTable
import java.awt.BorderLayout
import java.awt.Desktop
import java.awt.Font
import java.io.File
import java.io.IOException
import javax.swing.*
import javax.swing.event.CellEditorListener
import javax.swing.event.ChangeEvent
import javax.swing.table.DefaultTableModel

class ToolLauncherToolWindowFactory : ToolWindowFactory {
    private val toolsFile = File(System.getProperty("user.home"), "tools_list.txt")
    private val toolsListModel = DefaultTableModel(arrayOf("名字", "位置"), 0)

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val toolWindowPanel = JPanel(BorderLayout())

        val table = JBTable(toolsListModel)

        val font = Font("Arial", Font.PLAIN, 16)
        table.font = font

        table.rowHeight = 30

        val columnModel = table.columnModel
        columnModel.getColumn(0).preferredWidth = 200
        columnModel.getColumn(1).preferredWidth = 300

        table.setDefaultEditor(String::class.java, DefaultCellEditor(JTextField()))

        table.columnModel.getColumn(1).cellEditor = PathCellEditor(this)

        table.setComponentPopupMenu(createPopupMenu(table))

        loadTools()

        val addButton = JButton("添加")
        addButton.addActionListener {
            val toolName = JOptionPane.showInputDialog("名字")
            if (toolName != null && toolName.isNotEmpty()) {
                val toolPath = chooseFilePath()
                if (toolPath != null) {
                    toolsListModel.addRow(arrayOf(toolName, toolPath))
                    saveTools()
                }
            }
        }

        val removeButton = JButton("删除")
        removeButton.addActionListener {
            val selectedRow = table.selectedRow
            if (selectedRow != -1) {
                toolsListModel.removeRow(selectedRow)
                saveTools()
            }
        }

        val buttonPanel = JPanel()
        buttonPanel.add(addButton)
        buttonPanel.add(removeButton)

        toolWindowPanel.add(JScrollPane(table), BorderLayout.CENTER)
        toolWindowPanel.add(buttonPanel, BorderLayout.SOUTH)

        toolWindow.contentManager.addContent(
            toolWindow.contentManager.factory.createContent(
                toolWindowPanel,
                "",
                false
            )
        )
    }

    private fun loadTools() {
        if (toolsFile.exists()) {
            toolsFile.readLines().forEach {
                val parts = it.split(",")
                if (parts.size == 2) {
                    toolsListModel.addRow(parts.toTypedArray())
                }
            }
        }
    }

    private fun saveTools() {
        try {
            toolsFile.writeText("")
            for (i in 0 until toolsListModel.rowCount) {
                val toolName = toolsListModel.getValueAt(i, 0)
                val toolPath = toolsListModel.getValueAt(i, 1)
                toolsFile.appendText("$toolName,$toolPath\n")
            }
        } catch (e: IOException) {
            println(e.message)
        }
    }

    // Method to show file chooser and select a file
    fun chooseFilePath(initialPath: String? = null): String? {
        val fileChooser = JFileChooser()
        fileChooser.fileSelectionMode = JFileChooser.FILES_ONLY
        fileChooser.isAcceptAllFileFilterUsed = false
        fileChooser.addChoosableFileFilter(object : javax.swing.filechooser.FileFilter() {
            override fun accept(f: File): Boolean {
                return f.isDirectory || f.canExecute()
            }

            override fun getDescription(): String {
                return "Executable Files (.exe, .bat)"
            }
        })
        // Set initial path if provided
        if (initialPath != null) {
            fileChooser.selectedFile = File(initialPath)
        }

        val result = fileChooser.showOpenDialog(null)
        return if (result == JFileChooser.APPROVE_OPTION) {
            fileChooser.selectedFile.absolutePath
        } else {
            null
        }
    }

    private fun createPopupMenu(table: JTable): JPopupMenu {
        val popupMenu = JPopupMenu()
        val openInExplorerItem = JMenuItem("Open in Explorer")
        openInExplorerItem.addActionListener {
            val selectedRow = table.selectedRow
            val filePath = table.getValueAt(selectedRow, 1) as String
            openFileExplorer(filePath)
        }
        popupMenu.add(openInExplorerItem)
        return popupMenu
    }

    private fun openFileExplorer(filePath: String) {
        try {
            val file = File(filePath)
            if (file.exists()) {
                val desktop = Desktop.getDesktop()
                if (file.isDirectory) {
                    desktop.open(file)
                } else {
                    desktop.open(file.parentFile)
                }
            } else {
                JOptionPane.showMessageDialog(null, "File does not exist: $filePath")
            }
        } catch (e: Exception) {
            JOptionPane.showMessageDialog(null, "Error opening file explorer: ${e.message}")
        }
    }

    class PathCellEditor(private val factory: ToolLauncherToolWindowFactory) : DefaultCellEditor(JTextField()) {
        init {
            this.addCellEditorListener(object : CellEditorListener {
                override fun editingStopped(e: ChangeEvent?) {
                    val value = cellEditorValue.toString()
                    if (value.isEmpty()) return
                    val filePath = factory.chooseFilePath()
                    if (filePath != null) {
                        val textField = component as JTextField
                        textField.text = filePath
                    }
                }

                override fun editingCanceled(e: ChangeEvent?) {
                }
            })
        }
    }
}
