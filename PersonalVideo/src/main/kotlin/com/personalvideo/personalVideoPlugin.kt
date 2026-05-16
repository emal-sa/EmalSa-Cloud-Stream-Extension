package com.personalvideo

import android.app.AlertDialog
import android.content.Context
import android.text.InputType
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin

@CloudstreamPlugin
class PersonalVideoPlugin : Plugin() {
    private lateinit var provider: PersonalVideoProvider

    override fun load(context: Context) {
        provider = PersonalVideoProvider(context.applicationContext)
        openSettings = { settingsContext ->
            showSettingsDialog(settingsContext)
        }
        registerMainAPI(provider)
    }

    private fun showSettingsDialog(context: Context) {
        val padding = (24 * context.resources.displayMetrics.density).toInt()
        val input = EditText(context).apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_URI
            imeOptions = EditorInfo.IME_ACTION_DONE
            setSingleLine(true)
            setText(PersonalVideoSettings.getBaseUrl(context))
            setSelection(text.length)
            hint = "http://192.168.1.50:8080"
        }

        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(padding, padding / 2, padding, 0)
            addView(
                TextView(context).apply {
                    text = "URL server HTTP"
                },
            )
            addView(
                input,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                ),
            )
        }

        AlertDialog.Builder(context)
            .setTitle("Settings")
            .setView(container)
            .setPositiveButton("Save") { _, _ ->
                val baseUrl = PersonalVideoSettings.saveBaseUrl(context, input.text.toString())
                provider.updateBaseUrl(baseUrl)
            }
            .setNegativeButton("Cancel", null)
            .setNeutralButton("Reset") { _, _ ->
                val baseUrl = PersonalVideoSettings.resetBaseUrl(context)
                provider.updateBaseUrl(baseUrl)
            }
            .show()
    }
}
