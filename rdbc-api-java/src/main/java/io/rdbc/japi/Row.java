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

import io.rdbc.japi.exceptions.ConversionException;

import java.math.BigDecimal;
import java.time.*;
import java.util.Optional;
import java.util.UUID;

/**
 * Represents a row of a result returned by a database engine.
 * <p>
 * This class defines a set of methods that can be used to get values from the
 * row either by a column name or by a column index. Each method has a version
 * returning an {@link Optional} to allow null-safe handling of SQL
 * {@code NULL} values.
 */
public interface Row {

    /**
     * Returns an object of type {@code T} from column with a given index.
     * <p>
     * For SQL {@code NULL} values, {@link ConversionException} is thrown. For null-safety consider using
     * corresponding {@code *Opt} method.
     */
    <T> T getCol(int idx, Class<T> cls) throws ConversionException;

    /**
     * Returns an object of type {@code T} from column with a given index.
     * <p>
     * For SQL {@code NULL} values an empty {@link Optional} is returned.
     */
    <T> Optional<T> getColOpt(int idx, Class<T> cls) throws ConversionException;

    /**
     * Returns an object of type {@code T} from column with a given name.
     * <p>
     * For SQL {@code NULL} values, {@link ConversionException} is thrown. For null-safety consider using
     * corresponding {@code *Opt} method.
     */
    <T> T getCol(String name, Class<T> cls) throws ConversionException;

    /**
     * Returns an object of type {@code T} from column with a given name.
     * <p>
     * For SQL {@code NULL} values an empty {@link Optional} is returned.
     */
    <T> Optional<T> getColOpt(String name, Class<T> cls) throws ConversionException;

    /**
     * Returns a {@code String} from column with a given name.
     * <p>
     * For SQL {@code NULL} values, {@link ConversionException} is thrown. For null-safety consider using
     * corresponding {@code *Opt} method.
     */
    String getStr(String name) throws ConversionException;

    /**
     * Returns a {@code String} from column with a given name.
     * <p>
     * For SQL {@code NULL} values an empty {@link Optional} is returned.
     */
    Optional<String> getStrOpt(String name) throws ConversionException;

    /**
     * Returns a {@code String} from column with a given index.
     * <p>
     * For SQL {@code NULL} values, {@link ConversionException} is thrown. For null-safety consider using
     * corresponding {@code *Opt} method.
     */
    String getStr(int idx) throws ConversionException;

    /**
     * Returns a {@code String} from column with a given index.
     * <p>
     * For SQL {@code NULL} values an empty {@link Optional} is returned.
     */
    Optional<String> getStrOpt(int idx) throws ConversionException;

    /**
     * Returns a boolean value from column with a given name.
     * <p>
     * <ul>
     * <li>A single {@code 'T'}, {@code 'Y'} or {@code '1'} character values or {@code 1} numeric value are
     * considered {@code true}.</li>
     * <li>A single {@code 'F'}, {@code 'N'} or {@code '0'} character values or {@code 0} numeric value are
     * considered {@code false}.</li>
     * </ul>
     * <p>
     * For SQL {@code NULL} values, {@link ConversionException} is thrown. For null-safety consider using
     * corresponding {@code *Opt} method.
     */
    Boolean getBool(String name) throws ConversionException;

    /**
     * Returns a boolean value from column with a given name.
     * <p>
     * <ul>
     * <li>A single {@code 'T'}, {@code 'Y'} or {@code '1'} character values or {@code 1} numeric value are
     * considered {@code true}.</li>
     * <li>A single {@code 'F'}, {@code 'N'} or {@code '0'} character values or {@code 0} numeric value are
     * considered {@code false}.</li>
     * </ul>
     * <p>
     * For SQL {@code NULL} values an empty {@link Optional} is returned.
     */
    Optional<Boolean> getBoolOpt(String name) throws ConversionException;

    /**
     * Returns a boolean value from column with a given index.
     * <p>
     * <ul>
     * <li>A single {@code 'T'}, {@code 'Y'} or {@code '1'} character values or {@code 1} numeric value are
     * considered {@code true}.</li>
     * <li>A single {@code 'F'}, {@code 'N'} or {@code '0'} character values or {@code 0} numeric value are
     * considered {@code false}.</li>
     * </ul>
     * <p>
     * For SQL {@code NULL} values, {@link ConversionException} is thrown. For null-safety consider using
     * corresponding {@code *Opt} method.
     */
    Boolean getBool(int idx) throws ConversionException;

