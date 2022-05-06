package com.chenyihong.exampledemo.tripartitelogin

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.databinding.LayoutTripartiteLoginActivityBinding
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<LayoutTripartiteLoginActivityBinding>(this, R.layout.layout_tripartite_login_activity)

        binding.btnGoogleLogin.setOnClickListener {
            checkGoogleLoginAccount(false)
        }

        binding.btnGoogleLogout.setOnClickListener {
            logout()
        }

        binding.btnGoogleOneTapLogin.setOnClickListener {
            checkGoogleLoginAccount(true)
        }

        binding.btnFacebookLogin.setOnClickListener {

        }
    }

    private fun checkGoogleLoginAccount(oneTapLogin: Boolean) {
        val lastLoginAccountInfo = GoogleSignIn.getLastSignedInAccount(this)
        if (lastLoginAccountInfo == null) {
            if (oneTapLogin) {
                oneTapLogin()
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

    private fun oneTapLogin() {
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

    private fun logout() {
        Identity.getSignInClient(this)
            .signOut()
            .addOnSuccessListener {
                Log.i(TAG, "google call logout success")
                showToast("logout success")
            }
    }



    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}