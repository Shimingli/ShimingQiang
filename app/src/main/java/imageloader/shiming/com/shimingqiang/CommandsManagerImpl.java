package imageloader.shiming.com.shimingqiang;

import android.os.Handler;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public class CommandsManagerImpl implements ICommandsManager {
    public static final String TAG = "CommandsManager";
    private Deque<ICommand> mUndoCommands = new LinkedList<ICommand>();
    private Deque<ICommand> mRedoCommands = new LinkedList<ICommand>();
    private List<IOnDoAvailabilityChangedListener> mAvailabilityChangedListeners;
    private Handler mHandler = new Handler();

    public CommandsManagerImpl() {
        mAvailabilityChangedListeners = new ArrayList<IOnDoAvailabilityChangedListener>();
    }




    /**
     *
     * @param command
     * @param fromRedoStatck
     *            :是否来自redo
     */
    protected void addUndo(ICommand command, boolean fromRedoStatck) {
        synchronized (mUndoCommands) {
            if (command == null)
                return;
            boolean isEmpty = mUndoCommands.isEmpty();
            mUndoCommands.addLast(command);
            if (isEmpty) {
                fireUndoAvailabilityChanged(true);
            }
            if (!fromRedoStatck) {
                clearRedo();
            }
        }
    }

    /**
     * 插入一个undo命令
     *
     * @return
     */
    @Override
    public void addUndo(ICommand command) {
        addUndo(command, false);
    }

    protected void addRedo(ICommand command) {
        synchronized (mRedoCommands) {
            boolean isEmpty = mRedoCommands.isEmpty();
            mRedoCommands.addLast(command);
            if (isEmpty) {
                fireRedoAvailabilityChanged(true);
            }
        }
    }

    protected void fireRedoAvailabilityChanged(final boolean available) {
        for (IOnDoAvailabilityChangedListener listener : mAvailabilityChangedListeners) {
            final IOnDoAvailabilityChangedListener temp = listener;
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    temp.onRedoAvailabilityChanged(available);
                }
            });
        }
    }

    protected void fireUndoAvailabilityChanged(final boolean available) {
        for (IOnDoAvailabilityChangedListener listener : mAvailabilityChangedListeners) {
            final IOnDoAvailabilityChangedListener temp = listener;
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    temp.onUndoAvailabilityChanged(available);
                }
            });
        }
    }

    @Override
    public void clear() {
        clearRedo();
        clearUndo();
    }

    public void clearRedo() {
        if (mRedoCommands.size() > 0) {
            mRedoCommands.clear();
            fireRedoAvailabilityChanged(false);
        }
    }

    public void clearUndo() {
        if (mUndoCommands.size() > 0) {
            mUndoCommands.clear();
            fireUndoAvailabilityChanged(false);
        }
    }



}
