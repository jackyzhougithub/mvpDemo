package com.z.jk.linkagelistview;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.z.jk.linkagelistview.DialogFragment.JsonUtil;
import com.z.jk.linkagelistview.DialogFragment.TestDialogFragment;
import com.z.jk.linkagelistview.customDragGridView.DragAdapter;
import com.z.jk.linkagelistview.customDragGridView.DragGridView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private List<HashMap<String, Object>> dataSourceList = new ArrayList<HashMap<String, Object>>();
    ImageView mImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DragGridView mDragGridView = (DragGridView) findViewById(R.id.dragGridView);
        for (int i = 0; i < 30; i++) {
            HashMap<String, Object> itemHashMap = new HashMap<String, Object>();
            itemHashMap.put("item_image",R.drawable.ic_alipay);
            itemHashMap.put("item_text", "拖拽 " + Integer.toString(i));
            dataSourceList.add(itemHashMap);
        }


        final DragAdapter mSimpleAdapter =  new DragAdapter(this, dataSourceList);

        mDragGridView.setAdapter(mSimpleAdapter);
//        mImageView =( ImageView)findViewById(R.id.testimg);
//        LinkSelectView linkageView = (LinkSelectView) findViewById(R.id.linkageView);
//        List<LinkListBean> testlist = new ArrayList<>();
//        for (int i =0;i<15;i++){
//            List<LinkListBean> nextlist = new ArrayList<>();
//            LinkListBean bean = new LinkListBean("test"+i,i,0,nextlist);
//            for(int j=0;j<20;j++){
//                LinkListBean bean1 = new LinkListBean("hello"+j,j,0,null);
//                nextlist.add(bean1);
//            }
//            testlist.add(bean);
//        }
//        linkageView.setData(testlist,testlist.size()/2);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private final String IMAGE_TYPE = "image/*";

    private final int IMAGE_CODE = 0;

    private void testPicker() {
        Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);

        getAlbum.setType(IMAGE_TYPE);

        startActivityForResult(getAlbum, IMAGE_CODE);
    }

    private void showDialog(){
        TestDialogFragment dialogFragment = new TestDialogFragment();
        dialogFragment.show(getFragmentManager(),"test");
    }
    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK) {        //此处的 RESULT_OK 是系统自定义得一个常量
            return;

        }


        Bitmap bm = null;


        //外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口

        ContentResolver resolver = getContentResolver();


        //此处的用于判断接收的Activity是不是你想要的那个

        if (requestCode == IMAGE_CODE) {

            try {

                Uri originalUri = data.getData();        //获得图片的uri
                bm = MediaStore.Images.Media.getBitmap(resolver, originalUri);        //显得到bitmap图片
                mImageView.setImageBitmap(bm);
//                String[] proj = {MediaStore.Images.Media.DATA};
//                //好像是android多媒体数据库的封装接口，具体的看Android文档
//                Cursor cursor = getContentResolver().query(originalUri, proj, null, null, null);
//
//                //按我个人理解 这个是获得用户选择的图片的索引值
//
//                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//
//                //将光标移至开头 ，这个很重要，不小心很容易引起越界
//
//                cursor.moveToFirst();
//
//                //最后根据索引值获取图片路径
//
//                String path = cursor.getString(column_index);

            } catch (IOException e) {
                Log.e("Error",e.toString());

            }

        }

    }

    private void testGsonP(){
        String json1 = "{notjson12345";
        String json2 = "{\n" +
                "\t\"code\": 0,\n" +
                "\t\"msg\": \"成功\",\n" +
                "\t\"info\": false\n" +
                "}";
        JsonUtil.jsonToBean(json1, TestJson.class);
        JsonUtil.jsonToBean(json2,TestJson.class);
    }

    static class TestJson{
        int code;
        String msg;
        Boolean info;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
