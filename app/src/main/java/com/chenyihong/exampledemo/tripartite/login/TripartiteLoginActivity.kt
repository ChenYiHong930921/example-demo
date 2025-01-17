package com.chenyihong.exampledemo.tripartite.login

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.lifecycleScope
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.databinding.LayoutTripartiteLoginActivityBinding
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val TAG = "TripartiteLogin"

class TripartiteLoginActivity : BaseGestureDetectorActivity<LayoutTripartiteLoginActivityBinding>() {

    private val googleLoginLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        Log.i(TAG, "google login get account info result.resultCode:${result.resultCode}")
        try {
            val credential = Identity.getSignInClient(this).getSignInCredentialFromIntent(result.data)

            showToast("Google login success id:${credential.id}")

            Log.i(TAG, "google login get account info id:${credential.id}")
            Log.i(TAG, "google login get account info googleIdToken:${credential.googleIdToken}")
            Log.i(TAG, "google login get account info password:${credential.password}")
            Log.i(TAG, "google login get account info givenName:${credential.givenName}")
            Log.i(TAG, "google login get account info familyName:${credential.familyName}")
            Log.i(TAG, "google login get account info displayName:${credential.displayName}")
            Log.i(TAG, "google login get account info profilePictureUri:${credential.profilePictureUri}")
        } catch (exception: ApiException) {
            Log.e(TAG, "google login get account info error :${exception.message}")
            exception.printStackTrace()
        }
    }

    private lateinit var metaCallbackManager: CallbackManager
    private lateinit var profileTracker: ProfileTracker

    private var credentialManager: CredentialManager? = null

    override fun initViewBinding(layoutInflater: LayoutInflater): LayoutTripartiteLoginActivityBinding {
        return LayoutTripartiteLoginActivityBinding.inflate(layoutInflater)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        metaCallbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().registerCallback(metaCallbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                Log.i(TAG, "Meta login success")
                Log.i(TAG, "Meta login account info userId:${result.accessToken.userId}")
                Log.i(TAG, "Meta login account info token:${result.accessToken.token}")
                Log.i(TAG, "Meta login account info applicationId:${result.accessToken.applicationId}")

                showToast("Meta login success userId:${result.accessToken.userId}")
            }

            override fun onCancel() {
                Log.i(TAG, "Meta login canceled")
            }

            override fun onError(error: FacebookException) {
                Log.e(TAG, "Meta login failed error:${error.message}")
            }
        })

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
        //开始跟踪用户信息变化
        profileTracker.startTracking()

        credentialManager = CredentialManager.create(applicationContext)

        binding.includeTitle.tvTitle.text = "Tripartite Login"

        binding.btnGoogleCredentialManagerLogin.setOnClickListener {
            credentialManagerGoogleLogin()
        }
        binding.btnGoogleCredentialManagerLogout.setOnClickListener {
            credentialManagerGoogleLogout()
        }

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

    private fun getGetCredentialRequest(getGoogleIdOption: Boolean, filterByAuthorizedAccounts: Boolean): GetCredentialRequest {
        Log.i(TAG, "getGetCredentialRequest getGoogleIdOption:$getGoogleIdOption, filterByAuthorizedAccounts:$filterByAuthorizedAccounts")
        return if (getGoogleIdOption) {
            GetCredentialRequest.Builder()
                .addCredentialOption(GetGoogleIdOption.Builder()
                    .setServerClientId(getString(R.string.google_login_client))
                    .setFilterByAuthorizedAccounts(true)
                    .setAutoSelectEnabled(true)
                    .build())
                .build()
        } else {
            GetCredentialRequest.Builder()
                .addCredentialOption(GetSignInWithGoogleOption.Builder(getString(R.string.google_login_client)).build())
                .build()
        }
    }

    private suspend fun getCredentialFromCredentialManager(getGoogleIdOption: Boolean = true, filterByAuthorizedAccounts: Boolean = true): Credential? {
        return try {
            Log.i(TAG, "getCredentialFromCredentialManager getGoogleIdOption:$getGoogleIdOption, filterByAuthorizedAccounts:$filterByAuthorizedAccounts")
            credentialManager?.getCredential(this@TripartiteLoginActivity, request = getGetCredentialRequest(getGoogleIdOption, filterByAuthorizedAccounts))?.credential
        } catch (e: Exception) {
            when (e) {
                // 没有与App绑定过的账号(小米的即使设置filterByAuthorizedAccounts为false仍会报此异常)
                is NoCredentialException -> {
                    Log.e(TAG, "credentialManagerGoogleLogin failed by NoCredentialException")
                    if (getGoogleIdOption && filterByAuthorizedAccounts) {
                        getCredentialFromCredentialManager(getGoogleIdOption = true, filterByAuthorizedAccounts = false)
                    } else {
                        getCredentialFromCredentialManager(getGoogleIdOption = false, filterByAuthorizedAccounts = false)
                    }
                }

                // 取消登录
                is GetCredentialCancellationException -> {
                    Log.e(TAG, "credentialManagerGoogleLogin failed by GetCredentialCancellationException")
                    null
                }

                // 未知异常
                else -> {
                    Log.e(TAG, "credentialManagerGoogleLogin failed by unknown")
                    null
                }
            }
        }
    }

    private fun credentialManagerGoogleLogin() {
        lifecycleScope.launch(Dispatchers.IO) {
            getCredentialFromCredentialManager().let { credential ->
                if (credential is CustomCredential) {
                    if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        GoogleIdTokenCredential.createFrom(credential.data).let { googleIdTokenCredential ->
                            Log.i(TAG, "id :${googleIdTokenCredential.id}")
                            Log.i(TAG, "token :${googleIdTokenCredential.idToken}")
                            Log.i(TAG, "displayName :${googleIdTokenCredential.displayName}")
                            Log.i(TAG, "avatar :${googleIdTokenCredential.profilePictureUri}")
                            Log.i(TAG, "phoneNumber :${googleIdTokenCredential.phoneNumber}")
                            Log.i(TAG, "familyName :${googleIdTokenCredential.familyName}")
                            Log.i(TAG, "givenName :${googleIdTokenCredential.givenName}")
                        }
                    }
                }
            }
        }
    }


    private fun credentialManagerGoogleLogout() {
        lifecycleScope.launch(Dispatchers.IO) {
            credentialManager?.clearCredentialState(ClearCredentialStateRequest())
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
            Log.i(TAG, "google login last login account info id:${lastLoginAccountInfo.id}")
            Log.i(TAG, "google login last login account info googleIdToken:${lastLoginAccountInfo.idToken}")
            Log.i(TAG, "google login last login account info givenName:${lastLoginAccountInfo.givenName}")
            Log.i(TAG, "google login last login account info familyName:${lastLoginAccountInfo.familyName}")
            Log.i(TAG, "google login last login account info displayName:${lastLoginAccountInfo.displayName}")
            Log.i(TAG, "google login last login account info profilePictureUri:${lastLoginAccountInfo.photoUrl}")
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
                showToast("Google logout success")
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
        showToast("Meta logout success")
    }

    override fun onDestroy() {
        LoginManager.getInstance().unregisterCallback(metaCallbackManager)
        profileTracker.stopTracking()
        super.onDestroy()
    }
}