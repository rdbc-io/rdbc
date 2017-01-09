/*
 * Copyright 2016 rdbc contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.rdbc.japi.exceptions;

public class ConversionException extends RdbcException {
    private final Object value;
    private final Class<?> targetType;

    public ConversionException(Object value, Class<?> targetType, Throwable cause) {
        super(String.format("Value '%s' could not be converted to '%s'",
                value, targetType.getCanonicalName()),
                cause);
        this.value = value;
        this.targetType = targetType;
    }

    public ConversionException(Object value, Class<?> targetType) {
        this(value, targetType, null);
    }

    public Object getValue() {
        return value;
    }

    public Class<?> getTargetType() {
        return targetType;
    }
}
