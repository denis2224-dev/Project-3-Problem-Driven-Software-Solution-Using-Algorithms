import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable, finalize } from 'rxjs';

import { SolverJobStatus } from 'app/entities/enumerations/solver-job-status.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { SolverJobService } from '../service/solver-job.service';
import { ISolverJob } from '../solver-job.model';

import { SolverJobFormGroup, SolverJobFormService } from './solver-job-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-solver-job-update',
  templateUrl: './solver-job-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class SolverJobUpdate implements OnInit {
  readonly isSaving = signal(false);
  solverJob: ISolverJob | null = null;
  solverJobStatusValues = Object.keys(SolverJobStatus);

  protected solverJobService = inject(SolverJobService);
  protected solverJobFormService = inject(SolverJobFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: SolverJobFormGroup = this.solverJobFormService.createSolverJobFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ solverJob }) => {
      this.solverJob = solverJob;
      if (solverJob) {
        this.updateForm(solverJob);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const solverJob = this.solverJobFormService.getSolverJob(this.editForm);
    if (solverJob.id === null) {
      this.subscribeToSaveResponse(this.solverJobService.create(solverJob));
    } else {
      this.subscribeToSaveResponse(this.solverJobService.update(solverJob));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ISolverJob | null>): void {
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

  protected updateForm(solverJob: ISolverJob): void {
    this.solverJob = solverJob;
    this.solverJobFormService.resetForm(this.editForm, solverJob);
  }
}
