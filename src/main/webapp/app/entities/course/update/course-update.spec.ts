import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IDepartment } from 'app/entities/department/department.model';
import { DepartmentService } from 'app/entities/department/service/department.service';
import { ICourse } from '../course.model';
import { CourseService } from '../service/course.service';

import { CourseFormService } from './course-form.service';
import { CourseUpdate } from './course-update';

describe('Course Management Update Component', () => {
  let comp: CourseUpdate;
  let fixture: ComponentFixture<CourseUpdate>;
  let activatedRoute: ActivatedRoute;
  let courseFormService: CourseFormService;
  let courseService: CourseService;
  let departmentService: DepartmentService;

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

    fixture = TestBed.createComponent(CourseUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    courseFormService = TestBed.inject(CourseFormService);
    courseService = TestBed.inject(CourseService);
    departmentService = TestBed.inject(DepartmentService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Department query and add missing value', () => {
      const course: ICourse = { id: 3722 };
      const department: IDepartment = { id: 29518 };
      course.department = department;

      const departmentCollection: IDepartment[] = [{ id: 29518 }];
      vitest.spyOn(departmentService, 'query').mockReturnValue(of(new HttpResponse({ body: departmentCollection })));
      const additionalDepartments = [department];
      const expectedCollection: IDepartment[] = [...additionalDepartments, ...departmentCollection];
      vitest.spyOn(departmentService, 'addDepartmentToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ course });
      comp.ngOnInit();

      expect(departmentService.query).toHaveBeenCalled();
      expect(departmentService.addDepartmentToCollectionIfMissing).toHaveBeenCalledWith(
        departmentCollection,
        ...additionalDepartments.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.departmentsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const course: ICourse = { id: 3722 };
      const department: IDepartment = { id: 29518 };
      course.department = department;

      activatedRoute.data = of({ course });
      comp.ngOnInit();

      expect(comp.departmentsSharedCollection()).toContainEqual(department);
      expect(comp.course).toEqual(course);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICourse>();
      const course = { id: 2858 };
      vitest.spyOn(courseFormService, 'getCourse').mockReturnValue(course);
      vitest.spyOn(courseService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ course });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(course);
      saveSubject.complete();

      // THEN
      expect(courseFormService.getCourse).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(courseService.update).toHaveBeenCalledWith(expect.objectContaining(course));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICourse>();
      const course = { id: 2858 };
      vitest.spyOn(courseFormService, 'getCourse').mockReturnValue({ id: null });
      vitest.spyOn(courseService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ course: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(course);
      saveSubject.complete();

      // THEN
      expect(courseFormService.getCourse).toHaveBeenCalled();
      expect(courseService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ICourse>();
      const course = { id: 2858 };
      vitest.spyOn(courseService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ course });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(courseService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareDepartment', () => {
      it('should forward to departmentService', () => {
        const entity = { id: 29518 };
        const entity2 = { id: 15970 };
        vitest.spyOn(departmentService, 'compareDepartment');
        comp.compareDepartment(entity, entity2);
        expect(departmentService.compareDepartment).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
