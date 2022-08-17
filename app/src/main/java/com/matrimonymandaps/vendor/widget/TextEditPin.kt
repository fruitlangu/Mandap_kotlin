package com.matrimonymandaps.vendor.widget


import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View.OnKeyListener
import android.view.View.OnTouchListener
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.matrimonymandaps.vendor.R

import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class EditTextPin : ConstraintLayout {

    companion object {
        private const val TAG = "EditTextPin"
        @JvmStatic  var etOTP1: EditText? = null
        @JvmStatic  var etOTP2: EditText? = null
        @JvmStatic  var etOTP3: EditText? = null
        @JvmStatic  var etOTP4: EditText? = null
        @JvmStatic  var etOTP5: EditText? = null
        @JvmStatic  var etOTP6: EditText? = null
        @JvmStatic  var otpError: TextView? = null
        @JvmStatic  var onVerifyBtnEnabled= MutableLiveData<Boolean>(false)

        @JvmStatic   var otpText1:String?= ""
        @JvmStatic   var otpText2:String?=""
        @JvmStatic   var otpText3:String?=""
        @JvmStatic   var otpText4:String?=""
        @JvmStatic   var otpText5:String?=""
        @JvmStatic   var otpText6:String?=""

        fun onClear() {
            etOTP1!!.setText("")
            etOTP2!!.setText("")
            etOTP3!!.setText("")
            etOTP4!!.setText("")
            etOTP5!!.setText("")
            etOTP6!!.setText("")
            otpText1=""
            otpText2=""
            otpText3=""
            otpText4=""
            otpText5=""
            otpText6=""
        }

        fun onGetOtp():String{
            val getOtp:String=otpText1+otpText2+otpText3+otpText4+otpText5+otpText6
            return getOtp
        }
    }


    private val maximumLength:Int=6








    constructor(context: Context, attrs: AttributeSet) : super(context,attrs)
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)



    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        LayoutInflater.from(context).inflate(R.layout.edittext_pin, this, true)


       // val view: ConstraintLayout = findViewById(R.id.viewBackground)
       /* val shape = ContextCompat.getDrawable(context, R.drawable.underline) as LayerDrawable?
        val bgShape = shape!!.findDrawableByLayerId(R.id.underline) as GradientDrawable
        bgShape.setStroke(convertDpToPx(2), ContextCompat.getColor(context, underlineColor))*/
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            shape.setDrawable(0, bgShape)
        }
        view.background = shape*/
        etOTP1 = findViewById(R.id.tv_otp1)
        etOTP2 = findViewById(R.id.tv_otp2)
        etOTP3 = findViewById(R.id.tv_otp3)
        etOTP4 = findViewById(R.id.tv_otp4)
        etOTP5 = findViewById(R.id.tv_otp5)
        etOTP6 = findViewById(R.id.tv_otp6)
        otpError = findViewById(R.id.til_otp)
        etOTP1!!.requestFocus()



        /* val drawable = ContextCompat.getDrawable(context, background)
         setTextBackground(drawable)*/

        //Add TextWatchers to get focus
        etOTP1!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                otpError!!.text=""
                if (etOTP1!!.getText().toString().trim({ it <= ' ' }).length > 0) {
                    otpText1=s.toString()
                    etOTP1!!.setBackground(ContextCompat.getDrawable(context, R.drawable.rounde_white_text))
                    etOTP2!!.setBackground(ContextCompat.getDrawable(context, R.drawable.focus_rounded_text))
                    etOTP2!!.clearFocus()
                    etOTP2!!.requestFocus()

                }
              /*  if (s.length > 1) {
                    etOTP1!!.setSelection(1)
                    etOTP2!!.requestFocus()
                }*/
            }

            override fun afterTextChanged(s: Editable) {
                if (s.length == 1) {
                    Log.i("TEST", "String : $s")
                    etOTP2!!.requestFocus()
                } else if (s.length > 1) {
                    etOTP1!!.setText(s[0].toString())
                    etOTP2!!.setText(s.subSequence(1, s.length))
                }
                checkOtp()


            }
        })

        etOTP2!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                otpError!!.text=""
                if (etOTP2!!.getText().toString().trim({ it <= ' ' }).length > 0) {
                    etOTP2!!.setBackground(ContextCompat.getDrawable(context, R.drawable.rounde_white_text))
                    etOTP3!!.setBackground(ContextCompat.getDrawable(context, R.drawable.focus_rounded_text))
                    etOTP3!!.clearFocus()
                    etOTP3!!.requestFocus()
                    otpText2=s.toString()
                }
               /* if (s.length == 0) {
                    etOTP1!!.setSelection(1)
                    etOTP1!!.requestFocus()
                } else {
                    etOTP2!!.setSelection(1)
                    etOTP3!!.requestFocus()
                }*/
            }

            override fun afterTextChanged(s: Editable) {
                if (s.length == 1) {
                    etOTP3!!.requestFocus()
                } else if (s.length > 1) {
                    etOTP2!!.setText(s[0].toString())
                    etOTP3!!.setText(s.subSequence(1, s.length))
                }
                checkOtp()
            }
        })
        etOTP3!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                otpError!!.text=""
                if (etOTP3!!.getText().toString().trim({ it <= ' ' }).length > 0) {
                    etOTP3!!.setBackground(ContextCompat.getDrawable(context, R.drawable.rounde_white_text))
                    etOTP4!!.setBackground(ContextCompat.getDrawable(context, R.drawable.focus_rounded_text))
                    etOTP4!!.clearFocus()
                    etOTP4!!.requestFocus()
                    otpText3=s.toString()
                }
