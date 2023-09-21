package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CompensationServiceImplTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private Employee testEmployee;
    private Compensation testCompensation;

    private String baseUrl = "http://localhost:";

    @Before
    public void setup() {
        testEmployee = new Employee();
        testEmployee.setFirstName("Mike");
        testEmployee.setLastName("Napoleon");
        testEmployee.setDepartment("Leve1");
        testEmployee.setPosition("Position Level 1");

        testCompensation = new Compensation();
        testCompensation.setSalary(123456.12);
        testCompensation.setEffectiveDate("2023-01-01");
    }

    @Test
    public void testCreateCompensation() {
        Employee employee = restTemplate.postForEntity(
                baseUrl + port + "/employee", testEmployee, Employee.class).getBody();

        // add created employee to test compensation before creating it
        testCompensation.setEmployee(employee);
        Compensation compensation = restTemplate.postForEntity(
                baseUrl + port + "/compensation", testCompensation, Compensation.class).getBody();

        assertNotNull(compensation.getEmployee().getEmployeeId());
        assertCompensationEquivalance(testCompensation, compensation);

        // now test reading the created compensation back
        Compensation readCompensation = restTemplate.getForEntity(
                baseUrl + port + "/compensation/{id}",
                Compensation.class,
                compensation.getEmployee().getEmployeeId()).getBody();

        assertEquals(compensation.getEmployee().getEmployeeId(), readCompensation.getEmployee().getEmployeeId());
        assertCompensationEquivalance(compensation, readCompensation);

    }

    @Test
    public void testCreateCompensationForInvalidEmployeeShouldReturn500() {
        testEmployee.setEmployeeId("fake-id");

        Compensation testCompensation = new Compensation();
        testCompensation.setEmployee(testEmployee);
        testCompensation.setSalary(123456.12);
        testCompensation.setEffectiveDate("2023-01-01");

        HttpStatus status = restTemplate.postForEntity(
                baseUrl + port + "/compensation", testCompensation, Compensation.class).getStatusCode();

        assertTrue(status.is5xxServerError());
    }

    @Test
    public void testReadCompensaationForInvalidEmployeeShouldReturn500() {

        HttpStatus status = restTemplate.getForEntity(
                baseUrl + port + "/compensation/{id}",
                Compensation.class,
                "another-fake-id").getStatusCode();

        assertTrue(status.is5xxServerError());
    }


    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }

    private static void assertCompensationEquivalance(Compensation expected, Compensation actual) {
        assertEmployeeEquivalence(expected.getEmployee(), actual.getEmployee());
        assertEquals(expected.getSalary(), actual.getSalary());
        assertEquals(expected.getEffectiveDate(), actual.getEffectiveDate());
    }
}
