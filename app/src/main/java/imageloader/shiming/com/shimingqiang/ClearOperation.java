package imageloader.shiming.com.shimingqiang;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;


/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 * 清空绘图,UI线程跟DrawThread通信的数据类。ClearOperation并不产生Icommand
 *
 * todo  清除 bitmap
 *
 *
 */
public class ClearOperation extends DoodleOperation {

    public ClearOperation(FrameCache frameCache, IModelManager modelManager,
                          IVisualManager visualManager) {
        super(frameCache, modelManager, visualManager);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mFrameCache.clearBitmap();
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
    }


    @Override
    public Rect computerDirty() {
        return null;
    }
}

