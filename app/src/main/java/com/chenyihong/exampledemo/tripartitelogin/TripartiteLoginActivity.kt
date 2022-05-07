package com.chenyihong.exampledemo.tripartitelogin

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Base64.DEFAULT
import android.util.Base64.encodeToString
import android.util.Log
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.databinding.LayoutTripartiteLoginActivityBinding
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

const val TAG = "TripartiteLogin"

class TripartiteLoginActivity : AppCompatActivity() {

    private val googleLoginLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        Log.i(TAG, "google login get account info result.resultCode:" + result.resultCode)
        if (result.resultCode == Activity.RESULT_OK) {
            try {
                val credential = Identity.getSignInClient(this).getSignInCredentialFromIntent(result.data)

                showToast("login success id:" + credential.id)

                Log.i(TAG, "google login get account info id:" + credential.id)
                Log.i(TAG, "google login get account info googleIdToken:" + credential.googleIdToken)
                Log.i(TAG, "google login get account info password:" + credential.password)
                Log.i(TAG, "google login get account info givenName:" + credential.givenName)
                Log.i(TAG, "google login get account info familyName:" + credential.familyName)
                Log.i(TAG, "google login get account info displayName:" + credential.displayName)
                Log.i(TAG, "google login get account info profilePictureUri:" + credential.profilePictureUri)
            } catch (exception: ApiException) {
                Log.e(TAG, "google login get account info error :" + exception.message)
                exception.printStackTrace()
            }
        }
    }

    private lateinit var metaCallbackManager: CallbackManager
    private lateinit var profileTracker: ProfileTracker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<LayoutTripartiteLoginActivityBinding>(this, R.layout.layout_tripartite_login_activity)

        metaCallbackManager = CallbackManager.Factory.create()
        profileTracker = object : ProfileTracker() {
            override fun onCurrentProfileChanged(oldProfile: Profile?, currentProfile: Profile?) {
                currentProfile?.run {
                    Log.i(TAG, "Meta  onCurrentProfileChanged id:$id")
                    Log.i(TAG, "Meta  onCurrentProfileChanged firstName:$firstName")
                    Log.i(TAG, "Meta  onCurrentProfileChanged middleName:$middleName")
                    Log.i(TAG, "Meta  onCurrentProfileChanged lastName:$lastName")
                    Log.i(TAG, "Meta  onCurrentProfileChanged name:$name")
                    Log.i(TAG, "Meta  onCurrentProfileChanged pictureUri:$pictureUri")
                }
            }
        }
        profileTracker.startTracking()
        LoginManager.getInstance().registerCallback(metaCallbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                Log.i(TAG, "Meta login success")
                Log.i(TAG, "Meta login account info userId:" + result.accessToken.userId)
                Log.i(TAG, "Meta login account info token:" + result.accessToken.token)
                Log.i(TAG, "Meta login account info applicationId:" + result.accessToken.applicationId)
            }

            override fun onCancel() {
                Log.i(TAG, "Meta login canceled")
            }

            override fun onError(error: FacebookException) {
                Log.e(TAG, "Meta login failed error:${error.message}")
            }
        })

        binding.btnGoogleLogin.setOnClickListener {
            checkGoogleLoginAccount(false)
        }

        binding.btnGoogleLogout.setOnClickListener {
            googleLogout()
        }

        binding.btnGoogleOneTapLogin.setOnClickListener {
            checkGoogleLoginAccount(true)
        }

        binding.btnFacebookLogin.setOnClickListener {
            metaLogin()
        }

        binding.btnFacebookLogout.setOnClickListener {
            metaLogout()
        }
    }

    private fun checkGoogleLoginAccount(oneTapLogin: Boolean) {
        val lastLoginAccountInfo = GoogleSignIn.getLastSignedInAccount(this)
        if (lastLoginAccountInfo == null) {
            if (oneTapLogin) {
                googleOneTapLogin()
            } else {
                googleLogin()
            }
        } else {
            Log.i(TAG, "google login last login account info id:" + lastLoginAccountInfo.id)
            Log.i(TAG, "google login last login account info googleIdToken:" + lastLoginAccountInfo.idToken)
            Log.i(TAG, "google login last login account info givenName:" + lastLoginAccountInfo.givenName)
            Log.i(TAG, "google login last login account info familyName:" + lastLoginAccountInfo.familyName)
            Log.i(TAG, "google login last login account info displayName:" + lastLoginAccountInfo.displayName)
            Log.i(TAG, "google login last login account info photoUrl:" + lastLoginAccountInfo.photoUrl)
        }
    }

    private fun googleLogin() {
        val signInRequest = GetSignInIntentRequest.builder()
            .setServerClientId(getString(R.string.google_login_client))
            .build()

        Identity.getSignInClient(this)
            .getSignInIntent(signInRequest)
            .addOnSuccessListener { pendingIntent ->
                Log.i(TAG, "google call login success")
                googleLoginLauncher.launch(IntentSenderRequest.Builder(pendingIntent).build())
            }
            .addOnFailureListener {
                Log.i(TAG, "google call login failed message:${it.message}")
            }
    }

    private fun googleOneTapLogin() {
        val beginSignInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId(getString(R.string.google_login_client))
                .setFilterByAuthorizedAccounts(true)
                .build())
            .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                .setSupported(true)
                .build())
            .setAutoSelectEnabled(true)
            .build()

        Identity.getSignInClient(this)
            .beginSignIn(beginSignInRequest)
            .addOnSuccessListener {
                Log.i(TAG, "google call oneTap login success")
                googleLoginLauncher.launch(IntentSenderRequest.Builder(it.pendingIntent).build())
            }
            .addOnFailureListener {
                Log.i(TAG, "google call oneTap login failed message:${it.message}")
            }
    }

    private fun googleLogout() {
        Identity.getSignInClient(this)
            .signOut()
            .addOnSuccessListener {
                Log.i(TAG, "google call logout success")
                showToast("logout success")
            }
    }

    private fun metaLogin() {
        val currentAccessTokenActive = AccessToken.isCurrentAccessTokenActive()
        Log.i(TAG, "Meta login current AccessToken active :$currentAccessTokenActive")
        if (!currentAccessTokenActive) {
            LoginManager.getInstance().logIn(this, metaCallbackManager, listOf("public_profile"))
        }
    }

    private fun metaLogout() {
        Log.i(TAG, "Meta call logout")
        LoginManager.getInstance().logOut()
    }

    private fun checkKeyStoreHash() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)

                val signingInfo = info.signingInfo
                val apkContentsSigners = signingInfo.apkContentsSigners

                for (signature in apkContentsSigners) {
                    val md: MessageDigest = MessageDigest.getInstance("SHA")
                    md.update(signature.toByteArray())
                    val keyStoreHash = encodeToString(md.digest(), DEFAULT)
                    Log.d(TAG, "KeyHash keyStoreHash:${keyStoreHash}")
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.d(TAG, "KeyHash error1:${e.message}")
        } catch (e: NoSuchAlgorithmException) {
            Log.d(TAG, "KeyHash error2:${e.message}")
        }
    }

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        LoginManager.getInstance().unregisterCallback(metaCallbackManager)
        profileTracker.stopTracking()
        super.onDestroy()
    }
}