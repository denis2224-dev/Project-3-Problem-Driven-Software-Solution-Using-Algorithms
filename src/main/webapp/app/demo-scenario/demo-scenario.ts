import { NgClass } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, inject, OnDestroy, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { catchError, of, Subscription, switchMap, takeWhile, timer } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';

interface DemoDataSummary {
  faculties: number;
  departments: number;
  buildings: number;
  rooms: number;
  professors: number;
  studentGroups: number;
  courses: number;
  courseEvents: number;
  timeslots: number;
  professorPreferences: number;
  exams: number;
  message?: string | null;
}

interface SolverJobView {
  id: number;
  status?: string | null;
  startedAt?: string | null;
  finishedAt?: string | null;
  progressPercent?: number | null;
  message?: string | null;
  hardConflictCount?: number | null;
  softPenaltyScore?: number | null;
  backtrackCount?: number | null;
  domainReductionCount?: number | null;
  runtimeMs?: number | null;
}

interface SolverStatisticsView extends SolverJobView {
  jobId: number;
}

interface SolverResultView {
  jobId?: number | null;
  status?: string | null;
  message?: string | null;
  timetableId?: number | null;
  timetableVersionId?: number | null;
  timetableName?: string | null;
  totalHardConflicts?: number | null;
  totalSoftPenalty?: number | null;
  entries?: unknown[] | null;
}

@Component({
  selector: 'jhi-demo-scenario',
  templateUrl: './demo-scenario.html',
  styleUrl: './demo-scenario.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [NgClass, RouterLink],
})
export default class DemoScenario implements OnDestroy {
  readonly stages = [
    'CREATED',
    'VALIDATING_INPUT',
    'BUILDING_CONFLICT_GRAPH',
    'RUNNING_WELCH_POWELL',
    'RUNNING_AC3',
    'RUNNING_BACKTRACKING',
    'SCORING_SOFT_CONSTRAINTS',
    'COMPLETED',
  ];

  readonly summary = signal<DemoDataSummary | null>(null);
  readonly job = signal<SolverJobView | null>(null);
  readonly statistics = signal<SolverStatisticsView | null>(null);
  readonly result = signal<SolverResultView | null>(null);
  readonly loadingDemo = signal(false);
  readonly generating = signal(false);
  readonly error = signal<string | null>(null);

  private readonly terminalStatuses = new Set(['COMPLETED', 'FAILED', 'CANCELLED']);
  private readonly http = inject(HttpClient);
  private readonly applicationConfigService = inject(ApplicationConfigService);
  private pollingSubscription: Subscription | null = null;

  ngOnDestroy(): void {
    this.pollingSubscription?.unsubscribe();
  }

  loadDemoData(): void {
    this.loadingDemo.set(true);
    this.error.set(null);
    this.http
      .post<DemoDataSummary>(this.applicationConfigService.getEndpointFor('api/demo-data/load'), null)
      .pipe(
        catchError(() => {
          this.error.set('Demo data could not be loaded. Sign in as admin and try again.');
          return of(null);
        }),
      )
      .subscribe(summary => {
        this.summary.set(summary);
        this.loadingDemo.set(false);
      });
  }

  clearDemoData(): void {
    this.loadingDemo.set(true);
    this.error.set(null);
    this.http
      .delete<DemoDataSummary>(this.applicationConfigService.getEndpointFor('api/demo-data/clear'))
      .pipe(
        catchError(() => {
          this.error.set('Demo data could not be cleared. Sign in as admin and try again.');
          return of(null);
        }),
      )
      .subscribe(summary => {
        this.summary.set(summary);
        this.job.set(null);
        this.statistics.set(null);
        this.result.set(null);
        this.loadingDemo.set(false);
      });
  }

  generateTimetable(): void {
    this.generating.set(true);
    this.error.set(null);
    this.statistics.set(null);
    this.result.set(null);
    this.http
      .post<SolverJobView>(this.applicationConfigService.getEndpointFor('api/solver-jobs/generate-timetable'), null)
      .pipe(
        catchError(() => {
          this.error.set('Solver job could not be started. Sign in as admin and make sure demo data exists.');
          return of(null);
        }),
      )
      .subscribe(job => {
        if (!job?.id) {
          this.generating.set(false);
          return;
        }
        this.job.set(job);
        this.pollJob(job.id);
      });
  }

  stageState(stage: string): string {
    const status = this.job()?.status ?? 'CREATED';
    if (status === 'FAILED' || status === 'CANCELLED') {
      return stage === status ? 'active' : 'pending';
    }
    const currentIndex = this.stages.indexOf(status);
    const stageIndex = this.stages.indexOf(stage);
    if (stageIndex < currentIndex) {
      return 'done';
    }
    return stageIndex === currentIndex ? 'active' : 'pending';
  }

  formatStage(stage: string): string {
    return stage.replaceAll('_', ' ');
  }

  private pollJob(jobId: number): void {
    this.pollingSubscription?.unsubscribe();
    this.pollingSubscription = timer(0, 1500)
      .pipe(
        switchMap(() => this.http.get<SolverJobView>(this.applicationConfigService.getEndpointFor(`api/solver-jobs/${jobId}`))),
        takeWhile(job => !this.terminalStatuses.has(job.status ?? ''), true),
      )
      .subscribe({
        next: job => {
          this.job.set(job);
          if (this.terminalStatuses.has(job.status ?? '')) {
            this.generating.set(false);
            this.loadStatistics(jobId);
            this.loadResult(jobId);
          }
        },
        error: () => {
          this.generating.set(false);
          this.error.set('Solver job polling failed. Refresh the page or open solver jobs.');
        },
      });
  }

  private loadStatistics(jobId: number): void {
    this.http
      .get<SolverStatisticsView>(this.applicationConfigService.getEndpointFor(`api/solver-jobs/${jobId}/statistics`))
      .pipe(catchError(() => of(null)))
      .subscribe(statistics => this.statistics.set(statistics));
  }

  private loadResult(jobId: number): void {
    this.http
      .get<SolverResultView>(this.applicationConfigService.getEndpointFor(`api/solver-jobs/${jobId}/result`))
      .pipe(catchError(() => of(null)))
      .subscribe(result => this.result.set(result));
  }
}
