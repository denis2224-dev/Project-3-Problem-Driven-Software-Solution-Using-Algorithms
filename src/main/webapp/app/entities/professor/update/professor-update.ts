import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { IDepartment } from 'app/entities/department/department.model';
import { DepartmentService } from 'app/entities/department/service/department.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IProfessor } from '../professor.model';
import { ProfessorService } from '../service/professor.service';

import { ProfessorFormGroup, ProfessorFormService } from './professor-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-professor-update',
  templateUrl: './professor-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class ProfessorUpdate implements OnInit {
  readonly isSaving = signal(false);
  professor: IProfessor | null = null;

  departmentsSharedCollection = signal<IDepartment[]>([]);

  protected professorService = inject(ProfessorService);
  protected professorFormService = inject(ProfessorFormService);
  protected departmentService = inject(DepartmentService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ProfessorFormGroup = this.professorFormService.createProfessorFormGroup();

  compareDepartment = (o1: IDepartment | null, o2: IDepartment | null): boolean => this.departmentService.compareDepartment(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ professor }) => {
      this.professor = professor;
      if (professor) {
        this.updateForm(professor);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const professor = this.professorFormService.getProfessor(this.editForm);
    if (professor.id === null) {
      this.subscribeToSaveResponse(this.professorService.create(professor));
    } else {
      this.subscribeToSaveResponse(this.professorService.update(professor));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IProfessor | null>): void {
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

  protected updateForm(professor: IProfessor): void {
    this.professor = professor;
    this.professorFormService.resetForm(this.editForm, professor);

    this.departmentsSharedCollection.update(departments =>
      this.departmentService.addDepartmentToCollectionIfMissing<IDepartment>(departments, professor.department),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.departmentService
      .query()
      .pipe(map((res: HttpResponse<IDepartment[]>) => res.body ?? []))
      .pipe(
        map((departments: IDepartment[]) =>
          this.departmentService.addDepartmentToCollectionIfMissing<IDepartment>(departments, this.professor?.department),
        ),
      )
      .subscribe((departments: IDepartment[]) => this.departmentsSharedCollection.set(departments));
  }
}