//                if (s.length == 0) {
//                    etOTP2!!.requestFocus()
//                } else {
//                    etOTP3!!.setSelection(1)
//                    etOTP4!!.requestFocus()
//                }
            }

            override fun afterTextChanged(s: Editable) {
                if (s.length == 1) {
                    etOTP4!!.requestFocus()
                } else if (s.length > 1) {
                    etOTP3!!.setText(s[0].toString())
                    etOTP4!!.setText(s.subSequence(1, s.length))
                }
                checkOtp()
            }
        })
        etOTP4!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                otpError!!.text=""
                if (etOTP4!!.getText().toString().trim({ it <= ' ' }).length > 0) {
                    etOTP4!!.setBackground(ContextCompat.getDrawable(context, R.drawable.rounde_white_text))
                    etOTP5!!.setBackground(ContextCompat.getDrawable(context, R.drawable.focus_rounded_text))
                    etOTP5!!.clearFocus()
                    etOTP5!!.requestFocus()
                    otpText4=s.toString()
                }
               /* if (s.length == 0) {
                    etOTP3!!.requestFocus()
                } else {
                    etOTP4!!.setSelection(1)
                    etOTP5!!.requestFocus()
                }*/
            }

            override fun afterTextChanged(s: Editable) {
                if (s.length == 1) {
                    etOTP5!!.requestFocus()
                } else if (s.length > 1) {
                    etOTP4!!.setText(s[0].toString())
                    etOTP5!!.setText(s.subSequence(1, s.length))
                }
                checkOtp()
            }
        })
        etOTP5!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                otpError!!.text=""

                if (etOTP5!!.getText().toString().trim({ it <= ' ' }).length > 0) {
                    etOTP5!!.setBackground(ContextCompat.getDrawable(context, R.drawable.rounde_white_text))
                    etOTP6!!.setBackground(ContextCompat.getDrawable(context, R.drawable.focus_rounded_text))
                    etOTP6!!.clearFocus()
                    etOTP6!!.requestFocus()
                    otpText5=s.toString()
                }
               /* if (s.toString().length == 0) {
                    etOTP4!!.requestFocus()
                } else {
                    etOTP5!!.setSelection(1)
                    etOTP6!!.requestFocus()
                }*/
            }

            override fun afterTextChanged(s: Editable) {
                if (s.length == 1) {
                    etOTP6!!.requestFocus()
                } else if (s.length > 1) {
                    etOTP5!!.setText(s[0].toString())
                    etOTP6!!.setText(s.subSequence(1, s.length))
                }
                checkOtp()
            }
        })
        etOTP6!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                otpError!!.text=""
                if (etOTP6!!.getText().toString().trim({ it <= ' ' }).length > 0) {
                    etOTP6!!.setBackground(ContextCompat.getDrawable(context, R.drawable.rounde_white_text))
                    etOTP6!!.clearFocus()
                    etOTP1!!.setCursorVisible(false)
                    val imm = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(etOTP6!!.getWindowToken(), 0)
                    otpText6=s.toString()

                }

               /* if (s.length == 0) {
                    etOTP5!!.requestFocus()
                } else {
                    etOTP6!!.setSelection(1)
                }*/
            }



            override fun afterTextChanged(s: Editable) {
                checkOtp()
            }
        })

        editKeyText()
    }



    /*private fun handleAttrs(attrs: AttributeSet) {
        val style = getContext().theme.obtainStyledAttributes(attrs, R.styleable.EditTextPin, 0, 0)
        try {
            underlineColor = style.getResourceId(R.styleable.EditTextPin_underlineColor, R.color.underlineColor)
            background = style.getResourceId(R.styleable.EditTextPin_textBackground, R.drawable.background)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            style.recycle()
        }
    }*/


    fun checkOtp(){

        val textPin:String=etOTP1!!.text.toString()+etOTP2!!.text.toString()+etOTP3!!.text.toString()+etOTP4!!.text.toString()+etOTP5!!.text.toString()+etOTP6!!.text.toString()
        onVerifyBtnEnabled.value = textPin.length.equals(maximumLength)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun editKeyText() {
        etOTP1!!.setOnKeyListener(OnKeyListener { v, keyCode, event -> // You can identify which key pressed buy checking keyCode value
            // with KeyEvent.KEYCODE_
            if (keyCode == KeyEvent.KEYCODE_DEL) {

                // this is for backspace
                etOTP1!!.setBackground(ContextCompat.getDrawable(v.context, R.drawable.rounded_txt))
                onVerifyBtnEnabled.value=false
                otpText1=""
                Log.e("IME_TEST", "DEL KEY")
            }
            false
        })




        etOTP1!!.setOnTouchListener(OnTouchListener { v, event ->
            etOTP1!!.setCursorVisible(true)
            etOTP1!!.setBackground(ContextCompat.getDrawable(v.context, R.drawable.focus_rounded_text))
            changeFocusColor(etOTP6)
            changeFocusColor(etOTP3)
            changeFocusColor(etOTP4)
            changeFocusColor(etOTP5)
            changeFocusColor(etOTP2)
            false
        })
        etOTP6!!.setOnTouchListener(OnTouchListener { v, event ->
            etOTP6!!.setCursorVisible(true)
            etOTP6!!.setBackground(ContextCompat.getDrawable(v.context, R.drawable.focus_rounded_text))
            changeFocusColor(etOTP1)
            changeFocusColor(etOTP3)
            changeFocusColor(etOTP4)
            changeFocusColor(etOTP5)
            changeFocusColor(etOTP2)
            false
        })
        etOTP2!!.setOnTouchListener(OnTouchListener { v, event ->
            etOTP2!!.setCursorVisible(true)
            etOTP2!!.setBackground(ContextCompat.getDrawable(v.context, R.drawable.focus_rounded_text))
            changeFocusColor(etOTP1)
            changeFocusColor(etOTP3)
            changeFocusColor(etOTP4)
            changeFocusColor(etOTP5)
            changeFocusColor(etOTP6)
            false
        })
        etOTP3!!.setOnTouchListener(OnTouchListener { v, event ->
            etOTP3!!.setCursorVisible(true)
            etOTP3!!.setBackground(ContextCompat.getDrawable(v.context, R.drawable.focus_rounded_text))
            changeFocusColor(etOTP1)
            changeFocusColor(etOTP2)
            changeFocusColor(etOTP4)
            changeFocusColor(etOTP5)
            changeFocusColor(etOTP6)
            false
        })
        etOTP4!!.setOnTouchListener(OnTouchListener { v, event ->
            etOTP4!!.setCursorVisible(true)
            etOTP4!!.setBackground(ContextCompat.getDrawable(v.context, R.drawable.focus_rounded_text))
            changeFocusColor(etOTP1)
            changeFocusColor(etOTP2)
            changeFocusColor(etOTP3)
            changeFocusColor(etOTP5)
            changeFocusColor(etOTP6)
            false
        })
        etOTP5!!.setOnTouchListener(OnTouchListener { v, event ->
            etOTP5!!.setCursorVisible(true)
            etOTP5!!.setBackground(ContextCompat.getDrawable(v.context, R.drawable.focus_rounded_text))
            changeFocusColor(etOTP1)
            changeFocusColor(etOTP2)
            changeFocusColor(etOTP3)
            changeFocusColor(etOTP4)
            changeFocusColor(etOTP6)
            false
        })
        etOTP2!!.setOnKeyListener(OnKeyListener { v, keyCode, event -> // You can identify which key pressed buy checking keyCode value
            // with KeyEvent.KEYCODE_
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                // this is for backspace
                Log.e("IME_TEST", "DEL KEY")
                if (TextUtils.isEmpty(etOTP2!!.getText().toString().trim({ it <= ' ' }))) {

                    etOTP2!!.setBackground(ContextCompat.getDrawable(v.context, R.drawable.rounded_txt))
                    etOTP1!!.setBackground(ContextCompat.getDrawable(v.context, R.drawable.focus_rounded_text))
                    etOTP1!!.setCursorVisible(true)
                    etOTP1!!.clearFocus()
                    etOTP1!!.requestFocus()
                    onVerifyBtnEnabled.value=false
                    otpText2=""
                }
            }
            false
        })
        etOTP3!!.setOnKeyListener(OnKeyListener { v, keyCode, event -> // You can identify which key pressed buy checking keyCode value
            // with KeyEvent.KEYCODE_
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                // this is for backspace
                if (TextUtils.isEmpty(etOTP3!!.getText().toString().trim({ it <= ' ' }))) {

                    etOTP3!!.setBackground(ContextCompat.getDrawable(v.context, R.drawable.rounded_txt))
                    etOTP2!!.setBackground(ContextCompat.getDrawable(v.context, R.drawable.focus_rounded_text))
                    etOTP2!!.clearFocus()
                    etOTP2!!.requestFocus()
                    onVerifyBtnEnabled.value=false
                    otpText3=""
                }
                Log.e("IME_TEST", "DEL KEY")
            }
            false
        })
        etOTP4!!.setOnKeyListener(OnKeyListener { v, keyCode, event -> // You can identify which key pressed buy checking keyCode value
            // with KeyEvent.KEYCODE_
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                // this is for backspace

                if (TextUtils.isEmpty(etOTP4!!.getText().toString().trim({ it <= ' ' }))) {
                    etOTP4!!.setBackground(ContextCompat.getDrawable(v.context, R.drawable.rounded_txt))
                    etOTP3!!.setBackground(ContextCompat.getDrawable(v.context, R.drawable.focus_rounded_text))
                    etOTP3!!.clearFocus()
                    etOTP3!!.requestFocus()
                    onVerifyBtnEnabled.value=false
                    otpText4=""
                }
                Log.e("IME_TEST", "DEL KEY")
            }
            false
        })
        etOTP5!!.setOnKeyListener(OnKeyListener { v, keyCode, event -> // You can identify which key pressed buy checking keyCode value
            // with KeyEvent.KEYCODE_
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                // this is for backspace

                if (TextUtils.isEmpty(etOTP5!!.getText().toString().trim({ it <= ' ' }))) {
                    etOTP5!!.setBackground(ContextCompat.getDrawable(v.context, R.drawable.rounded_txt))
                    etOTP4!!.setBackground(ContextCompat.getDrawable(v.context, R.drawable.focus_rounded_text))
                    etOTP4!!.clearFocus()
                    etOTP4!!.requestFocus()
                    onVerifyBtnEnabled.value=false
                    otpText5=""
                }
                Log.e("IME_TEST", "DEL KEY")
            }
            false
        })
        etOTP6!!.setOnKeyListener(OnKeyListener { v, keyCode, event -> // You can identify which key pressed buy checking keyCode value
            // with KeyEvent.KEYCODE_
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                // this is for backspace

                if (TextUtils.isEmpty(etOTP6!!.getText().toString().trim({ it <= ' ' }))) {
                    etOTP6!!.setBackground(ContextCompat.getDrawable(v.context, R.drawable.rounded_txt))
                    etOTP5!!.setBackground(ContextCompat.getDrawable(v.context, R.drawable.focus_rounded_text))
                    etOTP5!!.clearFocus()
                    etOTP5!!.requestFocus()
                    onVerifyBtnEnabled.value=false
                    otpText6=""
                }
                Log.e("IME_TEST", "DEL KEY")
            }
            false
        })





        /*if(mOnPinEnteredListener!=null) {
            mOnPinEnteredListener.let { it?.ongetText(etOTP1, etOTP2, etOTP3, etOTP4, etOTP5, etOTP6) }
            mOnPinEnteredListener!!.onClear(etOTP1, etOTP2, etOTP3, etOTP4, etOTP5, etOTP6)
        }*/

    }


    fun changeFocusColor(editText: EditText?) {
        if (editText!!.text.toString().trim { it <= ' ' }.length > 0) {
            editText!!.background = ContextCompat.getDrawable(context, R.drawable.rounde_white_text)
        } else if (TextUtils.isEmpty(editText.text.toString().trim { it <= ' ' })) {
            editText!!.background = ContextCompat.getDrawable(context, R.drawable.rounded_txt)
        }
    }





      /*  fun clearText() {
            etOTP1!!.setText("")
            etOTP2!!.setText("")
            etOTP3!!.setText("")
            etOTP4!!.setText("")
            etOTP5!!.setText("")
            etOTP6!!.setText("")
        }*/


    private fun convertDpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().getDisplayMetrics().density) as Int
    }

    private fun setTextBackground(drawable: Drawable?) {
        etOTP1!!.background = drawable
        etOTP2!!.background = drawable
        etOTP3!!.background = drawable
        etOTP4!!.background = drawable
        etOTP5!!.background = drawable
        etOTP6!!.background = drawable
    }







    /* fun setOnPinEnteredListener(l: OnPinEnteredListener) {
         mOnPinEnteredListener = l
     }*/


}




