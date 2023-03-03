package com.diu.mlab.foodie.admin.data.repo

import android.app.Activity
import android.content.IntentSender.SendIntentException
import android.content.res.Resources
import android.util.Log
import androidx.core.app.ActivityCompat.startIntentSenderForResult
import com.diu.mlab.foodie.admin.R
import com.diu.mlab.foodie.admin.domain.model.SuperUser
import com.diu.mlab.foodie.admin.domain.repo.AuthRepo
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject


class AuthRepoImpl @Inject constructor(
    private val auth : FirebaseAuth,
    private val firestore: FirebaseFirestore
    ) : AuthRepo {

    private val REQUEST_CODE_GOOGLE_SIGN_IN = 69 /* unique request id */

    override fun firebaseLogin(
        credential: SignInCredential,
        success: () -> Unit,
        failed: (msg: String) -> Unit
    ) {
        val authCredential = GoogleAuthProvider.getCredential(credential.googleIdToken, null)
        auth.signInWithCredential(authCredential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithCredential:success")
                    val user = auth.currentUser!!
                    firestore.collection("superUserProfiles").document(credential.id)
                        .get()
                        .addOnSuccessListener { document ->
                            if (document != null) {
                                Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                                val superUser = document.toObject(SuperUser::class.java)!!
                                when (superUser.status) {
                                    "accepted" -> { success.invoke() }
                                    "denied" -> { failed.invoke("Permission Denied") }
                                    else -> { failed.invoke("Permission Pending") }
                                }
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

    override fun firebaseSignup(
        credential: SignInCredential,
        superUser: SuperUser,
        success: () -> Unit,
        failed: (msg: String) -> Unit
    ) {
        val authCredential = GoogleAuthProvider.getCredential(credential.googleIdToken, null)
        auth.signInWithCredential(authCredential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithCredential:success")
                    //val user = auth.currentUser!!
                    firestore.collection("superUserProfiles").document(credential.id)
                        .set(superUser)
                        .addOnSuccessListener {
                            Log.d("TAG", "DocumentSnapshot successfully written!")
                            success.invoke()
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


    override fun googleSignIn(activity: Activity, failed :(msg : String) -> Unit) {

        val request = GetSignInIntentRequest.builder()
            .setServerClientId(activity.getString(R.string.server_client_id))
            .build()
        Identity.getSignInClient(activity)
            .getSignInIntent(request)
            .addOnSuccessListener { result ->
                try {
                    startIntentSenderForResult(activity, result.intentSender, REQUEST_CODE_GOOGLE_SIGN_IN, null, 0, 0, 0, null)
                } catch (e: SendIntentException) {
                    Log.e("TAG", "Google Sign-in failed")
                    failed.invoke("Something went wrong")
                }
            }
            .addOnFailureListener { e ->
                Log.e("TAG", "Google Sign-in failed", e)
                failed.invoke("Something went wrong")

            }
    }


}