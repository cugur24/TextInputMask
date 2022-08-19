package com.cugur24.text_input_mask

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class CuutyGenerator(private val editTextRef:EditText):TextWatcher {
    private val phoneInputMask = "(5__) ___ __ __"
    private var isUserInput = true
    private var isDeletingBlank = false
    private var isDeleteMode = false
    private var deletingStartIndex = -1
    private var deleteSelectionMode = true
    private var oldPhone = ""
    private var isAddingMiddle = false
    private var addingStartingIndex = -1
    override fun beforeTextChanged(s:CharSequence?, start:Int, count:Int, after:Int) {
        s?.let{
            isDeleteMode = after < count
            val cursorPoint = editTextRef.selectionEnd
            isDeletingBlank = (cursorPoint > 1) && isDeleteMode && it[cursorPoint - 1].isWhitespace()
            if(isDeletingBlank) deleteSelectionMode = isDeletingBlank
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        //TODO: cursor setleme
        if(!isUserInput){
            editTextRef.text?.let { it ->
                if (!isDeleteMode && deletingStartIndex != -1) {
                    if(deleteSelectionMode){
                        editTextRef.setSelection(deletingStartIndex-1)
                        deleteSelectionMode = false
                    }else{
                        if(s?.count { it.isDigit() }==1){
                            editTextRef.setSelection(2)
                        }else{
                            editTextRef.setSelection(deletingStartIndex)
                        }
                    }
                } else {
                    when (val position = it.indexOf("_")) {
                        -1 -> editTextRef.setSelection(it.length)
                        else -> {
                            if(!isAddingMiddle)
                            editTextRef.setSelection(position)
                            else{
                                if (addingStartingIndex != -1)
                                    s?.subSequence(addingStartingIndex,s.length)
                                        ?.let { it1 ->
                                            val index = it1.indexOfFirst { it.isDigit() }
                                            editTextRef.setSelection(addingStartingIndex+index+1) }
                                isAddingMiddle = false
                                addingStartingIndex = -1
                            }
                        }
                    }
                }
            }
        }
        s?.let { text ->
            if(start!=text.length){
                val sub = text.subSequence(start+1,text.length)
                isAddingMiddle = start!=text.length && sub.indexOfLast { it.isDigit()} != -1
                addingStartingIndex = start
            }
        }
        if(isDeleteMode)
        deletingStartIndex = start
    }

    override fun afterTextChanged(s: Editable?) {
        //Yeni text olusturma
        if(isUserInput) {
            isUserInput = false
            if(isDeletingBlank){
                editTextRef.setText(generatePhoneInputMaskNew(deletingStartIndex,isDeleteMode,isDeletingBlank,editTextRef.text.toString()))
            }
            else editTextRef.setText(generatePhoneInputMaskNew(-1,isDeleteMode,isDeletingBlank,editTextRef.text.toString()))
        }
        else{
            isUserInput = true
        }


    }


    private fun generatePhoneInputMaskNew(start: Int=-1,isDeleteMode:Boolean, isDeletingBlank:Boolean, phone:String):String{
        if (cleanPhone(phone).length>10)return oldPhone
        val cleanPhone = if(isDeletingBlank && start!= -1){
            //TODO: duzelt
            val seq = phone.subSequence(0 until start)
            val index = seq.indexOfLast { it.isDigit() }
            val deletedBlankDigit = if(phone[index-1].isDigit())
                phone.removeRange(index..index)
            else
                phone
            cleanPhone(deletedBlankDigit)
        }else{
            cleanPhone(phone)
        }

        val newMaskedPhone = StringBuilder(phoneInputMask)
        return if(cleanPhone.length<=1) phoneInputMask
        else{
            for ((index,digit) in cleanPhone.withIndex()){
                if (index==0) continue
                if(newMaskedPhone.count { it.isDigit() }==10)break
                newMaskedPhone.setCharAt(newMaskedPhone.indexOf("_"),digit)
            }
            oldPhone = newMaskedPhone.toString()
            oldPhone
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