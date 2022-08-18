package com.cugur24.textinputmask

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cugur24.text_input_mask.CuutyGenerator
import com.cugur24.textinputmask.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mainActivityBinding: ActivityMainBinding
    private lateinit var phoneMaskListener:CuutyGenerator
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityBinding = ActivityMainBinding.inflate(layoutInflater)
         val view = mainActivityBinding.root
        setContentView(view)

        with(mainActivityBinding){
            phoneMaskListener = CuutyGenerator(etPhoneNumber)
            etPhoneNumber.addTextChangedListener(phoneMaskListener)
        }
    }


}