package imageloader.shiming.com.shimingqiang.back;


/**
 * Created by $ zhoudeheng on 2015/12/8.
 * Email zhoudeheng@qccr.com
 * 定义命令管理器的接口
 * 标准undo redo 做法
 */
public interface ICommandsManager {


    void clear();

    void clearUndo();

    void clearRedo();

}
