package com.example.ec.utils;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

public class StringUtilsTest {

    @Test
    public void shouldEncoder() {
        String password = "testPassword";
        String encodedPassword = StringUtils.encoder(password);
        assertNotNull(encodedPassword);
        assertNotEquals(password, encodedPassword);
        assertNotEquals("", encodedPassword);
    }

    @Test
    public void shouldEncoderWithNullArgument() {
        String password = null;
        assertThatThrownBy(() -> StringUtils.encoder(password))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