    /**
     * Returns a boolean value from column with a given index.
     * <p>
     * <ul>
     * <li>A single {@code 'T'}, {@code 'Y'} or {@code '1'} character values or {@code 1} numeric value are
     * considered {@code true}.</li>
     * <li>A single {@code 'F'}, {@code 'N'} or {@code '0'} character values or {@code 0} numeric value are
     * considered {@code false}.</li>
     * </ul>
     * <p>
     * For SQL {@code NULL} values an empty {@link Optional} is returned.
     */
    Optional<Boolean> getBoolOpt(int idx) throws ConversionException;

    /**
     * Returns a character from column with a given name.
     * <p>
     * Varchar types with a single character are convertible to a {@code Char}.
     * <p>
     * For SQL {@code NULL} values, {@link ConversionException} is thrown. For null-safety consider using
     * corresponding {@code *Opt} method.
     */
    Character getChar(String name) throws ConversionException;

    /**
     * Returns a character from column with a given name.
     * <p>
     * Varchar types with a single character are convertible to a {@code Char}.
     * <p>
     * For SQL {@code NULL} values an empty {@link Optional} is returned.
     */
    Optional<Character> getCharOpt(String name) throws ConversionException;

    /**
     * Returns a character from column with a given index.
     * <p>
     * Varchar types with a single character are convertible to a {@code Char}.
     * <p>
     * For SQL {@code NULL} values, {@link ConversionException} is thrown. For null-safety consider using
     * corresponding {@code *Opt} method.
     */
    Character getChar(int idx) throws ConversionException;

    /**
     * Returns a character from column with a given index.
     * <p>
     * Varchar types with a single character are convertible to a {@code Char}.
     * <p>
     * For SQL {@code NULL} values an empty {@link Optional} is returned.
     */
    Optional<Character> getCharOpt(int idx) throws ConversionException;

    /**
     * Returns a {@code Short} from column with a given name.
     * <p>
     * All numeric types can be converted to {@code Short}, but some conversions may
     * involve rounding or truncation.
     * <p>
     * For SQL {@code NULL} values, {@link ConversionException} is thrown. For null-safety consider using
     * corresponding {@code *Opt} method.
     */
    Short getShort(String name) throws ConversionException;

    /**
     * Returns a {@code Short} from column with a given name.
     * <p>
     * All numeric types can be converted to {@code Short}, but some conversions may
     * involve rounding or truncation.
     * <p>
     * For SQL {@code NULL} values an empty {@link Optional} is returned.
     */
    Optional<Short> getShortOpt(String name) throws ConversionException;

    /**
     * Returns a {@code Short} from column with a given index.
     * <p>
     * All numeric types can be converted to {@code Short}, but some conversions may
     * involve rounding or truncation.
     * <p>
     * For SQL {@code NULL} values, {@link ConversionException} is thrown. For null-safety consider using
     * corresponding {@code *Opt} method.
     */
    Short getShort(int idx) throws ConversionException;

    /**
     * Returns a {@code Short} from column with a given index.
     * <p>
     * All numeric types can be converted to {@code Short}, but some conversions may
     * involve rounding or truncation.
     * <p>
     * For SQL {@code NULL} values an empty {@link Optional} is returned.
     */
    Optional<Short> getShortOpt(int idx) throws ConversionException;

    /**
     * Returns an {@code int} from column with a given name.
     * <p>
     * All numeric types can be converted to {@code int}, but some conversions may
     * involve rounding or truncation.
     * <p>
     * For SQL {@code NULL} values, {@link ConversionException} is thrown. For null-safety consider using
     * corresponding {@code *Opt} method.
     */
    Integer getInt(String name) throws ConversionException;

    /**
     * Returns an {@code int} from column with a given name.
     * <p>
     * All numeric types can be converted to {@code int}, but some conversions may
     * involve rounding or truncation.
     * <p>
     * For SQL {@code NULL} values an empty {@link Optional} is returned.
     */
    Optional<Integer> getIntOpt(String name) throws ConversionException;

