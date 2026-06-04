import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { TranslateModule } from '@ngx-translate/core';

import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IExamScheduleEntry } from '../exam-schedule-entry.model';
import { ExamScheduleEntryService } from '../service/exam-schedule-entry.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './exam-schedule-entry-delete-dialog.html',
  imports: [TranslateDirective, TranslateModule, FormsModule, FontAwesomeModule, AlertError],
})
export class ExamScheduleEntryDeleteDialog {
  examScheduleEntry?: IExamScheduleEntry;

  protected readonly examScheduleEntryService = inject(ExamScheduleEntryService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.examScheduleEntryService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
