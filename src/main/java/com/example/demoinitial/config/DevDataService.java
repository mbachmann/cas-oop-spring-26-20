package com.example.demoinitial.config;

import com.example.demoinitial.domain.*;
import com.example.demoinitial.repository.DepartmentRepository;
import com.example.demoinitial.repository.EmployeeRepository;
import com.example.demoinitial.repository.PersonRepository;
import com.example.demoinitial.repository.ProjectRepository;
import com.example.demoinitial.utils.HasLogger;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Transactional service responsible for seeding development data into the database.
 *
 * <p>This service is only active under the {@code dev} Spring profile and is designed
 * to be idempotent: it checks for the existence of each entity before creating it,
 * making it safe to run against persistent databases (e.g., MySQL) on every application start.</p>
 *
 * <h3>Seeded data includes:</h3>
 * <ul>
 *   <li>Persons (Felix Muster, Max Mustermann, John Doe)</li>
 *   <li>Employees with addresses and phone numbers</li>
 *   <li>Departments (HR, Dev)</li>
 *   <li>Projects (Design: Arcos, Quality: My Quality Project)</li>
 *   <li>Chef/employee relationships</li>
 *   <li>Department assignments</li>
 * </ul>
 *
 * @see DevDataInitializer
 */
@Service
@Profile("dev")
@Transactional
public class DevDataService implements HasLogger {

    @Autowired
    @Qualifier("felixMuster")
    Person felixMuster;

    @Autowired
    @Qualifier("maxMustermann")
    Person maxMustermann;

    @Autowired
    PersonRepository personRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    ProjectRepository projectRepository;

    /**
     * Single transactional entry point for all seed data.
     * Only creates data if it doesn't already exist in the database.
     */
    public void createData() {

        createPersonData();
        Employee employeeFelixMuster = createEmployeeFelixMuster();
        createEmployeeJohnDoe();

        createDepartment();
        createPhone(employeeFelixMuster.getId());

        createDesignProject();
        createQualityProject();

        Employee employeeMaxMustermann = createEmployeeMaxMustermann();
        assignEmployeeToDesignProject(employeeMaxMustermann);

        // Assign Max Mustermann as chef to Felix Muster
        assignEmployeeAsChef(employeeMaxMustermann, List.of(employeeFelixMuster));

        assignEmployeeToDepartment(employeeMaxMustermann, "HR");
    }

    /**
     * Seeds person data. Creates Felix Muster, Max Mustermann, and John Doe
     * if they do not already exist in the database.
     */
    private void createPersonData() {
        createPersonIfNotExists(felixMuster);
        createPersonIfNotExists(maxMustermann);

        getLogger().debug("Person felixMuster and maxMustermann saved to DB");

        Person johnDoe = new Person();
        johnDoe.setFirstName("John");
        johnDoe.setLastName("Doe");
        createPersonIfNotExists(johnDoe);

        List<Person> persons = personRepository.findQueryByLastName("Mustermann");
        persons.forEach(p -> getLogger().debug("findQueryByLastName Mustermann = " + p));

        personRepository
                .findAll(Sort.by(Sort.Direction.ASC, "lastName"))
                .forEach(p -> personRepository.findById(p.getId())
                                              .ifPresent(found -> getLogger().debug("findAll, Sort by lastName ASC = " + found)));
    }

    /**
     * Saves a {@link Person} to the database only if no person with the same
     * first and last name already exists.
     *
     * @param person the person to persist if not already present
     */
    private void createPersonIfNotExists(Person person) {
        if (personRepository.findByFirstNameAndLastName(person.getFirstName(), person.getLastName()).isEmpty()) {
            personRepository.save(person);
        }
    }

    /**
     * Creates the employee "Felix Muster" with a Zürich address if not already present.
     *
     * @return the existing or newly created {@link Employee}
     */
    private Employee createEmployeeFelixMuster() {
        // Check if Felix Muster already exists
        List<Employee> existing = employeeRepository.findAllWithEagerRelationships();
        if (existing.stream().anyMatch(e -> "Felix Muster".equals(e.getName()))) {
            getLogger().debug("Employee Felix Muster already exists in database.");
            return existing.stream().filter(e -> "Felix Muster".equals(e.getName())).findFirst().orElse(null);
        }

        Address address = new Address();
        address.setCity("Zürich");
        address.setState("ZH");
        address.setStreet("Lagerstrasse 41");
        address.setZip("8004");

        Employee emp = new Employee();
        emp.setAddress(address);
        emp.setName("Felix Muster");
        emp.setSalary(80000);
        Employee saved = employeeRepository.save(emp);
        getLogger().debug("Employee Felix Muster created in database.");
        return saved;
    }

