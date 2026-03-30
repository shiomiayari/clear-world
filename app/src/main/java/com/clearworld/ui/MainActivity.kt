package com.clearworld.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.clearworld.R
import com.clearworld.db.ClearWorldDatabase
import com.clearworld.widget.statusLabelFor
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val fishAdapter = FishAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupRecyclerView()
        observeAquariumState()
        observeFish()
        checkAccessibilityService()

        // バナーをタップするとAccessibility設定画面へ
        findViewById<TextView>(R.id.banner_accessibility_stopped).setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }
    }

    override fun onResume() {
        super.onResume()
        // 画面に戻るたびにAccessibilityの状態を確認
        checkAccessibilityService()
    }

    private fun setupRecyclerView() {
        findViewById<RecyclerView>(R.id.rv_fish).apply {
            adapter = fishAdapter
            layoutManager = LinearLayoutManager(
                this@MainActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        }
    }

    private fun observeAquariumState() {
        val db = ClearWorldDatabase.getInstance(this)
        lifecycleScope.launch {
            db.aquariumStateDao().observe().collectLatest { state ->
                val transparency = state?.transparency ?: 60f
                updateAquariumUI(transparency)
            }
        }
    }

    private fun observeFish() {
        val db = ClearWorldDatabase.getInstance(this)
        lifecycleScope.launch {
            db.fishDao().observeAliveFish().collectLatest { fishList ->
                fishAdapter.submitList(fishList)
            }
        }
    }

    private fun updateAquariumUI(transparency: Float) {
        val aquariumView = findViewById<AquariumView>(R.id.view_aquarium)
        aquariumView.setTransparency(transparency)
        aquariumView.setStatusLabel(statusLabelFor(transparency))
    }

    private fun checkAccessibilityService() {
        val am = getSystemService(ACCESSIBILITY_SERVICE) as AccessibilityManager
        val isEnabled = am.getEnabledAccessibilityServiceList(
            android.accessibilityservice.AccessibilityServiceInfo.FEEDBACK_ALL_MASK
        ).any { it.resolveInfo.serviceInfo.packageName == packageName }

        val banner = findViewById<TextView>(R.id.banner_accessibility_stopped)
        banner.visibility = if (isEnabled) android.view.View.GONE else android.view.View.VISIBLE
    }
}
