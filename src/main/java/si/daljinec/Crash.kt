package si.daljinec

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ScrollView
import android.widget.TextView

class CrashApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            try {
                val i = Intent(this, CrashActivity::class.java)
                i.putExtra("t", Log.getStackTraceString(e))
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(i)
            } catch (_: Throwable) {
            }
            android.os.Process.killProcess(android.os.Process.myPid())
            System.exit(10)
        }
    }
}

class CrashActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tv = TextView(this)
        tv.text = intent.getStringExtra("t") ?: "Ni podatkov"
        tv.textSize = 11f
        tv.setTextIsSelectable(true)
        tv.setPadding(24, 64, 24, 24)
        val sv = ScrollView(this)
        sv.addView(tv)
        setContentView(sv)
    }
}
