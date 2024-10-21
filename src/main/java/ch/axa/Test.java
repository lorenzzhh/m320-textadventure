package ch.axa;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class Test {
    public static void main(String[] args) throws IOException {
        Test test = new Test();
        test.testParse();
    }

    public void testParse() throws IOException {

       ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();

        Order order = mapper.readValue(new File("src/main/resources/orderInput.yaml"), Order.class);

        System.out.println(order);
    }


    public record Order(String orderNo, LocalDate date, String customerName, List<OrderLine> orderLines) {
    }
    public record OrderLine(String item, int quantity, BigDecimal unitPrice) {
    }
}
