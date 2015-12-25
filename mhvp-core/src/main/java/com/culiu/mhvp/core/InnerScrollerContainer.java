package com.culiu.mhvp.core;

/**
 * InnerScroller's Container,
 * which Fragment that contains a InnerScroller must implements.
 * @author Xavier-S
 * @date 2015.07.23
 */
public interface InnerScrollerContainer {

    void setOuterScroller(OuterScroller outerScroller, int myPosition);

    InnerScroller getInnerScroller();
}
