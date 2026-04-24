package br.com.suamusica.smartlock

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.IntentSender.SendIntentException
import androidx.annotation.NonNull
import com.google.android.gms.auth.api.credentials.*
import io.flutter.Log
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.ActivityResultListener


/** SmartlockPlugin */
class SmartlockPlugin: FlutterPlugin, MethodCallHandler, ActivityAware, ActivityResultListener {
  companion object {
    const val TAG = "Smartlock"
    const val SHOW_HINTS = "showHints"
    const val CHANNEL_NAME = "br.com.suamusica.smartlock"
      private const val RC_READ = 38971
      private const val RC_HINT = 38972
      private const val RC_SAVE = 38973

  }
  private lateinit var channel : MethodChannel
  private var activity: Activity? = null
  private var activityBinding: ActivityPluginBinding? = null
  private lateinit var applicationContext: Context
  private lateinit var client: CredentialsClient
  private var resultSaved: Result? = null
  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    Log.d(TAG,"onAttachedToEngine");
    applicationContext = flutterPluginBinding.applicationContext
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, CHANNEL_NAME)
    channel.setMethodCallHandler(this)
  }


  override fun onDetachedFromActivity() {
    Log.d(TAG,"onDetachedFromActivity");
    activityBinding?.removeActivityResultListener(this)
    activityBinding = null
    activity = null
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    Log.d(TAG,"onReattachedToActivityForConfigChanges");
    onAttachedToActivity(binding)
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    Log.d(TAG,"onAttachedToActivity")
    activityBinding = binding
    this.activity = binding.activity
    binding.addActivityResultListener(this)
  }

  override fun onDetachedFromActivityForConfigChanges() {
    Log.d(TAG,"onDetachedFromActivityForConfigChanges");
    activityBinding?.removeActivityResultListener(this)
    activityBinding = null
    activity = null
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?): Boolean {
    if (requestCode != RC_HINT) {
      return false
    }

    val reply = resultSaved ?: return true
    resultSaved = null

    if (resultCode == Activity.RESULT_OK) {
      val credential: Credential? = data?.getParcelableExtra(Credential.EXTRA_KEY)
      reply.success(credential?.id)
    } else {
      Log.e(TAG, "Hint Read: NOT OK")
      reply.success(null)
    }

    return true
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    if (call.method == SHOW_HINTS) {
     showHints(result)
    } else {
      result.notImplemented()
    }
  }

  // https://gist.github.com/jakubkinst/9c48cbf5c5af4eff7a023c5f77022eb8
  private fun showHints(result: Result) {
    val currentActivity = activity
    if (currentActivity == null) {
      result.error("no_activity", "Plugin is not attached to an activity", null)
      return
    }

    if (resultSaved != null) {
      result.error("request_in_progress", "A hint picker request is already running", null)
      return
    }

    this.client = Credentials.getClient(currentActivity)
    resultSaved = result
    val hintRequest = HintRequest.Builder()
            .setHintPickerConfig(CredentialPickerConfig.Builder()
                    .setShowCancelButton(true)
                    .build())
            .setEmailAddressIdentifierSupported(true)
            .setAccountTypes(IdentityProviders.GOOGLE)
            .build()

    val intent: PendingIntent = client.getHintPickerIntent(hintRequest)
    try {
      currentActivity.startIntentSenderForResult(intent.intentSender, RC_HINT, null, 0, 0, 0)
    } catch (e: SendIntentException) {
      Log.e(TAG, "Could not start hint picker Intent", e)
      resultSaved = null
      result.success(null)
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

}
