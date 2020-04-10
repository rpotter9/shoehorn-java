package io.shadowstack;

import io.shadowstack.service.VoidConverter;
import org.junit.jupiter.api.Test;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static io.shadowstack.Fluently.shoehorn;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestVoidAndPrimitiveConversions {
    public static interface IntegerSquare {
        void printSquare(Integer x);
    }

    public static class DoubleSquare {
        @Mimic(type = IntegerSquare.class, method = "printSquare")
        @Convert(to = Void.class, use = VoidConverter.class)
        public void printSquare(@Convert(to = Integer.class, use = Integer2Double.class) Double x) {
            System.out.println(x * x);
        }
    }

    @Mapper
    public static class Integer2Double implements ArgumentConverter<Integer, Double> {
        static Integer2Double INSTANCE = Mappers.getMapper(Integer2Double.class);

        @Override
        public Double convert(Integer from) throws AdapterException {
            return from.doubleValue();
        }

        @Override
        public void convert(Integer from, Double to) throws AdapterException { }
    }

    @Test
    public void testMimic() throws AdapterException {
        IntegerSquare is = shoehorn(new DoubleSquare()).into(IntegerSquare.class).build();
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        is.printSquare(2);
        assertEquals("4.0\r\n", outContent.toString());
    }
}