import { NgClass } from '@angular/common';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, effect, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

import { catchError, forkJoin, map, Observable, of } from 'rxjs';

import { AccountService } from 'app/core/auth/account.service';
import { ApplicationConfigService } from 'app/core/config/application-config.service';

interface DashboardMetric {
  label: string;
  value: number;
  detail: string;
  route: string;
  accent: 'purple' | 'cyan' | 'blue' | 'pink';
}

interface DashboardSolverJob {
  id: number;
  status?: string | null;
  progressPercent?: number | null;
  hardConflictCount?: number | null;
  softPenaltyScore?: number | null;
  backtrackCount?: number | null;
  domainReductionCount?: number | null;
  runtimeMs?: number | null;
  message?: string | null;
}

@Component({
  selector: 'jhi-home',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './home.html',
  styleUrl: './home.scss',
  imports: [NgClass, RouterLink],
})
export default class Home {
  public readonly account = inject(AccountService).account;

  readonly metrics = signal<DashboardMetric[]>([
    { label: 'Courses', value: 0, detail: 'Curriculum units in the scheduling domain', route: '/course', accent: 'purple' },
    { label: 'Professors', value: 0, detail: 'Teaching staff with timetable constraints', route: '/professor', accent: 'cyan' },
    { label: 'Rooms', value: 0, detail: 'Lecture halls, labs, seminars, exam rooms', route: '/room', accent: 'blue' },
    { label: 'Course events', value: 0, detail: 'Lectures, laboratories, and seminars to place', route: '/course-event', accent: 'pink' },
  ]);
  readonly latestJob = signal<DashboardSolverJob | null>(null);
  readonly loading = signal(false);
  readonly dashboardError = signal<string | null>(null);

  private dashboardLoaded = false;
  private readonly router = inject(Router);
  private readonly http = inject(HttpClient);
  private readonly applicationConfigService = inject(ApplicationConfigService);

  constructor() {
    effect(() => {
      if (this.account() && !this.dashboardLoaded) {
        this.dashboardLoaded = true;
        this.loadDashboard();
      }
    });
  }

  login(): void {
    this.router.navigate(['/login']);
  }

  loadDashboard(): void {
    this.loading.set(true);
    this.dashboardError.set(null);

    forkJoin({
      courses: this.countResource('api/courses'),
      professors: this.countResource('api/professors'),
      rooms: this.countResource('api/rooms'),
      courseEvents: this.countResource('api/course-events'),
      latestJob: this.latestSolverJob(),
    }).subscribe({
      next: result => {
        this.metrics.set([
          { ...this.metrics()[0], value: result.courses },
          { ...this.metrics()[1], value: result.professors },
          { ...this.metrics()[2], value: result.rooms },
          { ...this.metrics()[3], value: result.courseEvents },
        ]);
        this.latestJob.set(result.latestJob);
        this.loading.set(false);
      },
      error: () => {
        this.dashboardError.set('Dashboard data could not be loaded.');
        this.loading.set(false);
      },
    });
  }

  statusClass(status: string | null | undefined): string {
    return (status ?? 'NO_JOB').toLowerCase().replaceAll('_', '-');
  }

  private countResource(endpoint: string): Observable<number> {
    return this.http
      .get<unknown[]>(this.applicationConfigService.getEndpointFor(endpoint), {
        params: { page: 0, size: 1 },
        observe: 'response',
      })
      .pipe(
        map((response: HttpResponse<unknown[]>) => Number(response.headers.get('X-Total-Count') ?? response.body?.length ?? 0)),
        catchError(() => of(0)),
      );
  }

  private latestSolverJob(): Observable<DashboardSolverJob | null> {
    return this.http
      .get<DashboardSolverJob[]>(this.applicationConfigService.getEndpointFor('api/solver-jobs'), {
        params: { page: 0, size: 1, sort: 'id,desc' },
        observe: 'response',
      })
      .pipe(
        map(response => response.body?.[0] ?? null),
        catchError(() => of(null)),
      );
  }
}
