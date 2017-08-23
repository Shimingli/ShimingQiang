package imageloader.shiming.com.shimingqiang;



/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public interface ISelectView<T> {
    void setUnSelectedListener(IObjectUnSelectedListener<T> listener);
    void setOnDeleteListener(IClickedListener listener);
    void setTransformChangedListener(ITransformChanged changed);
    void showSelectView();
    void dismissSelectView();
    boolean isSelectViewShowing();
}
