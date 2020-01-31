package com.farshidabz.kindnesswall.view.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.farshidabz.kindnesswall.data.model.BaseModel
import com.farshidabz.kindnesswall.data.model.CustomResult
import com.farshidabz.kindnesswall.data.repository.AuthRepo


/**
 * Created by farshid.abazari since 2019-10-31
 *
 * Usage:
 *
 * How to call:
 *
 * Useful parameter:
 *
 */

class AuthenticationViewModel(private val authRepo: AuthRepo) : ViewModel() {
    var phoneNumber = MutableLiveData<String>()

    /**
     * a function for intercepting mobile number change and send it to view class
     */
    fun onPhoneNumberChanged(text: CharSequence) {
        phoneNumber.postValue(text.toString())
    }

    fun registerUser(): LiveData<CustomResult<BaseModel>> {
        return authRepo.registerUser(phoneNumber.value ?: "")
    }
}