package com.mindex.challenge.service.impl;


import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import com.mindex.challenge.service.ReportingStructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

@Service
public class ReportingStructureServiceImpl implements ReportingStructureService {

    @Autowired
    private EmployeeService employeeService;

    @Override
    public ReportingStructure read(String employeeId) {
        ReportingStructure reportingStructure = new ReportingStructure();

        Employee employee = employeeService.read(employeeId);

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + employeeId);
        }

        // setting employee on the reporting structure after finding it via the call to
        // employee service
        reportingStructure.setEmployee(employee);

        // build the complete filled out direct reports structure for the given employee
        employee.setDirectReports(buildCompleteReportingStructure(employee.getDirectReports()));

        // using method to find number of reports under the specified employee
        reportingStructure.setNumberOfReports(getReportsCount(employeeId));

        return reportingStructure;
    }

    /**
     * Recursive function to build a complete direct reports structure.
     * @param reports
     * @return
     */
    private List<Employee> buildCompleteReportingStructure(List<Employee> reports) {

        if (reports == null) {
            return null;
        }

        List<Employee> directReports = new ArrayList<>();
        for (Employee employee: reports) {
            Employee fullEmployee = employeeService.read(employee.getEmployeeId());
            directReports.add(fullEmployee);
            if (fullEmployee.getDirectReports() != null) {
                fullEmployee.setDirectReports(buildCompleteReportingStructure(fullEmployee.getDirectReports()));
            }
        }
        return directReports;
    }

    private int getReportsCount(String employeeId) {
        int countOfReports = 0;
        Stack<String> stack = new Stack<>();
        stack.add(employeeId);

        // using a stack to do a depth first traversal of the direct reports tree
        while (!stack.isEmpty()) {
            String currentEmployeeId = stack.pop();

            Employee currentEmployee = employeeService.read(currentEmployeeId);
            List<Employee> directReports = currentEmployee.getDirectReports();

            if (directReports != null && !directReports.isEmpty()) {
                directReports.stream().forEach(r -> stack.push(r.getEmployeeId()));
            }
            countOfReports++;
        }

        // subtracting 1 from the countOfReports, so we don't count the top level employee as
        // their own report.  Return max of zero or that count - 1 so that an employee with no reports
        // returns zero rather than -1.
        return Math.max(0, countOfReports - 1);
    }
}
