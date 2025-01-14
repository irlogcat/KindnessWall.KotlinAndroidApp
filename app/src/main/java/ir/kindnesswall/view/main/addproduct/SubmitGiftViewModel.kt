package ir.kindnesswall.view.main.addproduct

import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.*
import ir.kindnesswall.data.local.AppPref
import ir.kindnesswall.data.local.dao.AppDatabase
import ir.kindnesswall.data.local.dao.catalog.GiftModel
import ir.kindnesswall.data.local.dao.submitrequest.RegisterGiftRequestModel
import ir.kindnesswall.data.model.CustomResult
import ir.kindnesswall.data.model.UploadImageResponse
import ir.kindnesswall.data.repository.FileUploadRepo
import ir.kindnesswall.data.repository.GiftRepo
import kotlinx.coroutines.launch
import java.math.BigDecimal

class SubmitGiftViewModel(
    private val fileUploadRepo: FileUploadRepo,
    private val giftRepo: GiftRepo,
    private val appDatabase: AppDatabase
) : ViewModel() {
    var hasRegion: Boolean = false
    var title = MutableLiveData<String>()
    var description = MutableLiveData<String>()
    var price = MutableLiveData<String>()

    var backupDataLiveData = MutableLiveData<RegisterGiftRequestModel>()

    var categoryId = MutableLiveData<Int>()
    var categoryName = MutableLiveData<String>()

    var regionId = MutableLiveData<Int>()
    var regionName = MutableLiveData<String>()

    var provinceId = MutableLiveData<Int>()
    var provinceName = MutableLiveData<String>()

    var cityId = MutableLiveData<Int>()
    var cityName = MutableLiveData<String>()

    var isNew = true

    var imagesToShow = arrayListOf<String>()
    var imagesToUpload = arrayListOf<String>()

    var uploadedImagesAddress = arrayListOf<String>()

    var uploadImagesLiveData = MutableLiveData<UploadImageResponse>()

    var editableGiftModel: GiftModel? = null

    fun onTitleTextChange(text: CharSequence) {
        title.value = text.toString()
    }

    fun onDescriptionTextChange(text: CharSequence) {
        description.value = text.toString()
    }

    fun onPriceTextChange(text: CharSequence) {
        price.value = text.toString()
    }

    fun submitGift(): LiveData<CustomResult<GiftModel>> {
        val registerGiftRequestModel = RegisterGiftRequestModel(
            title = title.value ?: "",
            description = description.value ?: "",
            price = price.value?.toBigDecimal() ?: BigDecimal.ZERO,
            giftImages = ArrayList(uploadedImagesAddress),
            categoryId = categoryId.value?.toInt() ?: 0,
            provinceId = provinceId.value?.toInt() ?: 0,
            regionId = regionId.value?.toInt(),
            regionName = null,
            cityId = cityId.value?.toInt() ?: 0,
            categoryName = null,
            provinceName = null,
            cityName = null,
            isNew = isNew,
            isBackup = null
        )

        return if (isNew) {
            giftRepo.registerGift(viewModelScope, registerGiftRequestModel)
        } else {
            registerGiftRequestModel.id = editableGiftModel!!.id.toInt()
            giftRepo.updateGift(viewModelScope, registerGiftRequestModel)
        }
    }

    fun uploadImages(context: Context, lifecycleOwner: LifecycleOwner) {
        fileUploadRepo.uploadFile(
            context,
            lifecycleOwner,
            imagesToUpload.first(),
            uploadImagesLiveData
        )
    }

    fun backupData(callback: (Boolean) -> Unit) {
        val registerGiftRequestModel = RegisterGiftRequestModel(
        title = title.value ?: "",
        description = description.value ?: "",
        giftImages= ArrayList(imagesToShow),
        categoryId = categoryId.value?.toInt() ?: 0,
        categoryName = categoryName.value ?: "",
        provinceId = provinceId.value?.toInt() ?: 0,
        provinceName = provinceName.value ?: "",
        regionId = regionId.value?.toInt(),
        regionName = regionName.value ?: "",
        cityId = cityId.value?.toInt() ?: 0,
        cityName = cityName.value ?: "",
        countryId = AppPref.countryId,
        isNew = isNew,
        isBackup = true,
        price =
            if (price.value.isNullOrEmpty()) BigDecimal.ZERO else price.value!!.toBigDecimal()
        )
        viewModelScope.launch {
            appDatabase.registerGiftRequestDao().insert(registerGiftRequestModel)
            callback.invoke(true)
        }
    }

    fun getBackUpData(): MutableLiveData<RegisterGiftRequestModel> {
        backupDataLiveData.value = appDatabase.registerGiftRequestDao().getItem()
        return backupDataLiveData
    }

    fun removeBackupData() {
        appDatabase.registerGiftRequestDao().delete()
    }

    fun clearData() {
        appDatabase.registerGiftRequestDao().delete()

        imagesToShow.clear()
        imagesToUpload.clear()
        uploadedImagesAddress.clear()

        categoryId.value = 0
        categoryName.value = ""
        provinceId.value = 0
        provinceName.value = ""
        regionId.value = 0
        regionName.value = ""
        cityId.value = 0
        cityName.value = ""
        isNew = true
    }

    fun setPhoneVisibility(value : String)= giftRepo.setSettingNumber(viewModelScope,value)
    fun getPhoneVisibility()=giftRepo.getSettingNumber(viewModelScope)
}