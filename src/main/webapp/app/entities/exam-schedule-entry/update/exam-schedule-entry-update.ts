import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { IExam } from 'app/entities/exam/exam.model';
import { ExamService } from 'app/entities/exam/service/exam.service';
import { IRoom } from 'app/entities/room/room.model';
import { RoomService } from 'app/entities/room/service/room.service';
import { TimeslotService } from 'app/entities/timeslot/service/timeslot.service';
import { ITimeslot } from 'app/entities/timeslot/timeslot.model';
import { TimetableVersionService } from 'app/entities/timetable-version/service/timetable-version.service';
import { ITimetableVersion } from 'app/entities/timetable-version/timetable-version.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IExamScheduleEntry } from '../exam-schedule-entry.model';
import { ExamScheduleEntryService } from '../service/exam-schedule-entry.service';

import { ExamScheduleEntryFormGroup, ExamScheduleEntryFormService } from './exam-schedule-entry-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-exam-schedule-entry-update',
  templateUrl: './exam-schedule-entry-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class ExamScheduleEntryUpdate implements OnInit {
  readonly isSaving = signal(false);
  examScheduleEntry: IExamScheduleEntry | null = null;

  examsSharedCollection = signal<IExam[]>([]);
  roomsSharedCollection = signal<IRoom[]>([]);
  timeslotsSharedCollection = signal<ITimeslot[]>([]);
  timetableVersionsSharedCollection = signal<ITimetableVersion[]>([]);

  protected examScheduleEntryService = inject(ExamScheduleEntryService);
  protected examScheduleEntryFormService = inject(ExamScheduleEntryFormService);
  protected examService = inject(ExamService);
  protected roomService = inject(RoomService);
  protected timeslotService = inject(TimeslotService);
  protected timetableVersionService = inject(TimetableVersionService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ExamScheduleEntryFormGroup = this.examScheduleEntryFormService.createExamScheduleEntryFormGroup();

  compareExam = (o1: IExam | null, o2: IExam | null): boolean => this.examService.compareExam(o1, o2);

  compareRoom = (o1: IRoom | null, o2: IRoom | null): boolean => this.roomService.compareRoom(o1, o2);

  compareTimeslot = (o1: ITimeslot | null, o2: ITimeslot | null): boolean => this.timeslotService.compareTimeslot(o1, o2);

  compareTimetableVersion = (o1: ITimetableVersion | null, o2: ITimetableVersion | null): boolean =>
    this.timetableVersionService.compareTimetableVersion(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ examScheduleEntry }) => {
      this.examScheduleEntry = examScheduleEntry;
      if (examScheduleEntry) {
        this.updateForm(examScheduleEntry);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const examScheduleEntry = this.examScheduleEntryFormService.getExamScheduleEntry(this.editForm);
    if (examScheduleEntry.id === null) {
      this.subscribeToSaveResponse(this.examScheduleEntryService.create(examScheduleEntry));
    } else {
      this.subscribeToSaveResponse(this.examScheduleEntryService.update(examScheduleEntry));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IExamScheduleEntry | null>): void {
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

  protected updateForm(examScheduleEntry: IExamScheduleEntry): void {
    this.examScheduleEntry = examScheduleEntry;
    this.examScheduleEntryFormService.resetForm(this.editForm, examScheduleEntry);

    this.examsSharedCollection.update(exams => this.examService.addExamToCollectionIfMissing<IExam>(exams, examScheduleEntry.exam));
    this.roomsSharedCollection.update(rooms => this.roomService.addRoomToCollectionIfMissing<IRoom>(rooms, examScheduleEntry.room));
    this.timeslotsSharedCollection.update(timeslots =>
      this.timeslotService.addTimeslotToCollectionIfMissing<ITimeslot>(timeslots, examScheduleEntry.timeslot),
    );
    this.timetableVersionsSharedCollection.update(timetableVersions =>
      this.timetableVersionService.addTimetableVersionToCollectionIfMissing<ITimetableVersion>(
        timetableVersions,
        examScheduleEntry.timetableVersion,
      ),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.examService
      .query()
      .pipe(map((res: HttpResponse<IExam[]>) => res.body ?? []))
      .pipe(map((exams: IExam[]) => this.examService.addExamToCollectionIfMissing<IExam>(exams, this.examScheduleEntry?.exam)))
      .subscribe((exams: IExam[]) => this.examsSharedCollection.set(exams));

    this.roomService
      .query()
      .pipe(map((res: HttpResponse<IRoom[]>) => res.body ?? []))
      .pipe(map((rooms: IRoom[]) => this.roomService.addRoomToCollectionIfMissing<IRoom>(rooms, this.examScheduleEntry?.room)))
      .subscribe((rooms: IRoom[]) => this.roomsSharedCollection.set(rooms));

    this.timeslotService
      .query()
      .pipe(map((res: HttpResponse<ITimeslot[]>) => res.body ?? []))
      .pipe(
        map((timeslots: ITimeslot[]) =>
          this.timeslotService.addTimeslotToCollectionIfMissing<ITimeslot>(timeslots, this.examScheduleEntry?.timeslot),
        ),
      )
      .subscribe((timeslots: ITimeslot[]) => this.timeslotsSharedCollection.set(timeslots));

    this.timetableVersionService
      .query()
      .pipe(map((res: HttpResponse<ITimetableVersion[]>) => res.body ?? []))
      .pipe(
        map((timetableVersions: ITimetableVersion[]) =>
          this.timetableVersionService.addTimetableVersionToCollectionIfMissing<ITimetableVersion>(
            timetableVersions,
            this.examScheduleEntry?.timetableVersion,
          ),
        ),
      )
      .subscribe((timetableVersions: ITimetableVersion[]) => this.timetableVersionsSharedCollection.set(timetableVersions));
  }
}
