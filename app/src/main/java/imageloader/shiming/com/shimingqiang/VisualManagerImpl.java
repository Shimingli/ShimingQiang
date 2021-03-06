package imageloader.shiming.com.shimingqiang;

import android.content.Context;
import android.view.MotionEvent;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;



/**
 * Created by $ zhoudeheng on 2015/12/8.
 * Email zhoudeheng@qccr.com
 * 实现一个可视化管理层类。其功能有
 * 1.管理可视化层的缓存;2.响应IPageManager的事件。
 * 该Manager使用二级缓存来管理Visual Elements
 */
public class VisualManagerImpl implements IVisualManager,
        IIsertableObjectListener, ITouchEventListener {

    public static final String TAG = "VisualManager";
    private static final int HARD_CACHE_CAPACITY = 20;
    private static final int SOFT_CACHE_CAPACITY = 20;
    private HashMap<InsertableObjectBase, VisualElementBase> mHardInsertObjectCache;
    /**
     * InsertableObjectBase使用了SoftReference，当内存空间不足时,
     * 此cache中的InsertableObjectBase会被垃圾回收掉
     */
    private HashMap<InsertableObjectBase, SoftReference<VisualElementBase>> mSoftInsertObjectCache;
    private IInternalDoodle mInternalDoodle;
    protected Context mContext;

    public VisualManagerImpl(Context context, IInternalDoodle internalDoodle) {
        mContext = context;
        mInternalDoodle = internalDoodle;
        mHardInsertObjectCache = new LinkedHashMap<InsertableObjectBase, VisualElementBase>(
                HARD_CACHE_CAPACITY, 1.0f, true) {
            @Override
            protected boolean removeEldestEntry(
                    Entry<InsertableObjectBase, VisualElementBase> eldest) {
                // TODO Auto-generated method stub
                if (mHardInsertObjectCache.size() > HARD_CACHE_CAPACITY
                        && eldest.getValue().canRemovedFromCache()) {
                    mSoftInsertObjectCache.put(
                            eldest.getKey(),
                            new SoftReference<VisualElementBase>(eldest
                                    .getValue()));
                    return true;
                }
                return false;
            }

        };
        mSoftInsertObjectCache = new LinkedHashMap<InsertableObjectBase, SoftReference<VisualElementBase>>(
                SOFT_CACHE_CAPACITY, 1.0f, true);

    }

    /**********************************************************************
     * IVisualManager实现开始
     ***********************************************************************/
    @Override
    public VisualElementBase getVisualElement(
            InsertableObjectBase insertableObject) {
        // TODO Auto-generated method stub
        if (insertableObject == null)
            return null;
        VisualElementBase visualElement = mHardInsertObjectCache
                .get(insertableObject);
        if (visualElement != null) {
            // 如果找到的话，把元素移到linkedhashmap的最前面，从而保证在LRU算法中是最后被删除
            mHardInsertObjectCache.remove(insertableObject);
            mHardInsertObjectCache.put(insertableObject, visualElement);
            return visualElement;
        }
        SoftReference<VisualElementBase> softReference = mSoftInsertObjectCache
                .get(insertableObject);
        if (softReference != null) {
            visualElement = softReference.get();
            if (visualElement != null) {
                mHardInsertObjectCache.put(insertableObject, visualElement);
                return visualElement;
            } else {
                mSoftInsertObjectCache.remove(insertableObject);
            }
        }
        visualElement = insertableObject.createVisualElement(mContext,
                mInternalDoodle);
        insertableObject.addPropertyChangedListener(visualElement);// 注册事件。softReference被回收的时候，注册的listener是不是不用remove？
        visualElement.init();
        mHardInsertObjectCache.put(insertableObject, visualElement);
        // 如果缓存中没有，则创建一个
        return visualElement;
    }

    /**
     * 从Hard cache中移除，放入soft cache中
     */
    protected void removeFromHardCache(InsertableObjectBase insertableObject) {
        if (insertableObject == null)
            return;
        VisualElementBase visualElement = mHardInsertObjectCache
                .get(insertableObject);
        if (visualElement != null) {
            // 如果找到的话，把元素移到linkedhashmap的最前面，从而保证在LRU算法中是最后被删除
            mHardInsertObjectCache.remove(insertableObject);
            mHardInsertObjectCache.put(insertableObject, visualElement);
            return;
        }
    }


    /**********************************************************************
     * IVisualManager实现开始
     ***********************************************************************/

    /**********************************************************************
     * IIsertableObjectListener实现开始
     ***********************************************************************/

    @Override
    public void onAdded(List<InsertableObjectBase> list, boolean fromUndoRedo) {
        // TODO Auto-generated method stub
        for (InsertableObjectBase object : list) {
            DoodleOperation operation = getVisualElement(object)
                    .createdAddedOperation();
            if (operation != null) {
                operation.setCreatingCommand(!fromUndoRedo);
                mInternalDoodle.insertOperation(operation);
            }
        }
    }


    @Override
    public void onClear() {
        // TODO Auto-generated method stub
        ClearOperation clearOperation = new ClearOperation(
                mInternalDoodle.getFrameCache(),
                mInternalDoodle.getModelManager(),
                mInternalDoodle.getVisualManager());
        mInternalDoodle.insertOperation(clearOperation);
        mHardInsertObjectCache.clear();
        mSoftInsertObjectCache.clear();
    }


    /**********************************************************************
     * IIsertableObjectListener实现结束
     ***********************************************************************/

    @Override
    public boolean onTouchEvent(MotionEvent event,
                                InsertableObjectBase actingInsertableObject) {
        // TODO Auto-generated method stub
        if (actingInsertableObject == null)
            return false;
        VisualElementBase visualElement = getVisualElement(actingInsertableObject);
        if (visualElement == null)
            return false;
        return visualElement.onTouchEvent(event);
    }

}
