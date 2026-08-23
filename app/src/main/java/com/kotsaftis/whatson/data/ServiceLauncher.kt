package com.kotsaftis.whatson.data

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri

object ServiceLauncher {
    fun open(context: Context, item: FeedItem) {
        val uri = item.link?.trim()?.takeIf { it.isNotEmpty() }?.let(Uri::parse) ?: return
        val packages = packagesFor(item.service)
        val installed = packages.firstOrNull { isInstalled(context, it) }
        val view = Intent(Intent.ACTION_VIEW, uri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (installed != null) setPackage(installed)
        }
        try {
            context.startActivity(view)
        } catch (_: ActivityNotFoundException) {
            val browser = Intent(Intent.ACTION_VIEW, uri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                context.startActivity(browser)
            } catch (_: ActivityNotFoundException) {
                // Nothing to open on this box.
            }
        }
    }

    fun packagesFor(service: String): List<String> {
        val key = service.lowercase()
        return when {
            "netflix" in key -> listOf("com.netflix.mediaclient")
            "prime" in key || "amazon" in key -> listOf(
                "com.amazon.amazonvideo.livingroom",
                "com.amazon.avod",
                "com.amazon.avod.thirdpartyclient",
            )
            key == "max" || "hbo" in key -> listOf(
                "com.wbd.stream",
                "com.hbo.hbonow",
                "com.hbo.android.app",
            )
            "disney" in key -> listOf("com.disney.disneyplus")
            "apple" in key -> listOf(
                "com.apple.atve.androidtv.appletv",
                "com.apple.atve.android.appletv",
            )
            "cosmote" in key -> listOf(
                "gr.cosmote.webtv",
                "com.cosmote.tvplus",
                "gr.cosmote.tv",
            )
            else -> emptyList()
        }
    }

    private fun isInstalled(context: Context, packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        }
    }
}
