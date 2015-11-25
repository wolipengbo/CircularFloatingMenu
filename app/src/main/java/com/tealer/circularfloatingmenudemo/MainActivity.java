package com.tealer.circularfloatingmenudemo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.nineoldandroids.view.ViewPropertyAnimator;
import com.tealer.views.CircularFloatingMenu;

import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {
   private ListView listTest;
    private float cfm_startY;
    private CircularFloatingMenu circularFloatingMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_main);
        listTest=(ListView)findViewById(R.id.list_test);
        circularFloatingMenu=(CircularFloatingMenu)findViewById(R.id.cfm_action_view);
        listTest.setAdapter(new AdapterTest(MainActivity.this));
        listTest.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float y = event.getY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        cfm_startY = y;
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_OUTSIDE:
                    case MotionEvent.ACTION_CANCEL:
                        if (cfm_startY - y > 0) {//向下滑动
                            hideCFMActionView(circularFloatingMenu);
                        } else {//向上滑动
                            showCFMActionView(circularFloatingMenu);
                        }
                        cfm_startY = 0;
                        break;

                }
                return false;
            }
        });
        circularFloatingMenu.setOnItemClickListener(new CircularFloatingMenu.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int index) {
                switch (index){
                    case 1:
                        Toast.makeText(MainActivity.this,"第一项被点击",Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(MainActivity.this,"第二项被点击",Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        Toast.makeText(MainActivity.this,"第三项被点击",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    /**
     * 显示圆形ActionView
     */
    protected  void showCFMActionView(View v){
        ViewPropertyAnimator.animate(v).translationY(0).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(500).start();
    }

    /**
     * 隐藏圆形ActionView
     */
    protected void hideCFMActionView(View v){
        ViewPropertyAnimator.animate(v).translationY(getResources().getDimension(R.dimen.dimens_dp_61)).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(500).start();
    }
    private class AdapterTest extends BaseAdapter{
         private Context m_ConText;
        public AdapterTest(Context context) {
            this.m_ConText=context;
        }

        @Override
        public int getCount() {
            return 100;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
          if(convertView==null){
              convertView=View.inflate(m_ConText,R.layout.layout_item_text_view,null);
              viewHolder=new ViewHolder();
              viewHolder.tv=(TextView)convertView.findViewById(R.id.tv_text);
              convertView.setTag(viewHolder);
          }else{
              viewHolder=(ViewHolder)convertView.getTag();
          }
            viewHolder.tv.setText("第"+position+"个Item");
            return convertView;
        }

        private class ViewHolder{
          TextView tv;
        }
    }





}
