package imageloader.shiming.com.shimingqiang;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;


/**
 * Created by $ zhoudeheng on 2015/12/8.
 * Email zhoudeheng@qccr.com
 * 当插入一个对象的时候，需要对应的操作
 * 对应改变undo redo
 */
public class AddedOperation extends DoodleOperation {
    protected InsertableObjectBase mInsertableObject;
    private VisualElementBase mVisualElement;

    public AddedOperation(FrameCache frameCache, IModelManager modelManager,
                          IVisualManager visualManager, InsertableObjectBase insertableObject) {
        super(frameCache, modelManager, visualManager);
        // TODO Auto-generated constructor stub
        mInsertableObject = insertableObject;
        // TODO Auto-generated constructor stub

        mVisualElement = null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        DrawStrategy drawStrategy = new AddElementDrawStrategy(canvas, mFrameCache,
                mVisualElement);
        if (drawStrategy != null)
            drawStrategy.draw();
    }

    @Override
    public Rect computerDirty() {
        // TODO Auto-generated method stub
        if (mVisualElement == null)
            return null;
        Rect rect = null;

        RectF rectF = mVisualElement.getBounds();
        if (rectF == null)
            return null;
        rect = new Rect((int) rectF.left, (int) rectF.top, (int) rectF.right,
                (int) rectF.bottom);
        return rect;
    }


}
