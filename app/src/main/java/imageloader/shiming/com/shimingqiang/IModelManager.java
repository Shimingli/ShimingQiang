package imageloader.shiming.com.shimingqiang;

import android.view.MotionEvent;

import java.util.List;

/**
 * Created by $ zhoudeheng on 2015/12/8.
 * Email zhoudeheng@qccr.com
 *
 */
public interface IModelManager {
    List<InsertableObjectBase> getInsertableObjectList();

    void clear();



    void addInsertableObject(InsertableObjectBase object,
                             boolean fromUndoRedo);



    void removeInsertableObject(InsertableObjectBase object,
                                boolean fromUndoRedo);



    void addIsertableObjectListener(IIsertableObjectListener listener);


    void addTouchEventListener(ITouchEventListener listener);


    /**
     * 退出选择模式
     */
    void exitSelectionMode();

    /**
     * 该控制器接受处理onTouchEvent
     *
     * @param event
     * @return
     */
    boolean onTouchEvent(MotionEvent event);



    /**
     * 如果当前有{@link InsertableObjectBase} object被选中，则释放掉选中状态
     */
    void unSelected();

    /**
     * 如果当前的{@link ISelectView} 显示，则dismiss掉。 该方法并不会修改一个
     * {@link InsertableObjectBase} object的选中状态
     */
    void dismissSelectView();

}
