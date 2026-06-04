import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { ConflictSeverity } from 'app/entities/enumerations/conflict-severity.model';
import { ConflictType } from 'app/entities/enumerations/conflict-type.model';
import { TimetableVersionService } from 'app/entities/timetable-version/service/timetable-version.service';
import { ITimetableVersion } from 'app/entities/timetable-version/timetable-version.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IScheduleConflict } from '../schedule-conflict.model';
import { ScheduleConflictService } from '../service/schedule-conflict.service';

import { ScheduleConflictFormGroup, ScheduleConflictFormService } from './schedule-conflict-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-schedule-conflict-update',
  templateUrl: './schedule-conflict-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class ScheduleConflictUpdate implements OnInit {
  readonly isSaving = signal(false);
  scheduleConflict: IScheduleConflict | null = null;
  conflictTypeValues = Object.keys(ConflictType);
  conflictSeverityValues = Object.keys(ConflictSeverity);

  timetableVersionsSharedCollection = signal<ITimetableVersion[]>([]);

  protected scheduleConflictService = inject(ScheduleConflictService);
  protected scheduleConflictFormService = inject(ScheduleConflictFormService);
  protected timetableVersionService = inject(TimetableVersionService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ScheduleConflictFormGroup = this.scheduleConflictFormService.createScheduleConflictFormGroup();

  compareTimetableVersion = (o1: ITimetableVersion | null, o2: ITimetableVersion | null): boolean =>
    this.timetableVersionService.compareTimetableVersion(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ scheduleConflict }) => {
      this.scheduleConflict = scheduleConflict;
      if (scheduleConflict) {
        this.updateForm(scheduleConflict);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const scheduleConflict = this.scheduleConflictFormService.getScheduleConflict(this.editForm);
    if (scheduleConflict.id === null) {
      this.subscribeToSaveResponse(this.scheduleConflictService.create(scheduleConflict));
    } else {
      this.subscribeToSaveResponse(this.scheduleConflictService.update(scheduleConflict));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IScheduleConflict | null>): void {
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

  protected updateForm(scheduleConflict: IScheduleConflict): void {
    this.scheduleConflict = scheduleConflict;
    this.scheduleConflictFormService.resetForm(this.editForm, scheduleConflict);

    this.timetableVersionsSharedCollection.update(timetableVersions =>
      this.timetableVersionService.addTimetableVersionToCollectionIfMissing<ITimetableVersion>(
        timetableVersions,
        scheduleConflict.timetableVersion,
      ),
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
            this.scheduleConflict?.timetableVersion,
          ),
        ),
      )
      .subscribe((timetableVersions: ITimetableVersion[]) => this.timetableVersionsSharedCollection.set(timetableVersions));
  }
}
