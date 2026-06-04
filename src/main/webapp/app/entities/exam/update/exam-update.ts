import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { ICourse } from 'app/entities/course/course.model';
import { CourseService } from 'app/entities/course/service/course.service';
import { StudentGroupService } from 'app/entities/student-group/service/student-group.service';
import { IStudentGroup } from 'app/entities/student-group/student-group.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IExam } from '../exam.model';
import { ExamService } from '../service/exam.service';

import { ExamFormGroup, ExamFormService } from './exam-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-exam-update',
  templateUrl: './exam-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class ExamUpdate implements OnInit {
  readonly isSaving = signal(false);
  exam: IExam | null = null;

  coursesSharedCollection = signal<ICourse[]>([]);
  studentGroupsSharedCollection = signal<IStudentGroup[]>([]);

  protected examService = inject(ExamService);
  protected examFormService = inject(ExamFormService);
  protected courseService = inject(CourseService);
  protected studentGroupService = inject(StudentGroupService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ExamFormGroup = this.examFormService.createExamFormGroup();

  compareCourse = (o1: ICourse | null, o2: ICourse | null): boolean => this.courseService.compareCourse(o1, o2);

  compareStudentGroup = (o1: IStudentGroup | null, o2: IStudentGroup | null): boolean =>
    this.studentGroupService.compareStudentGroup(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ exam }) => {
      this.exam = exam;
      if (exam) {
        this.updateForm(exam);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const exam = this.examFormService.getExam(this.editForm);
    if (exam.id === null) {
      this.subscribeToSaveResponse(this.examService.create(exam));
    } else {
      this.subscribeToSaveResponse(this.examService.update(exam));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IExam | null>): void {
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

  protected updateForm(exam: IExam): void {
    this.exam = exam;
    this.examFormService.resetForm(this.editForm, exam);

    this.coursesSharedCollection.update(courses => this.courseService.addCourseToCollectionIfMissing<ICourse>(courses, exam.course));
    this.studentGroupsSharedCollection.update(studentGroups =>
      this.studentGroupService.addStudentGroupToCollectionIfMissing<IStudentGroup>(studentGroups, exam.studentGroup),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.courseService
      .query()
      .pipe(map((res: HttpResponse<ICourse[]>) => res.body ?? []))
      .pipe(map((courses: ICourse[]) => this.courseService.addCourseToCollectionIfMissing<ICourse>(courses, this.exam?.course)))
      .subscribe((courses: ICourse[]) => this.coursesSharedCollection.set(courses));

    this.studentGroupService
      .query()
      .pipe(map((res: HttpResponse<IStudentGroup[]>) => res.body ?? []))
      .pipe(
        map((studentGroups: IStudentGroup[]) =>
          this.studentGroupService.addStudentGroupToCollectionIfMissing<IStudentGroup>(studentGroups, this.exam?.studentGroup),
        ),
      )
      .subscribe((studentGroups: IStudentGroup[]) => this.studentGroupsSharedCollection.set(studentGroups));
  }
}