    /**
     * Returns an {@code int} from column with a given index.
     * <p>
     * All numeric types can be converted to {@code int}, but some conversions may
     * involve rounding or truncation.
     * <p>
     * For SQL {@code NULL} values an empty {@link Optional} is returned.
     */
    Integer getInt(int idx) throws ConversionException;

    /**
     * Returns an {@code int} from column with a given index.
     * <p>
     * All numeric types can be converted to {@code int}, but some conversions may
     * involve rounding or truncation.
     * <p>
     * For SQL {@code NULL} values an empty {@link Optional} is returned.
     */
    Optional<Integer> getIntOpt(int idx) throws ConversionException;

    /**
     * Returns a {@code Long} from column with a given name.
     * <p>
     * All numeric types can be converted to {@code Long}, but some conversions may
     * involve rounding or truncation.
     * <p>
     * For SQL {@code NULL} values, {@link ConversionException} is thrown. For null-safety consider using
     * corresponding {@code *Opt} method.
     */
    Long getLong(String name) throws ConversionException;

    /**
     * Returns a {@code Long} from column with a given name.
     * <p>
     * All numeric types can be converted to {@code Long}, but some conversions may
     * involve rounding or truncation.
     * <p>
     * For SQL {@code NULL} values an empty {@link Optional} is returned.
     */
    Optional<Long> getLongOpt(String name) throws ConversionException;

    /**
     * Returns a {@code Long} from column with a given index.
     * <p>
     * All numeric types can be converted to {@code Long}, but some conversions may
     * involve rounding or truncation.
     * <p>
     * For SQL {@code NULL} values, {@link ConversionException} is thrown. For null-safety consider using
     * corresponding {@code *Opt} method.
     */
    Long getLong(int idx) throws ConversionException;

    /**
     * Returns a {@code Long} from column with a given index.
     * <p>
     * All numeric types can be converted to {@code Long}, but some conversions may
     * involve rounding or truncation.
     * <p>
     * For SQL {@code NULL} values an empty {@link Optional} is returned.
     */
    Optional<Long> getLongOpt(int idx) throws ConversionException;

    /**
     * Returns a {@link BigDecimal} from column with a given name.
     * <p>
     * All numeric types can be converted to {@link BigDecimal}, note however that
     * NaN value is not representable by a {@link BigDecimal}. If you expect values
     * to be NaN use {@code numeric} method instead.
     * <p>
     * For SQL {@code NULL} values, {@link ConversionException} is thrown. For null-safety consider using
     * corresponding {@code *Opt} method.
     */
    BigDecimal getBigDecimal(String name) throws ConversionException;

    /**
     * Returns a {@link BigDecimal} from column with a given name.
     * <p>
     * All numeric types can be converted to {@link BigDecimal}, note however that
     * NaN value is not representable by a {@link BigDecimal}. If you expect values
     * to be NaN use {@code numeric} method instead.
     * <p>
     * For SQL {@code NULL} values an empty {@link Optional} is returned.
     */
    Optional<BigDecimal> getBigDecimalOpt(String name) throws ConversionException;

    /**
     * Returns a {@link BigDecimal} from column with a given index.
     * <p>
     * All numeric types can be converted to {@link BigDecimal}, note however that
     * NaN value is not representable by a {@link BigDecimal}. If you expect values
     * to be NaN use {@code numeric} method instead.
     * <p>
     * For SQL {@code NULL} values, {@link ConversionException} is thrown. For null-safety consider using
     * corresponding {@code *Opt} method.
     */
    BigDecimal getBigDecimal(int idx) throws ConversionException;

    /**
     * Returns a {@link BigDecimal} from column with a given index.
     * <p>
     * All numeric types can be converted to {@link BigDecimal}, note however that
     * NaN value is not representable by a {@link BigDecimal}. If you expect values
     * to be NaN use {@code numeric} method instead.
     * <p>
     * For SQL {@code NULL} values an empty {@link Optional} is returned.
     */
    Optional<BigDecimal> getBigDecimalOpt(int idx) throws ConversionException;

    /**
     * Returns a {@link DecimalNumber} from column with a given name.
     * <p>
     * For SQL {@code NULL} values, {@link ConversionException} is thrown. For null-safety consider using
     * corresponding {@code *Opt} method.
     */
    DecimalNumber getDecimal(String name) throws ConversionException;

