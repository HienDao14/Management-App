package com.example.managementapp.main.repository.tenant

import android.util.Log
import com.example.managementapp.main.local.tenant.TenantDatabase
import com.example.managementapp.main.model.tenant.Tenant
import com.example.managementapp.main.remote.api.TenantApi
import com.example.managementapp.util.MyResource
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.Date
import javax.inject.Inject

class TenantRepositoryImp @Inject constructor(
    val tenantDatabase: TenantDatabase,
    val tenantApi: TenantApi
): TenantRepository{
    val tenantDao = tenantDatabase.tenantDao()

    override suspend fun getTenants(
        shouldMakeNetworkRequest: Boolean?,
        roomId: String
    ): MyResource<List<Tenant>> {
        return try {
            if(shouldMakeNetworkRequest == true){
                val list = tenantApi.getTenants(roomId)
                tenantDao.upsertTenants(list.tenants)
                MyResource.Success(data = list.tenants)
            } else {
                val list = tenantDao.getTenants(roomId)
                MyResource.Success(data = list)
            }
        }catch (e: Exception){
            MyResource.Error(
                message = e.message.toString()
            )
        }
    }

    override suspend fun getTenantsInApartment(
        shouldMakeNetworkRequest: Boolean?,
        apartmentId: String
    ): MyResource<List<Tenant>> {
        return try {
            if(shouldMakeNetworkRequest == true){
                val list = tenantApi.getTenantsInApartment(apartmentId)
                tenantDao.upsertTenants(list.tenants)
                MyResource.Success(data = list.tenants)
            } else {
                val list = tenantDao.getTenantsInApartment(apartmentId)
                MyResource.Success(data = list)
            }
        }catch (e: Exception){
            MyResource.Error(
                message = e.message.toString()
            )
        }
    }

    override suspend fun getTenant(
        shouldMakeNetworkRequest: Boolean?,
        roomId: String,
        tenantId: String
    ): MyResource<Tenant> {
        return try {
            if(shouldMakeNetworkRequest == true){
                val tenant = tenantApi.getTenantsById(tenantId, roomId)
                tenantDao.upsertTenant(tenant)
                MyResource.Success(data = tenant)
            } else {
                val tenant = tenantDao.getTenantById(roomId, tenantId)
                MyResource.Success(data = tenant)
            }
        } catch (e: Exception){
            MyResource.Error(
                message = e.message.toString()
            )
        }
    }

    override suspend fun createTenant(
        apartmentId: String,
        roomId: String,
        fullName: String,
        gender: String,
        phoneNumber: String,
        dob: String,
        placeOfOrigin: String,
        identityCardNumber: String,
        deposit: Int,
        startDate: String,
        endDate: String?,
        note: String?,
        roomName: String,
        identityCardImages: List<File>?
    ): MyResource<Unit> {
        return try {
            MultipartBody.Part.apply {
                val fullNameBody = createFormData("fullName", fullName)
                val genderBody = createFormData("gender", gender)
                val phoneNumBody = createFormData("phoneNum", phoneNumber)
                val dobBody = createFormData("dob", dob)
                val placeOfOriginBody = createFormData("placeOfOrigin", placeOfOrigin)
                val identityCardBody = createFormData("identityCard", identityCardNumber)
                val depositBody = createFormData("deposit", deposit.toString())
                val startDateBody = createFormData("startDate", startDate)
                val endDateBody = createFormData("endDate", endDate?: "")
                val noteBody = createFormData("note", note ?: "")
                val roomNameBody = createFormData("roomName", roomName)
                val imagesBody = ArrayList<MultipartBody.Part>()
                identityCardImages?.forEachIndexed {index, file ->
                    println(file.name)
                    val requestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                    imagesBody.add(createFormData("photos", file.name, requestBody))
                }
                println(imagesBody.size)
                tenantApi.createTenant(
                    apartmentId, roomId,
                    fullNameBody, genderBody, dobBody, phoneNumBody, placeOfOriginBody, identityCardBody, depositBody, startDateBody, endDateBody, noteBody, roomNameBody,
                    photos = if(imagesBody.isEmpty()) null else  imagesBody.toList()
                )
            }
            MyResource.Success(Unit)

        } catch (e: Exception){
            MyResource.Error(e.message.toString())
        }
    }

    override suspend fun updateTenant(
        tenantId: String,
        fullName: String,
        gender: String,
        phoneNumber: String,
        dob: String,
        placeOfOrigin: String,
        identityCardNumber: String,
        deposit: Int,
        startDate: String,
        endDate: String?,
        note: String?,
        roomName: String,
        identityCardImages: List<File>?
    ): MyResource<Unit> {
        return try {
            MultipartBody.Part.apply {
                val fullNameBody = createFormData("fullName", fullName)
                val genderBody = createFormData("gender", gender)
                val phoneNumBody = createFormData("phoneNum", phoneNumber)
                val dobBody = createFormData("dob", dob)
                val placeOfOriginBody = createFormData("placeOfOrigin", placeOfOrigin)
                val identityCardBody = createFormData("identityCard", identityCardNumber)
                val depositBody = createFormData("deposit", deposit.toString())
                val startDateBody = createFormData("startDate", startDate)
                val endDateBody = createFormData("endDate", endDate?: "")
                val noteBody = createFormData("note", note ?: "")
                val roomNameBody = createFormData("roomName", roomName)
                val imagesBody = ArrayList<MultipartBody.Part>()
                identityCardImages?.forEachIndexed {index, file ->
                    println(file.name)
                    val requestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                    imagesBody.add(createFormData("photos", file.name, requestBody))
                }
                tenantApi.updateTenant(
                    tenantId,
                    fullNameBody, genderBody, dobBody, phoneNumBody, placeOfOriginBody, identityCardBody, depositBody, startDateBody, endDateBody, noteBody, roomNameBody,
                    photos = if(imagesBody.isEmpty()) null else  imagesBody.toList()
                )
            }
            MyResource.Success(Unit)
        } catch (e: Exception){
            println(e.message.toString())
            MyResource.Error(e.message.toString())
        }
    }

    override suspend fun deleteTenant(roomId: String, tenantId: String): MyResource<Unit> {
        return try {
            tenantApi.deleteTenantById(tenantId, roomId)
            tenantDao.deleteTenantById(roomId, tenantId)
            MyResource.Success(Unit)
        }catch (e: Exception){
            MyResource.Error(e.message.toString())
        }
    }
}