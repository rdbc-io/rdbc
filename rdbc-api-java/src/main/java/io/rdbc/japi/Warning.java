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

package io.rdbc.japi;

/**
 * Represents a warning emitted by a database engine during statement
 * processing.
 */
public final class Warning {
    private final String msg;
    private final String code;

    private Warning(String msg, String code) {
        this.msg = msg;
        this.code = code;
    }

    /**
     * Creates new Warning instance.
     *
     * @param msg  database vendor specific warning message
     * @param code database vendor specific warning code
     */
    public static Warning of(String msg, String code) {
        return new Warning(msg, code);
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Warning warning = (Warning) o;

        return msg.equals(warning.msg) && code.equals(warning.code);
    }

    @Override
    public int hashCode() {
        int result = msg.hashCode();
        result = 31 * result + code.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Warning(" +
                "msg='" + msg + '\'' +
                ", code='" + code + '\'' +
                ')';
    }
}
