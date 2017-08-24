package imageloader.shiming.com.shimingqiang;


/**
 * Created by $ zhoudeheng on 2015/12/8.
 * Email zhoudeheng@qccr.com
 */
public interface IVisualManager extends IIsertableObjectListener,
        ITouchEventListener {
    /**
     * 通过insertableObject获得其对应的可视化元素
     *
     * @return
     */
    public VisualElementBase getVisualElement(
            InsertableObjectBase insertableObject);

}