import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { TimetableVersionService } from 'app/entities/timetable-version/service/timetable-version.service';
import { ITimetableVersion } from 'app/entities/timetable-version/timetable-version.model';
import { IScheduleConflict } from '../schedule-conflict.model';
import { ScheduleConflictService } from '../service/schedule-conflict.service';

import { ScheduleConflictFormService } from './schedule-conflict-form.service';
import { ScheduleConflictUpdate } from './schedule-conflict-update';

describe('ScheduleConflict Management Update Component', () => {
  let comp: ScheduleConflictUpdate;
  let fixture: ComponentFixture<ScheduleConflictUpdate>;
  let activatedRoute: ActivatedRoute;
  let scheduleConflictFormService: ScheduleConflictFormService;
  let scheduleConflictService: ScheduleConflictService;
  let timetableVersionService: TimetableVersionService;

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

    fixture = TestBed.createComponent(ScheduleConflictUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    scheduleConflictFormService = TestBed.inject(ScheduleConflictFormService);
    scheduleConflictService = TestBed.inject(ScheduleConflictService);
    timetableVersionService = TestBed.inject(TimetableVersionService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call TimetableVersion query and add missing value', () => {
      const scheduleConflict: IScheduleConflict = { id: 4995 };
      const timetableVersion: ITimetableVersion = { id: 27241 };
      scheduleConflict.timetableVersion = timetableVersion;

      const timetableVersionCollection: ITimetableVersion[] = [{ id: 27241 }];
      vitest.spyOn(timetableVersionService, 'query').mockReturnValue(of(new HttpResponse({ body: timetableVersionCollection })));
      const additionalTimetableVersions = [timetableVersion];
      const expectedCollection: ITimetableVersion[] = [...additionalTimetableVersions, ...timetableVersionCollection];
      vitest.spyOn(timetableVersionService, 'addTimetableVersionToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ scheduleConflict });
      comp.ngOnInit();

      expect(timetableVersionService.query).toHaveBeenCalled();
      expect(timetableVersionService.addTimetableVersionToCollectionIfMissing).toHaveBeenCalledWith(
        timetableVersionCollection,
        ...additionalTimetableVersions.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.timetableVersionsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const scheduleConflict: IScheduleConflict = { id: 4995 };
      const timetableVersion: ITimetableVersion = { id: 27241 };
      scheduleConflict.timetableVersion = timetableVersion;

      activatedRoute.data = of({ scheduleConflict });
      comp.ngOnInit();

      expect(comp.timetableVersionsSharedCollection()).toContainEqual(timetableVersion);
      expect(comp.scheduleConflict).toEqual(scheduleConflict);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IScheduleConflict>();
      const scheduleConflict = { id: 30168 };
      vitest.spyOn(scheduleConflictFormService, 'getScheduleConflict').mockReturnValue(scheduleConflict);
      vitest.spyOn(scheduleConflictService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ scheduleConflict });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(scheduleConflict);
      saveSubject.complete();

      // THEN
      expect(scheduleConflictFormService.getScheduleConflict).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(scheduleConflictService.update).toHaveBeenCalledWith(expect.objectContaining(scheduleConflict));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IScheduleConflict>();
      const scheduleConflict = { id: 30168 };
      vitest.spyOn(scheduleConflictFormService, 'getScheduleConflict').mockReturnValue({ id: null });
      vitest.spyOn(scheduleConflictService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ scheduleConflict: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(scheduleConflict);
      saveSubject.complete();

      // THEN
      expect(scheduleConflictFormService.getScheduleConflict).toHaveBeenCalled();
      expect(scheduleConflictService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IScheduleConflict>();
      const scheduleConflict = { id: 30168 };
      vitest.spyOn(scheduleConflictService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ scheduleConflict });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(scheduleConflictService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareTimetableVersion', () => {
      it('should forward to timetableVersionService', () => {
        const entity = { id: 27241 };
        const entity2 = { id: 13722 };
        vitest.spyOn(timetableVersionService, 'compareTimetableVersion');
        comp.compareTimetableVersion(entity, entity2);
        expect(timetableVersionService.compareTimetableVersion).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
