package com.example.buscardapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope // Importação essencial
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private val auth = SupabaseClient.supabase.auth
    private val postgrest = SupabaseClient.supabase.postgrest

    private val _userEmail = MutableStateFlow(auth.currentUserOrNull()?.email ?: "Utilizador")
    val userEmail: StateFlow<String> = _userEmail

    fun criarCartao(tipoPasse: String, onSucesso: () -> Unit) {
        val user = auth.currentUserOrNull() ?: return
        val userId = user.id

        viewModelScope.launch {
            try {
                // 1. Atualiza a tabela 'profiles' para ativar a visibilidade do cartão na UI
                postgrest["profiles"].update({
                    set("has_card", true)
                    set("card_type", tipoPasse)
                }) {
                    filter { eq("id", userId) }
                }

                // 2. Insere os dados detalhados na tabela 'user_cards' (saldo, etc)
                // Note: Use os nomes exatos das colunas da sua imagem (saldo, is_active, etc)
                postgrest["user_cards"].insert(
                    mapOf(
                        "user_id" to userId,
                        "card_type" to tipoPasse,
                        "is_active" to true,
                        "saldo" to 0.0, // Começa com saldo zero ou valor padrão
                        "trips_left" to 10, // Exemplo de valor inicial
                        "total_trips" to 0
                    )
                )

                // 3. Executa o callback para a HomeScreen recarregar os dados
                onSucesso()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}