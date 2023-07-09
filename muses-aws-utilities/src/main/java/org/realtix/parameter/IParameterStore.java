package org.realtix.parameter;

public interface IParameterStore<T> {

    String getParameter(String key);

    T getParameterObject(String key);

}
