import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IFaculty } from 'app/entities/faculty/faculty.model';
import { FacultyService } from 'app/entities/faculty/service/faculty.service';
import { IDepartment } from '../department.model';
import { DepartmentService } from '../service/department.service';

import { DepartmentFormService } from './department-form.service';
import { DepartmentUpdate } from './department-update';

describe('Department Management Update Component', () => {
  let comp: DepartmentUpdate;
  let fixture: ComponentFixture<DepartmentUpdate>;
  let activatedRoute: ActivatedRoute;
  let departmentFormService: DepartmentFormService;
  let departmentService: DepartmentService;
  let facultyService: FacultyService;

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

    fixture = TestBed.createComponent(DepartmentUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    departmentFormService = TestBed.inject(DepartmentFormService);
    departmentService = TestBed.inject(DepartmentService);
    facultyService = TestBed.inject(FacultyService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Faculty query and add missing value', () => {
      const department: IDepartment = { id: 15970 };
      const faculty: IFaculty = { id: 20867 };
      department.faculty = faculty;

      const facultyCollection: IFaculty[] = [{ id: 20867 }];
      vitest.spyOn(facultyService, 'query').mockReturnValue(of(new HttpResponse({ body: facultyCollection })));
      const additionalFaculties = [faculty];
      const expectedCollection: IFaculty[] = [...additionalFaculties, ...facultyCollection];
      vitest.spyOn(facultyService, 'addFacultyToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ department });
      comp.ngOnInit();

      expect(facultyService.query).toHaveBeenCalled();
      expect(facultyService.addFacultyToCollectionIfMissing).toHaveBeenCalledWith(
        facultyCollection,
        ...additionalFaculties.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.facultiesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const department: IDepartment = { id: 15970 };
      const faculty: IFaculty = { id: 20867 };
      department.faculty = faculty;

      activatedRoute.data = of({ department });
      comp.ngOnInit();

      expect(comp.facultiesSharedCollection()).toContainEqual(faculty);
      expect(comp.department).toEqual(department);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IDepartment>();
      const department = { id: 29518 };
      vitest.spyOn(departmentFormService, 'getDepartment').mockReturnValue(department);
      vitest.spyOn(departmentService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ department });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(department);
      saveSubject.complete();

      // THEN
      expect(departmentFormService.getDepartment).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(departmentService.update).toHaveBeenCalledWith(expect.objectContaining(department));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IDepartment>();
      const department = { id: 29518 };
      vitest.spyOn(departmentFormService, 'getDepartment').mockReturnValue({ id: null });
      vitest.spyOn(departmentService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ department: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(department);
      saveSubject.complete();

      // THEN
      expect(departmentFormService.getDepartment).toHaveBeenCalled();
      expect(departmentService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IDepartment>();
      const department = { id: 29518 };
      vitest.spyOn(departmentService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ department });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(departmentService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareFaculty', () => {
      it('should forward to facultyService', () => {
        const entity = { id: 20867 };
        const entity2 = { id: 15041 };
        vitest.spyOn(facultyService, 'compareFaculty');
        comp.compareFaculty(entity, entity2);
        expect(facultyService.compareFaculty).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
