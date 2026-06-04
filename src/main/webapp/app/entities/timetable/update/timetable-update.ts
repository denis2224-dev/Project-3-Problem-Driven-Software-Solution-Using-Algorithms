import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable, finalize } from 'rxjs';

import { TimetableStatus } from 'app/entities/enumerations/timetable-status.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { TimetableService } from '../service/timetable.service';
import { ITimetable } from '../timetable.model';

import { TimetableFormGroup, TimetableFormService } from './timetable-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-timetable-update',
  templateUrl: './timetable-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class TimetableUpdate implements OnInit {
  readonly isSaving = signal(false);
  timetable: ITimetable | null = null;
  timetableStatusValues = Object.keys(TimetableStatus);

  protected timetableService = inject(TimetableService);
  protected timetableFormService = inject(TimetableFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: TimetableFormGroup = this.timetableFormService.createTimetableFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ timetable }) => {
      this.timetable = timetable;
      if (timetable) {
        this.updateForm(timetable);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const timetable = this.timetableFormService.getTimetable(this.editForm);
    if (timetable.id === null) {
      this.subscribeToSaveResponse(this.timetableService.create(timetable));
    } else {
      this.subscribeToSaveResponse(this.timetableService.update(timetable));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ITimetable | null>): void {
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

  protected updateForm(timetable: ITimetable): void {
    this.timetable = timetable;
    this.timetableFormService.resetForm(this.editForm, timetable);
  }
}
