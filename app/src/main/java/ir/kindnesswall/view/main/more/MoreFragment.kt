package ir.kindnesswall.view.main.more

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import ir.kindnesswall.BaseFragment
import ir.kindnesswall.KindnessApplication
import ir.kindnesswall.R
import ir.kindnesswall.data.local.AppPref
import ir.kindnesswall.data.local.UserInfoPref
import ir.kindnesswall.data.local.UserPreferences
import ir.kindnesswall.data.model.CustomResult
import ir.kindnesswall.data.model.PhoneVisibility
import ir.kindnesswall.databinding.FragmentMoreBinding
import ir.kindnesswall.utils.NumberStatus
import ir.kindnesswall.utils.extentions.runOrStartAuth
import ir.kindnesswall.utils.openSupportForm
import ir.kindnesswall.view.main.MainActivity
import ir.kindnesswall.view.main.addproduct.SubmitGiftViewModel
import ir.kindnesswall.view.main.more.aboutus.AboutUsActivity
import ir.kindnesswall.view.profile.UserProfileActivity
import ir.kindnesswall.view.profile.blocklist.BlockListActivity
import ir.kindnesswall.view.reviewgift.ReviewGiftsActivity
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Created by farshid.abazari since 2019-11-07
 *
 * Usage:
 *
 * How to call:
 *
 * Useful parameter:
 *
 */

class MoreFragment() : BaseFragment() {
    var numview: Boolean = true
    lateinit var binding: FragmentMoreBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_more, container, false)
        val viewModel: SubmitGiftViewModel by viewModel()
        MainActivity.liveData.observe(binding.root.context as LifecycleOwner, Observer {
            if (UserInfoPref.bearerToken.isNotEmpty()) {
                val numberstatus = NumberStatus(viewModel, binding.moreNone, binding.moreCharity, binding.moreAll)
                numberstatus.getShowNumberStatus(binding.root.context)
            }

        })

        binding.moreCharity.setOnClickListener {
            UserPreferences.phoneVisibilityStatus = PhoneVisibility.JustCharities
            viewModel.setPhoneVisibility(PhoneVisibility.JustCharities).observe(this) {
                when (it.status) {
                    CustomResult.Status.LOADING -> {
                        Log.i("4566456456465465", "LOADING")
                    }
                    CustomResult.Status.ERROR -> {
                        Log.i("4566456456465465", "ERROR")
                    }
                    CustomResult.Status.SUCCESS -> {
                        Log.i("4566456456465465", "SUCCESS")
                    }
                }
            }
        }
        binding.moreAll.setOnClickListener {
            UserPreferences.phoneVisibilityStatus = PhoneVisibility.All
            viewModel.setPhoneVisibility(PhoneVisibility.All).observe(this) {
                when (it.status) {
                    CustomResult.Status.LOADING -> {
                        Log.i("4566456456465465", "LOADING")
                    }
                    CustomResult.Status.ERROR -> {
                        Log.i("4566456456465465", "ERROR")
                    }
                    CustomResult.Status.SUCCESS -> {
                        Log.i("4566456456465465", "SUCCESS")
                    }
                }
            }
        }
        binding.moreNone.setOnClickListener {
            UserPreferences.phoneVisibilityStatus = PhoneVisibility.None
            viewModel.setPhoneVisibility(PhoneVisibility.None).observe(this) {
                when (it.status) {
                    CustomResult.Status.LOADING -> {
                        Log.i("4566456456465465", "LOADING")
                    }
                    CustomResult.Status.ERROR -> {
                        Log.i("4566456456465465", "ERROR")
                    }
                    CustomResult.Status.SUCCESS -> {
                        Log.i("4566456456465465", "SUCCESS")
                    }
                }
            }
        }

        return binding.root
    }

    override fun configureViews() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.myProfileContainer.setOnClickListener {
            context?.let {
                UserProfileActivity.start(
                    it,
                    UserInfoPref.getUser()
                )
            }
        }
        binding.reviewGiftsContainer.setOnClickListener {
            context?.let {
                ReviewGiftsActivity.start(
                    it
                )
            }
        }
        binding.aboutUs.setOnClickListener { context?.let { AboutUsActivity.start(it) } }
        binding.blockedUsers.setOnClickListener { context?.let { BlockListActivity.start(it) } }

        binding.showNumber.setOnClickListener {
            if (numview.equals(true)) {
                numview = false
                binding.numViewGroup.visibility = View.VISIBLE
            } else {
                numview = true
                binding.numViewGroup.visibility = View.GONE
            }
        }
        binding.contactUs.setOnClickListener { openSupportForm(requireContext()) }
        binding.bugReport.setOnClickListener { openSupportForm(requireContext()) }
        binding.suggestions.setOnClickListener { openSupportForm(requireContext()) }

        when (UserPreferences.phoneVisibilityStatus) {
            PhoneVisibility.None -> {
                binding.moreNone.isChecked = true
            }
            PhoneVisibility.JustCharities -> {
                binding.moreCharity.isChecked = true
            }
            PhoneVisibility.All -> {
                binding.moreAll.isChecked = true
            }
            null -> {}
        }

        binding.logInLogOut.setOnClickListener {
            context?.runOrStartAuth {
                showPromptDialog(
                    getString(R.string.logout_message),
                    positiveButtonText = getString(R.string.yes),
                    negativeButtonText = getString(R.string.no),
                    onPositiveClickCallback = {
                        UserInfoPref.clear()
                        AppPref.clear()
                        KindnessApplication.instance.clearContactList()
                        activity?.recreate()
                    })
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.userInfo = UserInfoPref
    }
}