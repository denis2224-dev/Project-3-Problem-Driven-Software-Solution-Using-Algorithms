import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IExamScheduleEntry } from '../exam-schedule-entry.model';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-exam-schedule-entry-detail',
  templateUrl: './exam-schedule-entry-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, TranslateModule, RouterLink],
})
export class ExamScheduleEntryDetail {
  readonly examScheduleEntry = input<IExamScheduleEntry | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
