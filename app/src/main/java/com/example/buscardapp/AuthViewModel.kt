package com.example.buscardapp

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.IDToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.UUID

class AuthViewModel : ViewModel() {
    private val _authState = MutableStateFlow<String?>(null)
    val authState: StateFlow<String?> = _authState

    private val _userExists = MutableStateFlow(false)
    val userExists: StateFlow<Boolean> = _userExists

    // Função auxiliar para gerar o Nonce (Segurança Google)
    private fun generateNonce(): Pair<String, String> {
        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }
        return Pair(rawNonce, hashedNonce)
    }

    // --- REGISTO COM GOOGLE (Bloqueia se já existir) ---
    fun signUpWithGoogle(context: Context) {
        viewModelScope.launch {
            _userExists.value = false
            val (rawNonce, hashedNonce) = generateNonce()
            val credentialManager = CredentialManager.create(context)

            try {
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setServerClientId("744647664470-8odukj93lh37a56vdvom0ha3qiefo8fr.apps.googleusercontent.com")
                    .setNonce(hashedNonce)
                    .build()

                val request = GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()
                val result = credentialManager.getCredential(context, request)
                val credential = GoogleIdTokenCredential.createFrom(result.credential.data)

                supabase.auth.signInWith(IDToken) {
                    idToken = credential.idToken
                    provider = Google
                    nonce = rawNonce
                }

                val user = supabase.auth.currentUserOrNull()
                // Se a data de criação for diferente da data de login, o user já existia
                val isNewUser = user?.createdAt == user?.lastSignInAt

                if (!isNewUser) {
                    supabase.auth.signOut() // Expulsa o user porque ele já tinha conta
                    _authState.value = "Este e-mail do Google já está registado. Vai para o Login."
                    _userExists.value = true
                } else {
                    _authState.value = "Registo com Google feito com sucesso!"
                }
            } catch (e: Exception) {
                _authState.value = "Erro no Registo: ${e.localizedMessage}"
            }
        }
    }

    // --- LOGIN COM GOOGLE (Deixa entrar sempre) ---
    fun signInWithGoogle(context: Context) {
        viewModelScope.launch {
            _userExists.value = false
            val (rawNonce, hashedNonce) = generateNonce()
            val credentialManager = CredentialManager.create(context)

            try {
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setServerClientId("744647664470-8odukj93lh37a56vdvom0ha3qiefo8fr.apps.googleusercontent.com")
                    .setNonce(hashedNonce)
                    .build()

                val request = GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()
                val result = credentialManager.getCredential(context, request)
                val credential = GoogleIdTokenCredential.createFrom(result.credential.data)

                supabase.auth.signInWith(IDToken) {
                    idToken = credential.idToken
                    provider = Google
                    nonce = rawNonce
                }
                _authState.value = "Login Google efetuado!"
            } catch (e: Exception) {
                _authState.value = "Erro no Login: ${e.localizedMessage}"
            }
        }
    }

    // --- REGISTO MANUAL ---
    fun signUpWithEmail(email: String, pass: String) {
        viewModelScope.launch {
            try {
                _userExists.value = false
                supabase.auth.signUpWith(Email) {
                    this.email = email
                    password = pass
                }
                _authState.value = "Sucesso! Verifica o teu email."
            } catch (e: Exception) {
                if (e.message?.contains("already registered", true) == true) {
                    _authState.value = "Este e-mail já está em uso. Entra no Login."
                    _userExists.value = true
                } else {
                    _authState.value = "Erro: ${e.localizedMessage}"
                }
            }
        }
    }

    // --- LOGIN MANUAL ---
    fun signInWithEmail(email: String, pass: String) {
        viewModelScope.launch {
            try {
                _userExists.value = false
                supabase.auth.signInWith(Email) {
                    this.email = email
                    password = pass
                }
                _authState.value = "Login efetuado!"
            } catch (e: Exception) {
                _authState.value = "Erro: Credenciais inválidas."
            }
        }
    }
}