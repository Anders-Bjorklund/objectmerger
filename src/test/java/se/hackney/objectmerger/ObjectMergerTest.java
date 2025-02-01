package se.hackney.objectmerger;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;

public class ObjectMergerTest {
    @Test
    public void testMerge() {
        Company companyA = new Company();
        Employee emplA1 = new Employee();
        emplA1.getProperties().put("NAME", "John Olsson");
        Employee emplA2 = new Employee();
        emplA2.getProperties().put("NAME", "Sue Nilsson");
        emplA2.getProperties().put("AGE", "33");
        companyA.getEmployees().add(emplA1);
        companyA.getEmployees().add(emplA2);


        Company companyB = new Company();
        Employee emplB1 = new Employee();
        emplB1.getProperties().put("NAME", "Peggy Larsson");
        companyB.getEmployees().add(emplB1);

        ObjectMerger.merge(companyA, companyB);

        try {
            System.out.println(new ObjectMapper().writeValueAsString(companyA));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        assertTrue(companyA.getEmployees().size() == 3);
    }
}

@Data
class Company{
    List<Employee> employees = new ArrayList<>();
}

@Data
class Employee {
    Map<String, String> properties = new HashMap<>();
}