    /**
     * Returns a {@link DecimalNumber} from column with a given name.
     * <p>
     * For SQL {@code NULL} values an empty {@link Optional} is returned.
     */
    Optional<DecimalNumber> getDecimalOpt(String name) throws ConversionException;

    /**
     * Returns a {@link DecimalNumber} from column with a given index.
     * <p>
     * For SQL {@code NULL} values, {@link ConversionException} is thrown. For null-safety consider using
     * corresponding {@code *Opt} method.
     */
    DecimalNumber getDecimal(int idx) throws ConversionException;

    /**
     * Returns a {@link DecimalNumber} from column with a given index.
     * <p>
     * For SQL {@code NULL} values an empty {@link Optional} is returned.
     */
    Optional<DecimalNumber> getDecimalOpt(int idx) throws ConversionException;

    /**
     * Returns a {@code Double} from column with a given name.
     * <p>
     * All numeric types can be converted to {@code Double}, but some conversions
     * may involve rounding or truncation.
     * <p>
     * For SQL {@code NULL} values, {@link ConversionException} is thrown. For null-safety consider using
     * corresponding {@code *Opt} method.
     */
    Double getDouble(String name) throws ConversionException;

    /**
     * Returns a {@code Double} from column with a given name.
     * <p>
     * All numeric types can be converted to {@code Double}, but some conversions may
     * involve rounding or truncation.
     * <p>
     * For SQL {@code NULL} values an empty {@link Optional} is returned.
     */
    Optional<Double> getDoubleOpt(String name) throws ConversionException;

    /**
     * Returns a {@code Double} from column with a given index.
     * <p>
     * All numeric types can be converted to {@code Double}, but some conversions
     * may involve rounding or truncation.
     * <p>
     * For SQL {@code NULL} values, {@link ConversionException} is thrown. For null-safety consider using
     * corresponding {@code *Opt} method.
     */
    Double getDouble(int idx) throws ConversionException;

    /**
     * Returns a {@code Double} from column with a given index.
     * <p>
     * All numeric types can be converted to {@code Double}, but some conversions may
     * involve rounding or truncation.
     * <p>
     * For SQL {@code NULL} values an empty {@link Optional} is returned.
     */
    Optional<Double> getDoubleOpt(int idx) throws ConversionException;

    /**
     * Returns a {@code Float} from column with a given name.
     * <p>
     * All numeric types can be converted to {@code Float}, but some conversions may
     * involve rounding or truncation.
     * <p>
     * For SQL {@code NULL} values, {@link ConversionException} is thrown. For null-safety consider using
     * corresponding {@code *Opt} method.
     */
    Float getFloat(String name) throws ConversionException;

    /**
     * Returns a {@code Float} from column with a given name.
     * <p>
     * All numeric types can be converted to {@code Float}, but some conversions may
     * involve rounding or truncation.
     * <p>
     * For SQL {@code NULL} values an empty {@link Optional} is returned.
     */
    Optional<Float> getFloatOpt(String name) throws ConversionException;

    /**
     * Returns a {@code Float} from column with a given index.
     * <p>
     * All numeric types can be converted to {@code Float}, but some conversions may
     * involve rounding or truncation.
     * <p>
     * For SQL {@code NULL} values, {@link ConversionException} is thrown. For null-safety consider using
     * corresponding {@code *Opt} method.
     */
    Float getFloat(int idx) throws ConversionException;

    /**
     * Returns a {@code Float} from column with a given index.
     * <p>
     * All numeric types can be converted to {@code Float}, but some conversions may
     * involve rounding or truncation.
     * <p>
     * For SQL {@code NULL} values an empty {@link Optional} is returned.
     */
    Optional<Float> getFloatOpt(int idx) throws ConversionException;

    /**
     * Returns an {@code Instant} from column with a given name.
     * <p>
     * Note that regular timestamp values are not convertible to an {@code Instant}
     * because timestamp values do not hold a time zone information.
     * <p>
     * For SQL {@code NULL} values, {@link ConversionException} is thrown. For null-safety consider using
     * corresponding {@code *Opt} method.
     */
    Instant getInstant(String name) throws ConversionException;

