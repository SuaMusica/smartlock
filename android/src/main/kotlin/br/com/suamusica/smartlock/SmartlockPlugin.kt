package br.com.suamusica.smartlock

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import androidx.annotation.NonNull
import com.google.android.gms.auth.api.credentials.*
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import io.flutter.Log
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result


/** SmartlockPlugin */
class SmartlockPlugin: FlutterPlugin, MethodCallHandler, ActivityAware {
  companion object {
    const val TAG = "Smartlock"
    const val SHOW_HINTS = "showHints"
    const val CHANNEL_NAME = "br.com.suamusica.smartlock"
      private const val RC_READ = 38971
      private const val RC_HINT = 38972
      private const val RC_SAVE = 38973

  }
  private lateinit var channel : MethodChannel
  private lateinit var activity: Activity
  private lateinit var applicationContext: Context
  private lateinit var client: CredentialsClient

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    Log.d(TAG,"onAttachedToEngine");
    applicationContext = flutterPluginBinding.applicationContext
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, CHANNEL_NAME)
    channel.setMethodCallHandler(this)
  }


  override fun onDetachedFromActivity() {
    Log.d(TAG,"onDetachedFromActivity");
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    Log.d(TAG,"onReattachedToActivityForConfigChanges");
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    Log.d(TAG,"onAttachedToActivity")
    this.activity = binding.activity
    this.client = Credentials.getClient(activity)
  }

  override fun onDetachedFromActivityForConfigChanges() {
    Log.d(TAG,"onDetachedFromActivityForConfigChanges");
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    if (call.method == SHOW_HINTS) {
//      showHints()
      fetchCredentials()
      result.success(true)
    } else {
      result.notImplemented()
    }
  }

  fun fetchCredentials() {
    val credentialRequest = CredentialRequest.Builder()
            .setPasswordLoginSupported(true)
            .build()

    client.request(credentialRequest).addOnCompleteListener { task ->
      if (task.isSuccessful) {
        Log.d(TAG,"TASK IS SUCCESS")
      } else {
        val e = task.exception
        if (e is ResolvableApiException) {
          // This is most likely the case where the user has multiple saved
          // credentials and needs to pick one. This requires showing UI to
          // resolve the read request.
//          resolveResult(e, RC_READ)
          Log.d(TAG,"TASK IS ResolvableApiException")

        } else if (e is ApiException) {
          // The user must create an account or sign in manually.
          Log.e(TAG,"Unsuccessful credential request.", e)

          // no complete Credential found, let's try to fetch hints at least
          showHints()
        }
      }
    }
  }

  private fun showHints() {
//    var mCredentialsClient = Credentials.getClient(this.activity)

    val hintRequest = HintRequest.Builder()
            .setHintPickerConfig(CredentialPickerConfig.Builder()
                    .setShowCancelButton(true)
                    .build())
            .setEmailAddressIdentifierSupported(true)
            .setAccountTypes(IdentityProviders.GOOGLE)
            .build()

    val intent: PendingIntent = client.getHintPickerIntent(hintRequest)
    try {
      activity.startIntentSenderForResult(intent.intentSender, RC_HINT, null, 0, 0, 0)
    } catch (e: SendIntentException) {
      Log.e(TAG, "Could not start hint picker Intent", e)
    }

  }



  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

}
