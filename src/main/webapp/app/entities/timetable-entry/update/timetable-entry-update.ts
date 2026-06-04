import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { ICourseEvent } from 'app/entities/course-event/course-event.model';
import { CourseEventService } from 'app/entities/course-event/service/course-event.service';
import { IRoom } from 'app/entities/room/room.model';
import { RoomService } from 'app/entities/room/service/room.service';
import { TimeslotService } from 'app/entities/timeslot/service/timeslot.service';
import { ITimeslot } from 'app/entities/timeslot/timeslot.model';
import { TimetableVersionService } from 'app/entities/timetable-version/service/timetable-version.service';
import { ITimetableVersion } from 'app/entities/timetable-version/timetable-version.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { TimetableEntryService } from '../service/timetable-entry.service';
import { ITimetableEntry } from '../timetable-entry.model';

import { TimetableEntryFormGroup, TimetableEntryFormService } from './timetable-entry-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-timetable-entry-update',
  templateUrl: './timetable-entry-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class TimetableEntryUpdate implements OnInit {
  readonly isSaving = signal(false);
  timetableEntry: ITimetableEntry | null = null;

  timetableVersionsSharedCollection = signal<ITimetableVersion[]>([]);
  courseEventsSharedCollection = signal<ICourseEvent[]>([]);
  roomsSharedCollection = signal<IRoom[]>([]);
  timeslotsSharedCollection = signal<ITimeslot[]>([]);

  protected timetableEntryService = inject(TimetableEntryService);
  protected timetableEntryFormService = inject(TimetableEntryFormService);
  protected timetableVersionService = inject(TimetableVersionService);
  protected courseEventService = inject(CourseEventService);
  protected roomService = inject(RoomService);
  protected timeslotService = inject(TimeslotService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: TimetableEntryFormGroup = this.timetableEntryFormService.createTimetableEntryFormGroup();

  compareTimetableVersion = (o1: ITimetableVersion | null, o2: ITimetableVersion | null): boolean =>
    this.timetableVersionService.compareTimetableVersion(o1, o2);

  compareCourseEvent = (o1: ICourseEvent | null, o2: ICourseEvent | null): boolean => this.courseEventService.compareCourseEvent(o1, o2);

  compareRoom = (o1: IRoom | null, o2: IRoom | null): boolean => this.roomService.compareRoom(o1, o2);

  compareTimeslot = (o1: ITimeslot | null, o2: ITimeslot | null): boolean => this.timeslotService.compareTimeslot(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ timetableEntry }) => {
      this.timetableEntry = timetableEntry;
      if (timetableEntry) {
        this.updateForm(timetableEntry);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const timetableEntry = this.timetableEntryFormService.getTimetableEntry(this.editForm);
    if (timetableEntry.id === null) {
      this.subscribeToSaveResponse(this.timetableEntryService.create(timetableEntry));
    } else {
      this.subscribeToSaveResponse(this.timetableEntryService.update(timetableEntry));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ITimetableEntry | null>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving.set(false);
  }

  protected updateForm(timetableEntry: ITimetableEntry): void {
    this.timetableEntry = timetableEntry;
    this.timetableEntryFormService.resetForm(this.editForm, timetableEntry);

    this.timetableVersionsSharedCollection.update(timetableVersions =>
      this.timetableVersionService.addTimetableVersionToCollectionIfMissing<ITimetableVersion>(
        timetableVersions,
        timetableEntry.timetableVersion,
      ),
    );
    this.courseEventsSharedCollection.update(courseEvents =>
      this.courseEventService.addCourseEventToCollectionIfMissing<ICourseEvent>(courseEvents, timetableEntry.courseEvent),
    );
    this.roomsSharedCollection.update(rooms => this.roomService.addRoomToCollectionIfMissing<IRoom>(rooms, timetableEntry.room));
    this.timeslotsSharedCollection.update(timeslots =>
      this.timeslotService.addTimeslotToCollectionIfMissing<ITimeslot>(timeslots, timetableEntry.timeslot),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.timetableVersionService
      .query()
      .pipe(map((res: HttpResponse<ITimetableVersion[]>) => res.body ?? []))
      .pipe(
        map((timetableVersions: ITimetableVersion[]) =>
          this.timetableVersionService.addTimetableVersionToCollectionIfMissing<ITimetableVersion>(
            timetableVersions,
            this.timetableEntry?.timetableVersion,
          ),
        ),
      )
      .subscribe((timetableVersions: ITimetableVersion[]) => this.timetableVersionsSharedCollection.set(timetableVersions));

    this.courseEventService
      .query()
      .pipe(map((res: HttpResponse<ICourseEvent[]>) => res.body ?? []))
      .pipe(
        map((courseEvents: ICourseEvent[]) =>
          this.courseEventService.addCourseEventToCollectionIfMissing<ICourseEvent>(courseEvents, this.timetableEntry?.courseEvent),
        ),
      )
      .subscribe((courseEvents: ICourseEvent[]) => this.courseEventsSharedCollection.set(courseEvents));

    this.roomService
      .query()
      .pipe(map((res: HttpResponse<IRoom[]>) => res.body ?? []))
      .pipe(map((rooms: IRoom[]) => this.roomService.addRoomToCollectionIfMissing<IRoom>(rooms, this.timetableEntry?.room)))
      .subscribe((rooms: IRoom[]) => this.roomsSharedCollection.set(rooms));

    this.timeslotService
      .query()
      .pipe(map((res: HttpResponse<ITimeslot[]>) => res.body ?? []))
      .pipe(
        map((timeslots: ITimeslot[]) =>
          this.timeslotService.addTimeslotToCollectionIfMissing<ITimeslot>(timeslots, this.timetableEntry?.timeslot),
        ),
      )
      .subscribe((timeslots: ITimeslot[]) => this.timeslotsSharedCollection.set(timeslots));
  }
}