    /**
     * Returns an {@code Instant} from column with a given name.
     * <p>
     * Note that regular timestamp values are not convertible to an {@code Instant}
     * because timestamp values do not hold a time zone information.
     * <p>
     * For SQL {@code NULL} values an empty {@link Optional} is returned.
     */
    Optional<Instant> getInstantOpt(String name) throws ConversionException;

    /**
     * Returns an {@code Instant} from column with a given index.
     * <p>
     * Note that regular timestamp values are not convertible to an {@code Instant}
     * because timestamp values do not hold a time zone information.
     * <p>
     * For SQL {@code NULL} values, {@link ConversionException} is thrown. For null-safety consider using
     * corresponding {@code *Opt} method.
     */
    Instant getInstant(int idx) throws ConversionException;

    /**
     * Returns an {@code Instant} from column with a given index.
     * <p>
     * Note that regular timestamp values are not convertible to an {@code Instant}
     * because timestamp values do not hold a time zone information.
     * <p>
     * For SQL {@code NULL} values an empty {@link Optional} is returned.
     */
    Optional<Instant> getInstantOpt(int idx) throws ConversionException;

    /**
     * Returns an {@code Instant} from column with a given name, assuming
     * that the TIMESTAMP value stored in the database is in the provided time
     * zone.
     * <p>
     * The `zoneId` parameter is ignored in case the driver can create an instant
     * from the stored value without an user-provided time zone. This can happen
     * if the database stores TIMESTAMP values as instants or for columns with
     * TIMESTAMP WITH TIME ZONE types.
     * <p>
     * For SQL {@code NULL} values, {@link ConversionException} is thrown. For null-safety consider using
     * corresponding {@code *Opt} method.
     */
    Instant getInstant(String name, ZoneId zoneId) throws ConversionException;

    /**
     * Returns an {@code Instant} from column with a given name, assuming
     * that the TIMESTAMP value stored in the database is in the provided time
     * zone.
     * <p>
     * The `zoneId` parameter is ignored in case the driver can create an instant
     * from the stored value without an user-provided time zone. This can happen
     * if the database stores TIMESTAMP values as instants or for columns with
     * TIMESTAMP WITH TIME ZONE types.
     * Note that regular timestamp values are not convertible to an {@code Instant}
     * because timestamp values do not hold a time zone information.
     * <p>
     * For SQL {@code NULL} values an empty {@link Optional} is returned.
     */
    Optional<Instant> getInstantOpt(String name, ZoneId zoneId) throws ConversionException;

    /**
     * Returns an {@code Instant} from column with a given index, assuming
     * that the TIMESTAMP value stored in the database is in the provided time
     * zone.
     * <p>
     * The `zoneId` parameter is ignored in case the driver can create an instant
     * from the stored value without an user-provided time zone. This can happen
     * if the database stores TIMESTAMP values as instants or for columns with
     * TIMESTAMP WITH TIME ZONE types.
     * <p>
     * For SQL {@code NULL} values, {@link ConversionException} is thrown. For null-safety consider using
     * corresponding {@code *Opt} method.
     */
    Instant getInstant(int idx, ZoneId zoneId) throws ConversionException;

    /**
     * Returns an {@code Instant} from column with a given index, assuming
     * that the TIMESTAMP value stored in the database is in the provided time
     * zone.
     * <p>
     * The `zoneId` parameter is ignored in case the driver can create an instant
     * from the stored value without an user-provided time zone. This can happen
     * if the database stores TIMESTAMP values as instants or for columns with
     * TIMESTAMP WITH TIME ZONE types.
     * <p>
     * For SQL {@code NULL} values an empty {@link Optional} is returned.
     */
    Optional<Instant> getInstantOpt(int idx, ZoneId zoneId) throws ConversionException;

    /**
     * Returns a {@code LocalDateTime} from column with a given name.
     * <p>
     * For SQL date type that does not hold a time, {@code LocalDateTime} at start
     * of day is returned.
     * <p>
     * For SQL {@code NULL} values, {@link ConversionException} is thrown. For null-safety consider using
     * corresponding {@code *Opt} method.
     */
    LocalDateTime getLocalDateTime(String name) throws ConversionException;

