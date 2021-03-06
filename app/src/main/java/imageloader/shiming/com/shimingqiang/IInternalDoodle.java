package imageloader.shiming.com.shimingqiang;


import imageloader.shiming.com.shimingqiang.back.DoodleEnum;

/**
 * Created by $ zhoudeheng on 2015/12/8.
 * Email zhoudeheng@qccr.com
 * 定义Model层，visual层跟DoodleView交互的接口
 */
public interface IInternalDoodle {
    /**
     * 向Doodle插入一个操作
     *
     * @param operation
     */
    void insertOperation(DoodleOperation operation);

    /**
     * 获得当前的Stroke类型
     *
     * @return
     */
    int getStrokeType();

    /**
     * 获得DoodleView中的FrameCache
     *
     * @return
     */
    FrameCache getFrameCache();

    /**
     * 获得临时缓存。分段画笔，会用到此缓存
     *
     * @return
     */
    FrameCache getTempFrameCache();

    IModelManager getModelManager();

    IVisualManager getVisualManager();


    /**
     * 获得当前的输入模式
     *
     * @return
     */
    DoodleEnum.InputMode getInputMode();

    DoodleEnum.SelectionMode getSelectionMode();


}
