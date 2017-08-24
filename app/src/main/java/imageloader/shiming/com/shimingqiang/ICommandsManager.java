package imageloader.shiming.com.shimingqiang;



/**
 * Created by $ zhoudeheng on 2015/12/8.
 * Email zhoudeheng@qccr.com
 * 定义命令管理器的接口
 * 标准undo redo 做法
 */
public interface ICommandsManager {

    void addUndo(ICommand command);

    void clear();

    void clearUndo();

    void clearRedo();

}
