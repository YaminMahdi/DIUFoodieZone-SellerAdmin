package com.diu.mlab.foodie.admin.data.repo

import android.app.Activity
import android.content.Context
import android.credentials.GetCredentialException
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.diu.mlab.foodie.admin.domain.RequestState
import com.diu.mlab.foodie.admin.domain.model.SuperUser
import com.diu.mlab.foodie.admin.domain.repo.AuthRepo
import com.diu.mlab.foodie.admin.util.Constants
import com.diu.mlab.foodie.admin.util.copyUriToFile
import com.diu.mlab.foodie.admin.util.transformedEmailId
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject


class AuthRepoImpl @Inject constructor(
    private val auth : FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val context: Context
    ) : AuthRepo {

    override fun firebaseLogin(
        credential: GoogleIdTokenCredential,
        success: (superUser: SuperUser) -> Unit,
        failed: (msg: String) -> Unit
    ) {
        val authCredential = GoogleAuthProvider.getCredential(credential.idToken, null)
        auth.signInWithCredential(authCredential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithCredential:success")
                    firestore.collection("superUserProfiles").document(credential.id.transformedEmailId())
                        .get()
                        .addOnSuccessListener { document ->
                            if (document != null && document.exists()) {
                                Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                                val superUser = document.toObject(SuperUser::class.java)!!
                                success.invoke(superUser)
                            } else {
                                Log.d("TAG", "No such document")
                                failed.invoke("User doesn't exist")
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.d("TAG", "get failed with ", exception)
                            failed.invoke("Something went wrong")

                        }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                    failed.invoke("Something went wrong")
                }
            }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun firebaseSignup(
        credential: GoogleIdTokenCredential,
        superUser: SuperUser,
        success: () -> Unit,
        failed: (msg: String) -> Unit
    ) {
        val path = firestore.collection("superUserProfiles").document(credential.id.transformedEmailId())
        val authCredential = GoogleAuthProvider.getCredential(credential.idToken, null)
        auth.signInWithCredential(authCredential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithCredential:success")
                    path.get()
                        .addOnSuccessListener { document ->
                            if (document != null && document.exists()) {
                                Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                                val su = document.toObject(SuperUser::class.java)!!
                                when (su.status) {
                                    "accepted" -> { failed.invoke("Already Registered") }
                                    "denied" -> { failed.invoke("Permission Denied") }
                                    else -> { failed.invoke("Permission Pending") }
                                }
                            } else {
                                var tmpUser = superUser
                                val shopStoreRef = storage.reference.child("shop/${superUser.email}")
                                GlobalScope.launch(Dispatchers.IO){
                                    if(superUser.userType=="shop"){
                                        var logo = context.copyUriToFile(Uri.parse(superUser.pic))
                                        var cover = context.copyUriToFile(Uri.parse(superUser.cover))
                                        logo = Compressor.compress(context, logo) {
                                            default(height = 360, width = 360, format = Bitmap.CompressFormat.JPEG)
                                        }
                                        cover = Compressor.compress(context, cover) {
                                            default(width = 1080, format = Bitmap.CompressFormat.JPEG)
                                        }
                                        val logoLink = shopStoreRef.child("logo.jpg")
                                            .putFile(Uri.fromFile(logo)).await().storage.downloadUrl.await()

                                        val coverLink = shopStoreRef.child("cover.jpg")
                                            .putFile(Uri.fromFile(cover)).await().storage.downloadUrl.await()

                                        val qrLink = shopStoreRef.child("qr.jpg")
                                            .putFile(Uri.parse(superUser.qr)).await().storage.downloadUrl.await()
                                        tmpUser = superUser.copy(
                                            pic = logoLink.toString(),
                                            cover = coverLink.toString(),
                                            qr = qrLink.toString() )
                                    }


                                    path.set(tmpUser)
                                        .addOnSuccessListener {
                                            Log.d("TAG", "DocumentSnapshot successfully written!")
                                            success.invoke()
                                        }
                                        .addOnFailureListener { exception ->
                                            Log.d("TAG", "get failed with ", exception)
                                            failed.invoke("Something went wrong")
                                        }
                                }
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.d("TAG", "get failed with ", exception)
                            failed.invoke("Something went wrong")

                        }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                    failed.invoke("Something went wrong")
                }
            }

    }


    override suspend fun googleSignIn(activity: Activity, isAuthorized : Boolean): RequestState<GoogleIdTokenCredential> {
        return withContext(Dispatchers.IO){
            val signInGoogleOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(isAuthorized)
                .setServerClientId(Constants.SERVER_CLIENT_ID)
                .build()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(signInGoogleOption)
                .build()
            try {
                val result = CredentialManager
                    .create(context)
                    .getCredential(activity, request)
                val credential = result.credential
                if(credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL){
                    val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    RequestState.Success(googleCredential)
                }else
                    RequestState.Error("Only Google Account Allowed")
            } catch (e: GetCredentialCancellationException) {
                e.printStackTrace()
                if (e.type == GetCredentialException.TYPE_USER_CANCELED)
                    RequestState.Error("Canceled by user.",20)
                else
                    RequestState.Error(e.message ?: "Google Sign In Error!")
            } catch (e: Exception) {
                e.printStackTrace()
                RequestState.Error(e.localizedMessage ?: "Google Sign In Error!")
            }
        }
    }



}