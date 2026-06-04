import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { ICourse } from 'app/entities/course/course.model';
import { CourseService } from 'app/entities/course/service/course.service';
import { IProfessor } from 'app/entities/professor/professor.model';
import { ProfessorService } from 'app/entities/professor/service/professor.service';
import { StudentGroupService } from 'app/entities/student-group/service/student-group.service';
import { IStudentGroup } from 'app/entities/student-group/student-group.model';
import { ICourseEvent } from '../course-event.model';
import { CourseEventService } from '../service/course-event.service';

import { CourseEventFormService } from './course-event-form.service';
import { CourseEventUpdate } from './course-event-update';

describe('CourseEvent Management Update Component', () => {
  let comp: CourseEventUpdate;
  let fixture: ComponentFixture<CourseEventUpdate>;
  let activatedRoute: ActivatedRoute;
  let courseEventFormService: CourseEventFormService;
  let courseEventService: CourseEventService;
  let courseService: CourseService;
  let professorService: ProfessorService;
  let studentGroupService: StudentGroupService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    });

    fixture = TestBed.createComponent(CourseEventUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    courseEventFormService = TestBed.inject(CourseEventFormService);
    courseEventService = TestBed.inject(CourseEventService);
    courseService = TestBed.inject(CourseService);
    professorService = TestBed.inject(ProfessorService);
    studentGroupService = TestBed.inject(StudentGroupService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Course query and add missing value', () => {
      const courseEvent: ICourseEvent = { id: 726 };
      const course: ICourse = { id: 2858 };
      courseEvent.course = course;

      const courseCollection: ICourse[] = [{ id: 2858 }];
      vitest.spyOn(courseService, 'query').mockReturnValue(of(new HttpResponse({ body: courseCollection })));
      const additionalCourses = [course];
      const expectedCollection: ICourse[] = [...additionalCourses, ...courseCollection];
      vitest.spyOn(courseService, 'addCourseToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ courseEvent });
      comp.ngOnInit();

      expect(courseService.query).toHaveBeenCalled();
      expect(courseService.addCourseToCollectionIfMissing).toHaveBeenCalledWith(
        courseCollection,
        ...additionalCourses.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.coursesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Professor query and add missing value', () => {
      const courseEvent: ICourseEvent = { id: 726 };
      const professor: IProfessor = { id: 2234 };
      courseEvent.professor = professor;

      const professorCollection: IProfessor[] = [{ id: 2234 }];
      vitest.spyOn(professorService, 'query').mockReturnValue(of(new HttpResponse({ body: professorCollection })));
      const additionalProfessors = [professor];
      const expectedCollection: IProfessor[] = [...additionalProfessors, ...professorCollection];
      vitest.spyOn(professorService, 'addProfessorToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ courseEvent });
      comp.ngOnInit();

      expect(professorService.query).toHaveBeenCalled();
      expect(professorService.addProfessorToCollectionIfMissing).toHaveBeenCalledWith(
        professorCollection,
        ...additionalProfessors.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.professorsSharedCollection()).toEqual(expectedCollection);
    });

    it('should call StudentGroup query and add missing value', () => {
      const courseEvent: ICourseEvent = { id: 726 };
      const studentGroup: IStudentGroup = { id: 20706 };
      courseEvent.studentGroup = studentGroup;

      const studentGroupCollection: IStudentGroup[] = [{ id: 20706 }];
      vitest.spyOn(studentGroupService, 'query').mockReturnValue(of(new HttpResponse({ body: studentGroupCollection })));
      const additionalStudentGroups = [studentGroup];
      const expectedCollection: IStudentGroup[] = [...additionalStudentGroups, ...studentGroupCollection];
      vitest.spyOn(studentGroupService, 'addStudentGroupToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ courseEvent });
      comp.ngOnInit();

      expect(studentGroupService.query).toHaveBeenCalled();
      expect(studentGroupService.addStudentGroupToCollectionIfMissing).toHaveBeenCalledWith(
        studentGroupCollection,
        ...additionalStudentGroups.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.studentGroupsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const courseEvent: ICourseEvent = { id: 726 };
      const course: ICourse = { id: 2858 };
      courseEvent.course = course;
      const professor: IProfessor = { id: 2234 };
      courseEvent.professor = professor;
      const studentGroup: IStudentGroup = { id: 20706 };
      courseEvent.studentGroup = studentGroup;

      activatedRoute.data = of({ courseEvent });
      comp.ngOnInit();

      expect(comp.coursesSharedCollection()).toContainEqual(course);
      expect(comp.professorsSharedCollection()).toContainEqual(professor);
      expect(comp.studentGroupsSharedCollection()).toContainEqual(studentGroup);
      expect(comp.courseEvent).toEqual(courseEvent);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICourseEvent>();
      const courseEvent = { id: 9540 };
      vitest.spyOn(courseEventFormService, 'getCourseEvent').mockReturnValue(courseEvent);
      vitest.spyOn(courseEventService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ courseEvent });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(courseEvent);
      saveSubject.complete();

      // THEN
      expect(courseEventFormService.getCourseEvent).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(courseEventService.update).toHaveBeenCalledWith(expect.objectContaining(courseEvent));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICourseEvent>();
      const courseEvent = { id: 9540 };
      vitest.spyOn(courseEventFormService, 'getCourseEvent').mockReturnValue({ id: null });
      vitest.spyOn(courseEventService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ courseEvent: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(courseEvent);
      saveSubject.complete();

      // THEN
      expect(courseEventFormService.getCourseEvent).toHaveBeenCalled();
      expect(courseEventService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ICourseEvent>();
      const courseEvent = { id: 9540 };
      vitest.spyOn(courseEventService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ courseEvent });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(courseEventService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareCourse', () => {
      it('should forward to courseService', () => {
        const entity = { id: 2858 };
        const entity2 = { id: 3722 };
        vitest.spyOn(courseService, 'compareCourse');
        comp.compareCourse(entity, entity2);
        expect(courseService.compareCourse).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareProfessor', () => {
      it('should forward to professorService', () => {
        const entity = { id: 2234 };
        const entity2 = { id: 26501 };
        vitest.spyOn(professorService, 'compareProfessor');
        comp.compareProfessor(entity, entity2);
        expect(professorService.compareProfessor).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareStudentGroup', () => {
      it('should forward to studentGroupService', () => {
        const entity = { id: 20706 };
        const entity2 = { id: 6236 };
        vitest.spyOn(studentGroupService, 'compareStudentGroup');
        comp.compareStudentGroup(entity, entity2);
        expect(studentGroupService.compareStudentGroup).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