    /**
     * Returns a {@code LocalDateTime} from column with a given name.
     * <p>
     * For SQL date type that does not hold a time, {@code LocalDateTime} at start
     * of day is returned.
     * <p>
     * For SQL {@code NULL} values an empty {@link Optional} is returned.
     */
    Optional<LocalDateTime> getLocalDateTimeOpt(String name) throws ConversionException;

    /**
     * Returns a {@code LocalDateTime} from column with a given index.
     * <p>
     * For SQL date type that does not hold a time, {@code LocalDateTime} at start
     * of day is returned.
     * <p>
     * For SQL {@code NULL} values, {@link ConversionException} is thrown. For null-safety consider using
     * corresponding {@code *Opt} method.
     */
    LocalDateTime getLocalDateTime(int idx) throws ConversionException;

    /**
     * Returns a {@code LocalDateTime} from column with a given index.
     * <p>
     * For SQL date type that does not hold a time, {@code LocalDateTime} at start
     * of day is returned.
     * <p>
     * For SQL {@code NULL} values an empty {@link Optional} is returned.
     */
    Optional<LocalDateTime> getLocalDateTimeOpt(int idx) throws ConversionException;

    /**
     * Returns a {@code LocalDate} from column with a given name.
     * <p>
     * SQL types that represent a date with a time are convertible to
     * {@code LocalDate} - a time part is truncated.
     * <p>
     * For SQL {@code NULL} values, {@link ConversionException} is thrown. For null-safety consider using
     * corresponding {@code *Opt} method.
     */
    LocalDate getLocalDate(String name) throws ConversionException;

    /**
     * Returns a {@code LocalDate} from column with a given name.
     * <p>
     * SQL types that represent a date with a time are convertible to
     * {@code LocalDate} - a time part is truncated.
     * <p>
     * For SQL {@code NULL} values an empty {@link Optional} is returned.
     */
    Optional<LocalDate> getLocalDateOpt(String name) throws ConversionException;

    /**
     * Returns a {@code LocalDate} from column with a given index.
     * <p>
     * SQL types that represent a date with a time are convertible to
     * {@code LocalDate} - a time part is truncated.
     * <p>
     * For SQL {@code NULL} values, {@link ConversionException} is thrown. For null-safety consider using
     * corresponding {@code *Opt} method.
     */
    LocalDate getLocalDate(int idx) throws ConversionException;

    /**
     * Returns a {@code LocalDate} from column with a given index.
     * <p>
     * SQL types that represent a date with a time are convertible to
     * {@code LocalDate} - a time part is truncated.
     * <p>
     * For SQL {@code NULL} values an empty {@link Optional} is returned.
     */
    Optional<LocalDate> getLocalDateOpt(int idx) throws ConversionException;

    /**
     * Returns a {@code LocalDate} from column with a given name.
     * <p>
     * SQL types that represent a date with a time are convertible to
     * {@code LocalTime} - a date part is truncated.
     * <p>
     * For SQL {@code NULL} values, {@link ConversionException} is thrown. For null-safety consider using
     * corresponding {@code *Opt} method.
     */
    LocalTime getLocalTime(String name) throws ConversionException;

    /**
     * Returns a {@code LocalDate} from column with a given name.
     * <p>
     * SQL types that represent a date with a time are convertible to
     * {@code LocalTime} - a date part is truncated.
     * <p>
     * For SQL {@code NULL} values an empty {@link Optional} is returned.
     */
    Optional<LocalTime> getLocalTimeOpt(String name) throws ConversionException;

    /**
     * Returns a {@code LocalDate} from column with a given index.
     * <p>
     * SQL types that represent a date with a time are convertible to
     * {@code LocalTime} - a date part is truncated.
     * <p>
     * For SQL {@code NULL} values, {@link ConversionException} is thrown. For null-safety consider using
     * corresponding {@code *Opt} method.
     */
    LocalTime getLocalTime(int idx) throws ConversionException;

    /**
     * Returns a {@code LocalDate} from column with a given index.
     * <p>
     * SQL types that represent a date with a time are convertible to
     * {@code LocalTime} - a date part is truncated.
     * <p>
     * For SQL {@code NULL} values an empty {@link Optional} is returned.
     */
    Optional<LocalTime> getLocalTimeOpt(int idx) throws ConversionException;

