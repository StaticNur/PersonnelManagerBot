package com.codemastersTournament.PersonnelManagerBot.repository;

import com.codemastersTournament.PersonnelManagerBot.models.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByNameIgnoreCaseAndLastNameIgnoreCase(String name, String lastName);
    List<Employee> findByNameIgnoreCaseOrLastNameIgnoreCase(String name, String lastName);
    List<Employee> findByPositionIgnoreCase(String position);
    List<Employee> findByProjectIgnoreCase(String project);
    List<Employee> findByArrivalDateIsAfterAndArrivalDateIsBefore(Timestamp arrivalDate, Timestamp arrivalDate2);
}
