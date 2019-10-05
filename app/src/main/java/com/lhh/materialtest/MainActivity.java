package com.lhh.materialtest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Fruit[] fruits={
            new Fruit("苹果",R.drawable.apple),new Fruit("橙子",R.drawable.orange),
            new Fruit("樱桃",R.drawable.cherry),new Fruit("草莓",R.drawable.strawberry),
            new Fruit("雪梨",R.drawable.pear)};
    private List<Fruit> fruitList  = new ArrayList<>();
    private  FruitAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        //将toolbar实例传入 setSupportActionBar,这样既使用了Toolbar,又实现让它外观与功能都和ActionBar一致了。
        setSupportActionBar(toolbar);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        FloatingActionButton fab = findViewById(R.id.fab);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        //下拉刷新进度条颜色
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view,"Data deleted",Snackbar.LENGTH_SHORT)
                        .setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(MainActivity.this, "数据恢复", Toast.LENGTH_SHORT).show();

                            }
                        }).show();
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            //通常情况下，onRefrshu()方法中应该是去网络上请求最新的数据，然后再将这些数据展示出来
            @Override
            public void onRefresh() {
                //自定义刷新方法
                refreshFruit();
            }
        });
        //得到ActionBar的实例
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //让导航栏显示出来
            actionBar.setDisplayHomeAsUpEnabled(true);
            //设置导航栏图标。实际上，Toolbar最左侧的这个按钮就叫左HomeAsUp按钮，默认图标是一个返回箭头
            actionBar.setHomeAsUpIndicator(R.drawable.menu);
        }
        navigationView.setCheckedItem(R.id.nav_call);
        //让滑动子菜单项的图标显示
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
        initFruits();
        RecyclerView recyclerView = findViewById(R.id.recycle_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new FruitAdapter(fruitList);
        recyclerView.setAdapter(adapter);

    }

    private void refreshFruit() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    //因为本地刷新操作非常快，如果不沉睡线程的话，刷新立马结束
                    Thread.sleep(2000);
                }catch (Exception e){
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /*沉睡结束后，使用runOnUiThread()方法将线程切换回主线程，然后调用initFruits()方法重新生成数据
                        接着再调用FruitAdapter的notifyDataSetChanged()方法通知数据发生了变化，最后调用
                        swipeRefreshLayout.setRefreshing(false)表示刷新结束，并隐藏刷新进度条*/
                        initFruits();
                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    private void initFruits() {
         fruitList.clear();
         for(int i = 0;i<50;i++){
             Random random = new Random();
             int index = random.nextInt(fruits.length);
             fruitList.add(fruits[index]);
         }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }
//菜单项的子菜单
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            //HomeAsUp的id
            case android.R.id.home:
                Log.d("TAG", "动了");
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.backup:
                Toast.makeText(this, "备份", Toast.LENGTH_SHORT).show();
                break;
            case R.id.delete:
                Toast.makeText(this, "删除", Toast.LENGTH_SHORT).show();
                break;
            case R.id.setting:
                Toast.makeText(this, "设置", Toast.LENGTH_SHORT).show();
                break;
            default:
        }
        return true;
    }

}
