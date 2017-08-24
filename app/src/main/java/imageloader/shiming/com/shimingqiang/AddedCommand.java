package imageloader.shiming.com.shimingqiang;


/**
 * Created by $ zhoudeheng on 2015/12/8.
 * Email zhoudeheng@qccr.com
 * 新增一个对象的时候，对应的Command
 */
public class AddedCommand implements ICommand {

    protected InsertableObjectBase mInsertableObject;
    protected IModelManager mModelManager;


    public AddedCommand(InsertableObjectBase insertableObject,
                        IModelManager modelManager) {
        mInsertableObject = insertableObject;
        mModelManager = modelManager;
    }



}