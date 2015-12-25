package com.culiu.mhvp.demo;

import com.culiu.mhvp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @author Xavier-S
 * @date 2015.11.27 20:02
 */
public class RandomPic {

    private RandomPic() {
        random = new Random();
        mIntList = new ArrayList<Integer>();
    }

    public static RandomPic getInstance() {
        return Nested.instance;
    }

    private static class Nested {
        private static RandomPic instance = new RandomPic();
    }

    /**
     * 获得随机drawable图片资源id
     *
     * @return
     */
    public int getPicResId() {
        if (mIntList.size() == 0) {
            Collections.addAll(mIntList, picIds);
        }
        int index = random.nextInt(mIntList.size());
        int result = mIntList.get(index);
        mIntList.remove(index);
        return result;
    }

    private Random random;
    private List<Integer> mIntList;

    Integer[] picIds = new Integer[]{R.drawable.bg_1, R.drawable.bg_2, R.drawable.bg_3, R.drawable.bg_4,
            R.drawable.bg_5, R.drawable.bg_6, R.drawable.bg_7, R.drawable.bg_8, R.drawable.bg_9,
            R.drawable.bg_10, R.drawable.bg_11, R.drawable.bg_12, R.drawable.bg_13};
}
