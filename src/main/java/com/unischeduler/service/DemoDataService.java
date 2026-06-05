package com.unischeduler.service;

import com.unischeduler.domain.*;
import com.unischeduler.domain.enumeration.AcademicDayOfWeek;
import com.unischeduler.domain.enumeration.CourseEventType;
import com.unischeduler.domain.enumeration.ProfessorPreferenceType;
import com.unischeduler.domain.enumeration.RoomType;
import com.unischeduler.repository.*;
import com.unischeduler.service.dto.DemoDataSummaryDTO;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Creates a deterministic dataset for the university timetabling demo.
 */
@Service
@Transactional
public class DemoDataService {

    private static final String PROJECTOR = "projector";
    private static final String WHITEBOARD = "whiteboard";
    private static final int EVENTS_PER_STUDENT_GROUP = 8;

    private final FacultyRepository facultyRepository;
    private final DepartmentRepository departmentRepository;
    private final BuildingRepository buildingRepository;
    private final RoomRepository roomRepository;
    private final ProfessorRepository professorRepository;
    private final StudentGroupRepository studentGroupRepository;
    private final CourseRepository courseRepository;
    private final CourseEventRepository courseEventRepository;
    private final TimeslotRepository timeslotRepository;
    private final ProfessorPreferenceRepository professorPreferenceRepository;
    private final SolverJobRepository solverJobRepository;
    private final TimetableRepository timetableRepository;
    private final TimetableVersionRepository timetableVersionRepository;
    private final TimetableEntryRepository timetableEntryRepository;
    private final ScheduleConflictRepository scheduleConflictRepository;
    private final ExamRepository examRepository;
    private final ExamScheduleEntryRepository examScheduleEntryRepository;

    public DemoDataService(
        FacultyRepository facultyRepository,
        DepartmentRepository departmentRepository,
        BuildingRepository buildingRepository,
        RoomRepository roomRepository,
        ProfessorRepository professorRepository,
        StudentGroupRepository studentGroupRepository,
        CourseRepository courseRepository,
        CourseEventRepository courseEventRepository,
        TimeslotRepository timeslotRepository,
        ProfessorPreferenceRepository professorPreferenceRepository,
        SolverJobRepository solverJobRepository,
        TimetableRepository timetableRepository,
        TimetableVersionRepository timetableVersionRepository,
        TimetableEntryRepository timetableEntryRepository,
        ScheduleConflictRepository scheduleConflictRepository,
        ExamRepository examRepository,
        ExamScheduleEntryRepository examScheduleEntryRepository
    ) {
        this.facultyRepository = facultyRepository;
        this.departmentRepository = departmentRepository;
        this.buildingRepository = buildingRepository;
        this.roomRepository = roomRepository;
        this.professorRepository = professorRepository;
        this.studentGroupRepository = studentGroupRepository;
        this.courseRepository = courseRepository;
        this.courseEventRepository = courseEventRepository;
        this.timeslotRepository = timeslotRepository;
        this.professorPreferenceRepository = professorPreferenceRepository;
        this.solverJobRepository = solverJobRepository;
        this.timetableRepository = timetableRepository;
        this.timetableVersionRepository = timetableVersionRepository;
        this.timetableEntryRepository = timetableEntryRepository;
        this.scheduleConflictRepository = scheduleConflictRepository;
        this.examRepository = examRepository;
        this.examScheduleEntryRepository = examScheduleEntryRepository;
    }

    public DemoDataSummaryDTO load() {
        clear();

        Faculty faculty = facultyRepository.save(new Faculty().name("Faculty of Computers, Informatics and Microelectronics").code("FCIM"));

        List<Department> departments = departmentRepository.saveAll(
            List.of(
                new Department().name("Software Engineering and Automation").code("FAF").faculty(faculty),
                new Department().name("Cybernetics and Informatics").code("CI").faculty(faculty)
            )
        );

        List<Building> buildings = createBuildings();
        List<Room> rooms = createRooms(buildings);
        List<Timeslot> timeslots = createTimeslots();
        List<Professor> professors = createProfessors(departments);
        List<StudentGroup> groups = createStudentGroups(departments);
        List<Course> courses = createCourses(departments);

        createCourseEvents(courses, professors, groups);
        createProfessorPreferences(professors, timeslots);
        createExams(courses, groups);

        return summary("Demo scenario for FAF department loaded successfully.");
    }

