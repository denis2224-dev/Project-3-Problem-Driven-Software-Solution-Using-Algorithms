import { HttpClient } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, computed, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { catchError, of } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';

interface ExamColoringEntry {
  examId?: number | null;
  examName?: string | null;
  courseCode?: string | null;
  courseName?: string | null;
  studentGroupName?: string | null;
  color?: number | null;
  suggestedPeriod?: string | null;
}

interface ExamColoringResult {
  examCount: number;
  colorCount: number;
  conflictEdgeCount: number;
  conflictFree: boolean;
  entries: ExamColoringEntry[];
}

interface ExamPeriodGroup {
  period: string;
  color: number;
  entries: ExamColoringEntry[];
}

@Component({
  selector: 'jhi-exam-schedule',
  templateUrl: './exam-schedule.html',
  styleUrl: './exam-schedule.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink],
})
export default class ExamSchedule implements OnInit {
  readonly result = signal<ExamColoringResult | null>(null);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  readonly periods = computed<ExamPeriodGroup[]>(() => {
    const groups = new Map<number, ExamColoringEntry[]>();
    for (const entry of this.result()?.entries ?? []) {
      const color = entry.color ?? 0;
      groups.set(color, [...(groups.get(color) ?? []), entry]);
    }
    return [...groups.entries()]
      .sort(([left], [right]) => left - right)
      .map(([color, entries]) => ({ color, period: `Exam period ${color + 1}`, entries }));
  });

  private readonly http = inject(HttpClient);
  private readonly applicationConfigService = inject(ApplicationConfigService);

  ngOnInit(): void {
    this.loadColoring();
  }

  loadColoring(): void {
    this.loading.set(true);
    this.error.set(null);
    this.http
      .get<ExamColoringResult>(this.applicationConfigService.getEndpointFor('api/exams/coloring'))
      .pipe(
        catchError(() => {
          this.error.set('Exam coloring could not be loaded. Load demo data first, then refresh this page.');
          return of(null);
        }),
      )
      .subscribe(result => {
        this.result.set(result);
        this.loading.set(false);
      });
  }
}
