package com.example.toursimapp.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.viewpager.widget.PagerAdapter;

import com.airbnb.lottie.LottieAnimationView;
import com.example.toursimapp.R;

public class SliderAdapter extends PagerAdapter {

    public int[] slide_anims = {
            R.raw.solo_travel,
            R.raw.family,
            R.raw.friends
    };
    public String[] slide_headers = {
            "Want a Solo Trip",
            "Want to spend time with family",
            "Want to enjoy a trip with Friends"
    };
    public String[] slide_contents = {
            "We are providing you with a lot a varieties of places to visit" +
                    " to relax your mind from family stress, office burden, etc. Enjoy your time with" +
                    " yourself and a get a inner peace visiting various soothing places..",

            "We have a list of various family visiting location for your " +
                    "family to enjoy, have a good vacation for kids, places for parents to" +
                    " visit, temples, etc. We also have a special brochure for honeymoonm for new married" +
                    " couples to spent their precious time together.",

            "You are planning a cool exciting trip with your friends, but have a " +
                    "confusion about the places ? Don't worry!!!.. We are here to help. We will provide " +
                    "you with a lot of varities of exciting spots for fun, enjoyment and bonding.."
    };
    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return slide_headers.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout, container, false);

        LottieAnimationView anim = view.findViewById(R.id.lottie_anim);
        TextView header = view.findViewById(R.id.intro_header);
        TextView contents = view.findViewById(R.id.intro_content);

        Typeface face_1 = ResourcesCompat.getFont(context, R.font.poppins_dark);
        header.setTypeface(face_1);

        Typeface face_2 = ResourcesCompat.getFont(context, R.font.anaheim);
        contents.setTypeface(face_2);

        anim.setAnimation(slide_anims[position]);
        header.setText(slide_headers[position]);
        contents.setText(slide_contents[position]);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }
}
