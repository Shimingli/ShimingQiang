package imageloader.shiming.com.shimingqiang;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.view.MotionEvent;


/**
 * Created by $ zhoudeheng on 2015/12/8.
 * Email zhoudeheng@qccr.com
 */
public abstract class VisualElementBase implements
        IPropertyValueChangedListener {
    protected InsertableObjectBase mInsertableObject;
    protected IInternalDoodle mInternalDoodle;
    protected Context mContext;

    public VisualElementBase(Context context, IInternalDoodle internalDoodle,
                             InsertableObjectBase object) {
        mInternalDoodle = internalDoodle;
        mContext = context;
        mInsertableObject = object;
    }

    public InsertableObjectBase getInsertableObject() {
        return mInsertableObject;
    }

    public void setInsertableObject(InsertableObjectBase mInsertableObject) {
        this.mInsertableObject = mInsertableObject;
    }

    /**
     * 绘制
     *
     * @param canvas
     */
    public abstract void draw(Canvas canvas);

    /**
     * 接受并处理onTouchEvent
     *
     * @param event
     * @return
     */
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    /**
     * 是否能从Visual Elment cache中移除。 对于占用内存多，绘制的时候花费时间少的Visual
     * Element，如Image，应该设置为true 对于占用内存少，绘制的时候花费时间多的Visual
     * Element，如毛笔等，应该设置为false。 默认值为true
     *
     * @return
     */
    public boolean canRemovedFromCache() {
        return true;
    }

    /**
     * 第一次生成的时候，如果需要根据mInsertableObject进行一些初始化的工作，应该放在此处
     */
    public abstract void init();

    /**********************************************
     * IPropertyValueChangedListener开始
     **********************************************/
    @Override
    public void onPropertyValeChanged(InsertableObjectBase insertableObject,
                                      int propertyId, Object oldValue, Object newValue,
                                      boolean fromUndoRedo) {
        // TODO Auto-generated method stub

    }



    /**********************************************
     * IPropertyValueChangedListener结束
     **********************************************/
    /**
     * 向doodleview发送一个操作
     *
     * @param operation
     */
    protected void sendOperation(DoodleOperation operation) {
        mInternalDoodle.insertOperation(operation);
    }

    // /**
    // * 向doodleview发送一个绘制的操作
    // *
    // * @param operation
    // */
    // protected abstract void sendDrawOperation(MotionEvent motionEvent);
    /**
     * 返回添加时候的操作 默认的{@link AddedOperation}，将返回一个{@link AddedCommand}
     *
     * @return
     */
    public AddedOperation createdAddedOperation() {
        return new AddedOperation(mInternalDoodle.getFrameCache(),
                mInternalDoodle.getModelManager(),
                mInternalDoodle.getVisualManager(), mInsertableObject);
    }

    /**
     * 整个可绘制区域
     *
     * @return
     */
    public RectF getBounds() {
        return null;
    }
}