    /**
     * Returns a byte array from column with a given name.
     * <p>
     * Note that this method cannot be used to fetch raw value of any type from
     * the database, it should be used only to fetch binary data.
     * <p>
     * For SQL {@code NULL} values, {@link ConversionException} is thrown. For null-safety consider using
     * corresponding {@code *Opt} method.
     */
    byte[] getBytes(String name) throws ConversionException;

    /**
     * Returns a byte array from column with a given name.
     * <p>
     * Note that this method cannot be used to fetch raw value of any type from
     * the database, it should be used
     * only to fetch binary data.
     * <p>
     * For SQL {@code NULL} values an empty {@link Optional} is returned.
     */
    Optional<byte[]> getBytesOpt(String name) throws ConversionException;

    /**
     * Returns a byte array from column with a given index.
     * <p>
     * Note that this method cannot be used to fetch raw value of any type from
     * the database, it should be used only to fetch binary data.
     * <p>
     * For SQL {@code NULL} values, {@link ConversionException} is thrown. For null-safety consider using
     * corresponding {@code *Opt} method.
     */
    byte[] getBytes(int idx) throws ConversionException;

    /**
     * Returns a byte array from column with a given index.
     * <p>
     * Note that this method cannot be used to fetch raw value of any type from
     * the database, it should be used
     * only to fetch binary data.
     * <p>
     * For SQL {@code NULL} values an empty {@link Optional} is returned.
     */
    Optional<byte[]> getBytesOpt(int idx) throws ConversionException;

    /**
     * Returns an {@code UUID} from column with a given name.
     * <p>
     * A string type with a standard UUID representation as defined by
     * {@code UUID.fromString} is convertible to UUID.
     * <p>
     * For SQL {@code NULL} values, {@link ConversionException} is thrown. For null-safety consider using
     * corresponding {@code *Opt} method.
     */
    UUID getUuid(String name) throws ConversionException;

    /**
     * Returns an {@code UUID} from column with a given name.
     * <p>
     * A string type with a standard UUID representation as defined by
     * {@code UUID.fromString} is convertible to UUID.
     * <p>
     * For SQL {@code NULL} values an empty {@link Optional} is returned.
     */
    Optional<UUID> getUuidOpt(String name) throws ConversionException;

    /**
     * Returns an {@code UUID} from column with a given index.
     * <p>
     * A string type with a standard UUID representation as defined by
     * {@code UUID.fromString} is convertible to UUID.
     * <p>
     * For SQL {@code NULL} values, {@link ConversionException} is thrown. For null-safety consider using
     * corresponding {@code *Opt} method.
     */
    UUID getUuid(int idx) throws ConversionException;

    /**
     * Returns an {@code UUID} from column with a given index.
     * <p>
     * A string type with a standard UUID representation as defined by
     * {@code UUID.fromString} is convertible to UUID.
     * <p>
     * For SQL {@code NULL} values an empty {@link Optional} is returned.
     */
    Optional<UUID> getUuidOpt(int idx) throws ConversionException;

    /**
     * Returns a {@code ZonedDateTime} from column with a given name.
     * <p>
     * For SQL {@code NULL} values, {@link ConversionException} is thrown. For null-safety consider using
     * corresponding {@code *Opt} method.
     */
    ZonedDateTime getZonedDateTime(String name) throws ConversionException;

    /**
     * Returns a {@code ZonedDateTime} from column with a given name.
     * <p>
     * For SQL {@code NULL} values an empty {@link Optional} is returned.
     */
    Optional<ZonedDateTime> getZonedDateTimeOpt(String name) throws ConversionException;

    /**
     * Returns a {@code ZonedDateTime} from column with a given index.
     * <p>
     * For SQL {@code NULL} values, {@link ConversionException} is thrown. For null-safety consider using
     * corresponding {@code *Opt} method.
     */
    ZonedDateTime getZonedDateTime(int idx) throws ConversionException;

    /**
     * Returns a {@code ZonedDateTime} from column with a given index.
     * <p>
     * For SQL {@code NULL} values an empty {@link Optional} is returned.
     */
    Optional<ZonedDateTime> getZonedDateTimeOpt(int idx) throws ConversionException;
}
