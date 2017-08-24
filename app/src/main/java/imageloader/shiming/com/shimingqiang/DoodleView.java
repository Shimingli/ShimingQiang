package imageloader.shiming.com.shimingqiang;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * Created by $ zhoudeheng on 2015/12/8.
 * Email zhoudeheng@qccr.com
 * 主要的自定义画图的view
 * 实现一个支持可插入{@link InsertableObjectBase} object的view
 */
public class DoodleView extends SurfaceView implements IInternalDoodle {
    private static final String TAG = "DoodleView";
    private static final String INFO_PREFIX = "DoodleView:";
    private Handler mHandler = new Handler();
    private SurfaceHolder mSurfaceHolder;
    private DrawThread mDrawThread;
    private ICommandsManager mCommandsManager = new CommandsManagerImpl();

    /*
     * 一个阻塞队列，生产者为UI线程，消费者为DrawThread线程。
     * 该LinkedBlockingDeque的容量为无限大，故Ui线程永远不会被阻塞
     */
    private BlockingQueue<DoodleOperation> mDrawBlockingQueue = new LinkedBlockingQueue<DoodleOperation>();

    private FrameCache mFrameCache;

    /**
     * 专门为分段画笔预备的缓存
     */
    private FrameCache mTempFrameCache;

    private DoodleEnum.InputMode mInputMode = DoodleEnum.InputMode.DRAW;
    //测试
    private DoodleEnum.SelectionMode mSelectionMode = DoodleEnum.SelectionMode.NONE;

    private IModelManager mModelManager;
    private IVisualManager mVisualManager;
    private int mStrokeType = InsertableObjectStroke.STROKE_TYPE_NORMAL;