    public DemoDataSummaryDTO clear() {
        examScheduleEntryRepository.deleteAllInBatch();
        examRepository.deleteAllInBatch();
        scheduleConflictRepository.deleteAllInBatch();
        timetableEntryRepository.deleteAllInBatch();
        timetableVersionRepository.deleteAllInBatch();
        timetableRepository.deleteAllInBatch();
        solverJobRepository.deleteAllInBatch();
        professorPreferenceRepository.deleteAllInBatch();
        courseEventRepository.deleteAllInBatch();
        courseRepository.deleteAllInBatch();
        studentGroupRepository.deleteAllInBatch();
        professorRepository.deleteAllInBatch();
        roomRepository.deleteAllInBatch();
        timeslotRepository.deleteAllInBatch();
        departmentRepository.deleteAllInBatch();
        buildingRepository.deleteAllInBatch();
        facultyRepository.deleteAllInBatch();

        return summary("Demo scheduling data cleared.");
    }

    private List<Building> createBuildings() {
        return buildingRepository.saveAll(
            List.of(
                new Building().name("Main Academic Building").code("A").address("168 Stefan cel Mare Blvd"),
                new Building().name("Computing Laboratories Center").code("L").address("9 Studentilor Street"),
                new Building().name("Examination and Seminar Hall").code("E").address("2 Academiei Street")
            )
        );
    }

    private List<Room> createRooms(List<Building> buildings) {
        Building main = buildings.get(0);
        Building labs = buildings.get(1);
        Building exams = buildings.get(2);

        return roomRepository.saveAll(
            List.of(
                room("Aula 101", "A-101", 160, RoomType.LECTURE, "projector, audio, smart-board, whiteboard", main),
                room("Lecture Hall 203", "A-203", 120, RoomType.LECTURE, "projector, audio, whiteboard", main),
                room("Seminar Room 207", "A-207", 45, RoomType.SEMINAR, "projector, whiteboard", main),
                room("Seminar Room 309", "A-309", 40, RoomType.SEMINAR, "whiteboard, display", main),
                room("Programming Lab 1", "L-101", 34, RoomType.LABORATORY, "computers, linux, projector, whiteboard", labs),
                room("Programming Lab 2", "L-102", 32, RoomType.LABORATORY, "computers, linux, projector, whiteboard", labs),
                room("Networks Lab", "L-205", 36, RoomType.LABORATORY, "computers, network-lab, routers, whiteboard", labs),
                room("Electronics Lab", "L-310", 32, RoomType.LABORATORY, "computers, electronics-kit, oscilloscope, whiteboard", labs),
                room("Exam Hall Alpha", "E-100", 180, RoomType.EXAM, "projector, clock, whiteboard", exams),
                room("Exam Hall Beta", "E-200", 140, RoomType.EXAM, "projector, clock, whiteboard", exams)
            )
        );
    }

    private List<Timeslot> createTimeslots() {
        List<Timeslot> timeslots = new ArrayList<>();
        AcademicDayOfWeek[] weekdays = {
            AcademicDayOfWeek.MONDAY,
            AcademicDayOfWeek.TUESDAY,
            AcademicDayOfWeek.WEDNESDAY,
            AcademicDayOfWeek.THURSDAY,
            AcademicDayOfWeek.FRIDAY,
        };
        String[][] periods = {
            { "08:00", "09:30" },
            { "09:45", "11:15" },
            { "11:30", "13:00" },
            { "13:30", "15:00" },
            { "15:15", "16:45" },
            { "17:00", "18:30" },
        };

        for (AcademicDayOfWeek day : weekdays) {
            for (String[] period : periods) {
                timeslots.add(new Timeslot().dayOfWeek(day).startTime(period[0]).endTime(period[1]));
            }
        }

        return timeslotRepository.saveAll(timeslots);
    }

    private List<Professor> createProfessors(List<Department> departments) {
        Department faf = departments.get(0);
        Department ci = departments.get(1);

        return professorRepository.saveAll(
            List.of(
                professor("Adrian", "Rusu", "adrian.rusu@unischeduler.edu", "Associate Professor", faf),
                professor("Elena", "Ciobanu", "elena.ciobanu@unischeduler.edu", "Professor", faf),
                professor("Mihai", "Popescu", "mihai.popescu@unischeduler.edu", "Lecturer", faf),
                professor("Diana", "Ceban", "diana.ceban@unischeduler.edu", "Senior Lecturer", faf),
                professor("Victor", "Munteanu", "victor.munteanu@unischeduler.edu", "Professor", faf),
                professor("Irina", "Balan", "irina.balan@unischeduler.edu", "Associate Professor", faf),
                professor("Sergiu", "Matei", "sergiu.matei@unischeduler.edu", "Lecturer", faf),
                professor("Olesea", "Sandu", "olesea.sandu@unischeduler.edu", "Senior Lecturer", faf),
                professor("Natalia", "Grosu", "natalia.grosu@unischeduler.edu", "Professor", ci),
                professor("Andrei", "Lungu", "andrei.lungu@unischeduler.edu", "Associate Professor", ci),
                professor("Marina", "Toma", "marina.toma@unischeduler.edu", "Lecturer", ci),
                professor("Vlad", "Ionescu", "vlad.ionescu@unischeduler.edu", "Senior Lecturer", ci),
                professor("Ana", "Rotaru", "ana.rotaru@unischeduler.edu", "Associate Professor", ci),
                professor("Cristian", "Botezatu", "cristian.botezatu@unischeduler.edu", "Lecturer", ci),
                professor("Livia", "Dumitrescu", "livia.dumitrescu@unischeduler.edu", "Professor", ci),
                professor("Radu", "Moraru", "radu.moraru@unischeduler.edu", "Associate Professor", faf),
                professor("Tatiana", "Cojocaru", "tatiana.cojocaru@unischeduler.edu", "Senior Lecturer", faf),
                professor("Ion", "Postica", "ion.postica@unischeduler.edu", "Lecturer", faf),
                professor("Sanda", "Ursu", "sanda.ursu@unischeduler.edu", "Associate Professor", ci),
                professor("Eugen", "Ciobanu", "eugen.ciobanu@unischeduler.edu", "Professor", ci)
            )
        );
    }

