import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { ICourse } from 'app/entities/course/course.model';
import { CourseService } from 'app/entities/course/service/course.service';
import { StudentGroupService } from 'app/entities/student-group/service/student-group.service';
import { IStudentGroup } from 'app/entities/student-group/student-group.model';
import { IExam } from '../exam.model';
import { ExamService } from '../service/exam.service';

import { ExamFormService } from './exam-form.service';
import { ExamUpdate } from './exam-update';

describe('Exam Management Update Component', () => {
  let comp: ExamUpdate;
  let fixture: ComponentFixture<ExamUpdate>;
  let activatedRoute: ActivatedRoute;
  let examFormService: ExamFormService;
  let examService: ExamService;
  let courseService: CourseService;
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

    fixture = TestBed.createComponent(ExamUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    examFormService = TestBed.inject(ExamFormService);
    examService = TestBed.inject(ExamService);
    courseService = TestBed.inject(CourseService);
    studentGroupService = TestBed.inject(StudentGroupService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Course query and add missing value', () => {
      const exam: IExam = { id: 13366 };
      const course: ICourse = { id: 2858 };
      exam.course = course;

      const courseCollection: ICourse[] = [{ id: 2858 }];
      vitest.spyOn(courseService, 'query').mockReturnValue(of(new HttpResponse({ body: courseCollection })));
      const additionalCourses = [course];
      const expectedCollection: ICourse[] = [...additionalCourses, ...courseCollection];
      vitest.spyOn(courseService, 'addCourseToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ exam });
      comp.ngOnInit();

      expect(courseService.query).toHaveBeenCalled();
      expect(courseService.addCourseToCollectionIfMissing).toHaveBeenCalledWith(
        courseCollection,
        ...additionalCourses.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.coursesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call StudentGroup query and add missing value', () => {
      const exam: IExam = { id: 13366 };
      const studentGroup: IStudentGroup = { id: 20706 };
      exam.studentGroup = studentGroup;

      const studentGroupCollection: IStudentGroup[] = [{ id: 20706 }];
      vitest.spyOn(studentGroupService, 'query').mockReturnValue(of(new HttpResponse({ body: studentGroupCollection })));
      const additionalStudentGroups = [studentGroup];
      const expectedCollection: IStudentGroup[] = [...additionalStudentGroups, ...studentGroupCollection];
      vitest.spyOn(studentGroupService, 'addStudentGroupToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ exam });
      comp.ngOnInit();

      expect(studentGroupService.query).toHaveBeenCalled();
      expect(studentGroupService.addStudentGroupToCollectionIfMissing).toHaveBeenCalledWith(
        studentGroupCollection,
        ...additionalStudentGroups.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.studentGroupsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const exam: IExam = { id: 13366 };
      const course: ICourse = { id: 2858 };
      exam.course = course;
      const studentGroup: IStudentGroup = { id: 20706 };
      exam.studentGroup = studentGroup;

      activatedRoute.data = of({ exam });
      comp.ngOnInit();

      expect(comp.coursesSharedCollection()).toContainEqual(course);
      expect(comp.studentGroupsSharedCollection()).toContainEqual(studentGroup);
      expect(comp.exam).toEqual(exam);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IExam>();
      const exam = { id: 15727 };
      vitest.spyOn(examFormService, 'getExam').mockReturnValue(exam);
      vitest.spyOn(examService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ exam });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(exam);
      saveSubject.complete();

      // THEN
      expect(examFormService.getExam).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(examService.update).toHaveBeenCalledWith(expect.objectContaining(exam));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IExam>();
      const exam = { id: 15727 };
      vitest.spyOn(examFormService, 'getExam').mockReturnValue({ id: null });
      vitest.spyOn(examService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ exam: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(exam);
      saveSubject.complete();

      // THEN
      expect(examFormService.getExam).toHaveBeenCalled();
      expect(examService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IExam>();
      const exam = { id: 15727 };
      vitest.spyOn(examService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ exam });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(examService.update).toHaveBeenCalled();
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
