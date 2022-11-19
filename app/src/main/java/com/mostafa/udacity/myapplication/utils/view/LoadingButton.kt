package com.mostafa.udacity.myapplication.utils.view

import android.animation.PropertyValuesHolder.ofFloat
import android.animation.ValueAnimator
import android.animation.ValueAnimator.INFINITE
import android.animation.ValueAnimator.REVERSE
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.mostafa.udacity.myapplication.R
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private val valueAnimator = ValueAnimator()
    private var btnBackColor: Int
    private var textColor: Int
    private var textSize:Int
    private var progress : Double = 0.0

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when(new){
            ButtonState.Clicked -> invalidate()
            ButtonState.Completed -> {
                valueAnimator.cancel()
                invalidate()
            }
            ButtonState.Loading -> {
                animatedProgress()
                invalidate()
            }
        }
    }


    init {
        isEnabled = true
        val attrs = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LoadingButton,
            0,0
        )

        btnBackColor = attrs.getColor(
            R.styleable.LoadingButton_BtnBackgroundColor,
            Color.GRAY
        )

        textColor = attrs.getColor(
            R.styleable.LoadingButton_BtnTextColor,
            ContextCompat.getColor(context,R.color.white)
        )

        textSize = attrs.getInt(
            R.styleable.LoadingButton_BtnTextSize,
            16
        )
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 50.0f
        typeface = Typeface.create( "", Typeface.BOLD)
    }

    private val animatedRectPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = context.getColor(R.color.animated_rect)
    }

    private val animatedCirclePaint = Paint().apply {
        style = Paint.Style.STROKE
        isAntiAlias = true
        color = context.getColor(R.color.colorAccent)
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 30f
    }


    private var loadingCircle = RectF(
        760f,
        60f,
        800f,
        100f
    )
    //Log.d("Ahmed2","left : ${widthSize - 200f} , top : ${heightSize.toFloat()/3} , right : ${widthSize - 40f} , bottom : ${heightSize.toFloat()/3 + 40f}")

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //btn
        paint.color = btnBackColor
        canvas!!.drawRect(0f,0f,width.toFloat(),height.toFloat(),paint)

        if (buttonState == ButtonState.Loading){
            //animated btn (0,0,..lod..,h)
            paint.color = Color.GRAY
            canvas.drawRect(0f,0f,
                ( width * (progress/100) ).toFloat(),height.toFloat(),animatedRectPaint)

            //animated circle
            paint.color = Color.GREEN
            paint.strokeWidth = 40f
            canvas.drawArc(loadingCircle, 0f, (360 * progress / 100).toFloat(), true, animatedCirclePaint)
        }

        //text
        paint.color = textColor
        val btnText = if (buttonState == ButtonState.Loading)  resources.getString(R.string.button_loading) else resources.getString(R.string.button_name)
        canvas.drawText(btnText,(width/2).toFloat(),(height/2).toFloat(),paint)

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    fun updateBtnState(state:ButtonState){
        buttonState = state
    }
    @SuppressLint("Recycle")
    private fun animatedProgress(){
        val valuesRange = ofFloat("progress",0f,100f)
        valueAnimator.apply {
            setValues(valuesRange)
            duration = 3000
            repeatCount = INFINITE
            repeatMode = REVERSE
            addUpdateListener {
                progress = (it.animatedValue as Float).toDouble()
                invalidate()
            }
        }
        valueAnimator.start()
    }
}