    private List<StudentGroup> createStudentGroups(List<Department> departments) {
        Department faf = departments.get(0);
        Department ci = departments.get(1);

        return studentGroupRepository.saveAll(
            List.of(
                studentGroup("FAF-231", 1, 32, faf),
                studentGroup("FAF-232", 1, 34, faf),
                studentGroup("FAF-221", 2, 31, faf),
                studentGroup("FAF-222", 2, 29, faf),
                studentGroup("FAF-211", 3, 28, faf),
                studentGroup("FAF-212", 3, 27, faf),
                studentGroup("FAF-201", 4, 26, faf),
                studentGroup("SI-231", 1, 30, ci),
                studentGroup("SI-221", 2, 26, ci),
                studentGroup("SI-211", 3, 25, ci)
            )
        );
    }

    private List<Course> createCourses(List<Department> departments) {
        Department faf = departments.get(0);
        Department ci = departments.get(1);

        return courseRepository.saveAll(
            List.of(
                course("CS101", "Algorithms and Data Structures", 5, faf),
                course("CS102", "Object Oriented Programming", 5, faf),
                course("CS103", "Databases", 5, faf),
                course("CS104", "Operating Systems", 4, faf),
                course("CS105", "Computer Networks", 4, faf),
                course("CS106", "Web Engineering", 4, faf),
                course("CS107", "Software Architecture", 4, faf),
                course("CS108", "Artificial Intelligence", 5, faf),
                course("CS109", "Cybersecurity Fundamentals", 4, faf),
                course("CS110", "Cloud Computing", 4, faf),
                course("CS111", "Numerical Methods", 4, ci),
                course("CS112", "Human Computer Interaction", 4, faf),
                course("CS113", "Discrete Mathematics", 5, ci),
                course("CS114", "Probability and Statistics", 4, ci),
                course("CS115", "Requirements Engineering", 4, faf),
                course("CS116", "Software Testing", 4, faf),
                course("CS117", "Compiler Design", 5, faf),
                course("CS118", "Mobile Development", 4, faf),
                course("CS119", "DevOps and CI CD", 4, faf),
                course("CS120", "Parallel Computing", 5, ci),
                course("CS121", "Distributed Systems", 5, faf),
                course("CS122", "Microservices Engineering", 4, faf),
                course("CS123", "Data Mining", 4, ci),
                course("CS124", "Machine Learning Engineering", 5, ci),
                course("CS125", "Information Systems Security", 4, faf),
                course("CS126", "Advanced Java Programming", 4, faf),
                course("CS127", "Computer Graphics", 4, ci),
                course("CS128", "Enterprise Application Integration", 4, faf),
                course("CS129", "Data Warehousing", 4, ci),
                course("CS130", "Project Management for Software Teams", 3, faf)
            )
        );
    }

    private void createCourseEvents(List<Course> courses, List<Professor> professors, List<StudentGroup> groups) {
        List<CourseEvent> events = new ArrayList<>();

        for (int groupIndex = 0; groupIndex < groups.size(); groupIndex++) {
            StudentGroup group = groups.get(groupIndex);
            for (int eventIndex = 0; eventIndex < EVENTS_PER_STUDENT_GROUP; eventIndex++) {
                int courseIndex = (groupIndex * EVENTS_PER_STUDENT_GROUP + eventIndex) % courses.size();
                Course course = courses.get(courseIndex);
                Professor professor = professors.get((groupIndex * EVENTS_PER_STUDENT_GROUP + eventIndex) % professors.size());
                CourseEventType type = eventTypeFor(eventIndex);
                String requiredEquipment = requiredEquipmentFor(type, labRequirementFor(group, groupIndex, eventIndex));
                events.add(
                    new CourseEvent()
                        .course(course)
                        .professor(professor)
                        .studentGroup(group)
                        .eventType(type)
                        .durationMinutes(90)
                        .expectedStudents(group.getGroupSize())
                        .requiredEquipment(requiredEquipment)
                );
            }
        }

        courseEventRepository.saveAll(events);
    }

