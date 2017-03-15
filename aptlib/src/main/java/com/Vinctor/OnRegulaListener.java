package com.Vinctor;

import javax.lang.model.element.Element;

/**
 * Created by Vinctor on 2017/3/12.
 */

public interface OnRegulaListener<T extends Element> {
    /**
     * @param element
     * @return return null or "" to declare that the element is correct
     */
    String onRegula(T element);
}