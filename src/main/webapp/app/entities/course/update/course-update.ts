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
import { ICourse } from '../course.model';
import { CourseService } from '../service/course.service';

import { CourseFormGroup, CourseFormService } from './course-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-course-update',
  templateUrl: './course-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class CourseUpdate implements OnInit {
  readonly isSaving = signal(false);
  course: ICourse | null = null;

  departmentsSharedCollection = signal<IDepartment[]>([]);

  protected courseService = inject(CourseService);
  protected courseFormService = inject(CourseFormService);
  protected departmentService = inject(DepartmentService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CourseFormGroup = this.courseFormService.createCourseFormGroup();

  compareDepartment = (o1: IDepartment | null, o2: IDepartment | null): boolean => this.departmentService.compareDepartment(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ course }) => {
      this.course = course;
      if (course) {
        this.updateForm(course);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const course = this.courseFormService.getCourse(this.editForm);
    if (course.id === null) {
      this.subscribeToSaveResponse(this.courseService.create(course));
    } else {
      this.subscribeToSaveResponse(this.courseService.update(course));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ICourse | null>): void {
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

  protected updateForm(course: ICourse): void {
    this.course = course;
    this.courseFormService.resetForm(this.editForm, course);

    this.departmentsSharedCollection.update(departments =>
      this.departmentService.addDepartmentToCollectionIfMissing<IDepartment>(departments, course.department),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.departmentService
      .query()
      .pipe(map((res: HttpResponse<IDepartment[]>) => res.body ?? []))
      .pipe(
        map((departments: IDepartment[]) =>
          this.departmentService.addDepartmentToCollectionIfMissing<IDepartment>(departments, this.course?.department),
        ),
      )
      .subscribe((departments: IDepartment[]) => this.departmentsSharedCollection.set(departments));
  }
}
