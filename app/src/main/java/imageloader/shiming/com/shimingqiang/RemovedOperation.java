package imageloader.shiming.com.shimingqiang;


/**
 * Created by $ zhoudeheng on 2015/12/8.
 * Email zhoudeheng@qccr.com
 */
public class RemovedOperation extends DrawAllOperation {
    protected InsertableObjectBase mRemovedObject;

    public RemovedOperation(FrameCache frameCache, IModelManager modelManager,
                            IVisualManager visualManager, InsertableObjectBase removedObject) {
        super(frameCache, modelManager, visualManager);
        // TODO Auto-generated constructor stub
        mRemovedObject = removedObject;
    }



}