    /**
     * Creates the employee "John Doe" with a Basel address and a work phone
     * if not already present in the database.
     */
    private void createEmployeeJohnDoe() {
        // Check if John Doe already exists
        List<Employee> existing = employeeRepository.findAllWithEagerRelationships();
        if (existing.stream().anyMatch(e -> "John Doe".equals(e.getName()))) {
            getLogger().debug("Employee John Doe already exists in database.");
            return;
        }

        Address address = new Address();
        address.setCity("Basel");
        address.setState("BS");
        address.setStreet("Peter-Merianstrasse 41");
        address.setZip("4001");

        Phone phoneWork = new Phone();
        phoneWork.setPhonenumber("061 123 45 67");
        phoneWork.setType("Work");

        Employee emp = new Employee();
        emp.setAddress(address);
        emp.getPhones().add(phoneWork);
        emp.setName("John Doe");
        emp.setSalary(100000);
        phoneWork.setEmployee(emp);
        employeeRepository.save(emp);
        getLogger().debug("Employee John Doe created in database.");
    }

    /**
     * Creates the employee "Max Mustermann" with an Aarau address and a work phone
     * if not already present in the database.
     *
     * @return the existing or newly created {@link Employee}
     */
    private Employee createEmployeeMaxMustermann() {
        // Check if Max Mustermann already exists
        List<Employee> existing = employeeRepository.findAllWithEagerRelationships();
        if (existing.stream().anyMatch(e -> "Max Mustermann".equals(e.getName()))) {
            getLogger().debug("Employee Max Mustermann already exists in database.");
            return existing.stream().filter(e -> "Max Mustermann".equals(e.getName())).findFirst().orElse(null);
        }

        Address address = new Address();
        address.setCity("Aarau");
        address.setState("AG");
        address.setStreet("Bahnhofstrasse 41");
        address.setZip("5001");

        Phone phoneWork = new Phone();
        phoneWork.setPhonenumber("062 888 45 67");
        phoneWork.setType("Work");

        Employee emp = new Employee();
        emp.setAddress(address);
        emp.getPhones().add(phoneWork);
        emp.setName("Max Mustermann");
        emp.setSalary(90000);
        phoneWork.setEmployee(emp);
        Employee saved = employeeRepository.save(emp);
        getLogger().debug("Employee Max Mustermann created in database.");
        return saved;
    }

    /**
     * Adds a work phone (031 999 99 99) and a home phone (032 333 33 33) to the employee
     * with the given ID, if those phone numbers are not already assigned.
     *
     * @param empId the ID of the employee to add phones to
     */
    private void createPhone(Long empId) {
        employeeRepository.findByIdWithEagerRelationships(empId).ifPresent(emp -> {
            // Check if this employee already has both work and home phones
            boolean hasWorkPhone = emp.getPhones().stream()
                    .anyMatch(p -> "031 999 99 99".equals(p.getPhonenumber()));
            boolean hasHomePhone = emp.getPhones().stream()
                    .anyMatch(p -> "032 333 33 33".equals(p.getPhonenumber()));

            if (hasWorkPhone && hasHomePhone) {
                getLogger().debug("Employee " + emp.getName() + " already has all phones. Skipping phone creation.");
                return;
            }

            if (!hasWorkPhone) {
                Phone phoneWork = new Phone();
                phoneWork.setPhonenumber("031 999 99 99");
                phoneWork.setType("Work");
                phoneWork.setEmployee(emp);
                emp.getPhones().add(phoneWork);
            }

            if (!hasHomePhone) {
                Phone phoneHome = new Phone();
                phoneHome.setPhonenumber("032 333 33 33");
                phoneHome.setType("Home");
                phoneHome.setEmployee(emp);
                emp.getPhones().add(phoneHome);
            }

            employeeRepository.save(emp);
            getLogger().debug("Phones created/updated for employee " + emp.getName());
        });
    }

    /**
     * Creates the "HR" and "Dev" departments if no departments exist yet,
     * and assigns all unassigned employees to the "Dev" department.
     */
    private void createDepartment() {
        // Check if departments already exist
        long deptCount = departmentRepository.count();
        if (deptCount > 0) {
            getLogger().debug("Departments already exist in database. Skipping department creation.");
            return;
        }

        Department departmentHR = new Department();
        departmentHR.setName("HR");
        departmentRepository.save(departmentHR);

        Department department = new Department();
        department.setName("Dev");
        Department departmentDev = departmentRepository.save(department);

        employeeRepository.findAllWithEagerRelationships().forEach(emp -> {
            // Only assign if not already assigned to a department
            if (emp.getDepartment() == null) {
                departmentDev.getEmployees().add(emp);
                emp.setDepartment(departmentDev);
                employeeRepository.save(emp);
            }
        });

        departmentRepository.findByIdWithEagerRelationships(departmentDev.getId()).ifPresent(dep ->
                dep.getEmployees().forEach(e -> getLogger().info("Department " + dep.getName() + " - " + e)));
    }

