package org.realtix.parameter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ParameterStoreTest {

    ParameterStoreWrapper wrapper = mock(
            ParameterStoreWrapper.class
    );

    ParameterStore parameterStore;

    @BeforeEach
    void setUp() {
        when(wrapper.getParameter(any()))
                .thenReturn("value");
        parameterStore = new ParameterStore(wrapper);
    }

    @Test
    @DisplayName("Given key, test whether the value is fetched.")
    void getParameter() {
        assertEquals("value", parameterStore.getParameter("key"));
    }
}