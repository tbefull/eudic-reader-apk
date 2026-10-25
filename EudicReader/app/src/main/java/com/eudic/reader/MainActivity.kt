package com.eudic.reader

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.zip.ZipInputStream

class MainActivity : AppCompatActivity() {

    private var pendingImportType: String = "txt"

    private val pickFileLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                if (pendingImportType == "epub") {
                    openEpubFile(uri)
                } else {
                    openTxtFile(uri)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<CardView>(R.id.book_card).setOnClickListener {
            startActivity(Intent(this, ReaderActivity::class.java))
        }

        findViewById<TextView>(R.id.btn_import).setOnClickListener {
            showImportOptions()
        }
    }

    private fun showImportOptions() {
        val options = arrayOf("TXT 文本文件", "EPUB 电子书")
        AlertDialog.Builder(this)
            .setTitle("选择导入格式")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> launchFilePicker("text/plain", "txt")
                    1 -> launchFilePicker("application/epub+zip", "epub")
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun launchFilePicker(mimeType: String, type: String) {
        pendingImportType = type
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            this.type = mimeType
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        pickFileLauncher.launch(Intent.createChooser(intent, "选择文件"))
    }

    private fun openTxtFile(uri: Uri) {
        try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                val reader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
                val content = reader.readText()
                openReader(content, getFileName(uri) ?: "导入的书籍")
            }
        } catch (e: Exception) {
            Toast.makeText(this, "读取 TXT 失败", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openEpubFile(uri: Uri) {
        try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                val text = extractTextFromEpub(inputStream)
                if (text.isNotBlank()) {
                    openReader(text, getFileName(uri) ?: "EPUB 书籍")
                } else {
                    Toast.makeText(this, "EPUB 中未找到可读文本", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "解析 EPUB 失败", Toast.LENGTH_SHORT).show()
        }
    }

    private fun extractTextFromEpub(inputStream: java.io.InputStream): String {
        val chapters = mutableListOf<String>()
        ZipInputStream(inputStream).use { zip ->
            var entry = zip.nextEntry
            while (entry != null) {
                val name = entry.name.lowercase()
                if ((name.endsWith(".html") || name.endsWith(".xhtml") || name.endsWith(".htm"))
                    && !name.contains("toc") && !name.contains("nav") && !name.contains("cover")) {
                    val html = zip.bufferedReader(Charsets.UTF_8).readText()
                    val chapter = extractChapter(html)
                    if (chapter.isNotBlank()) {
                        chapters.add(chapter)
                    }
                }
                zip.closeEntry()
                entry = zip.nextEntry
            }
        }
        return chapters.joinToString("\n\n---\n\n").trim()
    }

    private fun extractChapter(html: String): String {
        val sb = StringBuilder()
        val headingPattern = Regex("<h[1-3][^>]*>(.*?)</h[1-3]>", RegexOption.DOT_MATCHES_ALL)
        val headings = headingPattern.findAll(html).map { it.groupValues[1] }.toList()
        headings.forEach { h ->
            val clean = stripTags(h).trim()
            if (clean.isNotBlank()) {
                sb.append("【").append(clean).append("】\n\n")
            }
        }
        val body = stripTags(html)
        if (body.isNotBlank()) {
            sb.append(body)
        }
        return sb.toString().trim()
    }

    private fun stripTags(html: String): String {
        var result = html
        result = result.replace(Regex("<script[^>]*>.*?</script>", RegexOption.DOT_MATCHES_ALL), "")
        result = result.replace(Regex("<style[^>]*>.*?</style>", RegexOption.DOT_MATCHES_ALL), "")
        result = result.replace(Regex("<[^>]+>"), " ")
        result = result.replace(Regex("\\s+"), " ")
        return result.trim()
    }

    private fun openReader(content: String, title: String) {
        if (content.isBlank()) {
            Toast.makeText(this, "文件内容为空", Toast.LENGTH_SHORT).show()
            return
        }
        val intent = Intent(this, ReaderActivity::class.java).apply {
            putExtra("imported_content", content)
            putExtra("book_title", title)
        }
        startActivity(intent)
        Toast.makeText(this, "导入成功", Toast.LENGTH_SHORT).show()
    }

    private fun getFileName(uri: Uri): String? {
        var name: String? = null
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (index >= 0) {
                    name = cursor.getString(index)
                }
            }
        }
        return name
    }
}
