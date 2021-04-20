package br.com.suamusica.smartlock_example

import android.R.attr
import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.annotation.Nullable
import io.flutter.embedding.android.FlutterActivity
import com.google.android.gms.auth.api.credentials.Credential


class MainActivity: FlutterActivity() {
    private  val RC_READ = 38971
    private  val RC_HINT = 38972
    private  val RC_SAVE = 38973
    private  val TAG = "TESTE"

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, data.toString())
        if (requestCode === RC_HINT) {
            if (resultCode === Activity.RESULT_OK) {
                val credential: Credential? = data?.getParcelableExtra(Credential.EXTRA_KEY)
credential?.let {
    Log.d(TAG,it.toString())
    Log.d(TAG, " NAME: " + it.name)
    Log.d(TAG, " id: " + it.id)
    Log.d(TAG, " givenname: " + it.givenName)
    Log.d(TAG, " familyname: " + it.familyName)
}
            } else {
                Log.e("TESTE", "Hint Read: NOT OK")
                Toast.makeText(this, "Hint Read Failed", Toast.LENGTH_SHORT).show()
            }
        }

    }


}
