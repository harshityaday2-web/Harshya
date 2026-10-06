package com.example.actions

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.net.Uri
import android.os.SystemClock
import android.provider.AlarmClock
import android.provider.CalendarContract
import android.view.KeyEvent
import android.widget.Toast
import java.net.URLEncoder

data class ExecutionResult(
    val success: Boolean,
    val messageInHindi: String,
    val appOpened: String? = null
)

object AppActionExecutor {

    fun isPackageInstalled(context: Context, packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    /**
     * WhatsApp message composer.
     * Can target specific phone or general WhatsApp share.
     */
    fun openWhatsApp(context: Context, contactOrPhone: String?, message: String): ExecutionResult {
        return try {
            val isInstalled = isPackageInstalled(context, "com.whatsapp") ||
                    isPackageInstalled(context, "com.whatsapp.w4b")

            val cleanPhone = contactOrPhone?.replace(Regex("[^0-9+]"), "")

            if (!cleanPhone.isNullOrBlank() && cleanPhone.length >= 10) {
                // Direct WhatsApp API URI
                val encodedText = URLEncoder.encode(message, "UTF-8")
                val uri = Uri.parse("https://api.whatsapp.com/send?phone=$cleanPhone&text=$encodedText")
                val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
                ExecutionResult(
                    success = true,
                    messageInHindi = "WhatsApp पर संदेश तैयार कर दिया गया है। पुष्टि के लिए Send दबाएं।",
                    appOpened = "WhatsApp"
                )
            } else {
                // Draft message in WhatsApp
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, message)
                    if (isInstalled) {
                        setPackage("com.whatsapp")
                    }
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
                ExecutionResult(
                    success = true,
                    messageInHindi = if (isInstalled) {
                        "WhatsApp खुल गया है। कृपया संपर्क चुनें और भेजें।"
                    } else {
                        "WhatsApp स्थापित नहीं है, शेयरिंग मेनू खोल दिया गया है।"
                    },
                    appOpened = "WhatsApp"
                )
            }
        } catch (e: Exception) {
            ExecutionResult(
                success = false,
                messageInHindi = "WhatsApp खोलने में त्रुटि हुई: ${e.localizedMessage ?: "ऐप अनुपलब्ध"}"
            )
        }
    }

