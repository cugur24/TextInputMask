package com.cugur24.text_input_mask

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class CuutyGenerator(private val editTextRef:EditText):TextWatcher {
    private val phoneInputMask = "(5__) ___ __ __"
    private var isUserInput = true
    private var isDeletingBlank = false
    private var isDeleteMode = false
    override fun beforeTextChanged(s:CharSequence?, start:Int, count:Int, after:Int) {
        s?.let{
            isDeleteMode = after < count
            val cursorPoint = editTextRef.selectionEnd
            isDeletingBlank = (cursorPoint > 1) && isDeleteMode && it[cursorPoint - 1].isWhitespace()
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if(isUserInput) {
            isUserInput = false
            if(isDeletingBlank){
                editTextRef.setText(generatePhoneInputMaskNew(start,isDeleteMode,isDeletingBlank,editTextRef.text.toString()))
            }
            else editTextRef.setText(generatePhoneInputMaskNew(-1,isDeleteMode,isDeletingBlank,editTextRef.text.toString()))
        }
        else{
            isUserInput = true
        }
    }

    override fun afterTextChanged(s: Editable?) {
        editTextRef.text?.let {
            when(val position = it.indexOf("_")){
                -1 -> editTextRef.setSelection(it.length)
                else  -> editTextRef.setSelection(position)
            }
        }
    }


    private fun generatePhoneInputMaskNew(start: Int=-1,isDeleteMode:Boolean, isDeletingBlank:Boolean, phone:String):String{
        val cleanPhone = if(isDeletingBlank && start!= -1){
            //TODO: duzelt
            val seq = phone.subSequence(0 until start)
            val index = seq.indexOfLast { it.isDigit() }
            val deletedBlankDigit = phone.removeRange(index..index)
            cleanPhone(deletedBlankDigit)
        }else{
            cleanPhone(phone)
        }

        val newMaskedPhone = StringBuilder(phoneInputMask)
        if(cleanPhone.length<=1) return phoneInputMask
        else{
            for ((index,digit) in cleanPhone.withIndex()){
                if (index==0) continue
                if(newMaskedPhone.count { it.isDigit() }==10)break
                newMaskedPhone.setCharAt(newMaskedPhone.indexOf("_"),digit)
            }
            return newMaskedPhone.toString()
        }
    }

    /**
     * Returns all digits in one sequence
     * if not returns ""
     */
    private fun cleanPhone(phone: String): String {
        val digits = StringBuilder("")
        phone.forEach {
            if(it.isDigit())digits.append(it)
        }
        return digits.toString()
    }
}