package imageloader.shiming.com.shimingqiang;

import android.graphics.Matrix;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;

import imageloader.shiming.com.shimingqiang.back.DoodleEnum;


/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 * 实现Model的管理类
 * Model管理类，具备的功能:
 * 1.提供增删改查数据层的接口；
 * 2.处理并分发相关事件,如touch事件;
 * 3.向VisualModel发通知
 */
public class ModelManager implements IModelManager
        {
    public static final String TAG = "ModelManager";

    private List<InsertableObjectBase> mInsertableObjects;
    private List<ISelectedChangedListener> mSeletedListeners;
    private List<IIsertableObjectListener> mIsertableObjectListeners;
    private List<ITouchEventListener> mTouchEventListeners;
    private IInternalDoodle mIInternalDoodle;

    /***
     * 当前正在操作的InsertableObjectBase
     */
    private InsertableObjectBase mActingInsertableObject;
    /**
     * 被选中对象的初始Matrix
     */
    private Matrix mSelectedOldMatrix = new Matrix();




    private boolean mIsObjectSelected = false;


    public ModelManager(IInternalDoodle internalDoodle) {
        mIInternalDoodle = internalDoodle;
        mInsertableObjects = new ArrayList<InsertableObjectBase>();
        mSeletedListeners = new ArrayList<ISelectedChangedListener>();
        mIsertableObjectListeners = new ArrayList<IIsertableObjectListener>();
        mTouchEventListeners = new ArrayList<ITouchEventListener>();


    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        if (mIInternalDoodle.getSelectionMode() == DoodleEnum.SelectionMode.SELECTION) {
            return handleSelectionMode(event);
        } else {
            return handleNoneMode(event);
        }
    }

    private boolean handleNoneMode(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                int strokeType = InsertableObjectStroke.STROKE_TYPE_NORMAL;
                if (mIInternalDoodle.getInputMode() == DoodleEnum.InputMode.DRAW) {
                    strokeType = mIInternalDoodle.getStrokeType();
                } else if (mIInternalDoodle.getInputMode() == DoodleEnum.InputMode.ERASE) {
                    strokeType = InsertableObjectStroke.STROKE_TYPE_ERASER;
                }
                InsertableObjectStroke stroke = InsertableObjectStroke
                        .newInsertableObjectStroke(strokeType);
                mActingInsertableObject = stroke;
                break;
            default:
                break;
        }

        boolean b = false;
        for (ITouchEventListener listener : mTouchEventListeners) {
            if (listener.onTouchEvent(event, mActingInsertableObject)) {
                b = true;
                break;
            }
        }

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_UP:
                if (mActingInsertableObject != null) {
                    if (!(mActingInsertableObject instanceof InsertableObjectStroke))
                        break;
                    InsertableObjectStroke stroke = (InsertableObjectStroke) mActingInsertableObject;
                    if (stroke.getStrokeType() == InsertableObjectStroke.STROKE_TYPE_ERASER) {// 橡皮擦不参与图形识别
                        VisualElementBase visualElement = mIInternalDoodle
                                .getVisualManager().getVisualElement(
                                        mActingInsertableObject);
                        VisualStrokeErase visualStrokeEarse = (VisualStrokeErase) visualElement;
                        if (visualStrokeEarse.intersects()) {
                            addInsertableObjectInternal(mActingInsertableObject,
                                    false, false);
                        }
                    } else {

                    }
                }
                mActingInsertableObject = null;
                break;
            default:
                break;
        }
        return b;
    }

    private boolean handleSelectionMode(MotionEvent event) {
        return true;
    }


    protected void addInsertableObjectInternal(InsertableObjectBase object,
                                               boolean notifyVisualManager, boolean fromUndoRedo) {
        if (object == null)
            return;
        mActingInsertableObject = object;
        if (!mInsertableObjects.add(object)) {
            mActingInsertableObject = null;
            return;
        }
        List<InsertableObjectBase> list = new ArrayList<InsertableObjectBase>();
        list.add(object);
        fireInsertableObjectAdded(list, notifyVisualManager, fromUndoRedo);
    }







    private void fireInsertableObjectAdded(List<InsertableObjectBase> list,
                                           boolean notifyVisualManager, boolean fromUndoRedo) {
        if (notifyVisualManager) {
            for (IIsertableObjectListener listener : mIsertableObjectListeners) {
                listener.onAdded(list, fromUndoRedo);
            }
        } else {
            for (IIsertableObjectListener listener : mIsertableObjectListeners) {
                if (!(listener instanceof VisualManagerImpl)) {
                    listener.onAdded(list, fromUndoRedo);
                }
            }
        }
    }



    // TODO: 2017/8/24  关键的地方 
    private void fireClear() {
        for (IIsertableObjectListener listener : mIsertableObjectListeners) {
            listener.onClear();
        }
    }


    private void fireUnSelected(InsertableObjectBase insertableObject) {
        mIsObjectSelected = false;
        for (ISelectedChangedListener listener : mSeletedListeners) {
            listener.onUnSelected(insertableObject);
        }
    }



    @Override
    public void addIsertableObjectListener(IIsertableObjectListener listener) {
        // TODO Auto-generated method stub
        mIsertableObjectListeners.add(listener);
    }


    @Override
    public void addTouchEventListener(ITouchEventListener listener) {
        // TODO Auto-generated method stub
        mTouchEventListeners.add(listener);
    }


    /*********************************************************************
     * IModelManager实现结束
     *********************************************************************/

    @Override
    public List<InsertableObjectBase> getInsertableObjectList() {
        // TODO Auto-generated method stub
        return mInsertableObjects;
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub
        mInsertableObjects.clear();
        fireClear();
    }


    /*********************************************************
     * LongClickDetector.OnLongClickListener实现开始
     *********************************************************/


    public void unSelected() {
        InsertableObjectBase temp = mActingInsertableObject;
        if (temp == null || !mIsObjectSelected)
            return;
        mActingInsertableObject = null;
        fireUnSelected(temp);
        Matrix redoMatrix = new Matrix(temp.getMatrix());
        /**
         * 重新绘制所有的，恢复原来的顺序
         */
        UnSelectedDrawAllOperation operation = new UnSelectedDrawAllOperation(
                mIInternalDoodle.getFrameCache(),
                mIInternalDoodle.getModelManager(),
                mIInternalDoodle.getVisualManager(), temp, mSelectedOldMatrix,
                redoMatrix);
        mIInternalDoodle.insertOperation(operation);
    }


    /*********************************************************
     * LongClickDetector.OnLongClickListener实现结束
     *********************************************************/

    @Override
    public void exitSelectionMode() {
        // TODO Auto-generated method stub
        if (mIInternalDoodle.getSelectionMode() != DoodleEnum.SelectionMode.SELECTION)
            return;

        unSelected();
    }


}