    private void createProfessorPreferences(List<Professor> professors, List<Timeslot> timeslots) {
        List<ProfessorPreference> preferences = new ArrayList<>();
        AcademicDayOfWeek[] weekdays = {
            AcademicDayOfWeek.MONDAY,
            AcademicDayOfWeek.TUESDAY,
            AcademicDayOfWeek.WEDNESDAY,
            AcademicDayOfWeek.THURSDAY,
            AcademicDayOfWeek.FRIDAY,
        };

        for (int index = 0; index < professors.size(); index++) {
            Professor professor = professors.get(index);
            AcademicDayOfWeek preferredDay = weekdays[index % weekdays.length];
            AcademicDayOfWeek avoidDay = weekdays[(index + 2) % weekdays.length];

            preferences.add(preference(professor, timeslotAt(timeslots, preferredDay, "09:45"), ProfessorPreferenceType.PREFERRED));
            preferences.add(preference(professor, timeslotAt(timeslots, avoidDay, "08:00"), ProfessorPreferenceType.AVOID));
            preferences.add(
                preference(professor, timeslotAt(timeslots, AcademicDayOfWeek.FRIDAY, "17:00"), ProfessorPreferenceType.UNAVAILABLE)
            );
        }

        professorPreferenceRepository.saveAll(preferences);
    }

    private void createExams(List<Course> courses, List<StudentGroup> groups) {
        List<Exam> exams = new ArrayList<>();

        for (int index = 0; index < courses.size(); index++) {
            Course course = courses.get(index);
            StudentGroup group = groups.get(index % groups.size());
            exams.add(
                new Exam()
                    .name(course.getName() + " Final Exam")
                    .durationMinutes(120)
                    .expectedStudents(group.getGroupSize())
                    .course(course)
                    .studentGroup(group)
            );
        }

        examRepository.saveAll(exams);
    }

    private Room room(String name, String code, int capacity, RoomType type, String equipment, Building building) {
        return new Room().name(name).code(code).capacity(capacity).roomType(type).equipment(equipment).building(building);
    }

    private Professor professor(String firstName, String lastName, String email, String title, Department department) {
        return new Professor().firstName(firstName).lastName(lastName).email(email).title(title).department(department);
    }

    private StudentGroup studentGroup(String name, int year, int groupSize, Department department) {
        return new StudentGroup().name(name).year(year).groupSize(groupSize).department(department);
    }

    private Course course(String code, String name, int credits, Department department) {
        return new Course().code(code).name(name).credits(credits).department(department);
    }

    private CourseEventType eventTypeFor(int eventIndex) {
        return switch (eventIndex % EVENTS_PER_STUDENT_GROUP) {
            case 2, 7 -> CourseEventType.LABORATORY;
            case 1, 4, 6 -> CourseEventType.SEMINAR;
            default -> CourseEventType.LECTURE;
        };
    }

    private String labRequirementFor(StudentGroup group, int groupIndex, int eventIndex) {
        String[] standardRequirements = { "computers", "linux", "network-lab", "electronics-kit" };
        String[] largeGroupRequirements = { "computers", "linux", "network-lab" };
        Integer groupSize = group.getGroupSize();
        String[] requirements = groupSize != null && groupSize > 32 ? largeGroupRequirements : standardRequirements;

        return requirements[Math.floorMod(groupIndex + eventIndex, requirements.length)];
    }

    private String requiredEquipmentFor(CourseEventType type, String labRequirement) {
        if (type == CourseEventType.LABORATORY) {
            return labRequirement;
        }
        if (type == CourseEventType.SEMINAR) {
            return WHITEBOARD;
        }
        return PROJECTOR;
    }

    private ProfessorPreference preference(Professor professor, Timeslot timeslot, ProfessorPreferenceType type) {
        return new ProfessorPreference().professor(professor).timeslot(timeslot).preferenceType(type);
    }

    private Timeslot timeslotAt(List<Timeslot> timeslots, AcademicDayOfWeek day, String startTime) {
        return timeslots
            .stream()
            .filter(timeslot -> day == timeslot.getDayOfWeek() && startTime.equals(timeslot.getStartTime()))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Missing demo timeslot " + day + " " + startTime));
    }

    private DemoDataSummaryDTO summary(String message) {
        return new DemoDataSummaryDTO(
            facultyRepository.count(),
            departmentRepository.count(),
            buildingRepository.count(),
            roomRepository.count(),
            professorRepository.count(),
            studentGroupRepository.count(),
            courseRepository.count(),
            courseEventRepository.count(),
            timeslotRepository.count(),
            professorPreferenceRepository.count(),
            examRepository.count(),
            message
        );
    }
}
