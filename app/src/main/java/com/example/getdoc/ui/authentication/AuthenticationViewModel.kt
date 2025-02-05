package com.example.getdoc.ui.authentication

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers

// DataStore initialization
val Context.dataStore by preferencesDataStore(name = "user_prefs")

class AuthenticationViewModel : ViewModel() {

    @SuppressLint("StaticFieldLeak")
    private var context: Context? = null
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val _authUiState = MutableStateFlow(AuthUiState())
    val authUiState = _authUiState.asStateFlow()

    private val _firebaseUser = MutableStateFlow<FirebaseUser?>(firebaseAuth.currentUser)
    val firebaseUser = _firebaseUser.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError = _authError.asStateFlow()

    // Initialize context safely
    fun init(context: Context) {
        if (this.context == null) {
            this.context = context.applicationContext
            Log.d("AuthenticationViewModel", "Context initialized successfully")
            viewModelScope.launch(Dispatchers.IO) {
                loadUserSession()
            }
        }
    }
    private suspend fun saveUserSession(email: String, role: Role?) {
        context?.dataStore?.edit { preferences ->
            preferences[PreferencesKeys.USER_EMAIL] = email
            preferences[PreferencesKeys.USER_ROLE] = role?.name?.uppercase() ?: ""
            preferences[PreferencesKeys.IS_LOGGED_IN] = true
            Log.d("AuthenticationViewModel", "User session saved successfully")
        }
    }

    private suspend fun loadUserSession() {
        context?.dataStore?.data?.first()?.let { preferences ->
            val email = preferences[PreferencesKeys.USER_EMAIL] ?: ""
            val roleString = preferences[PreferencesKeys.USER_ROLE] ?: ""
            val isLoggedIn = preferences[PreferencesKeys.IS_LOGGED_IN] ?: false

            if (isLoggedIn && email.isNotEmpty()) {
                try {
                    val role = Role.valueOf(roleString.uppercase())  // Fix enum handling
                    _authUiState.update { it.copy(email = email, role = role) }
                    _firebaseUser.value = firebaseAuth.currentUser
                    Log.d("AuthenticationViewModel", "User session loaded successfully")
                } catch (e: IllegalArgumentException) {
                    Log.e("AuthenticationViewModel", "Invalid role found in session: $roleString")
                    clearUserSession()
                }
            }
        }
    }

    private suspend fun clearUserSession() {
        context?.dataStore?.edit { preferences ->
            preferences[PreferencesKeys.USER_EMAIL] = ""
            preferences[PreferencesKeys.USER_ROLE] = ""
            preferences[PreferencesKeys.IS_LOGGED_IN] = false
            Log.d("AuthenticationViewModel", "User session cleared successfully")
        }
    }

    companion object PreferencesKeys {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USER_ROLE = stringPreferencesKey("user_role")
        val USER_EMAIL = stringPreferencesKey("user_email")
    }
}

// Role Enum (Ensure consistency in role names)


data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val role: Role? = null
)
