package com.wuli.delivery.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.icdispatch.ICBlock;
import com.app.activity.CoreFragmentActivity;
import com.wuli.delivery.App;
import com.wuli.delivery.R;
import com.wuli.delivery.base.BasePagedFragment;
import com.wuli.delivery.portal.bean.Expressage;
import com.wuli.delivery.portal.dao.ExpressageDao;
import com.wuli.delivery.portal.event.Event;
import com.wuli.delivery.portal.event.EventPublisher;
import com.wuli.delivery.utils.DateUtil;
import com.wuli.delivery.view.CustomViewPager;

import java.util.Random;

import butterknife.BindArray;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainActivity extends CoreFragmentActivity {

    @BindView(R.id.iv_open_left_content)
    ImageView ivOpenLeftContent;
    @BindView(R.id.iv_chat)
    ImageView ivChat;
    @BindString(R.string.msg_not_support_chat)
    String msgNotSupportChat;
    @BindView(R.id.layout_drawer)
    DrawerLayout layoutDrawer;
    @BindView(R.id.rb_home)
    RadioButton rbHome;
    @BindView(R.id.rb_express_delivery)
    RadioButton rbExpressDelivery;
    @BindView(R.id.rb_wuli_circle)
    RadioButton rbWuliCircle;
    @BindView(R.id.rb_user_center)
    RadioButton rbUserCenter;
    @BindView(R.id.iv_release_expressage)
    ImageView ivReleaseExpressage;
    @BindView(R.id.viewPager_content)
    CustomViewPager viewPagerContent;
    @BindView(R.id.layout_main_content)
    LinearLayout layoutMainContent;
    @BindView(R.id.layout_top_title)
    RelativeLayout layoutTopTitle;
    @BindArray(R.array.expressage_type_list)
    String[] expressageTypeList;
    @BindArray(R.array.expressage_release_status_list)
    String[] expressageLeadTypeList;

    private Unbinder unbinder;
    private DrawerLayout.SimpleDrawerListener mSimpleDrawerListener;
    private static final BasePagedFragment[] fragments = {HomeFragment.newInstance(), ExpressageFragment.newInstance(), WuliCircleFragment.newInstance(), UserCenterFragment.newInstance()};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        mSimpleDrawerListener = new MySimpleDrawerListener();
        layoutDrawer.addDrawerListener(mSimpleDrawerListener);
        layoutDrawer.setScrimColor(Color.TRANSPARENT);
        viewPagerContent.setAdapter(new MyPageAdapter(getSupportFragmentManager()));
        viewPagerContent.setPagingEnabled(false);
        viewPagerContent.setOffscreenPageLimit(4);

        showTab(0);
    }

    @OnClick({R.id.iv_open_left_content, R.id.iv_chat, R.id.iv_release_expressage, R.id.layout_top_title})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_open_left_content:
                if (layoutDrawer.isDrawerOpen(Gravity.LEFT)) {
                    layoutDrawer.closeDrawer(Gravity.LEFT, true);
                } else {
                    layoutDrawer.openDrawer(Gravity.LEFT, true);
                }
                break;
            case R.id.iv_chat:
                Toast.makeText(this, msgNotSupportChat, Toast.LENGTH_LONG).show();
                break;
            case R.id.iv_release_expressage:
                ReleaseExpressageLeadDialogFragment dialogFragment = new ReleaseExpressageLeadDialogFragment();
                dialogFragment.show(getSupportFragmentManager(), dialogFragment.getClass().getSimpleName());
                break;
            case R.id.layout_top_title:
                App.executeOn(App.getConcurrentThread(), new ICBlock() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 10; i++) {
                            Expressage expressage = new Expressage.Builder()
                                    .expressageLeadNum("2020202093010")
                                    .expressageLeadType(i % 2 == 0 ? "1" : "2")
                                    .releaseTime(DateUtil.currentTime(DateUtil.DATETIME_FORMAT_02))
                                    .deliveryTime("2018-10-07")
                                    .deliverySite("送15栋到一楼")
                                    .expressageDesc("赛鲸笔记本电脑可折叠")
                                    .expressageLeadReward("2")
                                    .expressageType(expressageTypeList[new Random().nextInt(3)])
                                    .deliveryUserName("王悦")
                                    .deliveryPhoneNumber("18925204762")
                                    .expressageLeadRemark("谢谢帮忙的小仙女，今日内取！")
                                    .build();

                            if ("1".equals(expressage.getExpressageLeadType())) {
                                expressage.setExpressageReleaseStatus(String.valueOf(new Random().nextInt(3)));
                            } else {
                                expressage.setExpressageReceiveStatus(String.valueOf(new Random().nextInt(2)));
                            }

                            ExpressageDao.save(expressage);

                        }

                        EventPublisher.getInstance().sendInsertDataSuccEvent(new Event.InsertDataSuccEvent());
                    }
                });
                break;
            default:
                break;
        }
    }

    @OnCheckedChanged({R.id.rb_home, R.id.rb_express_delivery, R.id.rb_wuli_circle, R.id.rb_user_center})
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.rb_home:
                if (isChecked) {
                    viewPagerContent.setCurrentItem(0);
                    rbExpressDelivery.setChecked(false);
                    rbWuliCircle.setChecked(false);
                    rbUserCenter.setChecked(false);
                }
                break;
            case R.id.rb_express_delivery:
                if (isChecked) {
                    viewPagerContent.setCurrentItem(1);
                    rbHome.setChecked(false);
                    rbWuliCircle.setChecked(false);
                    rbUserCenter.setChecked(false);
                }
                break;
            case R.id.rb_wuli_circle:
                if (isChecked) {
                    viewPagerContent.setCurrentItem(2);
                    rbHome.setChecked(false);
                    rbExpressDelivery.setChecked(false);
                    rbUserCenter.setChecked(false);
                }
                break;
            case R.id.rb_user_center:
                if (isChecked) {
                    viewPagerContent.setCurrentItem(3);
                    rbHome.setChecked(false);
                    rbExpressDelivery.setChecked(false);
                    rbWuliCircle.setChecked(false);
                }
                break;
            default:
                break;
        }
    }

    private void showTab(int index) {
        switch (index) {
            case 0:
                rbHome.setChecked(true);
                break;
            case 1:
                rbExpressDelivery.setChecked(true);
                break;
            case 2:
                rbWuliCircle.setChecked(true);
                break;
            case 3:
                rbUserCenter.setChecked(true);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
        if (layoutDrawer != null) {
            layoutDrawer.removeDrawerListener(mSimpleDrawerListener);
        }
    }

    static class MySimpleDrawerListener extends DrawerLayout.SimpleDrawerListener {
        @Override
        public void onDrawerOpened(View drawerView) {
            super.onDrawerOpened(drawerView);
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            super.onDrawerClosed(drawerView);
        }

        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
            super.onDrawerSlide(drawerView, slideOffset);
        }

        @Override
        public void onDrawerStateChanged(int newState) {
            super.onDrawerStateChanged(newState);
        }
    }

    static class MyPageAdapter extends FragmentPagerAdapter {

        public MyPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }
    }
}