    /**
     * Search YouTube or play video.
     */
    fun openYouTube(context: Context, query: String): ExecutionResult {
        return try {
            val encodedQuery = URLEncoder.encode(query, "UTF-8")
            val isYouTubeInstalled = isPackageInstalled(context, "com.google.android.youtube")

            val intent = if (isYouTubeInstalled) {
                Intent(Intent.ACTION_SEARCH).apply {
                    setPackage("com.google.android.youtube")
                    putExtra("query", query)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            } else {
                Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=$encodedQuery")).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            }
            context.startActivity(intent)
            ExecutionResult(
                success = true,
                messageInHindi = "YouTube पर \"$query\" के परिणाम खोल दिए गए हैं।",
                appOpened = "YouTube"
            )
        } catch (e: Exception) {
            ExecutionResult(
                success = false,
                messageInHindi = "YouTube खोलने में असमर्थ: ${e.localizedMessage}"
            )
        }
    }

    /**
     * Media Play/Pause/Next/Stop.
     */
    fun controlMedia(context: Context, command: String): ExecutionResult {
        return try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
            val keyCode = when (command.lowercase()) {
                "play", "chalao", "start" -> KeyEvent.KEYCODE_MEDIA_PLAY
                "pause", "roko", "stop", "chup" -> KeyEvent.KEYCODE_MEDIA_PAUSE
                "next", "agla" -> KeyEvent.KEYCODE_MEDIA_NEXT
                "previous", "pichla" -> KeyEvent.KEYCODE_MEDIA_PREVIOUS
                else -> KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
            }

            val eventDown = KeyEvent(KeyEvent.ACTION_DOWN, keyCode)
            val eventUp = KeyEvent(KeyEvent.ACTION_UP, keyCode)
            audioManager?.dispatchMediaKeyEvent(eventDown)
            audioManager?.dispatchMediaKeyEvent(eventUp)

            ExecutionResult(
                success = true,
                messageInHindi = "मीडिया कमांड निष्पादित किया गया: $command",
                appOpened = "Media Player"
            )
        } catch (e: Exception) {
            ExecutionResult(
                success = false,
                messageInHindi = "मीडिया नियंत्रण में त्रुटि: ${e.localizedMessage}"
            )
        }
    }

    /**
     * Set Android Alarm.
     */
    fun setAlarm(context: Context, hour: Int, minute: Int, message: String): ExecutionResult {
        return try {
            val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                putExtra(AlarmClock.EXTRA_HOUR, hour)
                putExtra(AlarmClock.EXTRA_MINUTES, minute)
                putExtra(AlarmClock.EXTRA_MESSAGE, message)
                putExtra(AlarmClock.EXTRA_SKIP_UI, false)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            ExecutionResult(
                success = true,
                messageInHindi = "$hour:$minute के लिए अलार्म सेट करने का अनुरोध भेजा गया।",
                appOpened = "Clock"
            )
        } catch (e: Exception) {
            ExecutionResult(
                success = false,
                messageInHindi = "अलार्म सेट नहीं हो सका: ${e.localizedMessage}"
            )
        }
    }

    /**
     * Set Android Timer in seconds.
     */
    fun setTimer(context: Context, seconds: Int, message: String): ExecutionResult {
        return try {
            val intent = Intent(AlarmClock.ACTION_SET_TIMER).apply {
                putExtra(AlarmClock.EXTRA_LENGTH, seconds)
                putExtra(AlarmClock.EXTRA_MESSAGE, message)
                putExtra(AlarmClock.EXTRA_SKIP_UI, false)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            val minutes = seconds / 60
            ExecutionResult(
                success = true,
                messageInHindi = "${if (minutes > 0) "$minutes मिनट " else ""}${seconds % 60} सेकंड का टाइमर सेट कर दिया गया है।",
                appOpened = "Clock"
            )
        } catch (e: Exception) {
            ExecutionResult(
                success = false,
                messageInHindi = "टाइमर सेट नहीं हो सका: ${e.localizedMessage}"
            )
        }
    }

    /**
     * Web Search via default browser.
     */
    fun openWebSearch(context: Context, query: String): ExecutionResult {
        return try {
            val encodedQuery = URLEncoder.encode(query, "UTF-8")
            val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
                putExtra(SearchManager.QUERY, query)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.google.com/search?q=$encodedQuery")
                ).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
                context.startActivity(browserIntent)
            }
            ExecutionResult(
                success = true,
                messageInHindi = "\"$query\" के लिए वेब सर्च खोला गया।",
                appOpened = "Browser"
            )
        } catch (e: Exception) {
            ExecutionResult(
                success = false,
                messageInHindi = "सर्च खोलने में त्रुटि: ${e.localizedMessage}"
            )
        }
    }

    /**
     * Calendar Event creation.
     */
    fun openCalendarEvent(context: Context, title: String, description: String): ExecutionResult {
        return try {
            val intent = Intent(Intent.ACTION_INSERT).apply {
                data = CalendarContract.Events.CONTENT_URI
                putExtra(CalendarContract.Events.TITLE, title)
                putExtra(CalendarContract.Events.DESCRIPTION, description)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            ExecutionResult(
                success = true,
                messageInHindi = "कैलेंडर इवेंट तैयार कर दिया गया है: $title",
                appOpened = "Calendar"
            )
        } catch (e: Exception) {
            ExecutionResult(
                success = false,
                messageInHindi = "कैलेंडर खोलने में असमर्थ: ${e.localizedMessage}"
            )
        }
    }

    /**
     * Share text via Android Share Sheet.
     */
    fun shareText(context: Context, text: String, title: String = "Share via Luna AI"): ExecutionResult {
        return try {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, text)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, title).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(shareIntent)
            ExecutionResult(
                success = true,
                messageInHindi = "टेक्स्ट शेयर करने का मेनू खोल दिया गया है।",
                appOpened = "Share Sheet"
            )
        } catch (e: Exception) {
            ExecutionResult(
                success = false,
                messageInHindi = "शेयर करने में त्रुटि: ${e.localizedMessage}"
            )
        }
    }
}
