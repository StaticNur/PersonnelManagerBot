package com.codemastersTournament.PersonnelManagerBot.service.impl;

import com.codemastersTournament.PersonnelManagerBot.models.Employee;
import com.codemastersTournament.PersonnelManagerBot.repository.EmployeeRepository;
import com.codemastersTournament.PersonnelManagerBot.utils.Consts;
import jakarta.ws.rs.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public Employee addEmployee(String info){
        Employee employee = new Employee();
        String[] lines = info.trim().split("\n");
        if(lines.length == 1){//Иванов Иван|дизайнер|магазин курток
            String[] str = info.split("\\|");
            String[] fullName = str[0].split(" ");
            /*if (StringUtils.containsAny(str, "0123456789")) {
                System.out.println("Строка содержит числа.");
            } else {
                System.out.println("Строка не содержит числа.");
            }*/
            employee.setName(fullName[1]);
            employee.setLastName(fullName[0]);
            employee.setPosition(str[1]);
            employee.setProject(str[2]);
        }else if(lines.length > 1){
            String[] fullName = lines[0].split(" ");
            employee.setName(fullName[1]);
            employee.setLastName(fullName[0]);
            employee.setPosition(lines[1]);
            employee.setProject(lines[2]);
        }
        Instant instant = Instant.now();
        Timestamp timestamp = Timestamp.from(instant);
        employee.setArrivalDate(timestamp);

       //фото по умолчанию
        String base64Data = "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";
        byte[] imageBytes = Base64.getDecoder().decode(base64Data);
        employee.setAvatar(imageBytes);

        return employeeRepository.save(employee);
    }
    @Transactional
    public void deleteEmployee(Employee employee){
        employeeRepository.delete(employee);
    }
    public Employee searchEmployeeById(Long id){
        return employeeRepository.findById(id).orElse(null);
    }
    public List<Employee> searchEmployeeByPosition(String position){
        return employeeRepository.findByPositionIgnoreCase(position.trim());
    }
    public List<Employee> searchEmployeeByProject(String project){
        return employeeRepository.findByProjectIgnoreCase(project.trim());
    }
    public List<Employee> searchEmployeeByDate(String interval){
        String[] twoDate = interval.trim().split("-");
        //SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

        System.out.println(Arrays.asList(twoDate));
        System.out.println(twoDate[0].trim());
        System.out.println(twoDate[1].trim());

        Instant instant = Instant.now();
        Date date1 = Date.from(instant);
        Date date2 = Date.from(instant);

        String[] date11 = twoDate[0].split(".");
        date1.setDate(Integer.parseInt(date11[0]));
        date1.setMonth(Integer.parseInt(date11[1]));
        date1.setYear(Integer.parseInt(date11[2]));
        System.out.println(date1);
        Date from = date1;

        String[] date22 = twoDate[1].split(".");
        date2.setDate(Integer.parseInt(date22[0]));
        date2.setMonth(Integer.parseInt(date22[1]));
        date2.setYear(Integer.parseInt(date22[2]));
        Date to = date2;

        return employeeRepository.findByArrivalDateIsAfterAndArrivalDateIsBefore((Timestamp) from, (Timestamp) to);
    }
    public List<Employee> viewAll(){
        return employeeRepository.findAll();
    }
    public List<Employee> searchEmployeeByFirstOrLastName(String firstOrLastName){
        String[] fullName = firstOrLastName.trim().split(" ");
        List<Employee> result = new ArrayList<>();
        if(fullName.length == 2){//Иванов Иван
            result = employeeRepository.findByNameIgnoreCaseAndLastNameIgnoreCase(fullName[1],fullName[0]);
        }else if(fullName.length == 1) {
            result = employeeRepository.findByNameIgnoreCaseOrLastNameIgnoreCase(firstOrLastName.trim(),firstOrLastName.trim());
        }
        if(result.isEmpty()){
            throw new NotFoundException();
        }
        return result;
    }

    @Transactional
    public void editEmployeeName(Long idEmployee, String name){
        Employee employee = employeeRepository.findById(idEmployee).orElse(null);
        if (employee != null){
            employee.setName(name);
        }
        employeeRepository.save(employee);
    }
    @Transactional
    public void editEmployeePatronymic(Long idEmployee, String patronymic){
        Employee employee = employeeRepository.findById(idEmployee).orElse(null);
        if (employee != null){
            employee.setPatronymic(patronymic);
        }
        employeeRepository.save(employee);
    }
    @Transactional
    public void editEmployeeLastName(Long idEmployee, String lastName){
        Employee employee = employeeRepository.findById(idEmployee).orElse(null);
        if (employee != null){
            employee.setLastName(lastName);
        }
        employeeRepository.save(employee);
    }
    @Transactional
    public void editEmployeePosition(Long idEmployee, String position){
        Employee employee = employeeRepository.findById(idEmployee).orElse(null);
        if (employee != null){
            employee.setPosition(position);
        }
        employeeRepository.save(employee);
    }
    @Transactional
    public void editEmployeeProject(Long idEmployee, String project){
        Employee employee = employeeRepository.findById(idEmployee).orElse(null);
        if (employee != null){
            employee.setProject(project);
        }
        employeeRepository.save(employee);
    }
    @Transactional
    public void editEmployeeAvatar(Long idEmployee, byte[] avatar){
        Employee employee = employeeRepository.findById(idEmployee).orElse(null);
        employee.setAvatar(avatar);
        employeeRepository.save(employee);
    }
}
