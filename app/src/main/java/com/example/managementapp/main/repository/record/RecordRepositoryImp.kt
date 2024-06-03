package com.example.managementapp.main.repository.record

import com.example.managementapp.main.local.record.RecordDatabase
import com.example.managementapp.main.model.record.Record
import com.example.managementapp.main.remote.api.RecordApi
import com.example.managementapp.util.MyResource
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class RecordRepositoryImp @Inject constructor(
    val recordDatabase: RecordDatabase,
    val recordApi: RecordApi
): RecordRepository {
    private val recordDao = recordDatabase.recordDao()

    override suspend fun getRecords(
        shouldMakeNetworkRequest: Boolean?,
        roomId: String
    ): MyResource<List<Record>> {
        return try {
            if(shouldMakeNetworkRequest == true){
                getRecordsFromNetwork(roomId)
            } else {
                getRecordsFromDb(roomId)
            }
        }catch (e: Exception){
            MyResource.Error(message = e.message.toString())
        }
    }

    override suspend fun getRecordsFromDb(roomId: String): MyResource<List<Record>> {
        return try {
            val records = recordDao.getRecords(roomId)
            MyResource.Success(
                data = records
            )
        }catch (e: Exception){
            MyResource.Error(
                message = e.message.toString()
            )
        }
    }

    override suspend fun getRecordsFromNetwork(roomId: String): MyResource<List<Record>> {
        return try {
            val records = recordApi.getRecords(roomId).records
            recordDao.upsertRecords(records)
            MyResource.Success(
                data = records
            )
        }catch (e: Exception){
            MyResource.Error(
                message = e.message.toString()
            )
        }
    }

    override suspend fun getRecord(
        shouldMakeNetworkRequest: Boolean?,
        recordId: String
    ): MyResource<Record> {
        return try {
            if(shouldMakeNetworkRequest == true){
                val record = recordApi.getRecord(recordId)
                MyResource.Success(data = record)
            } else {
                val record = recordDao.getRecord(recordId)
                MyResource.Success(data = record)
            }
        } catch (e: Exception){
            MyResource.Error(
                message = e.message.toString()
            )
        }
    }

    override suspend fun getTheLastRecord(shouldMakeNetworkRequest: Boolean?, roomId: String): MyResource<Record> {
        return try {
            if(shouldMakeNetworkRequest == true){
                val record = recordApi.getTheLastRecord(roomId, true)
                MyResource.Success(data = record)
            } else {
                val record = recordDao.getTheLastRecord(roomId)
                MyResource.Success(data = record)
            }
        } catch (e: Exception){
            MyResource.Error(
                message = e.message.toString()
            )
        }
    }

    override suspend fun createRecord(
        roomId: String,
        electricNumber: String,
        waterNumber: String,
        recordedAt: String,
        isTheLast: Boolean,
        recordImages: List<File>?
    ): MyResource<Unit> {
        return try {
            MultipartBody.Part.apply {
                val electricNumberBody = createFormData("electricNumber", electricNumber)
                val waterNumberBody = createFormData("waterNumber", waterNumber)
                val recordedAtBody = createFormData("recordedAt", recordedAt)
                val isTheLastBody = createFormData("isTheLast", isTheLast.toString())
                val imagesBody = ArrayList<MultipartBody.Part>()
                recordImages?.forEachIndexed {index, file ->
                    println(file.name)
                    val requestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                    imagesBody.add(createFormData("photos", file.name, requestBody))
                }
                recordApi.createRecord(roomId, electricNumberBody, waterNumberBody, recordedAtBody, isTheLastBody, imagesBody)
            }
            MyResource.Success(Unit)

        } catch (e: Exception){
            MyResource.Error(e.message.toString())
        }
    }

    override suspend fun updateRecord(
        roomId: String,
        recordId: String,
        electricNumber: String,
        waterNumber: String,
        recordedAt: String,
        recordImages: List<File>?
    ): MyResource<Unit> {
        return try {
            MultipartBody.Part.apply {
                val electricNumberBody = createFormData("electricNumber", electricNumber)
                val waterNumberBody = createFormData("waterNumber", waterNumber)
                val recordedAtBody = createFormData("recordedAt", recordedAt)
                val imagesBody = ArrayList<MultipartBody.Part>()
                recordImages?.forEachIndexed {index, file ->
                    println(file.name)
                    val requestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                    imagesBody.add(createFormData("photos", file.name, requestBody))
                }
                recordApi.updateRecord(roomId, recordId, electricNumberBody, waterNumberBody, recordedAtBody, imagesBody)
            }
            MyResource.Success(Unit)

        } catch (e: Exception){
            MyResource.Error(e.message.toString())
        }
    }

    override suspend fun deleteRecord(recordId: String): MyResource<Unit> {
        return try {
            recordApi.deleteRecord(recordId)
            MyResource.Success(Unit)
        } catch (e: Exception){
            MyResource.Error(e.message.toString())
        }
    }
}