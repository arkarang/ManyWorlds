package com.minepalm.manyworlds.api.util;

import java.util.function.Consumer;

public interface Callback<T> {

    void call(Consumer<T> callee);
}
