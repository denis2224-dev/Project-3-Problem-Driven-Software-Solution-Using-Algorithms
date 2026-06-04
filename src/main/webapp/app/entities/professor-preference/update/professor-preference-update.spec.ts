import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IProfessor } from 'app/entities/professor/professor.model';
import { ProfessorService } from 'app/entities/professor/service/professor.service';
import { TimeslotService } from 'app/entities/timeslot/service/timeslot.service';
import { ITimeslot } from 'app/entities/timeslot/timeslot.model';
import { IProfessorPreference } from '../professor-preference.model';
import { ProfessorPreferenceService } from '../service/professor-preference.service';

import { ProfessorPreferenceFormService } from './professor-preference-form.service';
import { ProfessorPreferenceUpdate } from './professor-preference-update';

describe('ProfessorPreference Management Update Component', () => {
  let comp: ProfessorPreferenceUpdate;
  let fixture: ComponentFixture<ProfessorPreferenceUpdate>;
  let activatedRoute: ActivatedRoute;
  let professorPreferenceFormService: ProfessorPreferenceFormService;
  let professorPreferenceService: ProfessorPreferenceService;
  let professorService: ProfessorService;
  let timeslotService: TimeslotService;

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

    fixture = TestBed.createComponent(ProfessorPreferenceUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    professorPreferenceFormService = TestBed.inject(ProfessorPreferenceFormService);
    professorPreferenceService = TestBed.inject(ProfessorPreferenceService);
    professorService = TestBed.inject(ProfessorService);
    timeslotService = TestBed.inject(TimeslotService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Professor query and add missing value', () => {
      const professorPreference: IProfessorPreference = { id: 23125 };
      const professor: IProfessor = { id: 2234 };
      professorPreference.professor = professor;

      const professorCollection: IProfessor[] = [{ id: 2234 }];
      vitest.spyOn(professorService, 'query').mockReturnValue(of(new HttpResponse({ body: professorCollection })));
      const additionalProfessors = [professor];
      const expectedCollection: IProfessor[] = [...additionalProfessors, ...professorCollection];
      vitest.spyOn(professorService, 'addProfessorToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ professorPreference });
      comp.ngOnInit();

      expect(professorService.query).toHaveBeenCalled();
      expect(professorService.addProfessorToCollectionIfMissing).toHaveBeenCalledWith(
        professorCollection,
        ...additionalProfessors.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.professorsSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Timeslot query and add missing value', () => {
      const professorPreference: IProfessorPreference = { id: 23125 };
      const timeslot: ITimeslot = { id: 11059 };
      professorPreference.timeslot = timeslot;

      const timeslotCollection: ITimeslot[] = [{ id: 11059 }];
      vitest.spyOn(timeslotService, 'query').mockReturnValue(of(new HttpResponse({ body: timeslotCollection })));
      const additionalTimeslots = [timeslot];
      const expectedCollection: ITimeslot[] = [...additionalTimeslots, ...timeslotCollection];
      vitest.spyOn(timeslotService, 'addTimeslotToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ professorPreference });
      comp.ngOnInit();

      expect(timeslotService.query).toHaveBeenCalled();
      expect(timeslotService.addTimeslotToCollectionIfMissing).toHaveBeenCalledWith(
        timeslotCollection,
        ...additionalTimeslots.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.timeslotsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const professorPreference: IProfessorPreference = { id: 23125 };
      const professor: IProfessor = { id: 2234 };
      professorPreference.professor = professor;
      const timeslot: ITimeslot = { id: 11059 };
      professorPreference.timeslot = timeslot;

      activatedRoute.data = of({ professorPreference });
      comp.ngOnInit();

      expect(comp.professorsSharedCollection()).toContainEqual(professor);
      expect(comp.timeslotsSharedCollection()).toContainEqual(timeslot);
      expect(comp.professorPreference).toEqual(professorPreference);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IProfessorPreference>();
      const professorPreference = { id: 6094 };
      vitest.spyOn(professorPreferenceFormService, 'getProfessorPreference').mockReturnValue(professorPreference);
      vitest.spyOn(professorPreferenceService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ professorPreference });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(professorPreference);
      saveSubject.complete();

      // THEN
      expect(professorPreferenceFormService.getProfessorPreference).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(professorPreferenceService.update).toHaveBeenCalledWith(expect.objectContaining(professorPreference));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IProfessorPreference>();
      const professorPreference = { id: 6094 };
      vitest.spyOn(professorPreferenceFormService, 'getProfessorPreference').mockReturnValue({ id: null });
      vitest.spyOn(professorPreferenceService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ professorPreference: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(professorPreference);
      saveSubject.complete();

      // THEN
      expect(professorPreferenceFormService.getProfessorPreference).toHaveBeenCalled();
      expect(professorPreferenceService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IProfessorPreference>();
      const professorPreference = { id: 6094 };
      vitest.spyOn(professorPreferenceService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ professorPreference });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(professorPreferenceService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareProfessor', () => {
      it('should forward to professorService', () => {
        const entity = { id: 2234 };
        const entity2 = { id: 26501 };
        vitest.spyOn(professorService, 'compareProfessor');
        comp.compareProfessor(entity, entity2);
        expect(professorService.compareProfessor).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareTimeslot', () => {
      it('should forward to timeslotService', () => {
        const entity = { id: 11059 };
        const entity2 = { id: 26281 };
        vitest.spyOn(timeslotService, 'compareTimeslot');
        comp.compareTimeslot(entity, entity2);
        expect(timeslotService.compareTimeslot).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
