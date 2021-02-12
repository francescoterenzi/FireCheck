package com.fireless.firecheck.ui.statistics

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.roundToInt

@SuppressLint("ViewConstructor")
class GraphView(context: Context, attributeSet: AttributeSet): View(context, attributeSet) {
    private lateinit var paint: Paint
    private var maxx = 0f
    private var maxy = 0f
    private var minx = 0f
    private var miny = 0f
    private var locxAxis = 0f
    private var locyAxis = 0f
    private lateinit var xvalues: FloatArray
    private lateinit var yvalues: FloatArray
    private var vectorLength: Int = 0
    private var axes = 1

    private val dataPointPaint = Paint().apply {
        color = Color.BLUE
        strokeWidth = 7f
        style = Paint.Style.STROKE
    }

    private val dataPointFillPaint = Paint().apply {
        color = Color.WHITE
    }

    private val dataPointLinePaint = Paint().apply {
        color = Color.BLUE
        strokeWidth = 7f
        isAntiAlias = true
    }

    private val axisLinePaint = Paint().apply {
        color = Color.RED
        strokeWidth = 10f
    }

    override fun onDraw(canvas: Canvas) {

        val canvasHeight = height.toFloat()
        val canvasWidth = width.toFloat()

        val xvaluesInPixels = toPixel(canvasWidth, minx, maxx, xvalues)
        val yvaluesInPixels = toPixel(canvasHeight, miny, maxy, yvalues)

        val locxAxisInPixels = toPixelInt(canvasHeight, miny, maxy, locxAxis)
        val locyAxisInPixels = toPixelInt(canvasWidth, minx, maxx, locyAxis)

        for (i in 0 until vectorLength - 1) {
            val realX = xvaluesInPixels[i].toFloat()
            val realY = yvaluesInPixels[i].toFloat()

            val nextDataPointX = xvaluesInPixels[i + 1]
            val nextDataPointY = yvaluesInPixels[i + 1]
            val endX = nextDataPointX.toFloat()
            val endY = nextDataPointY.toFloat()

            canvas.drawLine(realX, realY, endX, endY, dataPointLinePaint)
            canvas.drawCircle(realX, realY, 7f, dataPointFillPaint)
            canvas.drawCircle(realX, realY, 7f, dataPointPaint)
        }

        canvas.drawLine(
            0f,
            canvasHeight - locxAxisInPixels,
            canvasWidth,
            canvasHeight - locxAxisInPixels,
            axisLinePaint
        )

        canvas.drawLine(
            locyAxisInPixels.toFloat(),
            0f,
            locyAxisInPixels.toFloat(),
            canvasHeight,
            axisLinePaint
        )

        //Automatic axes markings, modify n to control the number of axes labels
        if (axes != 0) {

            var temp: Float
            val n = 3

            paint.textAlign = Paint.Align.CENTER
            paint.textSize = 20.0f

            for (i in 1..n) {
                temp = ((10 * (minx + (i - 1) * (maxx - minx) / n)).roundToInt() / 10).toFloat()
                canvas.drawText(
                    "" + temp,
                    toPixelInt(canvasWidth, minx, maxx, temp).toFloat(),
                    canvasHeight - locxAxisInPixels + 20,
                    paint
                )
                temp = (Math.round(10 * (miny + (i - 1) * (maxy - miny) / n)) / 10).toFloat()
                canvas.drawText(
                    "" + temp,
                    (locyAxisInPixels + 20).toFloat(),
                    canvasHeight - toPixelInt(canvasHeight, miny, maxy, temp).toFloat(),
                    paint
                )
            }

            canvas.drawText(
                "" + maxx,
                toPixelInt(canvasWidth, minx, maxx, maxx).toFloat(),
                canvasHeight - locxAxisInPixels + 20,
                paint
            )

            canvas.drawText(
                "" + maxy,
                (locyAxisInPixels + 20).toFloat(),
                canvasHeight - toPixelInt(canvasHeight, miny, maxy, maxy).toFloat(),
                paint
            )
        }
    }

    private fun toPixel(pixels: Float, min: Float, max: Float, value: FloatArray): IntArray {
        val p = DoubleArray(value.size)
        val pint = IntArray(value.size)
        for (i in value.indices) {
            p[i] = .1 * pixels + (value[i] - min) / (max - min) * .8 * pixels
            pint[i] = p[i].toInt()
        }
        return pint
    }

    private fun getAxes(xvalues: FloatArray, yvalues: FloatArray) {
        minx = getMin(xvalues)
        miny = getMin(yvalues)
        maxx = getMax(xvalues)
        maxy = getMax(yvalues)
        locyAxis = if (minx >= 0) minx else if (minx < 0 && maxx >= 0) 0f else maxx
        locxAxis = if (miny >= 0) miny else if (miny < 0 && maxy >= 0) 0f else maxy
    }

    private fun toPixelInt(pixels: Float, min: Float, max: Float, value: Float): Int {
        val pint: Int
        val p: Double = .1 * pixels + (value - min) / (max - min) * .8 * pixels
        pint = p.toInt()
        return pint
    }

    private fun getMax(v: FloatArray): Float {
        var largest = v[0]
        for (i in v.indices) if (v[i] > largest) largest = v[i]
        return largest
    }

    private fun getMin(v: FloatArray): Float {
        var smallest = v[0]
        for (i in v.indices) if (v[i] < smallest) smallest = v[i]
        return smallest
    }


    fun setData(xvalues: FloatArray, yvalues: FloatArray) {
        this.xvalues = xvalues
        this.yvalues = yvalues
        vectorLength = xvalues.size
        paint = Paint()
        getAxes(xvalues, yvalues)
    }
}