package com.example.workmanager

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.UUID

class ImageViewModel: ViewModel() {
    var unCommpressedUri = MutableLiveData<Uri>()
    var compressedUri = MutableLiveData<Uri>()

    var workId = MutableLiveData<UUID>()

    fun setWorkId(id: UUID){
        workId.value = id
    }
    fun setUnCompressedUri(uri: Uri){
        unCommpressedUri.value = uri
    }
    fun setCompressedUri(uri: Uri){
        compressedUri.value = uri
    }
}