package com.chenyihong.exampledemo.confirmterms

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.databinding.LayoutConfirmTermsExampleActivityBinding
import com.google.android.material.snackbar.Snackbar

class ConfirmTermsExampleActivity : AppCompatActivity() {

    private lateinit var binding: LayoutConfirmTermsExampleActivityBinding

    private val confirmTermsHelper = ConfirmTermsHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutConfirmTermsExampleActivityBinding.inflate(layoutInflater).apply {
            setContentView(root)
        }

        binding.btnWithCheckBox.setOnClickListener {
            confirmTermsHelper.showConfirmTermsView(this, ConfirmTermsConfiguration.Builder()
                .setConfirmTipContent("已阅读并同意\"隐私政策\"")
                .setClickableTerm("隐私政策", "https://lf3-cdn-tos.draftstatic.com/obj/ies-hotsoon-draft/juejin/7b28b328-1ae4-4781-8d46-430fef1b872e.html")
                .setShowCheckbox(true)
                .setTextColor(R.color.color_gray_999)
                .setClickableTextColor(R.color.color_black_3B3946)
                .build())
            binding.btnGetConfirmStatus.visibility = View.VISIBLE
        }
        binding.btnWithoutCheckBox.setOnClickListener {
            confirmTermsHelper.showConfirmTermsView(this, ConfirmTermsConfiguration.Builder()
                .setConfirmTipContent("By signing in you accept out Terms of use and Privacy policy")
                .addClickableTerms(
                    mapOf(
                        Pair("Terms of use", "https://docs.github.com/en/site-policy/github-terms/github-terms-of-service"),
                        Pair("Privacy policy", "https://docs.github.com/en/site-policy/privacy-policies/github-general-privacy-statement")
                    )
                )
                .setShowUnderline(true)
                .setTextColor(R.color.color_gray_999)
                .build())
            binding.btnGetConfirmStatus.visibility = View.GONE
        }
        binding.btnGetConfirmStatus.setOnClickListener {
            showSnackbar("Current confirm status:${confirmTermsHelper.confirmStatus}")
        }
    }

    private fun showSnackbar(message: String) {
        runOnUiThread {
            Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        confirmTermsHelper.hideConfirmTermsView()
    }
}