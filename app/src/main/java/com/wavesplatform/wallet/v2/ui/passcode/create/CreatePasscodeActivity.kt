package com.wavesplatform.wallet.v2.ui.passcode.create

import android.os.Bundle
import android.view.View
import javax.inject.Inject

import com.arellomobile.mvp.presenter.InjectPresenter

import com.wavesplatform.wallet.v2.ui.base.view.BaseActivity;

import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.wavesplatform.wallet.R
import com.wavesplatform.wallet.v2.ui.custom.PassCodeEntryKeypad
import com.wavesplatform.wallet.v2.ui.fingerprint.UseFingerprintActivity
import com.wavesplatform.wallet.v2.util.launchActivity
import kotlinx.android.synthetic.main.activity_create_passcode.*
import pyxis.uzuki.live.richutilskt.utils.toast


class CreatePasscodeActivity : BaseActivity(), CreatePasscodeView {

    @Inject
    @InjectPresenter
    lateinit var presenter: CreatePasscodePresenter

    @ProvidePresenter
    fun providePresenter(): CreatePasscodePresenter = presenter

    override fun configLayoutRes() = R.layout.activity_create_passcode


    override fun onViewReady(savedInstanceState: Bundle?) {
        setupToolbar(toolbar_view, View.OnClickListener { onBackPressed() }, false, icon = R.drawable.ic_toolbar_back_black)

        presenter.step = CreatePassCodeStep.CREATE

        pass_keypad.attachDots(pdl_dots)
        pass_keypad.setPadClickedListener(object : PassCodeEntryKeypad.OnPinEntryPadClickedListener{
            override fun onPassCodeEntered(passCode: String) {
                if (presenter.step == CreatePassCodeStep.CREATE){
                    presenter.passCode = passCode
                    moveToVerifyStep()
                }else if (presenter.step == CreatePassCodeStep.VERIFY){
                    if (presenter.passCode == passCode){
                        launchActivity<UseFingerprintActivity> {  }
                    }else{
                        pass_keypad.passCodesNotMatches()
                    }
                }
            }
        })
    }

    private fun moveToCreateStep() {
        text_title.setText(R.string.create_passcode_create_title)
        pass_keypad.clearPasscode()
        presenter.step = CreatePassCodeStep.CREATE
        supportActionBar?.setHomeButtonEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        toolbar.setNavigationOnClickListener(null)
    }

    private fun moveToVerifyStep() {
        text_title.setText(R.string.create_passcode_verify_title)
        pass_keypad.clearPasscode()
        presenter.step = CreatePassCodeStep.VERIFY
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener({
            moveToCreateStep()
        })
    }

    enum class CreatePassCodeStep(step: Int){
        CREATE(0),
        VERIFY(1)
    }

    override fun onBackPressed() {
        if (presenter.step == CreatePassCodeStep.VERIFY){
            moveToCreateStep()
        }
    }
}