    /**
     *
     * @param context
     * @param attrs
     */
    public DoodleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setZOrderOnTop(true);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);// 设置为透明

        mSurfaceHolder.addCallback(mSurfaceCallback);

        mModelManager = new ModelManager(this);
        mVisualManager = new VisualManagerImpl(getContext(), this);
        mModelManager.addIsertableObjectListener(mVisualManager);
        mModelManager.addTouchEventListener(mVisualManager);



    }

    private SurfaceHolder.Callback mSurfaceCallback = new SurfaceHolder.Callback() {

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (mDrawThread != null) {// 线程好像不能start两次，这里直接释放掉已有线程
                mDrawThread.mLoop = false;
                mDrawThread = null;
            }
            if (mFrameCache != null) {
                mFrameCache.recycle();
                mFrameCache = null;
            }
            if (mTempFrameCache != null) {
                mTempFrameCache.recycle();
                mTempFrameCache = null;
            }
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.i(TAG, INFO_PREFIX + "width" + getWidth());
            Log.i(TAG, INFO_PREFIX + "height" + getHeight());
            if (mFrameCache == null) {
                mFrameCache = new FrameCache(getWidth(), getHeight());
            }
            resetSegmentFrameCache();
            if (mDrawThread != null) {
                mDrawThread.mLoop = false;
                mDrawThread = null;
            }
            mDrawThread = new DrawThread();
            mDrawThread.mLoop = true;
            mDrawThread.start();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            if (mFrameCache != null) {
                mFrameCache.recycle();
                mFrameCache = null;
            }
            mFrameCache = new FrameCache(getWidth(), getHeight());
            if (mTempFrameCache != null) {
                mTempFrameCache.recycle();
                mTempFrameCache = null;
            }
            resetSegmentFrameCache();
            DrawAllOperation operation = new DrawAllOperation(mFrameCache,
                    mModelManager, mVisualManager);
            insertOperation(operation);// 第一次重绘所有的
        }
    };

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        exitSelectionMode();// 退出，防止内存泄露
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub

        boolean multiPoint = event.getPointerCount() > 1;
        if (multiPoint)
            return false;
        return mModelManager.onTouchEvent(event);// 事件通过mModelManager进一步分发
    }

    /**
     * 绘图线程。DoodleView绘制图形的过程将在该线程内完成
     *
     * @author noah
     */
    class DrawThread extends Thread {
        // private DrawStatus mDrawStatus;
        /**
         * 每一次,从mDrawBlockingQueue中取所有的DrawStatus,放入mDrawStatusList
         */
        private List<DoodleOperation> mDoodleOperationList = new LinkedList<DoodleOperation>();
        private boolean mLoop = false;

        @Override
        public void run() {
            // TODO Auto-generated method stub
            while (mLoop) {
                fetchData();
                if (mDoodleOperationList.size() <= 0)
                    continue;
                drawOperation();
            }
        }

        /**
         * 绘制操作。遍历绘制每一个操作
         */
        private void drawOperation() {
            for (DoodleOperation operation : mDoodleOperationList) {
                /**
                 * 笔画我们在主ui线程去绘制，原因是:在工作线程绘制会导致延迟现象（当然，我们可以在工作线程中加入丢点策略去解决）；
                 * 在主ui线程中没有延迟现象，是因为主ui线程会自动丢点
                 */
                if (operation instanceof StrokeTouchOperation
                         ) {
                    final DoodleOperation temp = operation;
                    mHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            handleDrawOperation(temp, true);
                        }
                    });
                } else {
                    handleDrawOperation(operation, true);
                }
            }
        }

        /**
         * 处理DrawOperation,核心两步:drawOperation.draw，以及undo,redo，入栈
         *
         * @param doodleOperation
         * @param draw
         *            是否绘制
         */
        private void handleDrawOperation(DoodleOperation doodleOperation,
                                         boolean draw) {
            if (!draw) {
                return;
            }
            Canvas canvas = null;
            try {
                Rect dirty = doodleOperation.computerDirty();
                if (dirty != null)
                    Log.i(TAG, "DoodleView,computerDirty：" + dirty.toString());
                canvas = mSurfaceHolder.lockCanvas(dirty);
                synchronized (mSurfaceHolder) {
                    doodleOperation.draw(canvas);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (canvas != null) {
                    try {
                        mSurfaceHolder.unlockCanvasAndPost(canvas);
                    } catch (Exception e2) {
                        // TODO: handle exception
                        e2.printStackTrace();
                    }
                }
            }
            ICommand command = doodleOperation.createCommand();
            if (command != null) {
                mCommandsManager.addUndo(command);
            }
        }

        protected void fetchData() {
            mDoodleOperationList.clear();
            try {// 如果mDrawBlockingQueue没有数据则阻塞当前线程
                // mDrawStatus = mDrawBlockingQueue.take();
                Log.i(TAG, "segment,mDrawBlockingQueue.size:"
                        + mDrawBlockingQueue.size());
                mDoodleOperationList.add(mDrawBlockingQueue.take());
                mDrawBlockingQueue.drainTo(mDoodleOperationList);
                Log.i(TAG, "segment,mDoodleOperationList.size:"
                        + mDoodleOperationList.size());
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置当前画笔的类型。 不允许设置为InsertableObjectStroke.STROKE_TYPE_ERASER；
     * 如果想要设置为InsertableObjectStroke.STROKE_TYPE_ERASER，
     *
     * @param type
     */
    public void setStrokeType(int type) {
        if (!InsertableObjectStroke.isSupported(type)
                || type == InsertableObjectStroke.STROKE_TYPE_ERASER) {
            throw ErrorUtil.getStrokeTypeNoteSupportedError(type);
        }
        mStrokeType = type;
        /**
         * 画笔属性改变的时候，需要保存一下图像识别的结果；否则会出现之前绘制的识别绘制不正常的问题
         */
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                resetSegmentFrameCache();// 此时，DoodleView可能还没有初始化好，所以要Post一下
            }
        });
    }

    private void resetSegmentFrameCache() {
        InsertableObjectStroke stroke = new InsertableObjectStroke(mStrokeType);
        VisualStrokeBase visualStroke = (VisualStrokeBase) stroke
                .createVisualElement(getContext(), this);
        if (visualStroke.isSegmentDraw()) {
            mTempFrameCache = new FrameCache(getWidth(), getHeight());
        } else {
            if (mTempFrameCache != null)
                mTempFrameCache.recycle();// 清空分段画笔缓存，节省内存
            mTempFrameCache = null;
        }
    }

    /**
     * 设置某支画笔的相关属性
     *
     * @param type
     *            要设置的画笔类型
     * @param color
     *            画笔颜色
     * @param strokeWidth
     *            画笔宽度
     * @param alpha
     *            画笔透明度。0-255
     */
    public void setStrokeAttrs(int type, int color, float strokeWidth, int alpha) {
        if (!InsertableObjectStroke.isSupported(type)) {
            throw ErrorUtil.getStrokeTypeNoteSupportedError(type);
        }
        PropertyConfigStrokeUtils.setStrokeAttrs(type, color, strokeWidth,
                alpha);
        /**
         * 画笔属性改变的时候，需要保存一下图像识别的结果；否则会出现之前绘制的识别绘制不正常的问题
         */
        if (type != InsertableObjectStroke.STROKE_TYPE_ERASER
                && type == mStrokeType) {

        }
    }



    /**
     * 清空所有(包括图片等)。该操作不可逆
     */
    public void clear() {
        mModelManager.exitSelectionMode();
        //删除的方法最为关键
        mModelManager.clear();

        mCommandsManager.clear();


    }



    /**
     * 生成整个DoodleView的Bitmap
     *
     * @param drawBg
     *            :是否绘制背景
     * @return 生成的DoobleView的Bitmap,大小等于DoodleView的大小
     */
    public Bitmap newWholeBitmap(boolean drawBg) {
        FrameCache frameCache = new FrameCache(getWidth(), getHeight());
        // 重新绘制所有笔画
        List<InsertableObjectBase> list = new ArrayList<InsertableObjectBase>();// 复制一份新数据，否则会出异常
        list.addAll(mModelManager.getInsertableObjectList());
        RedrawStrategy drawStrategy = new RedrawStrategy(null, frameCache, null,
                list, mVisualManager);
        drawStrategy.draw();

        Drawable drawable = getBackground();
        if (drawBg && drawable != null && drawable.isVisible()) {// 绘制背景
            FrameCache backgroundCache = new FrameCache(getWidth(), getHeight());
            drawable.setBounds(0, 0, getWidth(), getHeight());
            drawable.draw(backgroundCache.getCanvas());

            Rect rect = new Rect(0, 0, getWidth(), getHeight());
            Paint paint = new Paint();
            paint.setDither(true);
            paint.setAntiAlias(true);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
            frameCache.getCanvas().drawBitmap(backgroundCache.getBitmap(),
                    rect, rect, paint);
            backgroundCache.recycle();
        }

        return frameCache.getBitmap();
    }


    /**
     * 生成绘制区域的bitmap
     *
     * @return
     */
    public Bitmap newBitmap() {
        List<InsertableObjectBase> list = new ArrayList<InsertableObjectBase>();// 复制一份新数据，否则会出异常
        list.addAll(mModelManager.getInsertableObjectList());
        if (list.size() <= 0)
            return null;
        Bitmap wholeBitmap = newWholeBitmap(false);
        RectF rectF = new RectF();
        for (InsertableObjectBase insertableObjectBase : list) {
            RectF rectF2 = InsertableObjectBase
                    .getTransformedRectF(insertableObjectBase);
            if (rectF2 != null) {
                rectF.union(rectF2);
            }
        }
        return createBitmap(wholeBitmap, rectF);
    }

    private Bitmap createBitmap(Bitmap wholeBitmap, RectF rectF) {
        int x = (int) rectF.left;
        int y = (int) rectF.top;
        int width = (int) (rectF.right - rectF.left);
        int height = (int) (rectF.bottom - rectF.top);
        if (x < 0) {
            width += x;
            x = 0;
        }
        if (x + width > wholeBitmap.getWidth()) {
            width = wholeBitmap.getWidth() - x;
        }
        if (y < 0) {
            height += y;
            y = 0;
        }
        if (y + height > wholeBitmap.getHeight()) {
            height = wholeBitmap.getHeight() - y;
        }
        Bitmap bitmap = Bitmap.createBitmap(wholeBitmap, x, y, width, height);
        return bitmap;
    }


    /*********************************************
     * IManagerFetcher接口实现开始
     ********************************************/
    @Override
    public IModelManager getModelManager() {
        // TODO Auto-generated method stub
        return mModelManager;
    }

    @Override
    public IVisualManager getVisualManager() {
        // TODO Auto-generated method stub
        return mVisualManager;
    }


    @Override
    public DoodleEnum.InputMode getInputMode() {
        return mInputMode;
    }

    @Override
    public FrameCache getFrameCache() {
        // TODO Auto-generated method stub
        return mFrameCache;
    }

    /*********************************************
     * IManagerFetcher接口实现结束
     ********************************************/

    /*********************************************
     * IInternaleDoodle接口实现开始
     ********************************************/
    @Override
    public FrameCache getTempFrameCache() {
        // TODO Auto-generated method stub
        return mTempFrameCache;
    }

    @Override
    public void insertOperation(DoodleOperation operation) {
        try {
            mDrawBlockingQueue.put(operation);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            Log.i(TAG, INFO_PREFIX
                    + "mDrawBlockingDeque插入数据失败，不应该发生；mDrawBlockingDeque.size:"
                    + mDrawBlockingQueue.size());
        }
    }

    @Override
    public int getStrokeType() {
        // TODO Auto-generated method stub
        return mStrokeType;
    }

    @Override
    public DoodleEnum.SelectionMode getSelectionMode() {
        // TODO Auto-generated method stub
        return mSelectionMode;
    }


    /**
     * 退出选中模式
     */
    public void exitSelectionMode() {
        // TODO Auto-generated method stub
        mModelManager.exitSelectionMode();
    }



}
