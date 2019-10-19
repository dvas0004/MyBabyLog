package me.davidvassallo.mybabylog

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.wearable.activity.ConfirmationActivity
import android.support.wearable.activity.WearableActivity
import android.widget.Button
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.net.URLEncoder
import java.text.SimpleDateFormat



class MainActivity : WearableActivity() {

    private fun sendAndRequestResponse(textView: TextView, queue: RequestQueue, action: String) {
        // Request a string response from the provided URL.

        // Post parameters
        val timestamp = SimpleDateFormat("HH:mm:ss dd.MM.yyyy").format(java.util.Date())
        val actionEncoded = URLEncoder.encode(action, "UTF-8")
        val timestampEncoded = URLEncoder.encode(timestamp, "UTF-8")

        val baseUrl = resources.getString(R.string.google_app_script_url)
        val secret = resources.getString(R.string.google_app_script_secret)
        val url = "$baseUrl?k=$secret&a=$actionEncoded&t=$timestampEncoded"


        val stringRequest = StringRequest(
            Request.Method.GET, url,
            Response.Listener { response ->

                if (action == "lastFeed"){
                    val alertDialog = AlertDialog.Builder(this@MainActivity).create()
                    alertDialog.setTitle("Last Feeding Time")
                    alertDialog.setMessage(response)
                    alertDialog.setButton(
                        AlertDialog.BUTTON_NEUTRAL, "OK"
                    ) { dialog, which -> dialog.dismiss() }
                    alertDialog.show()
                } else {
                    val intent = Intent(this, ConfirmationActivity::class.java).apply {
                        putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.SUCCESS_ANIMATION)
                        putExtra(ConfirmationActivity.EXTRA_MESSAGE, getString(R.string.msg_sent))
                    }
                    startActivity(intent)
                    textView.text = "Response is: $response"
                }

            },
            Response.ErrorListener { error ->
                val intent = Intent(this, ConfirmationActivity::class.java).apply {
                    putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.FAILURE_ANIMATION)
                    putExtra(ConfirmationActivity.EXTRA_MESSAGE, getString(R.string.msg_fail))
                }
                startActivity(intent)
                textView.text = error.message
            }
        )


        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Enables Always-on
        setAmbientEnabled()
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val textView = findViewById<TextView>(R.id.results)


        val btnStartFeed = findViewById<Button>(R.id.startFeeding)
        btnStartFeed.setOnClickListener { v ->
            sendAndRequestResponse(textView, queue, "Feeding Started")
        }

        val btnEndFeed = findViewById<Button>(R.id.endFeeding)
        btnEndFeed.setOnClickListener { v ->
            sendAndRequestResponse(textView, queue, "Feeding Ended")
        }

        val btnLastFeed = findViewById<Button>(R.id.lastFeedbtn)
        btnLastFeed.setOnClickListener { v ->
            sendAndRequestResponse(textView, queue, "lastFeed")
        }

        val btnPoop = findViewById<Button>(R.id.poop)
        btnPoop.setOnClickListener { v ->
            sendAndRequestResponse(textView, queue, "Poop Detected!!")
        }

    }
}
