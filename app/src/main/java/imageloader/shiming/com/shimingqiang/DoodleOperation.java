package imageloader.shiming.com.shimingqiang;

import android.graphics.Canvas;
import android.graphics.Rect;

import imageloader.shiming.com.shimingqiang.back.ICommand;


/**
 * Created by $ zhoudeheng on 2015/12/8.
 * Email zhoudeheng@qccr.com
 */
public abstract class DoodleOperation {
    public static final String TAG = "DoodleOperation";
    protected IModelManager mModelManager;
    protected IVisualManager mVisualManager;
    protected FrameCache mFrameCache;

    protected boolean mIsCreatingCommand = true;

    // }

    public DoodleOperation(FrameCache frameCache, IModelManager modelManager,
                           IVisualManager visualManager) {
        mFrameCache = frameCache;
        mModelManager = modelManager;
        mVisualManager = visualManager;
    }

    /**
     * 绘制过程。绘制策略由DrawStrategy类完成；最终绘制还是通过VisualElementBase类来完成的
     */
    public void draw(Canvas canvas) {
        onDraw(canvas);
    }


    /**
     *
     * @param canvas
     *            在其上绘制的canvas
     */
    protected abstract void onDraw(Canvas canvas);

    /**
     * 获取Dirty区域
     *
     * @return
     */
    public abstract Rect computerDirty();



    /**
     * 是否创建ICommand。
     *
     * @param creating
     *            :是否创建。 该变量将在{@link DoodleOperation#onCreateCommand()}
     *            里面使用到，如果creating为false，则createCommand默认直接返回null。 该值默认为true
     */
    public void setCreatingCommand(boolean creating) {
        mIsCreatingCommand = creating;
    }
}
