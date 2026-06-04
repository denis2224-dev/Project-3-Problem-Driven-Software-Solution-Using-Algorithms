import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { TimeslotService } from '../service/timeslot.service';
import { ITimeslot } from '../timeslot.model';

import { TimeslotFormService } from './timeslot-form.service';
import { TimeslotUpdate } from './timeslot-update';

describe('Timeslot Management Update Component', () => {
  let comp: TimeslotUpdate;
  let fixture: ComponentFixture<TimeslotUpdate>;
  let activatedRoute: ActivatedRoute;
  let timeslotFormService: TimeslotFormService;
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

    fixture = TestBed.createComponent(TimeslotUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    timeslotFormService = TestBed.inject(TimeslotFormService);
    timeslotService = TestBed.inject(TimeslotService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const timeslot: ITimeslot = { id: 26281 };

      activatedRoute.data = of({ timeslot });
      comp.ngOnInit();

      expect(comp.timeslot).toEqual(timeslot);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ITimeslot>();
      const timeslot = { id: 11059 };
      vitest.spyOn(timeslotFormService, 'getTimeslot').mockReturnValue(timeslot);
      vitest.spyOn(timeslotService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ timeslot });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(timeslot);
      saveSubject.complete();

      // THEN
      expect(timeslotFormService.getTimeslot).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(timeslotService.update).toHaveBeenCalledWith(expect.objectContaining(timeslot));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ITimeslot>();
      const timeslot = { id: 11059 };
      vitest.spyOn(timeslotFormService, 'getTimeslot').mockReturnValue({ id: null });
      vitest.spyOn(timeslotService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ timeslot: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(timeslot);
      saveSubject.complete();

      // THEN
      expect(timeslotFormService.getTimeslot).toHaveBeenCalled();
      expect(timeslotService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ITimeslot>();
      const timeslot = { id: 11059 };
      vitest.spyOn(timeslotService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ timeslot });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(timeslotService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
