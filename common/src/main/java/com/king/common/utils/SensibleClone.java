package com.king.common.utils;

/**
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年4月19日
 * @param <T>
 */
public interface SensibleClone<T extends SensibleClone<T>> extends Cloneable {
  public T sensibleClone();
}
