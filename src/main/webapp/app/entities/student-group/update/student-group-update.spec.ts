import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IDepartment } from 'app/entities/department/department.model';
import { DepartmentService } from 'app/entities/department/service/department.service';
import { StudentGroupService } from '../service/student-group.service';
import { IStudentGroup } from '../student-group.model';

import { StudentGroupFormService } from './student-group-form.service';
import { StudentGroupUpdate } from './student-group-update';

describe('StudentGroup Management Update Component', () => {
  let comp: StudentGroupUpdate;
  let fixture: ComponentFixture<StudentGroupUpdate>;
  let activatedRoute: ActivatedRoute;
  let studentGroupFormService: StudentGroupFormService;
  let studentGroupService: StudentGroupService;
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

    fixture = TestBed.createComponent(StudentGroupUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    studentGroupFormService = TestBed.inject(StudentGroupFormService);
    studentGroupService = TestBed.inject(StudentGroupService);
    departmentService = TestBed.inject(DepartmentService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Department query and add missing value', () => {
      const studentGroup: IStudentGroup = { id: 6236 };
      const department: IDepartment = { id: 29518 };
      studentGroup.department = department;

      const departmentCollection: IDepartment[] = [{ id: 29518 }];
      vitest.spyOn(departmentService, 'query').mockReturnValue(of(new HttpResponse({ body: departmentCollection })));
      const additionalDepartments = [department];
      const expectedCollection: IDepartment[] = [...additionalDepartments, ...departmentCollection];
      vitest.spyOn(departmentService, 'addDepartmentToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ studentGroup });
      comp.ngOnInit();

      expect(departmentService.query).toHaveBeenCalled();
      expect(departmentService.addDepartmentToCollectionIfMissing).toHaveBeenCalledWith(
        departmentCollection,
        ...additionalDepartments.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.departmentsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const studentGroup: IStudentGroup = { id: 6236 };
      const department: IDepartment = { id: 29518 };
      studentGroup.department = department;

      activatedRoute.data = of({ studentGroup });
      comp.ngOnInit();

      expect(comp.departmentsSharedCollection()).toContainEqual(department);
      expect(comp.studentGroup).toEqual(studentGroup);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IStudentGroup>();
      const studentGroup = { id: 20706 };
      vitest.spyOn(studentGroupFormService, 'getStudentGroup').mockReturnValue(studentGroup);
      vitest.spyOn(studentGroupService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ studentGroup });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(studentGroup);
      saveSubject.complete();

      // THEN
      expect(studentGroupFormService.getStudentGroup).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(studentGroupService.update).toHaveBeenCalledWith(expect.objectContaining(studentGroup));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IStudentGroup>();
      const studentGroup = { id: 20706 };
      vitest.spyOn(studentGroupFormService, 'getStudentGroup').mockReturnValue({ id: null });
      vitest.spyOn(studentGroupService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ studentGroup: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(studentGroup);
      saveSubject.complete();

      // THEN
      expect(studentGroupFormService.getStudentGroup).toHaveBeenCalled();
      expect(studentGroupService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IStudentGroup>();
      const studentGroup = { id: 20706 };
      vitest.spyOn(studentGroupService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ studentGroup });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(studentGroupService.update).toHaveBeenCalled();
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
