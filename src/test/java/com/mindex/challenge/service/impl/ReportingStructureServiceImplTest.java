package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
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
public class ReportingStructureServiceImplTest {

    @LocalServerPort
    private int port;

    @Autowired
    TestRestTemplate restTemplate;

    private String baseUrl = "http://localhost:";

    @Test
    public void testGetReportingStructureForEmployeeWithDirectReports() {
        String lennonId = "16a596ae-edd3-4847-99fe-c4518e82c86f";
        ReportingStructure reportingStructure = restTemplate.getForEntity(baseUrl + port + "/reportingStructure/{id}",
                ReportingStructure.class,
                lennonId).getBody();

        assertNotNull(reportingStructure);
        assertEquals(reportingStructure.getNumberOfReports(), 4);
    }

    @Test
    public void testGetReportingStructureForEmployeeWithNoDirectReports() {
        String macartneyId = "b7839309-3348-463b-a7e3-5de1c168beb3";

        ReportingStructure reportingStructure = restTemplate.getForEntity(baseUrl + port + "/reportingStructure/{id}",
                ReportingStructure.class,
                macartneyId).getBody();

        assertNotNull(reportingStructure);
        assertEquals(reportingStructure.getNumberOfReports(), 0);
    }

    @Test
    public void testGetReportingStructureOfInvalidEmployeeShouldReturn500() {
        HttpStatus status = restTemplate.getForEntity(baseUrl + port + "/reportingStructure/{id}",
                ReportingStructure.class,
                "fake-uid").getStatusCode();

        assertTrue(status.is5xxServerError());
    }
}
