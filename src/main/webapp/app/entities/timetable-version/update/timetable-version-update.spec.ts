import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { SolverJobService } from 'app/entities/solver-job/service/solver-job.service';
import { ISolverJob } from 'app/entities/solver-job/solver-job.model';
import { TimetableService } from 'app/entities/timetable/service/timetable.service';
import { ITimetable } from 'app/entities/timetable/timetable.model';
import { TimetableVersionService } from '../service/timetable-version.service';
import { ITimetableVersion } from '../timetable-version.model';

import { TimetableVersionFormService } from './timetable-version-form.service';
import { TimetableVersionUpdate } from './timetable-version-update';

describe('TimetableVersion Management Update Component', () => {
  let comp: TimetableVersionUpdate;
  let fixture: ComponentFixture<TimetableVersionUpdate>;
  let activatedRoute: ActivatedRoute;
  let timetableVersionFormService: TimetableVersionFormService;
  let timetableVersionService: TimetableVersionService;
  let timetableService: TimetableService;
  let solverJobService: SolverJobService;

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

    fixture = TestBed.createComponent(TimetableVersionUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    timetableVersionFormService = TestBed.inject(TimetableVersionFormService);
    timetableVersionService = TestBed.inject(TimetableVersionService);
    timetableService = TestBed.inject(TimetableService);
    solverJobService = TestBed.inject(SolverJobService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Timetable query and add missing value', () => {
      const timetableVersion: ITimetableVersion = { id: 13722 };
      const timetable: ITimetable = { id: 31934 };
      timetableVersion.timetable = timetable;

      const timetableCollection: ITimetable[] = [{ id: 31934 }];
      vitest.spyOn(timetableService, 'query').mockReturnValue(of(new HttpResponse({ body: timetableCollection })));
      const additionalTimetables = [timetable];
      const expectedCollection: ITimetable[] = [...additionalTimetables, ...timetableCollection];
      vitest.spyOn(timetableService, 'addTimetableToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ timetableVersion });
      comp.ngOnInit();

      expect(timetableService.query).toHaveBeenCalled();
      expect(timetableService.addTimetableToCollectionIfMissing).toHaveBeenCalledWith(
        timetableCollection,
        ...additionalTimetables.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.timetablesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call SolverJob query and add missing value', () => {
      const timetableVersion: ITimetableVersion = { id: 13722 };
      const solverJob: ISolverJob = { id: 6466 };
      timetableVersion.solverJob = solverJob;

      const solverJobCollection: ISolverJob[] = [{ id: 6466 }];
      vitest.spyOn(solverJobService, 'query').mockReturnValue(of(new HttpResponse({ body: solverJobCollection })));
      const additionalSolverJobs = [solverJob];
      const expectedCollection: ISolverJob[] = [...additionalSolverJobs, ...solverJobCollection];
      vitest.spyOn(solverJobService, 'addSolverJobToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ timetableVersion });
      comp.ngOnInit();

      expect(solverJobService.query).toHaveBeenCalled();
      expect(solverJobService.addSolverJobToCollectionIfMissing).toHaveBeenCalledWith(
        solverJobCollection,
        ...additionalSolverJobs.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.solverJobsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const timetableVersion: ITimetableVersion = { id: 13722 };
      const timetable: ITimetable = { id: 31934 };
      timetableVersion.timetable = timetable;
      const solverJob: ISolverJob = { id: 6466 };
      timetableVersion.solverJob = solverJob;

      activatedRoute.data = of({ timetableVersion });
      comp.ngOnInit();

      expect(comp.timetablesSharedCollection()).toContainEqual(timetable);
      expect(comp.solverJobsSharedCollection()).toContainEqual(solverJob);
      expect(comp.timetableVersion).toEqual(timetableVersion);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ITimetableVersion>();
      const timetableVersion = { id: 27241 };
      vitest.spyOn(timetableVersionFormService, 'getTimetableVersion').mockReturnValue(timetableVersion);
      vitest.spyOn(timetableVersionService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ timetableVersion });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(timetableVersion);
      saveSubject.complete();

      // THEN
      expect(timetableVersionFormService.getTimetableVersion).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(timetableVersionService.update).toHaveBeenCalledWith(expect.objectContaining(timetableVersion));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ITimetableVersion>();
      const timetableVersion = { id: 27241 };
      vitest.spyOn(timetableVersionFormService, 'getTimetableVersion').mockReturnValue({ id: null });
      vitest.spyOn(timetableVersionService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ timetableVersion: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(timetableVersion);
      saveSubject.complete();

      // THEN
      expect(timetableVersionFormService.getTimetableVersion).toHaveBeenCalled();
      expect(timetableVersionService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ITimetableVersion>();
      const timetableVersion = { id: 27241 };
      vitest.spyOn(timetableVersionService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ timetableVersion });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(timetableVersionService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareTimetable', () => {
      it('should forward to timetableService', () => {
        const entity = { id: 31934 };
        const entity2 = { id: 13392 };
        vitest.spyOn(timetableService, 'compareTimetable');
        comp.compareTimetable(entity, entity2);
        expect(timetableService.compareTimetable).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareSolverJob', () => {
      it('should forward to solverJobService', () => {
        const entity = { id: 6466 };
        const entity2 = { id: 24390 };
        vitest.spyOn(solverJobService, 'compareSolverJob');
        comp.compareSolverJob(entity, entity2);
        expect(solverJobService.compareSolverJob).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
