import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IDepartment } from 'app/entities/department/department.model';
import { DepartmentService } from 'app/entities/department/service/department.service';
import { IProfessor } from '../professor.model';
import { ProfessorService } from '../service/professor.service';

import { ProfessorFormService } from './professor-form.service';
import { ProfessorUpdate } from './professor-update';

describe('Professor Management Update Component', () => {
  let comp: ProfessorUpdate;
  let fixture: ComponentFixture<ProfessorUpdate>;
  let activatedRoute: ActivatedRoute;
  let professorFormService: ProfessorFormService;
  let professorService: ProfessorService;
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

    fixture = TestBed.createComponent(ProfessorUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    professorFormService = TestBed.inject(ProfessorFormService);
    professorService = TestBed.inject(ProfessorService);
    departmentService = TestBed.inject(DepartmentService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Department query and add missing value', () => {
      const professor: IProfessor = { id: 26501 };
      const department: IDepartment = { id: 29518 };
      professor.department = department;

      const departmentCollection: IDepartment[] = [{ id: 29518 }];
      vitest.spyOn(departmentService, 'query').mockReturnValue(of(new HttpResponse({ body: departmentCollection })));
      const additionalDepartments = [department];
      const expectedCollection: IDepartment[] = [...additionalDepartments, ...departmentCollection];
      vitest.spyOn(departmentService, 'addDepartmentToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ professor });
      comp.ngOnInit();

      expect(departmentService.query).toHaveBeenCalled();
      expect(departmentService.addDepartmentToCollectionIfMissing).toHaveBeenCalledWith(
        departmentCollection,
        ...additionalDepartments.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.departmentsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const professor: IProfessor = { id: 26501 };
      const department: IDepartment = { id: 29518 };
      professor.department = department;

      activatedRoute.data = of({ professor });
      comp.ngOnInit();

      expect(comp.departmentsSharedCollection()).toContainEqual(department);
      expect(comp.professor).toEqual(professor);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IProfessor>();
      const professor = { id: 2234 };
      vitest.spyOn(professorFormService, 'getProfessor').mockReturnValue(professor);
      vitest.spyOn(professorService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ professor });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(professor);
      saveSubject.complete();

      // THEN
      expect(professorFormService.getProfessor).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(professorService.update).toHaveBeenCalledWith(expect.objectContaining(professor));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IProfessor>();
      const professor = { id: 2234 };
      vitest.spyOn(professorFormService, 'getProfessor').mockReturnValue({ id: null });
      vitest.spyOn(professorService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ professor: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(professor);
      saveSubject.complete();

      // THEN
      expect(professorFormService.getProfessor).toHaveBeenCalled();
      expect(professorService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IProfessor>();
      const professor = { id: 2234 };
      vitest.spyOn(professorService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ professor });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(professorService.update).toHaveBeenCalled();
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
