package com.eudic.reader

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ReaderActivity : AppCompatActivity() {

    private lateinit var chineseText: TextView
    private lateinit var poemChinese: TextView
    private lateinit var btnTranslate: TextView
    private var isTranslated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reader)

        chineseText = findViewById(R.id.chinese_text)
        poemChinese = findViewById(R.id.poem_chinese)
        btnTranslate = findViewById(R.id.btn_translate)

        // 关闭按钮
        findViewById<TextView>(R.id.btn_close).setOnClickListener {
            finish()
        }

        // AI 翻译按钮
        btnTranslate.setOnClickListener {
            if (isTranslated) {
                // 已经翻译过，切换显示/隐藏
                toggleTranslation()
            } else {
                // 模拟 AI 翻译过程
                simulateAITranslation()
            }
        }

        // 底部功能按钮
        findViewById<TextView>(R.id.btn_toc).setOnClickListener {
            Toast.makeText(this, "目录功能开发中", Toast.LENGTH_SHORT).show()
        }
        findViewById<TextView>(R.id.btn_bookmark).setOnClickListener {
            Toast.makeText(this, "暂无书签", Toast.LENGTH_SHORT).show()
        }
        findViewById<TextView>(R.id.btn_notes).setOnClickListener {
            Toast.makeText(this, "暂无笔记", Toast.LENGTH_SHORT).show()
        }
        findViewById<TextView>(R.id.btn_search).setOnClickListener {
            Toast.makeText(this, "搜索功能开发中", Toast.LENGTH_SHORT).show()
        }
    }

    private fun simulateAITranslation() {
        // 显示翻译中状态
        btnTranslate.text = "..."
        btnTranslate.isEnabled = false
        Toast.makeText(this, "AI 正在翻译...", Toast.LENGTH_SHORT).show()

        // 模拟网络请求延迟（1.2秒）
        Handler(Looper.getMainLooper()).postDelayed({
            isTranslated = true
            chineseText.visibility = View.VISIBLE
            poemChinese.visibility = View.VISIBLE
            btnTranslate.text = "中A"
            btnTranslate.isEnabled = true
            Toast.makeText(this, "翻译完成", Toast.LENGTH_SHORT).show()
        }, 1200)
    }

    private fun toggleTranslation() {
        val visible = chineseText.visibility == View.VISIBLE
        chineseText.visibility = if (visible) View.GONE else View.VISIBLE
        poemChinese.visibility = if (visible) View.GONE else View.VISIBLE
    }
}
