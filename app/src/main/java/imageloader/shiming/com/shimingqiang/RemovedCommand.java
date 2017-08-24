package imageloader.shiming.com.shimingqiang;


/**
 * Created by $ zhoudeheng on 2015/12/8.
 * Email zhoudeheng@qccr.com
 * 删除一个对象的时候，对应的Command
 */
public class RemovedCommand implements ICommand {

    protected InsertableObjectBase mInsertableObject;
    protected IModelManager mModelManager;

    public RemovedCommand(InsertableObjectBase insertableObject,
                          IModelManager modelManager) {
        mInsertableObject = insertableObject;
        mModelManager = modelManager;
    }


}
