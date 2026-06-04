import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IFaculty } from '../faculty.model';
import { FacultyService } from '../service/faculty.service';

import { FacultyFormService } from './faculty-form.service';
import { FacultyUpdate } from './faculty-update';

describe('Faculty Management Update Component', () => {
  let comp: FacultyUpdate;
  let fixture: ComponentFixture<FacultyUpdate>;
  let activatedRoute: ActivatedRoute;
  let facultyFormService: FacultyFormService;
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

    fixture = TestBed.createComponent(FacultyUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    facultyFormService = TestBed.inject(FacultyFormService);
    facultyService = TestBed.inject(FacultyService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const faculty: IFaculty = { id: 15041 };

      activatedRoute.data = of({ faculty });
      comp.ngOnInit();

      expect(comp.faculty).toEqual(faculty);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IFaculty>();
      const faculty = { id: 20867 };
      vitest.spyOn(facultyFormService, 'getFaculty').mockReturnValue(faculty);
      vitest.spyOn(facultyService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ faculty });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(faculty);
      saveSubject.complete();

      // THEN
      expect(facultyFormService.getFaculty).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(facultyService.update).toHaveBeenCalledWith(expect.objectContaining(faculty));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IFaculty>();
      const faculty = { id: 20867 };
      vitest.spyOn(facultyFormService, 'getFaculty').mockReturnValue({ id: null });
      vitest.spyOn(facultyService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ faculty: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(faculty);
      saveSubject.complete();

      // THEN
      expect(facultyFormService.getFaculty).toHaveBeenCalled();
      expect(facultyService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IFaculty>();
      const faculty = { id: 20867 };
      vitest.spyOn(facultyService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ faculty });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(facultyService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
