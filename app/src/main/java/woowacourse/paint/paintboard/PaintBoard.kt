package woowacourse.paint.paintboard

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import woowacourse.paint.R
import woowacourse.paint.paintboard.line.Lines
import woowacourse.paint.paintboard.line.LinesBitmap

class PaintBoard(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var penColor: Int = DEFAULT_BRUSH_COLOR
    private var lastPenColor: Int = DEFAULT_BRUSH_COLOR
    var penWidth: Float = DEFAULT_BRUSH_WIDTH
    var canvasColor: Int = DEFAULT_CANVAS_COLOR
    private val lines = Lines()
    private lateinit var linesBitmap: LinesBitmap
    private val canvasPaint = Paint().apply {
        color = ContextCompat.getColor(this@PaintBoard.context, canvasColor)
    }

    fun getBitmap(): Bitmap {
        return linesBitmap.create(lines)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        linesBitmap = LinesBitmap(w, h, canvasPaint)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawPaint(canvasPaint)
        lines.forEach {
            canvas.drawPath(it.path, it.paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val pointX = event.x
        val pointY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> startDrawing(pointX, pointY)
            MotionEvent.ACTION_MOVE -> drawing(pointX, pointY)
            MotionEvent.ACTION_UP -> endDrawing(pointX, pointY)
        }
        invalidate()
        return true
    }

    private fun startDrawing(x: Float, y: Float) {
        lines.startLine(x, y, newPaint())
    }

    private fun drawing(x: Float, y: Float) {
        lines.drawingLine(x, y)
    }

    private fun endDrawing(x: Float, y: Float) {
        lines.finishLine(x, y)
    }

    fun revertDrawing() {
        lines.revert()
        invalidate()
    }

    fun setEraserMode() {
        lastPenColor = penColor
        penColor = canvasColor
    }

    fun setPenMode() {
        penColor = lastPenColor
    }

    fun setPenColor(@ColorRes colorId: Int) {
        lastPenColor = penColor
        penColor = colorId
    }

    private fun newPaint() = Paint().apply {
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        strokeWidth = this@PaintBoard.penWidth
        color = ContextCompat.getColor(this@PaintBoard.context, this@PaintBoard.penColor)
    }

    companion object {
        val DEFAULT_BRUSH_COLOR = R.color.black
        val DEFAULT_CANVAS_COLOR = R.color.white
        const val DEFAULT_BRUSH_WIDTH = 30f
    }
}
