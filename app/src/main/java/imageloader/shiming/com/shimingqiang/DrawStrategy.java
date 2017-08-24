package imageloader.shiming.com.shimingqiang;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;


/**
 * Created by $ zhoudeheng on 2015/12/8.
 * Email zhoudeheng@qccr.com
 * 一个策略主要包括：绘制缓存,绘制visual element,以及更新缓存
 */
public abstract class DrawStrategy {
    public static Paint sBitmapPaint;
    static {
        sBitmapPaint = new Paint();
        sBitmapPaint.setDither(true);
        sBitmapPaint.setAntiAlias(true);
        sBitmapPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
    }
    protected Canvas mViewCanvas;
    protected FrameCache mFrameCache;

    protected VisualElementBase mVisualElement;


    public DrawStrategy(Canvas canvas, FrameCache frameCache,
                        VisualElementBase visualElement) {
        mViewCanvas = canvas;
        mFrameCache = frameCache;
        mVisualElement = visualElement;

    }

    public abstract void draw();

    /**
     * 绘制缓存
     */
    protected void drawCache() {
        if (mViewCanvas != null && mFrameCache != null
                && mFrameCache.getBitmap() != null) {// 第一次绘制的时候，mFrameCache.getBitmap()有可能为null
            drawBitmap(mViewCanvas, mFrameCache.getBitmap());
        }
    }

    /**
     * 绘制可视化元素
     */
    protected void drawVisualElement() {
        if (mVisualElement != null)
            drawWholeVisualElement(mViewCanvas, mVisualElement);
    }

    /**
     * 更新缓存
     */
    protected abstract void updateCache();

    protected void drawBitmap(Canvas canvas, Bitmap bitmap) {
        canvas.drawBitmap(bitmap, 0, 0, sBitmapPaint);
    }


    /**
     * 调用visualElement.draw进行绘制
     *
     * @param canvas
     * @param visualElement
     */
    protected void drawWholeVisualElement(Canvas canvas,
                                          VisualElementBase visualElement) {
        visualElement.draw(canvas);
    }
}
