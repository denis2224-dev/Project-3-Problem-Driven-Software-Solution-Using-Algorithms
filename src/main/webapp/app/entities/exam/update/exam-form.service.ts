import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IExam, NewExam } from '../exam.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IExam for edit and NewExamFormGroupInput for create.
 */
type ExamFormGroupInput = IExam | PartialWithRequiredKeyOf<NewExam>;

type ExamFormDefaults = Pick<NewExam, 'id'>;

type ExamFormGroupContent = {
  id: FormControl<IExam['id'] | NewExam['id']>;
  name: FormControl<IExam['name']>;
  durationMinutes: FormControl<IExam['durationMinutes']>;
  expectedStudents: FormControl<IExam['expectedStudents']>;
  course: FormControl<IExam['course']>;
  studentGroup: FormControl<IExam['studentGroup']>;
};

export type ExamFormGroup = FormGroup<ExamFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ExamFormService {
  createExamFormGroup(exam?: ExamFormGroupInput): ExamFormGroup {
    const examRawValue = {
      ...this.getFormDefaults(),
      ...(exam ?? { id: null }),
    };
    return new FormGroup<ExamFormGroupContent>({
      id: new FormControl(
        { value: examRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(examRawValue.name, {
        validators: [Validators.required, Validators.maxLength(160)],
      }),
      durationMinutes: new FormControl(examRawValue.durationMinutes, {
        validators: [Validators.required, Validators.min(30), Validators.max(240)],
      }),
      expectedStudents: new FormControl(examRawValue.expectedStudents, {
        validators: [Validators.required, Validators.min(1)],
      }),
      course: new FormControl(examRawValue.course, {
        validators: [Validators.required],
      }),
      studentGroup: new FormControl(examRawValue.studentGroup, {
        validators: [Validators.required],
      }),
    });
  }

  getExam(form: ExamFormGroup): IExam | NewExam {
    return form.getRawValue();
  }

  resetForm(form: ExamFormGroup, exam: ExamFormGroupInput): void {
    const examRawValue = { ...this.getFormDefaults(), ...exam };
    form.reset({
      ...examRawValue,
      id: { value: examRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): ExamFormDefaults {
    return {
      id: null,
    };
  }
}
