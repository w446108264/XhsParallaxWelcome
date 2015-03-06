package com.xhs.xhsparallaxwelcome;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import com.xhs.view.parallaxpager.ParallaxContainer;

/**
 * @author zhongdaxia 2014-12-15
 */

public class MainActivity extends Activity {

    ImageView iv_man;
    ImageView rl_weibo;
    ParallaxContainer parallaxContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * 动画支持11以上sdk,11以下默认不显示动画
         * 若需要支持11以下动画,也可导入https://github.com/JakeWharton/NineOldAndroids
         */
        if (android.os.Build.VERSION.SDK_INT > 10) {
            iv_man = (ImageView) findViewById(R.id.iv_man);
            parallaxContainer = (ParallaxContainer) findViewById(R.id.parallax_container);

            if (parallaxContainer != null) {
                parallaxContainer.setImage(iv_man);
                parallaxContainer.setLooping(false);

                iv_man.setVisibility(View.VISIBLE);
                parallaxContainer.setupChildren(getLayoutInflater(),
                        R.layout.view_intro_1, R.layout.view_intro_2,
                        R.layout.view_intro_3, R.layout.view_intro_4,
                        R.layout.view_intro_5, R.layout.view_login);
            }
        }
        else{
            setContentView(R.layout.view_login);
        }

        rl_weibo = (ImageView) findViewById(R.id.rl_weibo);
        rl_weibo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Uri uri = Uri.parse("market://details?id=com.xingin.xhs");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                }
            }
        });
    }
}
