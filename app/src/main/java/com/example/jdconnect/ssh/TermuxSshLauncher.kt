package com.example.jdconnect.ssh

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import com.example.jdconnect.model.Server

/**
 * Helper for launching Termux to run an SSH command to a given server.
 *
 * This uses Termux's RUN_COMMAND interface. The user must enable
 * "allow-external-apps" in Termux config and grant the RUN_COMMAND
 * permission to JDconnect.
 */
object TermuxSshLauncher {

    private const val TERMUX_PACKAGE = "com.termux"
    private const val RUN_COMMAND_ACTION = "com.termux.RUN_COMMAND"
    private const val RUN_COMMAND_SERVICE = "com.termux.app.RunCommandService"

    fun launch(context: Context, server: Server, username: String = "root") {
        val host = server.vpnAddress ?: server.hostname
        if (host.isNullOrBlank()) {
            Toast.makeText(context, "No SSH host for this server", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isTermuxInstalled(context)) {
            Toast.makeText(
                context,
                "Termux is not installed. Install it, then retry SSH.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        // Build the SSH command that Termux should execute.
        val command = "ssh $username@$host"

        try {
            val intent = Intent(RUN_COMMAND_ACTION).apply {
                setClassName(TERMUX_PACKAGE, RUN_COMMAND_SERVICE)
                // Run an interactive shell and execute our command.
                putExtra("com.termux.RUN_COMMAND_PATH", "/data/data/com.termux/files/usr/bin/bash")
                putExtra("com.termux.RUN_COMMAND_ARGUMENTS", arrayOf("-lc", command))
                putExtra("com.termux.RUN_COMMAND_BACKGROUND", false)
            }

            context.startService(intent)
        } catch (e: ActivityNotFoundException) {
            // Fallback: try to open ssh:// URI, or prompt user to install/configure another SSH app.
            try {
                val uri = Uri.parse("ssh://$username@$host")
                val viewIntent = Intent(Intent.ACTION_VIEW, uri)
                context.startActivity(viewIntent)
            } catch (_: Exception) {
                Toast.makeText(
                    context,
                    "No SSH client available. Install Termux or another SSH app.",
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Unable to launch Termux SSH. Check Termux permissions.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun isTermuxInstalled(context: Context): Boolean {
        return try {
            context.packageManager.getPackageInfo(TERMUX_PACKAGE, PackageManager.GET_ACTIVITIES)
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        }
    }
}
