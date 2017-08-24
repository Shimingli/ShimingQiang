package imageloader.shiming.com.shimingqiang;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by $ zhoudeheng on 2015/12/8.
 * Email zhoudeheng@qccr.com
 * 可插入object
 */
public abstract class InsertableObjectBase {
    /**
     * 0-100留给默认实现的类型
     */
    public static final int TYPE_NONE = 0;
    public static final int TYPE_STROKE = 1;// 画笔
    public static final int PROPERTY_ID_DRAWNRECTF = 2;
    protected int mType = TYPE_NONE;

    protected RectF mInitRectF;// 绘制区域;记录初始位置。该变量只应该记录初始位置

    // protected float mRotationAngle = 0;// 选中角度
    protected Matrix mMatrix = new Matrix();// 矩形变换。旋转，平移，缩放，都是通过这个矩阵来实现


    protected HashMap<String, Object> mExtraProperties;

    protected List<IPropertyValueChangedListener> mPropertyValueChangedListenerList;

    public InsertableObjectBase(int type) {
        mType = type;
        mExtraProperties = new HashMap<String, Object>();
        mPropertyValueChangedListenerList = new ArrayList<IPropertyValueChangedListener>();
    }



    public RectF getInitRectF() {
        return new RectF(mInitRectF);
    }

    public void setInitRectF(RectF initRectF) {
        RectF oldValue = this.mInitRectF;
        this.mInitRectF = initRectF;
        firePropertyChanged(PROPERTY_ID_DRAWNRECTF, oldValue, initRectF);
    }

    /**
     * 是否可以被选中。默认为false
     *
     * @return
     */
    public boolean isSelectable() {
        return false;
    }



    public Matrix getMatrix() {
        return mMatrix;
    }





    public void firePropertyChanged(int propertyId, Object oldValue,
                                    Object newValue) {
        firePropertyChanged(propertyId, oldValue, newValue, false);
    }

    public void firePropertyChanged(int propertyId, Object oldValue,
                                    Object newValue, boolean fromUndoRedo) {
        if (mPropertyValueChangedListenerList.size() > 0) {
            for (IPropertyValueChangedListener listener : mPropertyValueChangedListenerList) {
                listener.onPropertyValeChanged(this, propertyId, oldValue,
                        newValue, fromUndoRedo);
            }
        }
    }




    public void addPropertyChangedListener(
            IPropertyValueChangedListener listener) {
        if (!mPropertyValueChangedListenerList.contains(listener)) {
            mPropertyValueChangedListenerList.add(listener);
        }
    }


    /**
     * 创建该对象对应的Visual层元素
     *
     * @return
     */
    public abstract VisualElementBase createVisualElement(Context context,
                                                          IInternalDoodle internalDoodle);

    /**
     * 创建该对象对应的ITransformChanged
     *
     * @return
     */

    /**
     * 可以使用橡皮擦，擦除。默认为true
     *
     * @return
     */
    public boolean canErased() {
        return true;
    }


    /**
     * 获得变换后的RectF
     *
     * @return
     */
    public static RectF getTransformedRectF(
            InsertableObjectBase insertableObject) {
        RectF src = insertableObject.getInitRectF();
        RectF dst = new RectF(src);
        Matrix matrix = insertableObject.getMatrix();
        if (matrix != null)
            matrix.mapRect(dst, src);
        return dst;
    }

}