    /**
     * Creates the {@link DesignProject} "Arcos" with innovation level 3
     * and assigns all employees not yet part of this project to it.
     * Skips creation if the project already exists in the database.
     */
    private void createDesignProject() {
        // Check if Arcos project already exists
        List<?> existing = projectRepository.findWithEagerProjectByName("Arcos");
        if (!existing.isEmpty()) {
            getLogger().debug("Design Project 'Arcos' already exists in database.");
            return;
        }

        DesignProject project = new DesignProject("Arcos");
        project.setInnovationLevel(3);

        employeeRepository.findAllWithEagerRelationships().forEach(emp -> {
            // Only add if not already in project
            if (emp.getProjects().stream().noneMatch(p -> "Arcos".equals(p.getName()))) {
                project.getEmployees().add(emp);
                emp.getProjects().add(project);
            }
        });
        projectRepository.save(project);
        getLogger().debug("Design Project 'Arcos' created in database.");
    }

    /**
     * Creates the {@link QualityProject} "My Quality Project" and assigns all employees
     * not yet part of this project to it.
     * Skips creation if the project already exists in the database.
     */
    private void createQualityProject() {
        // Check if My Quality Project already exists
        List<?> existing = projectRepository.findWithEagerProjectByName("My Quality Project");
        if (!existing.isEmpty()) {
            getLogger().debug("Quality Project 'My Quality Project' already exists in database.");
            return;
        }

        QualityProject project = new QualityProject("My Quality Project");

        employeeRepository.findAllWithEagerRelationships().forEach(emp -> {
            // Only add if not already in project
            if (emp.getProjects().stream().noneMatch(p -> "My Quality Project".equals(p.getName()))) {
                project.getEmployees().add(emp);
                emp.getProjects().add(project);
            }
        });
        projectRepository.save(project);
        getLogger().debug("Quality Project 'My Quality Project' created in database.");
    }

    /**
     * Assigns the given employee to the design project "Arcos".
     * Only assigns if the employee is not already part of the project.
     *
     * @param employee the employee to assign to the Arcos project
     */
    private void assignEmployeeToDesignProject(Employee employee) {
        projectRepository.findWithEagerProjectByName("Arcos").forEach(project -> {
            // Check if already assigned
            if (!project.getEmployees().contains(employee)) {
                project.getEmployees().add(employee);
                employee.getProjects().add(project);
                projectRepository.save(project);
                getLogger().debug(employee.getName() + " assigned to project Arcos.");
            } else {
                getLogger().debug(employee.getName() + " already assigned to project Arcos.");
            }
        });
    }

    /**
     * Assigns the given chef to one or more employees.
     * Only assigns if an employee does not already have a chef.
     *
     * @param chef      the employee to be assigned as chef
     * @param employees the list of employees who should report to this chef
     */
    private void assignEmployeeAsChef(Employee chef, List<Employee> employees) {
        employees.forEach(employee -> {
            // Check if not already assigned a chef
            if (employee.getChef() == null) {
                employee.setChef(chef);
                chef.getEmployees().add(employee);
                employeeRepository.save(employee);
                getLogger().debug(chef.getName() + " assigned as chef to " + employee.getName());
            } else {
                getLogger().debug(employee.getName() + " already has a chef assigned: " + employee.getChef().getName());
            }
        });
    }

    /**
     * Assigns the given employee to a department identified by name.
     * Only assigns if the employee is not already in the specified department.
     *
     * <p>This method is public and can be called externally to reassign
     * any employee to any existing department.</p>
     *
     * @param employee       the employee to assign
     * @param departmentName the name of the target department (e.g., "HR", "Dev")
     */
    public void assignEmployeeToDepartment(Employee employee, String departmentName) {
        departmentRepository.findAll().stream()
                            .filter(dept -> departmentName.equals(dept.getName()))
                            .findFirst()
                            .ifPresent(department -> {
                                // Check if employee is not already in this department
                                if (employee.getDepartment() == null || !department.getId().equals(employee.getDepartment().getId())) {
                                    employee.setDepartment(department);
                                    department.getEmployees().add(employee);
                                    employeeRepository.save(employee);
                                    getLogger().info("Employee '" + employee.getName() + "' added to department '" + departmentName + "'.");
                                } else {
                                    getLogger().debug("Employee '" + employee.getName() + "' is already in department '" + departmentName + "'.");
                                }
                            });
    }

}

