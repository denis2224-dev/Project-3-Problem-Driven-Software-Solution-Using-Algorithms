import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { SolverJobService } from '../service/solver-job.service';
import { ISolverJob } from '../solver-job.model';

import { SolverJobFormService } from './solver-job-form.service';
import { SolverJobUpdate } from './solver-job-update';

describe('SolverJob Management Update Component', () => {
  let comp: SolverJobUpdate;
  let fixture: ComponentFixture<SolverJobUpdate>;
  let activatedRoute: ActivatedRoute;
  let solverJobFormService: SolverJobFormService;
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

    fixture = TestBed.createComponent(SolverJobUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    solverJobFormService = TestBed.inject(SolverJobFormService);
    solverJobService = TestBed.inject(SolverJobService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const solverJob: ISolverJob = { id: 24390 };

      activatedRoute.data = of({ solverJob });
      comp.ngOnInit();

      expect(comp.solverJob).toEqual(solverJob);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ISolverJob>();
      const solverJob = { id: 6466 };
      vitest.spyOn(solverJobFormService, 'getSolverJob').mockReturnValue(solverJob);
      vitest.spyOn(solverJobService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ solverJob });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(solverJob);
      saveSubject.complete();

      // THEN
      expect(solverJobFormService.getSolverJob).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(solverJobService.update).toHaveBeenCalledWith(expect.objectContaining(solverJob));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ISolverJob>();
      const solverJob = { id: 6466 };
      vitest.spyOn(solverJobFormService, 'getSolverJob').mockReturnValue({ id: null });
      vitest.spyOn(solverJobService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ solverJob: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(solverJob);
      saveSubject.complete();

      // THEN
      expect(solverJobFormService.getSolverJob).toHaveBeenCalled();
      expect(solverJobService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ISolverJob>();
      const solverJob = { id: 6466 };
      vitest.spyOn(solverJobService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ solverJob });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(solverJobService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
