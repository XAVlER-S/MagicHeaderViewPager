# MagicHeaderViewPager
HeaderViewPager with a header can be fixed and many scrollable Fragments (listFragments, gridFragments and ScrollFragments) inside.

# Introduction
### 0. Example
**0.1、Simply immobilizing tabs and keeping locaiton**

![](https://raw.githubusercontent.com/XavierSAndroid/MagicHeaderViewPager/master/pics/1.gif)
<br>
<br>

**0.2、Support large Header taller than screen**

![](https://raw.githubusercontent.com/XavierSAndroid/MagicHeaderViewPager/master/pics/2.gif)
<br>
<br>

**0.3、Support Mixed ListView items and their heights need not be the same**

![](https://raw.githubusercontent.com/XavierSAndroid/MagicHeaderViewPager/master/pics/3.gif)
<br>
<br>

**0.4、Combined with pull to refresh**

![](https://raw.githubusercontent.com/XavierSAndroid/MagicHeaderViewPager/master/pics/4.gif)
<br>
<br>

**0.5、Magic header can be drag down as you like. The header and inner ListView(ScrollView) can respond touch respctively**

![](https://raw.githubusercontent.com/XavierSAndroid/MagicHeaderViewPager/master/pics/5.gif)
<br>
<br>

**0.6、Inner Scroller(ListView、GridView、ScrollView) can be scrolled even if content is null, and the empty content can be customized to improve user experience**

![](https://raw.githubusercontent.com/XavierSAndroid/MagicHeaderViewPager/master/pics/6.gif)
<br>
<br>

**0.7、Dispatch touch event as expected**

![](https://raw.githubusercontent.com/XavierSAndroid/MagicHeaderViewPager/master/pics/7.gif)
<br>
<br>

**0.8、Example of height auto completion: empty content colored green, and auto completion colored blue**

![](https://raw.githubusercontent.com/XavierSAndroid/MagicHeaderViewPager/master/pics/8.gif)
<br>
<br>

### 1. Dependency

Add the following lines to your build.gradle for pure MagicHeaderViewPager.
```Java
dependencies {
    compile 'com.culiu.android:mhvp-core:2.1.2@aar'
}
```

For using integrated Pull-To-Refresh:
```Java
dependencies {
    compile 'com.culiu.android:mhvp-core:2.1.2@aar'
    compile 'com.culiu.android:mhvp-integrated-ptr:2.1.2@aar'
}
```

For more details, see [Guide](https://github.com/XavierSAndroid/MagicHeaderViewPager/tree/master/doc) or [Demo](https://github.com/XavierSAndroid/MagicHeaderViewPager/tree/master/demo).

# Developed By

* Xavier S - <Xavier-S@foxmail.com> 　 <X.S.Xavier.S@gmail.com>
* https://github.com/XavierSAndroid 　 https://github.com/CuliuDev

# License

Copyright (c) 2015 [Xavier-S](mailto:X.S.Xavier.S@gmail.com)

Licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)
