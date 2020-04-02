package com.puddlealley.flux.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.jakewharton.rxbinding3.view.clicks
import com.puddlealley.flux.R
import com.puddlealley.flux.service.CodeVerificationResult
import com.puddlealley.flux.store.AppStore
import com.puddlealley.flux.store.device.SecretCaveEvents
import com.puddlealley.splash.android.connect
import com.puddlealley.splash.android.events
import io.reactivex.rxkotlin.merge
import io.reactivex.rxkotlin.ofType
import kotlinx.android.synthetic.main.activity_secret_cave.*
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.security.AccessController.getContext


class SecretCaveActivity : AppCompatActivity() {

    private val appStore: AppStore by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_secret_cave)

        appStore.events(this) {
            val buttonAClicked = buttonA.clicks().map { SecretCaveEvents.LetteredEntered(getString(R.string.code_1_value)) }.share()
            val buttonBClicked = buttonB.clicks().map { SecretCaveEvents.LetteredEntered(getString(R.string.code_2_value)) }.share()
            val buttonCClicked = buttonC.clicks().map { SecretCaveEvents.LetteredEntered(getString(R.string.code_3_value)) }.share()


            val buttonClearClicked = btn_clear.clicks() //TODO: Flush buffer when button is clicked
                .map {
                    SecretCaveEvents.ClearCodeClicked
                }
                .share()

            // emits CodeEntered after every 6th letter
            val codeEntered =
                listOf(buttonAClicked, buttonBClicked, buttonCClicked)
                    .merge()
                    .map { it.letter }
                    .buffer(resources.getInteger(R.integer.passwordLength))
                    .map { SecretCaveEvents.CodeEntered(it.joinToString(separator = "")) }

            listOf(
                buttonAClicked,
                buttonBClicked,
                buttonCClicked,
                buttonClearClicked,
                codeEntered
            ).merge()
        }

        appStore.updates.connect(this) {
            val secretCaveState =  it.secretCaveState
            Timber.d("updating Ui: $secretCaveState")

            progressBar.isVisible = secretCaveState.loading
            codeState.isVisible = !secretCaveState.loading

            buttonA.isEnabled = !secretCaveState.loading
            buttonB.isEnabled = !secretCaveState.loading
            buttonC.isEnabled = !secretCaveState.loading
            //TODO: Transform clear button to cancel button during loading state

            enteredCode.text = secretCaveState.enteredCode

            if (secretCaveState.codeCorrect) {
                codeState.setImageResource(R.drawable.ic_check_circle_green_24dp)
            } else {
                codeState.setImageResource(R.drawable.ic_error_red_24dp)
            }
        }

        appStore.actions
            .ofType<CodeVerificationResult.Error>()
            .connect(this){
                 // show a toast when error occurs
        }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, SecretCaveActivity::class.java)
        }
    }

}

