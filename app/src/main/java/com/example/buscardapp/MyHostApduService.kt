package com.example.buscardapp

import android.nfc.cardemulation.HostApduService
import android.os.Bundle

class MyHCEService : HostApduService() {

    // Este método é chamado quando o iPhone (leitor) envia o comando
    override fun processCommandApdu(commandApdu: ByteArray?, extras: Bundle?): ByteArray {
        val command = commandApdu?.joinToString("") { "%02X".format(it) } ?: ""

        // Verifica se o comando é o "SELECT AID" que configuraste no XML
        // O comando que o iPhone envia é: 00A4040007F001020304050600
        return if (command == "00A4040007F001020304050600") {
            // Aqui respondes com o que quiseres + "9000" (Sucesso em NFC)
            val responseText = "SALDO:20.00"
            responseText.toByteArray() + byteArrayOf(0x90.toByte(), 0x00.toByte())
        } else {
            // Responde "Erro" (6F00) se o comando for desconhecido
            byteArrayOf(0x6F.toByte(), 0x00.toByte())
        }
    }

    override fun onDeactivated(reason: Int) {
        // Chamado quando a ligação NFC é cortada
    }
}