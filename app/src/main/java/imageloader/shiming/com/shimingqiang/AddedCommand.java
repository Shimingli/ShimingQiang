package imageloader.shiming.com.shimingqiang;


/**
 * Created by $ zhoudeheng on 2015/12/8.
 * Email zhoudeheng@qccr.com
 * 新增一个对象的时候，对应的Command
 */
public class AddedCommand implements ICommand {
    // protected DrawStrokeOperation mDrawStrokeOperation;
    // protected DrawTool mDrawTool;
    // protected List<DrawTool> mAllDrawTools;
    protected InsertableObjectBase mInsertableObject;
    protected IModelManager mModelManager;

    // public DrawStrokeCommand(DrawStrokeOperation operation,
    // List<DrawTool> allDrawTools) {
    // mDrawStrokeOperation = operation;
    // mDrawTool = operation.getDrawTool();
    // mAllDrawTools = allDrawTools;
    // }
    public AddedCommand(InsertableObjectBase insertableObject,
                        IModelManager modelManager) {
        mInsertableObject = insertableObject;
        mModelManager = modelManager;
    }



}