import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { ICourseEvent } from 'app/entities/course-event/course-event.model';
import { CourseEventService } from 'app/entities/course-event/service/course-event.service';
import { IRoom } from 'app/entities/room/room.model';
import { RoomService } from 'app/entities/room/service/room.service';
import { TimeslotService } from 'app/entities/timeslot/service/timeslot.service';
import { ITimeslot } from 'app/entities/timeslot/timeslot.model';
import { TimetableVersionService } from 'app/entities/timetable-version/service/timetable-version.service';
import { ITimetableVersion } from 'app/entities/timetable-version/timetable-version.model';
import { TimetableEntryService } from '../service/timetable-entry.service';
import { ITimetableEntry } from '../timetable-entry.model';

import { TimetableEntryFormService } from './timetable-entry-form.service';
import { TimetableEntryUpdate } from './timetable-entry-update';

describe('TimetableEntry Management Update Component', () => {
  let comp: TimetableEntryUpdate;
  let fixture: ComponentFixture<TimetableEntryUpdate>;
  let activatedRoute: ActivatedRoute;
  let timetableEntryFormService: TimetableEntryFormService;
  let timetableEntryService: TimetableEntryService;
  let timetableVersionService: TimetableVersionService;
  let courseEventService: CourseEventService;
  let roomService: RoomService;
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

    fixture = TestBed.createComponent(TimetableEntryUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    timetableEntryFormService = TestBed.inject(TimetableEntryFormService);
    timetableEntryService = TestBed.inject(TimetableEntryService);
    timetableVersionService = TestBed.inject(TimetableVersionService);
    courseEventService = TestBed.inject(CourseEventService);
    roomService = TestBed.inject(RoomService);
    timeslotService = TestBed.inject(TimeslotService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call TimetableVersion query and add missing value', () => {
      const timetableEntry: ITimetableEntry = { id: 12100 };
      const timetableVersion: ITimetableVersion = { id: 27241 };
      timetableEntry.timetableVersion = timetableVersion;

      const timetableVersionCollection: ITimetableVersion[] = [{ id: 27241 }];
      vitest.spyOn(timetableVersionService, 'query').mockReturnValue(of(new HttpResponse({ body: timetableVersionCollection })));
      const additionalTimetableVersions = [timetableVersion];
      const expectedCollection: ITimetableVersion[] = [...additionalTimetableVersions, ...timetableVersionCollection];
      vitest.spyOn(timetableVersionService, 'addTimetableVersionToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ timetableEntry });
      comp.ngOnInit();

      expect(timetableVersionService.query).toHaveBeenCalled();
      expect(timetableVersionService.addTimetableVersionToCollectionIfMissing).toHaveBeenCalledWith(
        timetableVersionCollection,
        ...additionalTimetableVersions.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.timetableVersionsSharedCollection()).toEqual(expectedCollection);
    });

    it('should call CourseEvent query and add missing value', () => {
      const timetableEntry: ITimetableEntry = { id: 12100 };
      const courseEvent: ICourseEvent = { id: 9540 };
      timetableEntry.courseEvent = courseEvent;

      const courseEventCollection: ICourseEvent[] = [{ id: 9540 }];
      vitest.spyOn(courseEventService, 'query').mockReturnValue(of(new HttpResponse({ body: courseEventCollection })));
      const additionalCourseEvents = [courseEvent];
      const expectedCollection: ICourseEvent[] = [...additionalCourseEvents, ...courseEventCollection];
      vitest.spyOn(courseEventService, 'addCourseEventToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ timetableEntry });
      comp.ngOnInit();

      expect(courseEventService.query).toHaveBeenCalled();
      expect(courseEventService.addCourseEventToCollectionIfMissing).toHaveBeenCalledWith(
        courseEventCollection,
        ...additionalCourseEvents.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.courseEventsSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Room query and add missing value', () => {
      const timetableEntry: ITimetableEntry = { id: 12100 };
      const room: IRoom = { id: 31469 };
      timetableEntry.room = room;

      const roomCollection: IRoom[] = [{ id: 31469 }];
      vitest.spyOn(roomService, 'query').mockReturnValue(of(new HttpResponse({ body: roomCollection })));
      const additionalRooms = [room];
      const expectedCollection: IRoom[] = [...additionalRooms, ...roomCollection];
      vitest.spyOn(roomService, 'addRoomToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ timetableEntry });
      comp.ngOnInit();

      expect(roomService.query).toHaveBeenCalled();
      expect(roomService.addRoomToCollectionIfMissing).toHaveBeenCalledWith(
        roomCollection,
        ...additionalRooms.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.roomsSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Timeslot query and add missing value', () => {
      const timetableEntry: ITimetableEntry = { id: 12100 };
      const timeslot: ITimeslot = { id: 11059 };
      timetableEntry.timeslot = timeslot;

      const timeslotCollection: ITimeslot[] = [{ id: 11059 }];
      vitest.spyOn(timeslotService, 'query').mockReturnValue(of(new HttpResponse({ body: timeslotCollection })));
      const additionalTimeslots = [timeslot];
      const expectedCollection: ITimeslot[] = [...additionalTimeslots, ...timeslotCollection];
      vitest.spyOn(timeslotService, 'addTimeslotToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ timetableEntry });
      comp.ngOnInit();

      expect(timeslotService.query).toHaveBeenCalled();
      expect(timeslotService.addTimeslotToCollectionIfMissing).toHaveBeenCalledWith(
        timeslotCollection,
        ...additionalTimeslots.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.timeslotsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const timetableEntry: ITimetableEntry = { id: 12100 };
      const timetableVersion: ITimetableVersion = { id: 27241 };
      timetableEntry.timetableVersion = timetableVersion;
      const courseEvent: ICourseEvent = { id: 9540 };
      timetableEntry.courseEvent = courseEvent;
      const room: IRoom = { id: 31469 };
      timetableEntry.room = room;
      const timeslot: ITimeslot = { id: 11059 };
      timetableEntry.timeslot = timeslot;

      activatedRoute.data = of({ timetableEntry });
      comp.ngOnInit();

      expect(comp.timetableVersionsSharedCollection()).toContainEqual(timetableVersion);
      expect(comp.courseEventsSharedCollection()).toContainEqual(courseEvent);
      expect(comp.roomsSharedCollection()).toContainEqual(room);
      expect(comp.timeslotsSharedCollection()).toContainEqual(timeslot);
      expect(comp.timetableEntry).toEqual(timetableEntry);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ITimetableEntry>();
      const timetableEntry = { id: 15795 };
      vitest.spyOn(timetableEntryFormService, 'getTimetableEntry').mockReturnValue(timetableEntry);
      vitest.spyOn(timetableEntryService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ timetableEntry });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(timetableEntry);
      saveSubject.complete();

      // THEN
      expect(timetableEntryFormService.getTimetableEntry).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(timetableEntryService.update).toHaveBeenCalledWith(expect.objectContaining(timetableEntry));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ITimetableEntry>();
      const timetableEntry = { id: 15795 };
      vitest.spyOn(timetableEntryFormService, 'getTimetableEntry').mockReturnValue({ id: null });
      vitest.spyOn(timetableEntryService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ timetableEntry: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(timetableEntry);
      saveSubject.complete();

      // THEN
      expect(timetableEntryFormService.getTimetableEntry).toHaveBeenCalled();
      expect(timetableEntryService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ITimetableEntry>();
      const timetableEntry = { id: 15795 };
      vitest.spyOn(timetableEntryService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ timetableEntry });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(timetableEntryService.update).toHaveBeenCalled();
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

    describe('compareCourseEvent', () => {
      it('should forward to courseEventService', () => {
        const entity = { id: 9540 };
        const entity2 = { id: 726 };
        vitest.spyOn(courseEventService, 'compareCourseEvent');
        comp.compareCourseEvent(entity, entity2);
        expect(courseEventService.compareCourseEvent).toHaveBeenCalledWith(entity, entity2);
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
  });
});
