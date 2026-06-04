import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { SolverJobService } from 'app/entities/solver-job/service/solver-job.service';
import { ISolverJob } from 'app/entities/solver-job/solver-job.model';
import { TimetableService } from 'app/entities/timetable/service/timetable.service';
import { ITimetable } from 'app/entities/timetable/timetable.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { TimetableVersionService } from '../service/timetable-version.service';
import { ITimetableVersion } from '../timetable-version.model';

import { TimetableVersionFormGroup, TimetableVersionFormService } from './timetable-version-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-timetable-version-update',
  templateUrl: './timetable-version-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class TimetableVersionUpdate implements OnInit {
  readonly isSaving = signal(false);
  timetableVersion: ITimetableVersion | null = null;

  timetablesSharedCollection = signal<ITimetable[]>([]);
  solverJobsSharedCollection = signal<ISolverJob[]>([]);

  protected timetableVersionService = inject(TimetableVersionService);
  protected timetableVersionFormService = inject(TimetableVersionFormService);
  protected timetableService = inject(TimetableService);
  protected solverJobService = inject(SolverJobService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: TimetableVersionFormGroup = this.timetableVersionFormService.createTimetableVersionFormGroup();

  compareTimetable = (o1: ITimetable | null, o2: ITimetable | null): boolean => this.timetableService.compareTimetable(o1, o2);

  compareSolverJob = (o1: ISolverJob | null, o2: ISolverJob | null): boolean => this.solverJobService.compareSolverJob(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ timetableVersion }) => {
      this.timetableVersion = timetableVersion;
      if (timetableVersion) {
        this.updateForm(timetableVersion);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const timetableVersion = this.timetableVersionFormService.getTimetableVersion(this.editForm);
    if (timetableVersion.id === null) {
      this.subscribeToSaveResponse(this.timetableVersionService.create(timetableVersion));
    } else {
      this.subscribeToSaveResponse(this.timetableVersionService.update(timetableVersion));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ITimetableVersion | null>): void {
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

  protected updateForm(timetableVersion: ITimetableVersion): void {
    this.timetableVersion = timetableVersion;
    this.timetableVersionFormService.resetForm(this.editForm, timetableVersion);

    this.timetablesSharedCollection.update(timetables =>
      this.timetableService.addTimetableToCollectionIfMissing<ITimetable>(timetables, timetableVersion.timetable),
    );
    this.solverJobsSharedCollection.update(solverJobs =>
      this.solverJobService.addSolverJobToCollectionIfMissing<ISolverJob>(solverJobs, timetableVersion.solverJob),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.timetableService
      .query()
      .pipe(map((res: HttpResponse<ITimetable[]>) => res.body ?? []))
      .pipe(
        map((timetables: ITimetable[]) =>
          this.timetableService.addTimetableToCollectionIfMissing<ITimetable>(timetables, this.timetableVersion?.timetable),
        ),
      )
      .subscribe((timetables: ITimetable[]) => this.timetablesSharedCollection.set(timetables));

    this.solverJobService
      .query()
      .pipe(map((res: HttpResponse<ISolverJob[]>) => res.body ?? []))
      .pipe(
        map((solverJobs: ISolverJob[]) =>
          this.solverJobService.addSolverJobToCollectionIfMissing<ISolverJob>(solverJobs, this.timetableVersion?.solverJob),
        ),
      )
      .subscribe((solverJobs: ISolverJob[]) => this.solverJobsSharedCollection.set(solverJobs));
  }
}
