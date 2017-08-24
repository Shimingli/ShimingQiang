package imageloader.shiming.com.shimingqiang;

/**
 * Created by $ zhoudeheng on 2015/12/8.
 * Email zhoudeheng@qccr.com
 */
public  interface IPropertyValueChangedListener {
     void onPropertyValeChanged(
             InsertableObjectBase insertableObject, int propertyId,
             Object oldValue, Object newValue, boolean fromUndoRedo);

}
