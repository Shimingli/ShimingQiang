package imageloader.shiming.com.shimingqiang;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private DoodleView mDoodleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDoodleView = (DoodleView) findViewById(R.id.doodle_view);
        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               mDoodleView.setStrokeType(InsertableObjectStroke.STROKE_TYPE_NORMAL);
           }
       });
        findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDoodleView.setStrokeType(InsertableObjectStroke.STROKE_TYPE_PEN);
            }
        });
        findViewById(R.id.btn3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDoodleView.setStrokeType(InsertableObjectStroke.STROKE_TYPE_BRUSH);
            }
        });
        findViewById(R.id.btn4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDoodleView.setStrokeType(InsertableObjectStroke.STROKE_TYPE_PENCIL);
            }
        });
        findViewById(R.id.btn5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDoodleView.setStrokeType(InsertableObjectStroke.STROKE_TYPE_MARKER);
            }
        });
        findViewById(R.id.btn6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDoodleView.setStrokeType(InsertableObjectStroke.STROKE_TYPE_AIRBRUSH);
            }
        });
        findViewById(R.id.btn7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDoodleView.clear();
            }
        });
    }
}
