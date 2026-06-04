import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IExam } from 'app/entities/exam/exam.model';
import { ExamService } from 'app/entities/exam/service/exam.service';
import { IRoom } from 'app/entities/room/room.model';
import { RoomService } from 'app/entities/room/service/room.service';
import { TimeslotService } from 'app/entities/timeslot/service/timeslot.service';
import { ITimeslot } from 'app/entities/timeslot/timeslot.model';
import { TimetableVersionService } from 'app/entities/timetable-version/service/timetable-version.service';
import { ITimetableVersion } from 'app/entities/timetable-version/timetable-version.model';
import { IExamScheduleEntry } from '../exam-schedule-entry.model';
import { ExamScheduleEntryService } from '../service/exam-schedule-entry.service';

import { ExamScheduleEntryFormService } from './exam-schedule-entry-form.service';
import { ExamScheduleEntryUpdate } from './exam-schedule-entry-update';

describe('ExamScheduleEntry Management Update Component', () => {
  let comp: ExamScheduleEntryUpdate;
  let fixture: ComponentFixture<ExamScheduleEntryUpdate>;
  let activatedRoute: ActivatedRoute;
  let examScheduleEntryFormService: ExamScheduleEntryFormService;
  let examScheduleEntryService: ExamScheduleEntryService;
  let examService: ExamService;
  let roomService: RoomService;
  let timeslotService: TimeslotService;
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

    fixture = TestBed.createComponent(ExamScheduleEntryUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    examScheduleEntryFormService = TestBed.inject(ExamScheduleEntryFormService);
    examScheduleEntryService = TestBed.inject(ExamScheduleEntryService);
    examService = TestBed.inject(ExamService);
    roomService = TestBed.inject(RoomService);
    timeslotService = TestBed.inject(TimeslotService);
    timetableVersionService = TestBed.inject(TimetableVersionService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Exam query and add missing value', () => {
      const examScheduleEntry: IExamScheduleEntry = { id: 25016 };
      const exam: IExam = { id: 15727 };
      examScheduleEntry.exam = exam;

      const examCollection: IExam[] = [{ id: 15727 }];
      vitest.spyOn(examService, 'query').mockReturnValue(of(new HttpResponse({ body: examCollection })));
      const additionalExams = [exam];
      const expectedCollection: IExam[] = [...additionalExams, ...examCollection];
      vitest.spyOn(examService, 'addExamToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ examScheduleEntry });
      comp.ngOnInit();

      expect(examService.query).toHaveBeenCalled();
      expect(examService.addExamToCollectionIfMissing).toHaveBeenCalledWith(
        examCollection,
        ...additionalExams.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.examsSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Room query and add missing value', () => {
      const examScheduleEntry: IExamScheduleEntry = { id: 25016 };
      const room: IRoom = { id: 31469 };
      examScheduleEntry.room = room;

      const roomCollection: IRoom[] = [{ id: 31469 }];
      vitest.spyOn(roomService, 'query').mockReturnValue(of(new HttpResponse({ body: roomCollection })));
      const additionalRooms = [room];
      const expectedCollection: IRoom[] = [...additionalRooms, ...roomCollection];
      vitest.spyOn(roomService, 'addRoomToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ examScheduleEntry });
      comp.ngOnInit();

      expect(roomService.query).toHaveBeenCalled();
      expect(roomService.addRoomToCollectionIfMissing).toHaveBeenCalledWith(
        roomCollection,
        ...additionalRooms.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.roomsSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Timeslot query and add missing value', () => {
      const examScheduleEntry: IExamScheduleEntry = { id: 25016 };
      const timeslot: ITimeslot = { id: 11059 };
      examScheduleEntry.timeslot = timeslot;

      const timeslotCollection: ITimeslot[] = [{ id: 11059 }];
      vitest.spyOn(timeslotService, 'query').mockReturnValue(of(new HttpResponse({ body: timeslotCollection })));
      const additionalTimeslots = [timeslot];
      const expectedCollection: ITimeslot[] = [...additionalTimeslots, ...timeslotCollection];
      vitest.spyOn(timeslotService, 'addTimeslotToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ examScheduleEntry });
      comp.ngOnInit();

      expect(timeslotService.query).toHaveBeenCalled();
      expect(timeslotService.addTimeslotToCollectionIfMissing).toHaveBeenCalledWith(
        timeslotCollection,
        ...additionalTimeslots.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.timeslotsSharedCollection()).toEqual(expectedCollection);
    });

    it('should call TimetableVersion query and add missing value', () => {
      const examScheduleEntry: IExamScheduleEntry = { id: 25016 };
      const timetableVersion: ITimetableVersion = { id: 27241 };
      examScheduleEntry.timetableVersion = timetableVersion;

      const timetableVersionCollection: ITimetableVersion[] = [{ id: 27241 }];
      vitest.spyOn(timetableVersionService, 'query').mockReturnValue(of(new HttpResponse({ body: timetableVersionCollection })));
      const additionalTimetableVersions = [timetableVersion];
      const expectedCollection: ITimetableVersion[] = [...additionalTimetableVersions, ...timetableVersionCollection];
      vitest.spyOn(timetableVersionService, 'addTimetableVersionToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ examScheduleEntry });
      comp.ngOnInit();

      expect(timetableVersionService.query).toHaveBeenCalled();
      expect(timetableVersionService.addTimetableVersionToCollectionIfMissing).toHaveBeenCalledWith(
        timetableVersionCollection,
        ...additionalTimetableVersions.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.timetableVersionsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const examScheduleEntry: IExamScheduleEntry = { id: 25016 };
      const exam: IExam = { id: 15727 };
      examScheduleEntry.exam = exam;
      const room: IRoom = { id: 31469 };
      examScheduleEntry.room = room;
      const timeslot: ITimeslot = { id: 11059 };
      examScheduleEntry.timeslot = timeslot;
      const timetableVersion: ITimetableVersion = { id: 27241 };
      examScheduleEntry.timetableVersion = timetableVersion;

      activatedRoute.data = of({ examScheduleEntry });
      comp.ngOnInit();

      expect(comp.examsSharedCollection()).toContainEqual(exam);
      expect(comp.roomsSharedCollection()).toContainEqual(room);
      expect(comp.timeslotsSharedCollection()).toContainEqual(timeslot);
      expect(comp.timetableVersionsSharedCollection()).toContainEqual(timetableVersion);
      expect(comp.examScheduleEntry).toEqual(examScheduleEntry);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IExamScheduleEntry>();
      const examScheduleEntry = { id: 619 };
      vitest.spyOn(examScheduleEntryFormService, 'getExamScheduleEntry').mockReturnValue(examScheduleEntry);
      vitest.spyOn(examScheduleEntryService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ examScheduleEntry });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(examScheduleEntry);
      saveSubject.complete();

      // THEN
      expect(examScheduleEntryFormService.getExamScheduleEntry).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(examScheduleEntryService.update).toHaveBeenCalledWith(expect.objectContaining(examScheduleEntry));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IExamScheduleEntry>();
      const examScheduleEntry = { id: 619 };
      vitest.spyOn(examScheduleEntryFormService, 'getExamScheduleEntry').mockReturnValue({ id: null });
      vitest.spyOn(examScheduleEntryService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ examScheduleEntry: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(examScheduleEntry);
      saveSubject.complete();

      // THEN
      expect(examScheduleEntryFormService.getExamScheduleEntry).toHaveBeenCalled();
      expect(examScheduleEntryService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IExamScheduleEntry>();
      const examScheduleEntry = { id: 619 };
      vitest.spyOn(examScheduleEntryService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ examScheduleEntry });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(examScheduleEntryService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareExam', () => {
      it('should forward to examService', () => {
        const entity = { id: 15727 };
        const entity2 = { id: 13366 };
        vitest.spyOn(examService, 'compareExam');
        comp.compareExam(entity, entity2);
        expect(examService.compareExam).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareRoom', () => {
      it('should forward to roomService', () => {
        const entity = { id: 31469 };
        const entity2 = { id: 22394 };
        vitest.spyOn(roomService, 'compareRoom');
        comp.compareRoom(entity, entity2);
        expect(roomService.compareRoom).toHaveBeenCalledWith(entity, entity2);
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
