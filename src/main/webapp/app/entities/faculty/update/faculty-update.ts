import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable, finalize } from 'rxjs';

import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IFaculty } from '../faculty.model';
import { FacultyService } from '../service/faculty.service';

import { FacultyFormGroup, FacultyFormService } from './faculty-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-faculty-update',
  templateUrl: './faculty-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class FacultyUpdate implements OnInit {
  readonly isSaving = signal(false);
  faculty: IFaculty | null = null;

  protected facultyService = inject(FacultyService);
  protected facultyFormService = inject(FacultyFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: FacultyFormGroup = this.facultyFormService.createFacultyFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ faculty }) => {
      this.faculty = faculty;
      if (faculty) {
        this.updateForm(faculty);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const faculty = this.facultyFormService.getFaculty(this.editForm);
    if (faculty.id === null) {
      this.subscribeToSaveResponse(this.facultyService.create(faculty));
    } else {
      this.subscribeToSaveResponse(this.facultyService.update(faculty));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IFaculty | null>): void {
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

  protected updateForm(faculty: IFaculty): void {
    this.faculty = faculty;
    this.facultyFormService.resetForm(this.editForm, faculty);
  }
}
