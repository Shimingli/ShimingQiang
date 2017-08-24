package imageloader.shiming.com.shimingqiang;

import android.graphics.Canvas;


/**
 * Created by $ zhoudeheng on 2015/12/8.
 * Email zhoudeheng@qccr.com
 * 策略绘制的顺序为:刷新缓存,绘制缓存
 * 新增一个visual element(例如图片)的时候，应该使用该策略
 */
public class AddElementDrawStrategy extends DrawStrategy {

    public AddElementDrawStrategy(Canvas canvas, FrameCache frameCache,
                                  VisualElementBase visualElement) {
        super(canvas, frameCache, visualElement);
    }

    @Override
    public void draw() {

        updateCache();
        drawCache();
    }

    @Override
    protected void updateCache() {
        if (mFrameCache == null)
            return;
        Canvas canvas = mFrameCache.getCanvas();
        drawWholeVisualElement(canvas, mVisualElement);
    }

}
