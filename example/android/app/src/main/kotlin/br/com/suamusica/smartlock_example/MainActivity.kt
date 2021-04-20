package br.com.suamusica.smartlock_example

import android.content.Intent
import android.util.Log
import androidx.annotation.Nullable
import io.flutter.embedding.android.FlutterActivity


class MainActivity: FlutterActivity() {
    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("TESTE", data.toString())
    }


}
