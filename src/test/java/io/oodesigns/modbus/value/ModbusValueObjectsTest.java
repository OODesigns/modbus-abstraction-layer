package io.oodesigns.modbus.value;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Modbus domain value objects")
class ModbusValueObjectsTest {

    @Nested
    @DisplayName("Addresses")
    class AddressTest {

        @ParameterizedTest
        @ValueSource(ints = {0, 1, 65535})
        void addressesSpanTheWholeModbusAddressSpace(int candidate) {
            assertEquals(candidate, new StartAddress(candidate).value());
            assertEquals(candidate, new RegisterAddress(candidate).value());
            assertEquals(candidate, new CoilAddress(candidate).value());
        }

        @ParameterizedTest
        @ValueSource(ints = {-1, 65536})
        void addressesOutsideTheAddressSpaceAreRejected(int candidate) {
            assertThrows(IllegalArgumentException.class, () -> new StartAddress(candidate));
            assertThrows(IllegalArgumentException.class, () -> new RegisterAddress(candidate));
            assertThrows(IllegalArgumentException.class, () -> new CoilAddress(candidate));
        }
    }

    @Nested
    @DisplayName("RegisterCount")
    class RegisterCountTest {

        @ParameterizedTest
        @ValueSource(ints = {1, 64, 125})
        void acceptsCountsAllowedByTheModbusSpecification(int candidate) {
            assertEquals(candidate, new RegisterCount(candidate).value());
        }

        @ParameterizedTest
        @ValueSource(ints = {0, -1, 126})
        void rejectsCountsOutsideTheModbusSpecification(int candidate) {
            assertThrows(IllegalArgumentException.class, () -> new RegisterCount(candidate));
        }
    }

    @Nested
    @DisplayName("CoilCount")
    class CoilCountTest {

        @ParameterizedTest
        @ValueSource(ints = {1, 1000, 2000})
        void acceptsCountsAllowedByTheModbusSpecification(int candidate) {
            assertEquals(candidate, new CoilCount(candidate).value());
        }

        @ParameterizedTest
        @ValueSource(ints = {0, -1, 2001})
        void rejectsCountsOutsideTheModbusSpecification(int candidate) {
            assertThrows(IllegalArgumentException.class, () -> new CoilCount(candidate));
        }
    }

    @Nested
    @DisplayName("RegisterValue")
    class RegisterValueTest {

        @ParameterizedTest
        @ValueSource(ints = {0, 32768, 65535})
        void acceptsUnsigned16BitValues(int candidate) {
            assertEquals(candidate, new RegisterValue(candidate).value());
        }

        @ParameterizedTest
        @ValueSource(ints = {-1, 65536})
        void rejectsValuesOutsideAnUnsigned16BitWord(int candidate) {
            assertThrows(IllegalArgumentException.class, () -> new RegisterValue(candidate));
        }
    }

    @Nested
    @DisplayName("RegisterValues")
    class RegisterValuesTest {

        @Test
        void exposesAnImmutableCopyOfTheRegisters() {
            int[] raw = {1, 2, 3};
            RegisterValues values = new RegisterValues(raw);

            raw[0] = 99;

            assertArrayEquals(new int[] {1, 2, 3}, values.asArray());
            values.asArray()[0] = 77;
            assertEquals(1, values.at(0).orElseThrow().value());
            assertEquals(3, values.size());
        }

        @Test
        void rejectsMissingOrOutOfRangeRegisters() {
            assertThrows(IllegalArgumentException.class, () -> new RegisterValues(null));
            assertThrows(IllegalArgumentException.class, () -> new RegisterValues(new int[] {65536}));
        }

        @Test
        void anEmptyReadIsRepresentableButHasNoRegisters() {
            assertEquals(0, new RegisterValues(new int[0]).size());
            assertEquals(java.util.Optional.empty(), new RegisterValues(new int[0]).at(0));
        }
    }

    @Nested
    @DisplayName("CoilValues")
    class CoilValuesTest {

        @Test
        void exposesAnImmutableCopyOfTheCoils() {
            boolean[] raw = {true, false};
            CoilValues values = new CoilValues(raw);

            raw[0] = false;

            assertEquals(true, values.at(0).orElseThrow().value());
            assertEquals(2, values.size());
        }

        @Test
        void rejectsMissingCoils() {
            assertThrows(IllegalArgumentException.class, () -> new CoilValues(null));
        }
    }
}
