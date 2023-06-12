/*
 * ChoiDujour
 * Copyright (C) 2023 BlackMesa123
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.mesalabs.choidujour.ui.activity

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.SharedPreferences
import android.icu.text.DateFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.SystemProperties
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.Spinner
import android.widget.Toast

import java.io.File
import java.io.IOException
import java.util.UUID

import org.json.JSONException

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SwitchCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator

import com.google.android.material.snackbar.Snackbar

import io.mesalabs.oneui.support.activity.AppBarActivity
import io.mesalabs.oneui.support.view.recyclerview.SupportGoToTopClickListener

import org.lineageos.updater.UpdatesCheckReceiver
import org.lineageos.updater.UpdatesListActivity
import org.lineageos.updater.UpdatesListAdapter
import org.lineageos.updater.controller.UpdaterController
import org.lineageos.updater.controller.UpdaterService
import org.lineageos.updater.controller.UpdaterService.LocalBinder
import org.lineageos.updater.download.DownloadClient
import org.lineageos.updater.misc.BuildInfoUtils
import org.lineageos.updater.misc.Constants
import org.lineageos.updater.misc.StringGenerator
import org.lineageos.updater.misc.Utils
import org.lineageos.updater.model.UpdateInfo
import org.lineageos.updater.model.UpdateStatus

import io.mesalabs.choidujour.R

class UpdatesActivity : AppBarActivity(), UpdatesListActivity {
    private var updaterService: UpdaterService? = null
    private lateinit var broadcastReceiver: BroadcastReceiver

    private lateinit var listAdapter: UpdatesListAdapter

    private var refreshIconView: View? = null
    private lateinit var refreshAnimation: RotateAnimation

    private var toBeExported: UpdateInfo? = null
    private val exportUpdate : ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val intent: Intent? = result.data
            if (intent != null) {
                val uri = intent.data as UpdateInfo
                exportUpdate(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_updates)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        listAdapter = UpdatesListAdapter(this)
        recyclerView.adapter = listAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        val animator = recyclerView.itemAnimator
        if (animator is SimpleItemAnimator) {
            animator.supportsChangeAnimations = false
        }
        recyclerView.seslSetGoToTopEnabled(true)
        recyclerView.seslSetOnGoToTopClickListener(SupportGoToTopClickListener())

        broadcastReceiver = object : BroadcastReceiver()  {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    UpdaterController.ACTION_UPDATE_STATUS -> {
                        val downloadId = intent.getStringExtra(UpdaterController.EXTRA_DOWNLOAD_ID)
                        handleDownloadStatusChange(downloadId)
                        listAdapter.notifyItemChanged(downloadId)
                    }
                    UpdaterController.ACTION_DOWNLOAD_PROGRESS, UpdaterController.ACTION_INSTALL_PROGRESS -> {
                        val downloadId = intent.getStringExtra(UpdaterController.EXTRA_DOWNLOAD_ID)
                        listAdapter.notifyItemChanged(downloadId)
                    }
                    UpdaterController.ACTION_UPDATE_REMOVED -> {
                        val downloadId = intent.getStringExtra(UpdaterController.EXTRA_DOWNLOAD_ID)
                        listAdapter.removeItem(downloadId)
                    }
                }
            }
        }

        defaultHomeAsUp()

        appBarTitleExpanded = getString(org.lineageos.updater.R.string.header_title_text,
            Utils.getDisplayVersion(BuildInfoUtils.getBuildVersion()))

        updateLastCheckedString()

        val subTitleView = AppCompatTextView(this)
        subTitleView.gravity = Gravity.CENTER

        var subTitleText = getString(org.lineageos.updater.R.string.header_android_version,
            Build.VERSION.RELEASE)
        subTitleText += "\n"
        subTitleText += StringGenerator.getDateLocalizedUTC(this,
            DateFormat.LONG, BuildInfoUtils.getBuildDateTimestamp())

        subTitleView.text = subTitleText

        setAppBarCustomSubtitleView(subTitleView)

        refreshAnimation = RotateAnimation(
            360.0f, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        refreshAnimation.interpolator = LinearInterpolator()
        refreshAnimation.duration = 1000
    }

    override fun onStart() {
        super.onStart()

        val intent = Intent(this, UpdaterService::class.java)
        startService(intent)
        bindService(intent, connection, BIND_AUTO_CREATE)

        val intentFilter = IntentFilter()
        intentFilter.addAction(UpdaterController.ACTION_UPDATE_STATUS)
        intentFilter.addAction(UpdaterController.ACTION_DOWNLOAD_PROGRESS)
        intentFilter.addAction(UpdaterController.ACTION_INSTALL_PROGRESS)
        intentFilter.addAction(UpdaterController.ACTION_UPDATE_REMOVED)
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter)
    }

    override fun onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
        if (updaterService != null) {
            unbindService(connection)
        }
        super.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_refresh -> {
                downloadUpdatesList(true)
                return true
            }
            R.id.menu_preferences -> {
                showPreferencesDialog()
                return true
            }
            R.id.menu_show_changelog -> {
                val openUrl = Intent(Intent.ACTION_VIEW,
                    Uri.parse(Utils.getChangelogURL(this)))
                startActivity(openUrl)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private var connection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as LocalBinder
            updaterService = binder.service
            listAdapter.setUpdaterController(updaterService!!.updaterController)
            getUpdatesList()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            listAdapter.setUpdaterController(null)
            updaterService = null
            listAdapter.notifyDataSetChanged()
        }
    }

    @Throws(IOException::class, JSONException::class)
    private fun loadUpdatesList(jsonFile: File, manualRefresh: Boolean) {
        Log.d(TAG, "Adding remote updates")
        val controller: UpdaterController = updaterService!!.updaterController
        var newUpdates = false

        val updates = Utils.parseJson(jsonFile, true)
        val updatesOnline: ArrayList<String> = ArrayList()
        for (update in updates) {
            newUpdates = newUpdates or controller.addUpdate(update)
            updatesOnline.add(update.downloadId)
        }
        controller.setUpdatesAvailableOnline(updatesOnline, true)

        if (manualRefresh) {
            showSnackbar(
                if (newUpdates)
                    org.lineageos.updater.R.string.snack_updates_found
                else
                    org.lineageos.updater.R.string.snack_no_updates_found,
                Snackbar.LENGTH_SHORT
            )
        }

        val updateIds: ArrayList<String> = ArrayList()
        val sortedUpdates = controller.updates
        if (sortedUpdates.isEmpty()) {
            findViewById<View>(R.id.no_new_updates_view).visibility = View.VISIBLE
            findViewById<View>(R.id.recycler_view).visibility = View.GONE
        } else {
            findViewById<View>(R.id.no_new_updates_view).visibility = View.GONE
            findViewById<View>(R.id.recycler_view).visibility = View.VISIBLE
            sortedUpdates.sortedWith { o1, o2 -> o2.timestamp.compareTo(o1.timestamp) }
            for (update in sortedUpdates) {
                updateIds.add(update.downloadId)
            }
            listAdapter.setData(updateIds)
            listAdapter.notifyDataSetChanged()
        }
    }

    private fun getUpdatesList() {
        val jsonFile = Utils.getCachedUpdateList(this)
        if (jsonFile.exists()) {
            try {
                loadUpdatesList(jsonFile, false)
                Log.d(TAG, "Cached list parsed")
            } catch (e: IOException) {
                Log.e(TAG, "Error while parsing json list", e)
            } catch (e: JSONException) {
                Log.e(TAG, "Error while parsing json list", e)
            }
        } else {
            downloadUpdatesList(false)
        }
    }

    private fun processNewJson(json: File, jsonNew: File, manualRefresh: Boolean) {
        try {
            loadUpdatesList(jsonNew, manualRefresh)
            val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
            val millis = System.currentTimeMillis()
            preferences.edit().putLong(Constants.PREF_LAST_UPDATE_CHECK, millis).apply()
            updateLastCheckedString()
            if (json.exists() && Utils.isUpdateCheckEnabled(this)
                && Utils.checkForNewUpdates(json, jsonNew)) {
                UpdatesCheckReceiver.updateRepeatingUpdatesCheck(this)
            }
            // In case we set a one-shot check because of a previous failure
            UpdatesCheckReceiver.cancelUpdatesCheck(this)
            //noinspection ResultOfMethodCallIgnored
            jsonNew.renameTo(json)
        } catch (e: IOException) {
            Log.e(TAG, "Could not read json", e)
            showSnackbar(org.lineageos.updater.R.string.snack_updates_check_failed,
                Snackbar.LENGTH_LONG)
        } catch (e: JSONException) {
            Log.e(TAG, "Could not read json", e)
            showSnackbar(org.lineageos.updater.R.string.snack_updates_check_failed,
                Snackbar.LENGTH_LONG)
        }
    }

    private fun downloadUpdatesList(manualRefresh: Boolean) {
        val jsonFile = Utils.getCachedUpdateList(this)
        val jsonFileTmp = File(jsonFile.absolutePath + UUID.randomUUID())
        val url = Utils.getServerURL(this)
        Log.d(TAG, "Checking $url")

        val callback = object : DownloadClient.DownloadCallback {
            override fun onFailure(cancelled: Boolean) {
                Log.e(TAG, "Could not download updates list")
                runOnUiThread {
                    if (!cancelled) {
                        showSnackbar(org.lineageos.updater.R.string.snack_updates_check_failed,
                            Snackbar.LENGTH_LONG)
                    }
                    refreshAnimationStop()
                }
            }

            override fun onResponse(headers: DownloadClient.Headers?) {
            }

            override fun onSuccess() {
                runOnUiThread {
                    Log.d(TAG, "List downloaded")
                    processNewJson(jsonFile, jsonFileTmp, manualRefresh)
                    refreshAnimationStop()
                }
            }
        }

        try {
            val downloadClient = DownloadClient.Builder()
                .setUrl(url)
                .setDestination(jsonFileTmp)
                .setDownloadCallback(callback)
                .build()
            refreshAnimationStart()
            downloadClient.start()
        } catch (exception: IOException) {
            Log.e(TAG, "Could not build download client")
            showSnackbar(org.lineageos.updater.R.string.snack_updates_check_failed,
                Snackbar.LENGTH_LONG)
        }
    }

    private fun updateLastCheckedString() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val lastCheck = preferences.getLong(Constants.PREF_LAST_UPDATE_CHECK, -1) / 1000
        val lastCheckString = getString(
            org.lineageos.updater.R.string.header_last_updates_check,
            StringGenerator.getDateLocalized(this, DateFormat.LONG, lastCheck),
            StringGenerator.getTimeLocalized(this, lastCheck)
        )
        /*
        val headerLastCheck = findViewById<TextView>(R.id.header_last_check)
        headerLastCheck.text = lastCheckString
        */
    }

    private fun handleDownloadStatusChange(downloadId: String?) {
        val update: UpdateInfo = updaterService!!.updaterController.getUpdate(downloadId)
        when (update.status) {
            UpdateStatus.PAUSED_ERROR -> showSnackbar(org.lineageos.updater.R.string.snack_download_failed,
                Snackbar.LENGTH_LONG)
            UpdateStatus.VERIFICATION_FAILED -> showSnackbar(org.lineageos.updater.R.string.snack_download_verification_failed,
                Snackbar.LENGTH_LONG)
            UpdateStatus.VERIFIED -> showSnackbar(org.lineageos.updater.R.string.snack_download_verified,
                Snackbar.LENGTH_LONG)
            else -> {
                // no-op
            }
        }
    }

    override fun getInstance(): AppCompatActivity = this

    override fun exportUpdate(update: UpdateInfo) {
        toBeExported = update

        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.setType("application/zip")
        intent.putExtra(Intent.EXTRA_TITLE, update.name)

        exportUpdate.launch(intent)
    }

    override fun showSnackbar(stringId: Int, duration: Int) {
        Snackbar.make(coordinatorLayout.parent as View, stringId, duration).show()
    }

    private fun refreshAnimationStart() {
        if (refreshIconView == null) {
            refreshIconView = findViewById(R.id.menu_refresh)
        }
        if (refreshIconView != null) {
            refreshAnimation.repeatCount = Animation.INFINITE
            refreshIconView!!.startAnimation(refreshAnimation)
            refreshIconView!!.isEnabled = false
        }
    }

    private fun refreshAnimationStop() {
        if (refreshIconView != null) {
            refreshAnimation.repeatCount = 0
            refreshIconView!!.isEnabled = true
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun showPreferencesDialog() {
        val view: View = LayoutInflater.from(this)
            .inflate(org.lineageos.updater.R.layout.preferences_dialog, null)
        val autoCheckInterval = view.findViewById<Spinner>(
            org.lineageos.updater.R.id.preferences_auto_updates_check_interval)
        val autoDelete = view.findViewById<SwitchCompat>(
            org.lineageos.updater.R.id.preferences_auto_delete_updates)
        val meteredNetworkWarning = view.findViewById<SwitchCompat>(
            org.lineageos.updater.R.id.preferences_metered_network_warning)
        val updateRecovery = view.findViewById<SwitchCompat>(
            org.lineageos.updater.R.id.preferences_update_recovery)

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        autoCheckInterval.setSelection(Utils.getUpdateCheckSetting(this))
        autoDelete.isChecked =
            prefs.getBoolean(Constants.PREF_AUTO_DELETE_UPDATES, false)
        meteredNetworkWarning.isChecked =
            prefs.getBoolean(Constants.PREF_METERED_NETWORK_WARNING, true)

        if (resources.getBoolean(org.lineageos.updater.R.bool.config_hideRecoveryUpdate)) {
            // Hide the update feature if explicitly requested.
            // Might be the case of A-only devices using prebuilt vendor images.
            updateRecovery.visibility = View.GONE
        } else if (Utils.isRecoveryUpdateExecPresent()) {
            updateRecovery.isChecked =
                SystemProperties.getBoolean(Constants.UPDATE_RECOVERY_PROPERTY, false)
        } else {
            // There is no recovery updater script in the device, so the feature is considered
            // forcefully enabled, just to avoid users to be confused and complain that
            // recovery gets overwritten. That's the case of A/B and recovery-in-boot devices.

            // There is no recovery updater script in the device, so the feature is considered
            // forcefully enabled, just to avoid users to be confused and complain that
            // recovery gets overwritten. That's the case of A/B and recovery-in-boot devices.
            updateRecovery.isChecked = true
            updateRecovery.setOnTouchListener(object : OnTouchListener {
                private var forcedUpdateToast: Toast? = null

                override fun onTouch(v: View, event: MotionEvent): Boolean {
                    if (forcedUpdateToast != null) {
                        forcedUpdateToast!!.cancel()
                    }
                    forcedUpdateToast = Toast.makeText(
                        applicationContext,
                        getString(org.lineageos.updater.R.string.toast_forced_update_recovery),
                        Toast.LENGTH_SHORT)
                    forcedUpdateToast!!.show()
                    return true
                }
            })
        }

        AlertDialog.Builder(this)
            .setTitle(org.lineageos.updater.R.string.menu_preferences)
            .setView(view)
            .setOnDismissListener {
                prefs.edit()
                    .putInt(Constants.PREF_AUTO_UPDATES_CHECK_INTERVAL,
                        autoCheckInterval.selectedItemPosition)
                    .putBoolean(Constants.PREF_AUTO_DELETE_UPDATES,
                        autoDelete.isChecked)
                    .putBoolean(Constants.PREF_METERED_NETWORK_WARNING,
                        meteredNetworkWarning.isChecked)
                    .apply()
                if (Utils.isUpdateCheckEnabled(this)) {
                    UpdatesCheckReceiver.scheduleRepeatingUpdatesCheck(this)
                } else {
                    UpdatesCheckReceiver.cancelRepeatingUpdatesCheck(this)
                    UpdatesCheckReceiver.cancelUpdatesCheck(this)
                }

                if (Utils.isRecoveryUpdateExecPresent()) {
                    val enableRecoveryUpdate = updateRecovery.isChecked
                    SystemProperties.set(Constants.UPDATE_RECOVERY_PROPERTY,
                        enableRecoveryUpdate.toString())
                }
            }
            .show()
    }
}
