package imageloader.shiming.com.shimingqiang;

import android.graphics.Matrix;


/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public class UnSelectedDrawAllOperation extends DrawAllOperation {
    protected InsertableObjectBase mInsertableObject;
    protected Matrix mUndoMatrix = null;
    protected Matrix mRedoMatrix = null;

    public UnSelectedDrawAllOperation(FrameCache frameCache,
                                      IModelManager modelManager, IVisualManager visualManager,
                                      InsertableObjectBase insertableObject, Matrix undoMatrix,
                                      Matrix redoMatrix) {
        super(frameCache, modelManager, visualManager);

        mInsertableObject = insertableObject;
        mUndoMatrix = undoMatrix;
        mRedoMatrix = redoMatrix;
    }

    @Override
    public ICommand onCreateCommand() {

        return null;
    }
}

